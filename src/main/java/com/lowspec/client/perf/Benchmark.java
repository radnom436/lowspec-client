package com.lowspec.client.perf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Benchmark {
    private static boolean running = false;
    private static long startTime;
    private static long frameCount = 0;
    private static final List<Long> frameTimes = new ArrayList<>();
    private static int durationSeconds = 30;

    public static void start(int durationSeconds) {
        if (running) return;
        running = true;
        Benchmark.durationSeconds = durationSeconds;
        startTime = System.nanoTime();
        frameCount = 0;
        frameTimes.clear();
    }

    public static void onFrame(float deltaTime) {
        if (!running) return;

        frameCount++;
        frameTimes.add((long)(deltaTime * 1_000_000_000L));

        if (System.nanoTime() - startTime >= durationSeconds * 1_000_000_000L) {
            finish();
        }
    }

    public static void finish() {
        if (!running) return;
        running = false;

        long elapsed = System.nanoTime() - startTime;
        double avgFps = (frameCount * 1_000_000_000.0) / elapsed;

        // Sort for percentile
        List<Long> sorted = new ArrayList<>(frameTimes);
        sorted.sort(Long::compareTo);

        long p1 = sorted.get((int)(sorted.size() * 0.01));
        long p99 = sorted.get((int)(sorted.size() * 0.99));
        long median = sorted.get(sorted.size() / 2);

        // Calculate min/max
        long min = sorted.get(0);
        long max = sorted.get(sorted.size() - 1);

        // Print results
        System.out.println("=== LowSpec Client Benchmark Results ===");
        System.out.printf("Duration: %.2f seconds%n", elapsed / 1_000_000_000.0);
        System.out.printf("Frames: %d%n", frameCount);
        System.out.printf("Average FPS: %.1f%n", avgFps);
        System.out.printf("1%% Low FPS: %.1f%n", 1_000_000_000.0 / p1);
        System.out.printf("Median Frame Time: %.2f ms%n", median / 1_000_000.0);
        System.out.printf("Min Frame Time: %.2f ms%n", min / 1_000_000.0);
        System.out.printf("Max Frame Time: %.2f ms%n", max / 1_000_000.0);
        System.out.println("========================================");
    }

    public static boolean isRunning() {
        return running;
    }

    public static String getResults() {
        if (frameTimes.isEmpty()) return "No data";

        long elapsed = System.nanoTime() - startTime;
        double avgFps = (frameCount * 1_000_000_000.0) / elapsed;

        List<Long> sorted = new ArrayList<>(frameTimes);
        sorted.sort(Long::compareTo);

        long p1 = sorted.get((int)(sorted.size() * 0.01));
        long median = sorted.get(sorted.size() / 2);

        return String.format("Avg: %.1f FPS | 1%% Low: %.1f FPS | Median: %.2f ms",
                avgFps, 1_000_000_000.0 / p1, median / 1_000_000.0);
    }
}