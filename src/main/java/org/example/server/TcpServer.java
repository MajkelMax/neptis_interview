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

public class TcpServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Serwer działa na porcie 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie!");
                //Obsługa kilku użytkowników na raz
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

            // Odczytanie id użytkownika z komunikatu klienta
            Long userId = Long.parseLong(in.readLine());

            // Pobranie pojazdów i ofert ubezpieczeniowych dla danego użytkownika z bazy danych
            String result = getDataFromDatabase(userId);

            // Odesłanie danych do klienta
            out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDataFromDatabase(Long userId) {
        StringBuilder result = new StringBuilder();

        try {
            // Pobranie danych z bazy danych
            String url = "jdbc:postgresql://localhost:5432/janosik"; // WPROWADŹ WŁASNE DANE !!!
            String username = "postgres";
            String password = "root";

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                // Zapytanie SQL
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}