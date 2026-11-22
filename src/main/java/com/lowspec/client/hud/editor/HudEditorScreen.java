package com.lowspec.client.hud.editor;

import com.lowspec.client.LowSpecClient;
import com.lowspec.client.Ui;
import com.lowspec.client.config.ConfigManager;
import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class HudEditorScreen extends Screen {
    private static final int GRID_SIZE = 10;
    private final List<Widget> widgets = new ArrayList<>();
    private Widget selectedWidget = null;
    private boolean dragging = false;
    private int dragStartX, dragStartY;
    private int widgetStartX, widgetStartY;
    private float openProgress = 0f;
    private long openNs;

    private enum EditorMode {
        DRAG("Drag"),
        RESIZE("Resize"),
        SETTINGS("Settings");

        private final String label;

        EditorMode(String label) {
            this.label = label;
        }
    }

    private EditorMode mode = EditorMode.DRAG;

    public HudEditorScreen() {
        super(Component.literal("HUD Editor"));
    }

    protected void init() {
        openNs = System.nanoTime();
    }

    public void render(GuiGraphicsExtractor g, int mx, int my, float delta) {
        Minecraft mc = Minecraft.getInstance();
        Font f = mc.font;
        int W = mc.getWindow().getGuiScaledWidth();
        int H = mc.getWindow().getGuiScaledHeight();

        long now = System.nanoTime();
        openProgress = Math.min(1f, (now - openNs) / 150_000_000f);

        g.fill(0, 0, W, H, Ui.a(0x70000000, openProgress));

        drawGrid(g, W, H, openProgress);

        for (Widget widget : widgets) {
            if (widget.isEnabled()) {
                drawWidget(g, f, widget, mx, my);
            }
        }

        drawToolbar(g, f, W, H, openProgress);
    }

    private void drawGrid(GuiGraphicsExtractor g, int W, int H, float progress) {
        int gridAlpha = (int)(0x33 * progress);
        int color = 0x22FFFFFF | (gridAlpha << 24);

        for (int x = 0; x < W; x += GRID_SIZE) {
            g.fill(x, 0, x + 1, H, color);
        }
        for (int y = 0; y < H; y += GRID_SIZE) {
            g.fill(0, y, W, y + 1, color);
        }
    }

    private void drawWidget(GuiGraphicsExtractor g, Font f, Widget widget, int mx, int my) {
        int x = widget.getAbsoluteX(mc.getWindow().getGuiScaledWidth());
        int y = widget.getAbsoluteY(mc.getWindow().getGuiScaledHeight(), f);
        int w = widget.getRenderWidth(f);
        int h = widget.getRenderHeight(f);

        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        boolean selected = widget == selectedWidget;

        if (selected || hover) {
            int boxColor = selected ? 0xFF55FFAA : 0xFFFFFFFF;
            boxColor = 0x80FFFFFF | ((boxColor & 0xFF) << 24);
            Ui.rr(g, x - 2, y - 2, w + 4, h + 4, boxColor);
        }

        if (selected) {
            int handleSize = 8;
            int[][] handles = {
                    {x - handleSize/2, y - handleSize/2},
                    {x + w - handleSize/2, y - handleSize/2},
                    {x - handleSize/2, y + h - handleSize/2},
                    {x + w - handleSize/2, y + h - handleSize/2}
            };
            for (int[] handle : handles) {
                g.fill(handle[0], handle[1], handle[0] + handleSize, handle[1] + handleSize, 0xFF55FFAA);
            }
        }

        String label = widget.getId();
        g.text(f, Component.literal(label), x + 2, y - 10, 0xFFFFFFFF, false);

        widget.render(g, f, mx - x, my - y, delta);
    }

    private void drawToolbar(GuiGraphicsExtractor g, Font f, int W, int H, float progress) {
        int barH = 40;
        int barY = H - barH - 10;
        int barW = 300;
        int barX = (W - barW) / 2;

        Ui.glass(g, barX, barY, barW, barH, progress);

        EditorMode[] modes = EditorMode.values();
        for (int i = 0; i < modes.length; i++) {
            EditorMode m = modes[i];
            int bx = barX + 10 + i * 90;
            int by = barY + 10;
            boolean active = m == mode;
            Ui.rr(g, bx, by, 80, 20, active ? 0x8055FFAA : 0x50FFFFFF);
            g.text(f, Component.literal(m.label), bx + 20, by + 5, 0xFFFFFFFF, false);
        }

        int closeX = barX + barW - 80;
        Ui.rr(g, closeX, barY + 10, 70, 20, 0x80FF6666);
        g.text(f, Component.literal("Close"), closeX + 15, barY + 15, 0xFFFFFFFF, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX;
        int my = (int)mouseY;

        Minecraft mc = Minecraft.getInstance();
        int barW = 300;
        int barX = (mc.getWindow().getGuiScaledWidth() - barW) / 2;
        int closeX = barX + barW - 80;
        int barY = mc.getWindow().getGuiScaledHeight() - 50;
        if (mx >= closeX && mx < closeX + 70 && my >= barY + 10 && my < barY + 30) {
            this.onClose();
            return true;
        }

        EditorMode[] modes = EditorMode.values();
        for (int i = 0; i < modes.length; i++) {
            int bx = barX + 10 + i * 90;
            int by = barY + 10;
            if (mx >= bx && mx < bx + 80 && my >= by && my < by + 20) {
                mode = modes[i];
                return true;
            }
        }

        for (Widget widget : widgets) {
            if (!widget.isEnabled()) continue;
            int x = widget.getAbsoluteX(mc.getWindow().getGuiScaledWidth());
            int y = widget.getAbsoluteY(mc.getWindow().getGuiScaledHeight(), mc.font);
            int w = widget.getRenderWidth(mc.font);
            int h = widget.getRenderHeight(mc.font);

            if (mx >= x && mx < x + w && my >= y && my < y + h) {
                selectedWidget = widget;
                if (button == 0) {
                    dragging = true;
                    dragStartX = mx;
                    dragStartY = my;
                    widgetStartX = widget.getOffset().x;
                    widgetStartY = widget.getOffset().y;
                }
                return true;
            }
        }

        selectedWidget = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && selectedWidget != null) {
            int mx = (int)mouseX;
            int my = (int)mouseY;

            int deltaX = mx - dragStartX;
            int deltaY = my - dragStartY;

            int snappedX = ((widgetStartX + deltaX) / GRID_SIZE) * GRID_SIZE;
            int snappedY = ((widgetStartY + deltaY) / GRID_SIZE) * GRID_SIZE;

            selectedWidget.setOffset(snappedX, snappedY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        if (selectedWidget != null) {
            ConfigManager config = LowSpecClient.getConfigManager();
            config.get().defaultProfile.getModule(selectedWidget.getId())
                    .x = selectedWidget.getOffset().x;
            config.get().defaultProfile.getModule(selectedWidget.getId())
                    .y = selectedWidget.getOffset().y;
            config.save();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private enum EditorMode {
        DRAG("Drag"),
        RESIZE("Resize"),
        SETTINGS("Settings");

        private final String label;

        EditorMode(String label) {
            this.label = label;
        }
    }
}