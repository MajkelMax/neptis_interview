package org.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TcpServer {

    private static final String url = "jdbc:postgresql://localhost:5432/janosik";
    private static final String username = "postgres";
    private static final String password = "root";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Serwer działa na porcie 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie!");

                // Obsługa kilku użytkowników na raz
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            Long userId = Long.parseLong(in.readLine());
            String username = in.readLine();
            String password = in.readLine();

            String[] usernameAndPassword = getUsernameAndPasswordFromDatabase(userId);

            if (username.equals(usernameAndPassword[0]) && password.equals(usernameAndPassword[1])) {
                String result = getDataFromDatabase(userId);
                // Odesłanie danych do klienta
                out.println(result);
            } else {
                String result = "false";
                out.println(result);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private static String getDataFromDatabase(Long userId) {
        StringBuilder result = new StringBuilder();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Zapytanie SQL pobierające dane potrzebne do zrealizowania zadania
            String query = "SELECT vehicles.*, insurance_offers.*\n" +
                    "FROM vehicles\n" +
                    "LEFT JOIN insurance_offers ON vehicles.id = insurance_offers.vehicle_id\n" +
                    "WHERE vehicles.login = (SELECT login FROM users WHERE id = ?);";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Dodanie danych do wyniku, oddzielając je znakiem "$"
                        result.append(resultSet.getString("vehicle_id")).append("$");
                        result.append(resultSet.getString("brand")).append("$");
                        result.append(resultSet.getString("model")).append("$");
                        result.append(resultSet.getString("insurer")).append("$");
                        result.append(resultSet.getString("price")).append("$");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private static String[] getUsernameAndPasswordFromDatabase(Long userId) {
        String[] data = new String[2];

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Zapytanie SQL zaciągające dane użytkownika
            String query = "SELECT * FROM users WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        data[0] = resultSet.getString("login");
                        data[1] = resultSet.getString("password");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}