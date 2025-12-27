package com.recomtree.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client class for connecting to movie recommendation server
public class RecomClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Scanner scanner = new Scanner(System.in);
        String line; // Variable for reading server responses

        // Display welcome ASCII art
        printWelcomeBanner();

        try {
            // Connect to server
            System.out.println("Connecting to server...");
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Print welcome message from server
            System.out.println("\n========================================");
            // Read all welcome lines from server
            while ((line = in.readLine()) != null) {
                if ("<END_OF_RESPONSE>".equals(line)) {
                    break;
                }
                System.out.println(line);
            }
            System.out.println("========================================");
            System.out.println("\nType 'exit' to quit\n");

            // Main loop for user input
            while (true) {
                System.out.print("\n> ");
                String input = scanner.nextLine();

                // Check if user wants to exit
                if ("exit".equalsIgnoreCase(input)) {
                    out.println("Goodbye!");
                    break;
                }

                // Send command to server
                out.println(input);

                // Read response from server
                while ((line = in.readLine()) != null) {
                    if ("<END_OF_RESPONSE>".equals(line)) {
                        break;
                    }
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            // Clean up resources
            if (scanner != null) scanner.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        }
    }

    // Method to print welcome banner with ASCII art
    private static void printWelcomeBanner() {
        System.out.println("\n==========================================");
        System.out.println("           RECOMTREE - MOVIES               ");
        System.out.println("         Recommendation System              ");
        System.out.println();
        System.out.println("              &&& &&  & &&");
        System.out.println("         && &\\/&\\|& ()|/ @, &&");
        System.out.println("         &\\/(/&/&||/& /_/)_&/_&");
        System.out.println("      &() &\\/&|()|/&\\/ '%\" & ()");
        System.out.println("     &_\\_&&_\\ |& |&&/&__%_/_& &&");
        System.out.println("   &&   && & &| &| /& & % ()& /&&");
        System.out.println("    ()&_---()&\\&\\|&&-&&--%---()~");
        System.out.println("        &&     \\|||");
        System.out.println("                |||");
        System.out.println("                |||");
        System.out.println("                |||");
        System.out.println("          , -=-~  .-^- _");

        System.out.println("==========================================\n");
    }
}