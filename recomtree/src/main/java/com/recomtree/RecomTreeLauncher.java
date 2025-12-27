package com.recomtree;

import java.io.File;

public class RecomTreeLauncher {
    public static void main(String[] args) throws Exception {
        String jarPath = new File(RecomTreeLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();

        // Launch server in new CMD
        new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", "java", "-cp", jarPath, "com.recomtree.server.RecommendationServer").start();

        // Wait 2 seconds for server to start
        Thread.sleep(1000);

        // Launch client in new CMD
        new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", "java", "-cp", jarPath, "com.recomtree.client.RecomClient").start();
    }
}

