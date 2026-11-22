package com.lowspec.client.hud;

import com.lowspec.client.LowSpecClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.DeltaTracker;

import java.util.ArrayList;
import java.util.List;

public class HudManager {
    private final List<Widget> widgets = new ArrayList<>();

    private static HudManager INSTANCE;

    private HudManager() {
    }

    public static HudManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudManager();
        }
        return INSTANCE;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    public void removeWidget(Widget widget) {
        widgets.remove(widget);
    }

    public List<Widget> getWidgets() {
        return new ArrayList<>(widgets);
    }

    public void render(GuiGraphicsExtractor g, DeltaTracker delta, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int y = 6;
        int x = 6;

        if (LowSpecClient.isEnabled("fps")) {
            String fpsText = getFpsText();
            g.text(mc.font, fpsText, x, y, 0xFFFFFFFF, false);
            y += 16;
        }

        if (LowSpecClient.isEnabled("cps")) {
            String cpsText = getCpsText();
            g.text(mc.font, cpsText, x, y, 0xFFFFFFFF, false);
            y += 16;
        }

        if (LowSpecClient.isEnabled("coords") && mc.player != null) {
            String coordsText = String.format("XYZ  %d  %d  %d",
                    (int)Math.floor(mc.player.getX()),
                    (int)Math.floor(mc.player.getY()),
                    (int)Math.floor(mc.player.getZ()));
            g.text(mc.font, coordsText, x, y, 0xFFFFFFFF, false);
            y += 16;
        }

        if (LowSpecClient.isEnabled("memory")) {
            Runtime rt = Runtime.getRuntime();
            int used = (int)((rt.totalMemory() - rt.freeMemory()) >> 20);
            int max = (int)(rt.maxMemory() >> 20);
            String memText = String.format("MEM  %d / %d MB", used, max);
            g.text(mc.font, memText, x, y, 0xFFFFFFFF, false);
        }
    }

    private String getFpsText() {
        return "FPS  --";
    }

    private String getCpsText() {
        return "CPS  --";
    }

    public void clear() {
        widgets.clear();
    }
}