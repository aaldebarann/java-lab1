package org.example.lab1;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class MainClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerName;
    private Consumer<String> serverMessageHandler;
    private boolean connected = false;

    public MainClient(String serverAddress, int port, String playerName, Consumer<String> messageHandler) throws IOException {
        this.playerName = playerName;
        this.serverMessageHandler = messageHandler;
        connectToServer(serverAddress, port);
    }

    private void connectToServer(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;

        // Отправляем имя серверу
        out.println(playerName);

        // Запускаем поток для чтения сообщений от сервера
        new Thread(this::listenToServer).start();
    }

    private void listenToServer() {
        try {
            String serverMessage;
            while (connected && (serverMessage = in.readLine()) != null) {
                serverMessageHandler.accept(serverMessage);
            }
        } catch (IOException e) {
            if (connected) {
                System.err.println("Ошибка соединения с сервером: " + e.getMessage());
                disconnect();
            }
        }
    }

    public void sendReady() {
        if (connected) out.println("READY");
    }

    public void sendPause() {
        if (connected) out.println("PAUSE");
    }

    public void sendShoot() {
        if (connected) out.println("SHOOT");
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }
}