package org.example.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.List;


public class Client {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Scanner scanner = new Scanner(System.in);

            // Wprowadzenie ID użytkownika
            System.out.print("Podaj ID użytkownika: ");
            String input = scanner.next();
            out.println(input);

            // Odczytanie danych z serwera
            String response = in.readLine();

            // Przetwarzanie danych i wyświetlanie
            processAndDisplayData(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processAndDisplayData(String response) {
        String[] data = response.split("\\$");
        Map<Integer, List<String>> hashMap = new HashMap<>();

        if (data.length == 1){
            System.out.println("Brak danych użytkownika !!!");
        }

        int multi = 0;
        int tempIndex = 0;

        for (int i = 0; i < data.length; i++) {
            if (multi == i) {
                tempIndex = multi;
                multi += 5;
            } else {
                addElement(hashMap, Integer.parseInt(data[tempIndex]), data[i]);
            }
        }

        int separate = 3;
        int separateCompare = 0;

        // Wyświetlanie przetworzonych danych
        for (int i : hashMap.keySet()) {
            List<String> values = hashMap.get(i);
            System.out.println("Car id: " + i);
            for (String value : values) {
                System.out.println("  " + value);
                if (separate == separateCompare) {
                    System.out.println("------------------");
                    separate += 4;
                }
                separateCompare++;
            }
            System.out.println("####################");
        }
    }

    private static void addElement(Map<Integer, List<String>> map, Integer key, String value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            List<String> newList = new ArrayList<>();
            newList.add(value);
            map.put(key, newList);
        }
    }
}
