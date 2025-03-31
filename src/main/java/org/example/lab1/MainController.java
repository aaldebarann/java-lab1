package org.example.lab1;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class MainController {

    public static int MAX_PLAYERS = 4;
    @FXML
    Circle creep, minicreep,
    arrow1, arrow2, arrow3, arrow4;
    ArrayList<Circle> arrows = new ArrayList<>();
    @FXML
    Polygon player1, player2, player3, player4;
    ArrayList<Polygon> players = new ArrayList<>();
    @FXML
    TextArea username1, username2, username3, username4;
    ArrayList<TextArea> names = new ArrayList<>();
    @FXML
    AnchorPane mainPane;
    @FXML
    private Label score1, score2, score3, score4,
            shots1, shots2, shots3, shots4;
    ArrayList<Label> scores = new ArrayList<>();
    ArrayList<Label> shots = new ArrayList<>();

    Thread t = null;

    int port = 8080;
    InetAddress ip = null;
    Socket cs;
    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;

    String playerName;

    int playerId = -1;
    boolean isReady = false;
    Queue<Message> messageQueue;
    GameState gameState;

    void updateCreep(Circle creep, CreepState creepState) {
        creep.setLayoutX(creepState.x());
        creep.setLayoutY(creepState.y());
    }
    void updateArrow(Circle arr, ArrowState arrowState) {
        arr.setLayoutX(arrowState.x());
        arr.setLayoutY(arrowState.y());
    }
    void updatePlayer(int i, PlayerState playerState) {
        scores.get(i).setText(String.valueOf(playerState.score()));
        shots.get(i).setText(String.valueOf(playerState.shots()));
        names.get(i).setText(playerState.name());
        updateArrow(arrows.get(i), playerState.arrow());
        if(!playerState.isShooting())
            arrows.get(i).setVisible(false); // TODO: bad code
    }
    void update() {
        updateCreep(creep, gameState.creep());
        updateCreep(minicreep, gameState.miniCreep());
        for(int i = 0; i < MAX_PLAYERS; i++) {
            int k = gameState.numPlayers();
            if(i >= k) {
                players.get(i).setVisible(false);
                arrows.get(i).setVisible(false);
                shots.get(i).setVisible(false);
                names.get(i).setVisible(false);
                scores.get(i).setVisible(false);
            } else {
                players.get(i).setVisible(true);
                arrows.get(i).setVisible(true);
                shots.get(i).setVisible(true);
                names.get(i).setVisible(true);
                scores.get(i).setVisible(true);
                updatePlayer(i, gameState.playerStates()[i]);
            }
        }
    }

    @FXML
    public void ready() {
        if(!isReady && gameState.stage() == Stage.WAITING_TO_START) {
            messageQueue.add(new Message(Action.READY, playerId, "nosence"));
            isReady = true;
        }
    }
    /*@FXML
    public void end() {
    }
    */
    @FXML
    void pause(){
        if(isReady && gameState.stage() == Stage.RUNNING) {
            messageQueue.add(new Message(Action.PAUSE, playerId, "nosence"));
            isReady = false;
        }
    }
    @FXML
    void cont(){
        if(!isReady && gameState.stage() == Stage.PAUSED) {
            messageQueue.add(new Message(Action.RESUME, playerId, "nosence"));
            isReady = true;
        }
    }
    @FXML
    public void shoot() {
        if(gameState.stage() == Stage.RUNNING) {
            messageQueue.add(new Message(Action.SHOOT, playerId, "nosence"));
        }
    }

    private GameState connect() {
        System.out.println("Trying to connect to game");
        GameState gameState = null;
        Gson json = new Gson();
        try {
            ip = InetAddress.getLocalHost();
            cs = new Socket(ip, port);
            cs.setSoTimeout(3000);
            int port = cs.getPort();
            System.out.println("Connect (" + port + ")");

            is = cs.getInputStream();
            dis = new DataInputStream(is);

            String str = dis.readUTF();
            System.out.println("Input message: " + str);
            gameState = json.fromJson(str, GameState.class);

            Message msg;
            if(gameState.numPlayers() == MAX_PLAYERS ||
                    (gameState.stage() != Stage.WAITING_TO_START && gameState.stage() != Stage.NO_PLAYERS) ) {
                // connecting is impossible
                msg = new Message(Action.PASS, playerId, "hello world");
                System.out.println("Connection is impossible");
            } else {
                playerId = gameState.numPlayers();
                msg = new Message(Action.CONNECTED, playerId, playerName);
            }

            os = cs.getOutputStream();
            dos = new DataOutputStream(os);

            String str1 = json.toJson(msg);
            System.out.println("Send :" + str1);

            dos.writeUTF(str1);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gameState; // TODO: try/catch block
    }

    private GameState getGameState(Message msg) {
        // TODO: connect() method duplicated?
        System.out.println("Updating info");
        GameState gameState;
        Gson json = new Gson();
        try {
            String str = dis.readUTF();
            System.out.println("Input message: " + str);
            gameState = json.fromJson(str, GameState.class);

            String str1 = json.toJson(msg);
            System.out.println("Send :" + str1);

            dos.writeUTF(str1);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gameState; // TODO: try/catch block
    }


    public void startListening() {
        messageQueue = new LinkedList<>();
        if(t == null)
        {
            t = new Thread(
                    ()->
                    {
                        gameState = connect();
                        if(gameState == null) {
                            System.exit(1);
                        }
                        //names.get(playerId).setStyle("-fx-control-inner-background: #c9c0cb;");
                        names.get(playerId).setFont(new Font(22));
                        while (true) {
                            Message nextMessage= new Message(Action.PASS, playerId, "sh-boom");;
                            if(!messageQueue.isEmpty()) {
                                nextMessage = messageQueue.poll();
                            }
                            gameState = getGameState(nextMessage);

                            Platform.runLater(this::update);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );
            t.start();
        }
    }

    @FXML
    public void initialize(){
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        arrows.add(arrow1);
        arrows.add(arrow2);
        arrows.add(arrow3);
        arrows.add(arrow4);
        scores.add(score1);
        scores.add(score2);
        scores.add(score3);
        scores.add(score4);
        shots.add(shots1);
        shots.add(shots2);
        shots.add(shots3);
        shots.add(shots4);
        names.add(username1);
        names.add(username2);
        names.add(username3);
        names.add(username4);
        System.out.println("Введите имя:");
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String inputString = "noname";
        try {
            inputString = bufferRead.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        playerName = inputString;

        System.out.println("Name: "+playerName);
        startListening();
    }
}