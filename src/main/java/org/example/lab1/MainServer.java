package org.example.lab1;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.gson.Gson;

public class MainServer {
    int port = 8080;
    InetAddress ip = null;

    ServerSocket ss;
    Socket cs;
    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;

    final double[] pyPos = {239, 369, 523, 97};

    Thread t = null;

    Game game;
    int pausedPlayerId = -1;

    Runnable gameThread = () ->
    {
        while (game.stage != Stage.KILLED) {
            try {
                synchronized (game) {
                    switch (game.stage) {
                        case RUNNING:
                            game.update();
                            break;
                        case NO_PLAYERS:
                            if(game.numPlayers > 0) {
                                game.stage = Stage.WAITING_TO_START;
                                System.out.println("game: waiting to start");
                            } else {
                                System.out.println("game: no players - waiting for players");
                            }
                            break;
                        case WAITING_TO_START:
                        case PAUSED:
                            boolean allReady = true;
                            for(int i = 0 ; i < game.numPlayers; i++) {
                                allReady = allReady && game.players[i].isReady;
                            }
                            if(allReady) {
                                game.stage = Stage.RUNNING;
                                System.out.println("game: started");
                            }
                        default:
                    }
                }
                Thread.sleep(20);
            } catch (InterruptedException e) {
                game.stage = Stage.KILLED;
                t = null;
            }
        }
    };

    void addPlayer(String name) {
        if(game.numPlayers == game.MAX_PLAYERS) {
            // INFO: this branch should not be reached
            System.out.println("The limit of players has been reached");
            System.exit(1);
        }
        if(game.stage != Stage.WAITING_TO_START && game.stage != Stage.NO_PLAYERS) {
            // INFO: this branch should not be reached
            System.out.println("Connecting to a running game");
            System.exit(1);
        }
        int i = game.numPlayers++;
        // TODO: xBound yBound duplicated in Player
        game.players[i] = new Player(name, pyPos[i], game.xBound,game.yBound, game);
    }

    void act(Message msg) {
        switch (msg.action()) {
            case CONNECTED:
                System.out.println("player add");
                addPlayer(msg.info());
                break;
            case SHOOT:
                System.out.println("shoot");
                if(!game.players[msg.playerId()].isShooting) {
                    game.players[msg.playerId()].isShooting = true;
                    game.players[msg.playerId()].shots++;
                }
                break;
            case PAUSE:
                System.out.println("pause");
                pausedPlayerId = msg.playerId();
                game.players[pausedPlayerId].isReady = false;
                game.stage = Stage.PAUSED;
                break;
            case READY:
                System.out.println("ready");
                game.players[msg.playerId()].isReady = true;
                break;
            case RESUME:
                System.out.println("resume");
                if(pausedPlayerId == msg.playerId()) {
                    game.players[msg.playerId()].isReady = true;
                }
                break;
        }
    }

    void start() {
        game = new Game();
        try {
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 0 , ip);
            System.out.println("Start");
        } catch (IOException e) {
            game.stage = Stage.KILLED;
            throw new RuntimeException(e);
        }
        if (t == null) {
            t = new Thread(gameThread);
            t.start();
        }
        while (game.stage != Stage.KILLED) {
            Gson json = new Gson();
            //System.out.println("socket: "+json.toJson(game.getState()));
            try {
                // TODO: how to close streams if exception occurs?
                cs = ss.accept(); // ожидание клиента
                int port = cs.getPort();
                //System.out.println("socket: connect (" + port + ")");
                synchronized (game) {
                    // TODO: chose synchronized area
                    // >> OUTPUT
                    os = cs.getOutputStream();
                    dos = new DataOutputStream(os);

                    String str1 = json.toJson(game.getState());
                    //System.out.println("socket: send: " + str1);

                    dos.writeUTF(str1);
                    dos.flush();

                    //System.out.println("socket: successfully sent");
                    // >> INPUT
                    is = cs.getInputStream();
                    dis = new DataInputStream(is);

                    String str = dis.readUTF();
                    //System.out.println("socket: input message: "+str);
                    Message msg = json.fromJson(str, Message.class);

                    act(msg);

                    is.close();
                    dis.close();
                    os.close();
                    dos.close();
                }

            } catch (IOException e) {
                game.stage = Stage.KILLED;
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) {
        MainServer server = new MainServer();
        server.start();
    }

}
