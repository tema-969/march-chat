package ru.geekbrains.march.chat.server;



import java.sql.*;

public class DbAutenticationProvider implements AuthenticationProvider{

    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psST;

    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
            stmt = connection.createStatement();
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Невозможно подключиться к БД");
        }
    }

    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            ResultSet resSet = stmt.executeQuery("select nickname from clients where login = '" + login + "' and password = '" + password + "';");
            if (resSet.next()) {
                return resSet.getString("nickname");
            }
            return null;
        }catch (SQLException throwables){
            throwables.printStackTrace();
            return null;
        }
    }
    
    
    public void changeNickname(String oldNickname, String newNickname){
        try {
            psST = connection.prepareStatement("UPDATE clients SET nickname = '" + newNickname +"' where nickname = '" + oldNickname +"';" );
            psST.executeUpdate();
            psST.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //throw new UnsupportedOperationException();
    }

    public  void disconnect() {
        try {
            if (stmt != null){
                stmt.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
       if (connection != null){
           try {
               connection.close();
           }catch (SQLException throwables){
               throwables.printStackTrace();
           }
       }
    }
}
