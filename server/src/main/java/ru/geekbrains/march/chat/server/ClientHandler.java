package ru.geekbrains.march.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {

                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/login ")) {
                        String usernameFromLogin = msg.split("\\s")[1];
                        if (server.isNickBusy(usernameFromLogin)) {
                            sendMessage("/login_failed Current nickname is already used" + username);
                            continue;
                        }

                        username = usernameFromLogin;
                        sendMessage("/login_ok " + username);
                        server.subscribe(this);
                        break;
                    }
                }
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")){
                        if(msg.equals("/who am i")){
                            sendMessage("ECHO: your username : " + this.getUsername());
                            continue;
                        }
                        if (msg.equals("/exit")){
                            break;
                        }
                        if (msg.startsWith("/w ")){
                            String usName = msg.split("\\s",3)[1];
                            String prtMsg = msg.split("\\s",3)[2];
                            if(server.isNickBusy(usName)){
                                server.privateMsg(usName,username +": " + prtMsg);
                                sendMessage(username + ": " + prtMsg);
                                continue;
                            }
                        }
                    }
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
