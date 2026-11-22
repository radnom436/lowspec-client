package com.lowspec.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen extends Screen {
    private final float[] on = new float[12];
    private final float[] hover = new float[12];
    private final float[] actionHover = new float[2];
    private float doneHover;
    private boolean listening;
    private long openNs, lastNs;
    private int px, py, pw, ph, cw, rows, doneY;

    // Module identifiers
    private static final String[] MODULE_IDS = {
            "fps", "cps", "coords", "memory", "keys", "armor",
            "effects", "sprint", "zoom", "nametag", "font", "perf"
    };

    // Module labels
    private static final String[] MODULE_LABELS = {
            "FPS counter", "CPS counter", "Coordinates", "Memory usage", "Keystrokes", "Armor HUD",
            "Effect timer", "Toggle sprint", "Zoom", "Name tag + logo", "Smooth font", "Performance mode"
    };

    // Module defaults
    private static final boolean[] MODULE_DEFAULTS = {
            true, true, false, false, true, true,
            true, false, true, true, false, false
    };

    public MenuScreen() {
        super(Component.literal("LowSpec Client"));
    }

    private int cellX(int i) {
        return px + 10 + (i % 2) * (cw + 10);
    }

    private int cellY(int i) {
        return py + 40 + (i / 2) * 26;
    }

    private int actionY(int j) {
        return py + 40 + rows * 26 + j * 26;
    }

    @Override
    protected void init() {
        pw = 300;
        cw = (pw - 30) / 2;
        rows = (MODULE_IDS.length + 1) / 2;
        ph = 40 + rows * 26 + 2 * 26 + 34;
        px = (this.width - pw) / 2;
        py = Math.max(2, (this.height - ph) / 2);
        listening = false;

        // Invisible vanilla buttons: they only handle clicks. Drawing is custom below.
        for (int i = 0; i < MODULE_IDS.length; i++) {
            int index = i;
            Button b = Button.builder(Component.empty(), btn -> {
                LowSpecClient.setEnabled(MODULE_IDS[index], !LowSpecClient.isEnabled(MODULE_IDS[index]));
            }).bounds(cellX(i), cellY(i), cw, 22).build();
            this.addRenderableWidget(b);
        }

        // armor position: click to cycle
        Button armorBtn = Button.builder(Component.empty(), b -> {
            // TODO: cycle armor position
        }).bounds(px + 10, actionY(0), pw - 20, 22).build();
        this.addRenderableWidget(armorBtn);

        // zoom key: click, then press the new key
        Button zoomBtn = Button.builder(Component.empty(), b -> listening = !listening)
                .bounds(px + 10, actionY(1), pw - 20, 22).build();
        this.addRenderableWidget(zoomBtn);

        doneY = actionY(2) + 4;
        Button doneBtn = Button.builder(Component.empty(), b -> this.onClose())
                .bounds(px + 10, doneY, pw - 20, 22).build();
        this.addRenderableWidget(doneBtn);

        openNs = lastNs = System.nanoTime();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listening) {
            int code = event.key();
            listening = false;
            if (code != net.minecraft.client.KeyMapping.DEFAULT_KEYBOARD_BIND_ESCAPE.getKey().getValue()) {
                // TODO: set new zoom key
            }
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        long now = System.nanoTime();
        float dt = Math.min((now - lastNs) / 1e9f, 0.25f);
        lastNs = now;
        float k = 1f - (float) Math.exp(-dt * 16f);
        float p = Math.min(1f, (now - openNs) / 200_000_000f);
        float e = 1f - (1f - p) * (1f - p) * (1f - p);
        int oy = (int) ((1f - e) * 10);
        Font f = this.font;

        g.fill(0, 0, this.width, this.height, Ui.a(0x70000000, e));
        Ui.glass(g, px, py + oy, pw, ph, e);

        Logo.draw(g, px + 12, py + oy + 8, 2, e);
        Ui.text(g, f, "LowSpec Client", px + 12 + 30, py + oy + 16, Ui.a(0xFFFFFFFF, e), false);

        for (int i = 0; i < MODULE_IDS.length; i++) {
            int x = cellX(i), y = cellY(i) + oy;
            boolean hov = mx >= x && mx < x + cw && my >= cellY(i) && my < cellY(i) + 22;
            hover[i] += ((hov ? 1f : 0f) - hover[i]) * k;
            on[i] += ((LowSpecClient.isEnabled(MODULE_IDS[i]) ? 1f : 0f) - on[i]) * k;

            Ui.rr(g, x, y, cw, 22, Ui.a(Ui.mix(0x30FFFFFF, 0x55FFFFFF, hover[i]), e));
            Ui.text(g, f, MODULE_LABELS[i], x + 8, y + 7, Ui.a(0xFFFFFFFF, e), false);

            int sx = x + cw - 24 - 8;
            int sy = y + 5;
            Ui.rr(g, sx, sy, 24, 12, Ui.a(Ui.mix(0x66FFFFFF, 0xFF55FFAA, on[i]), e));
            Ui.rr(g, sx + 2 + (int) (on[i] * 12), sy + 2, 8, 8, Ui.a(0xFFFFFFFF, e));
        }

        // action rows: armor position + zoom key
        for (int j = 0; j < 2; j++) {
            int ay = actionY(j);
            boolean hov = mx >= px + 10 && mx < px + pw - 10 && my >= ay && my < ay + 22;
            actionHover[j] += ((hov ? 1f : 0f) - actionHover[j]) * k;
            Ui.rr(g, px + 10, ay + oy, pw - 20, 22, Ui.a(Ui.mix(0x30FFFFFF, 0x55FFFFFF, actionHover[j]), e));

            String label = j == 0 ? "Armor HUD position" : "Zoom key";
            String value;
            int vcol = 0xFF55FFAA;
            if (j == 0) {
                // TODO: show current armor position
                value = "Vertical (right)";
            } else if (listening) {
                value = "press a key...";
                vcol = 0xFFFFE066;
            } else {
                value = LowSpecClient.getConfigManager().get().defaultProfile.getModule("zoom").enabled ? "C" : "C";
            }
            Ui.text(g, f, label, px + 18, ay + oy + 7, Ui.a(0xFFFFFFFF, e), false);
            Ui.text(g, f, value, px + pw - 18 - Ui.tw(f, value), ay + oy + 7, Ui.a(vcol, e), false);
        }

        boolean dh = mx >= px + 10 && mx < px + pw - 10 && my >= doneY && my < doneY + 22;
        doneHover += ((dh ? 1f : 0f) - doneHover) * k;
        Ui.rr(g, px + 10, doneY + oy, pw - 20, 22, Ui.a(Ui.mix(0x44FFFFFF, 0x99FFFFFF, doneHover), e));
        int dw = Ui.tw(f, "Done");
        Ui.text(g, f, "Done", px + pw / 2 - dw / 2, doneY + oy + 7, Ui.a(0xFFFFFFFF, e), false);
    }
}