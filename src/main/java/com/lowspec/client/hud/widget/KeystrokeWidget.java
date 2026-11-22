package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.options.KeyBinding;

public class KeystrokeWidget extends Widget {
    public enum Layout {
        ARROWS("Arrows"),
        WASD("WASD"),
        HORIZONTAL("Horizontal");

        private final String label;

        Layout(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private Layout layout = Layout.WASD;
    private boolean showMouse = true;
    private boolean showSpace = true;
    private boolean showSprintSneak = true;

    public KeystrokeWidget() {
        super("keys");
        this.anchor = Anchor.BOTTOM_LEFT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return layout == Layout.HORIZONTAL ? 100 : 34;
    }

    @Override
    public int getRenderHeight(Font font) {
        if (layout == Layout.HORIZONTAL) return 34;
        if (layout == Layout.ARROWS) return 34;
        return 110; // WASD
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        switch (layout) {
            case ARROWS:
                renderArrows(g, font, mc);
                break;
            case WASD:
                renderWasd(g, font, mc);
                break;
            case HORIZONTAL:
                renderHorizontal(g, font, mc);
                break;
        }
    }

    private void renderArrows(GuiGraphicsExtractor g, Font font, Minecraft mc) {
        int W = mc.getWindow().getGuiScaledWidth();
        int H = mc.getWindow().getGuiScaledHeight();

        boolean[] keys = {
                mc.options.keyUp.isDown(),
                mc.options.keyLeft.isDown(),
                mc.options.keyDown.isDown(),
                mc.options.keyRight.isDown()
        };

        int y = H - 40;
        String[] labels = {"W", "A", "S", "D"};
        int[] x = {W / 2 - 35, W / 2 - 35, W / 2 - 35, W / 2 - 35};

        for (int i = 0; i < 4; i++) {
            int col = keys[i] ? 0xFFFFAA00 : 0xFFAAAAAA;
            g.drawString(font, labels[i], x[i], y + i * 10, col);
        }
    }

    private void renderWasd(GuiGraphicsExtractor g, Font font, Minecraft mc) {
        int W = mc.getWindow().getGuiScaledWidth();
        int H = mc.getWindow().getGuiScaledHeight();

        boolean up = mc.options.keyUp.isDown();
        boolean left = mc.options.keyLeft.isDown();
        boolean down = mc.options.keyDown.isDown();
        boolean right = mc.options.keyRight.isDown();

        int row2 = H - 40;
        int row3 = H - 20;
        int KEY = 24;

        // W
        g.drawString(font, "W", W / 2 - 17, row2, up ? 0xFFFFAA00 : 0xFFAAAAAA);
        // A S D
        g.drawString(font, "A", W / 2 - 51, row3, left ? 0xFFFFAA00 : 0xFFAAAAAA);
        g.drawString(font, "S", W / 2 - 17, row3, down ? 0xFFFFAA00 : 0xFFAAAAAA);
        g.drawString(font, "D", W / 2 + 17, row3, right ? 0xFFFFAA00 : 0xFFAAAAAA);
    }

    private void renderHorizontal(GuiGraphicsExtractor g, Font font, Minecraft mc) {
        int W = mc.getWindow().getGuiScaledWidth();
        int H = mc.getWindow().getGuiScaledHeight();

        int y = H - 24;
        int x = W / 2;

        String wKey = mc.options.keyUp.isDown() ? "W" : "w";
        String aKey = mc.options.keyLeft.isDown() ? "A" : "a";
        String sKey = mc.options.keyDown.isDown() ? "S" : "s";
        String dKey = mc.options.keyRight.isDown() ? "D" : "d";

        g.drawString(font, aKey, x - 51, y, 0xFFFFFFFF);
        g.drawString(font, wKey, x - 17, y, 0xFFFFFFFF);
        g.drawString(font, sKey, x - 17, y + 16, 0xFFFFFFFF);
        g.drawString(font, dKey, x + 17, y, 0xFFFFFFFF);

        if (showMouse) {
            boolean lmb = mc.options.keyAttack.isDown();
            boolean rmb = mc.options.keyUse.isDown();
            g.drawString(font, "LMB", x - 17, y + 32, lmb ? 0xFFFFAA00 : 0xFFAAAAAA);
            g.drawString(font, "RMB", x + 17, y + 32, rmb ? 0xFFFFAA00 : 0xFFAAAAAA);
        }

        if (showSpace) {
            boolean space = mc.options.keyJump.isDown();
            g.drawString(font, "SPACE", x - 25, y + 48, space ? 0xFFFFAA00 : 0xFFAAAAAA);
        }

        if (showSprintSneak) {
            boolean sprint = mc.options.keySprint.isDown();
            boolean sneak = mc.options.keySneak.isDown();
            g.drawString(font, "SHIFT", x - 25, y + 64, sneak ? 0xFFAAAAAA : 0xFF666666);
            g.drawString(font, "CTRL", x + 15, y + 64, sprint ? 0xFFFFAA00 : 0xFFAAAAAA);
        }
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setShowMouse(boolean showMouse) {
        this.showMouse = showMouse;
    }

    public boolean isShowMouse() {
        return showMouse;
    }

    public void setShowSpace(boolean showSpace) {
        this.showSpace = showSpace;
    }

    public boolean isShowSpace() {
        return showSpace;
    }

    public void setShowSprintSneak(boolean showSprintSneak) {
        this.showSprintSneak = showSprintSneak;
    }

    public boolean isShowSprintSneak() {
        return showSprintSneak;
    }
}