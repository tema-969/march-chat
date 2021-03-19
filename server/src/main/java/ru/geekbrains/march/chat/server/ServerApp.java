package ru.geekbrains.march.chat.server;

import com.sun.corba.se.spi.activation.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    public static void main(String[] args) {
        int counter = 0;

        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Сервер запущен на порту 8189. Ожидаем подключение клиента... ");
            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Клиент подключился.");

            while (true) {
                String msg = in.readUTF();

                if (msg.equals("/stat")){
                    out.writeUTF("Количество сообщений = "+counter);
                }else {
                    counter++;
                System.out.println(msg);
                out.writeUTF("ECHO: " + msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}