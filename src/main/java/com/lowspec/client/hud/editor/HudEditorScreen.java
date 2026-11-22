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

    @Override
    protected void init() {
        openNs = System.nanoTime();
        // Populate with active widgets
        ConfigManager config = LowSpecClient.getConfigManager();
        // In practice, this would load from HudManager
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        Minecraft mc = Minecraft.getInstance();
        Font f = mc.font;
        int W = mc.getWindow().getGuiScaledWidth();
        int H = mc.getWindow().getGuiScaledHeight();

        // Animation
        long now = System.nanoTime();
        openProgress = Math.min(1f, (now - openNs) / 150_000_000f);

        // Darken background
        g.fill(0, 0, W, H, Ui.a(0x70000000, openProgress));

        // Draw grid
        drawGrid(g, W, H, openProgress);

        // Draw widgets
        for (Widget widget : widgets) {
            if (widget.isEnabled()) {
                drawWidget(g, f, widget, mx, my);
            }
        }

        // Draw toolbar
        drawToolbar(g, f, W, H, openProgress);
    }

    private void drawGrid(GuiGraphicsExtractor g, int W, int H, float progress) {
        int gridAlpha = (int)(0x33 * progress);
        int color = 0x22FFFFFF | (gridAlpha << 24);

        // Vertical lines
        for (int x = 0; x < W; x += GRID_SIZE) {
            g.fill(x, 0, x + 1, H, color);
        }
        // Horizontal lines
        for (int y = 0; y < H; y += GRID_SIZE) {
            g.fill(0, y, W, y + 1, color);
        }
    }

    private void drawWidget(GuiGraphicsExtractor g, Font f, Widget widget, int mx, int my) {
        int x = widget.getAbsoluteX(mc.getWindow().getGuiScaledWidth());
        int y = widget.getAbsoluteY(mc.getWindow().getGuiScaledHeight(), f);
        int w = widget.getRenderWidth(f);
        int h = widget.getRenderHeight(f);

        // Check hover
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        boolean selected = widget == selectedWidget;

        // Selection box
        if (selected || hover) {
            int boxColor = selected ? 0xFF55FFAA : 0xFFFFFFFF;
            boxColor = 0x80FFFFFF | ((boxColor & 0xFF) << 24);
            Ui.rr(g, x - 2, y - 2, w + 4, h + 4, boxColor);
        }

        // Resize handles (corners)
        if (selected) {
            int handleSize = 8;
            int[][] handles = {
                    {x - handleSize/2, y - handleSize/2}, // TL
                    {x + w - handleSize/2, y - handleSize/2}, // TR
                    {x - handleSize/2, y + h - handleSize/2}, // BL
                    {x + w - handleSize/2, y + h - handleSize/2} // BR
            };
            for (int[] handle : handles) {
                g.fill(handle[0], handle[1], handle[0] + handleSize, handle[1] + handleSize, 0xFF55FFAA);
            }
        }

        // Widget label
        String label = widget.getId();
        g.drawString(f, label, x + 2, y - 10, 0xFFFFFFFF);

        // Let widget render itself
        widget.render(g, f, mx - x, my - y, delta);
    }

    private void drawToolbar(GuiGraphicsExtractor g, Font f, int W, int H, float progress) {
        int barH = 40;
        int barY = H - barH - 10;
        int barW = 300;
        int barX = (W - barW) / 2;

        Ui.glass(g, barX, barY, barW, barH, progress);

        // Mode buttons
        EditorMode[] modes = EditorMode.values();
        for (int i = 0; i < modes.length; i++) {
            EditorMode m = modes[i];
            int bx = barX + 10 + i * 90;
            int by = barY + 10;
            boolean active = m == mode;
            Ui.rr(g, bx, by, 80, 20, active ? 0x8055FFAA : 0x50FFFFFF);
            g.drawString(f, m.label, bx + 20, by + 5, 0xFFFFFFFF);
        }

        // Close button
        int closeX = barX + barW - 80;
        Ui.rr(g, closeX, barY + 10, 70, 20, 0x80FF6666);
        g.drawString(f, "Close", closeX + 15, barY + 15, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX;
        int my = (int)mouseY;

        // Check close button
        int barW = 300;
        int barX = (this.width - barW) / 2;
        int closeX = barX + barW - 80;
        int barY = this.height - 50;
        if (mx >= closeX && mx < closeX + 70 && my >= barY + 10 && my < barY + 30) {
            this.onClose();
            return true;
        }

        // Check mode buttons
        EditorMode[] modes = EditorMode.values();
        for (int i = 0; i < modes.length; i++) {
            int bx = barX + 10 + i * 90;
            int by = barY + 10;
            if (mx >= bx && mx < bx + 80 && my >= by && my < by + 20) {
                mode = modes[i];
                return true;
            }
        }

        // Check widget selection
        for (Widget widget : widgets) {
            if (!widget.isEnabled()) continue;
            int x = widget.getAbsoluteX(mc.getWindow().getGuiScaledWidth());
            int y = widget.getAbsoluteY(mc.getWindow().getGuiScaledHeight(), mc.font);
            int w = widget.getRenderWidth(mc.font);
            int h = widget.getRenderHeight(mc.font);

            if (mx >= x && mx < x + w && my >= y && my < y + h) {
                selectedWidget = widget;
                if (button == 0) { // Left click
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

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && selectedWidget != null) {
            int mx = (int)mouseX;
            int my = (int)mouseY;

            int deltaX = mx - dragStartX;
            int deltaY = my - dragStartY;

            // Snap to grid
            int snappedX = ((widgetStartX + deltaX) / GRID_SIZE) * GRID_SIZE;
            int snappedY = ((widgetStartY + deltaY) / GRID_SIZE) * GRID_SIZE;

            selectedWidget.setOffset(snappedX, snappedY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        if (selectedWidget != null) {
            // Save position to config
            ConfigManager config = LowSpecClient.getConfigManager();
            config.get().defaultProfile.getModule(selectedWidget.getId())
                    .x = selectedWidget.getOffset().x;
            config.get().defaultProfile.getModule(selectedWidget.getId())
                    .y = selectedWidget.getOffset().y;
            config.save();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}