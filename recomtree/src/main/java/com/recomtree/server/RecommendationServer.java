package com.recomtree.server;

import com.recomtree.composite.Genre;
import com.recomtree.persistence.TreePersistence;
import com.recomtree.command.CommandInvoker;
import com.recomtree.service.CatalogService;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Duration;

// Server for the recommendation system
public class RecommendationServer {
    private static final int PORT = 8888;
    private static Genre rootCatalog;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ActivityMetrics metrics = new ActivityMetrics();

    // Class to track activity metrics
    private static class ActivityMetrics {
        private final AtomicInteger totalConnections = new AtomicInteger(0);
        private final AtomicInteger currentConnections = new AtomicInteger(0);
        private final AtomicInteger totalCommands = new AtomicInteger(0);
        private final ConcurrentHashMap<String, AtomicInteger> commandCounts = new ConcurrentHashMap<>();
        private final AtomicInteger adminLogins = new AtomicInteger(0);
        private final AtomicInteger userLogins = new AtomicInteger(0);
        private final AtomicInteger failedLogins = new AtomicInteger(0);
        private final LocalDateTime serverStartTime = LocalDateTime.now();

        public void incrementTotalConnections() {
            totalConnections.incrementAndGet();
        }

        public void incrementCurrentConnections() {
            currentConnections.incrementAndGet();
        }

        public void decrementCurrentConnections() {
            currentConnections.decrementAndGet();
        }

        public void incrementCommand(String commandName) {
            totalCommands.incrementAndGet();
            commandCounts.computeIfAbsent(commandName, k -> new AtomicInteger(0)).incrementAndGet();
        }

        public void incrementAdminLogins() {
            adminLogins.incrementAndGet();
        }

        public void incrementUserLogins() {
            userLogins.incrementAndGet();
        }

        public void incrementFailedLogins() {
            failedLogins.incrementAndGet();
        }

        public String getSummary() {
            Duration uptime = Duration.between(serverStartTime, LocalDateTime.now());
            long hours = uptime.toHours();
            long minutes = uptime.toMinutesPart();
            long seconds = uptime.toSecondsPart();

            StringBuilder sb = new StringBuilder();
            sb.append("\n========== SERVER ACTIVITY METRICS ==========\n");
            sb.append("Server uptime: ").append(hours).append("h ").append(minutes).append("m ").append(seconds).append("s\n");
            sb.append("Total connections: ").append(totalConnections.get()).append("\n");
            sb.append("Current active connections: ").append(currentConnections.get()).append("\n");
            sb.append("Total commands executed: ").append(totalCommands.get()).append("\n");
            sb.append("\nLogin statistics:\n");
            sb.append("  - Admin logins: ").append(adminLogins.get()).append("\n");
            sb.append("  - User logins: ").append(userLogins.get()).append("\n");
            sb.append("  - Failed logins: ").append(failedLogins.get()).append("\n");

            if (!commandCounts.isEmpty()) {
                sb.append("\nCommand usage:\n");
                commandCounts.forEach((cmd, count) ->
                    sb.append("  - ").append(cmd).append(": ").append(count.get()).append(" times\n")
                );
            }
            sb.append("=============================================\n");
            return sb.toString();
        }
    }

    // Method to log messages with timestamp
    private static void log(String message) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        System.out.println("[" + timestamp + "] " + message);
    }

    // Method to display metrics
    private static void displayMetrics() {
        System.out.println(metrics.getSummary());
    }

    public static void main(String[] args) {
        log("========================================");
        log("Starting RecomTree Server...");
        log("========================================");

        // Load saved data
        log("Loading catalog data...");
        rootCatalog = TreePersistence.load();
        log("Catalog loaded successfully!");

        // Save when server shuts down
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log("Server shutting down...");
                log("Displaying final metrics...");
                displayMetrics();
                log("Saving catalog state...");
                TreePersistence.save(rootCatalog);
                log("State saved. Goodbye!");
            }
        });

        // Start metrics display thread (every 5 minutes)
        Thread metricsThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300000); // 5 minutes
                    displayMetrics();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        metricsThread.setDaemon(true);
        metricsThread.start();
        log("Metrics reporting enabled (every 5 minutes)");

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            log("Server listening on port " + PORT);
            log("Waiting for client connections...");

            // Keep accepting clients
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();

                metrics.incrementTotalConnections();
                metrics.incrementCurrentConnections();

                log(">>> NEW CLIENT CONNECTED: " + clientInfo);
                log("    [Total connections: " + metrics.totalConnections.get() +
                    " | Active: " + metrics.currentConnections.get() + "]");

                // Create new thread for each client
                ClientHandler handler = new ClientHandler(clientSocket, clientInfo);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server connection error: " + e.getMessage());
        }
    }

    // Class to handle each client connection
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private String currentRole;
        private CommandInvoker invoker;
        private String clientInfo;
        private LocalDateTime sessionStart;

        public ClientHandler(Socket socket, String clientInfo) {
            this.socket = socket;
            this.clientInfo = clientInfo;
            this.currentRole = "GUEST";
            this.sessionStart = LocalDateTime.now();
            CatalogService service = new CatalogService(rootCatalog);
            this.invoker = new CommandInvoker(service);
        }

        // Helper method to log with client info
        private void logClient(String message) {
            log("[" + clientInfo + "] " + message);
        }

        @Override
        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                logClient("Connection established, sending welcome message...");

                // Send welcome message
                String[] welcomeMessages = {
                    "Welcome to RecomTree!",
                    "",
                    "Please log in:",
                    "  - Admin: LOGIN admin admin123",
                    "  - User:  LOGIN user user123",
                    "",
                    "Type HELP to see all available commands",
                    "<END_OF_RESPONSE>"
                };

                for (String msg : welcomeMessages) {
                    out.println(msg);
                    if (!"<END_OF_RESPONSE>".equals(msg)) {
                        logClient("<<< SENT: " + msg);
                    }
                }
                logClient("<<< SENT: <END_OF_RESPONSE>");

                String request;
                // Read commands from client
                while ((request = in.readLine()) != null) {
                    logClient(">>> RECEIVED: " + request);

                    // Check if client wants to exit
                    if ("EXIT".equalsIgnoreCase(request.trim())) {
                        logClient("Client requested exit");
                        break;
                    }

                    // Split command into parts
                    String[] parts = request.trim().split("\\s+");
                    String cmd = parts[0].toUpperCase();

                    // Handle login command
                    if ("LOGIN".equals(cmd)) {
                        handleLogin(out, parts);
                        logClient("<<< SENT: <END_OF_RESPONSE>");
                        out.println("<END_OF_RESPONSE>");
                        continue;
                    }

                    // Execute command
                    logClient("Executing command as role: " + currentRole);
                    metrics.incrementCommand(cmd);
                    String response = invoker.invoke(request, currentRole);

                    // Send response to client
                    String[] responseLines = response.split("\n");
                    for (String line : responseLines) {
                        out.println(line);
                        logClient("<<< SENT: " + line);
                    }
                    logClient("<<< SENT: <END_OF_RESPONSE>");
                    out.println("<END_OF_RESPONSE>");
                }
            } catch (IOException e) {
                logClient("ERROR: " + e.getMessage());
            } finally {
                // Close everything
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();

                    Duration sessionDuration = Duration.between(sessionStart, LocalDateTime.now());
                    long minutes = sessionDuration.toMinutes();
                    long seconds = sessionDuration.toSecondsPart();

                    metrics.decrementCurrentConnections();
                    logClient("<<< CLIENT DISCONNECTED (Session duration: " + minutes + "m " + seconds + "s)");
                    logClient("    [Active connections: " + metrics.currentConnections.get() + "]");
                } catch (IOException e) {
                    logClient("ERROR closing socket: " + e.getMessage());
                }
            }
        }

        // Method to handle login
        private void handleLogin(PrintWriter out, String[] parts) {
            if (parts.length < 3) {
                String msg = "USAGE: LOGIN <username> <password>";
                out.println(msg);
                logClient("<<< SENT: " + msg);
                return;
            }

            String username = parts[1];
            String password = parts[2];

            logClient("Login attempt - Username: " + username);

            // Check credentials
            String response;
            if ("admin".equals(username) && "admin123".equals(password)) {
                currentRole = "ADMIN";
                response = "CONNECTION SUCCESSFUL: You are now ADMIN.";
                metrics.incrementAdminLogins();
                logClient("Login successful as ADMIN");
            } else if ("user".equals(username) && "user123".equals(password)) {
                currentRole = "USER";
                response = "CONNECTION SUCCESSFUL: You are now USER.";
                metrics.incrementUserLogins();
                logClient("Login successful as USER");
            } else {
                response = "ERROR: Invalid credentials.";
                metrics.incrementFailedLogins();
                logClient("Login failed - Invalid credentials");
            }

            out.println(response);
            logClient("<<< SENT: " + response);
        }
    }
}

