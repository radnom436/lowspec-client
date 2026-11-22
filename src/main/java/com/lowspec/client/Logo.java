package com.lowspec.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Logo {
    private static final int[][] RIM = {
            {2, 10}, {1, 11}, {0, 12}, {0, 12}, {0, 12}, {0, 12},
            {0, 12}, {0, 12}, {0, 12}, {0, 12}, {1, 11}, {2, 10}};

    public static void draw(GuiGraphicsExtractor g, int x, int y, int s, float m) {
        for (int r = 0; r < 12; r++) {
            g.fill(x + RIM[r][0] * s, y + r * s, x + RIM[r][1] * s, y + (r + 1) * s, Ui.a(0xE6DFFBFF, m));
        }
        for (int r = 1; r <= 10; r++) {
            int a = 1, b = 11;
            if (r == 1 || r == 10) { a = 3; b = 9; }
            else if (r == 2 || r == 9) { a = 2; b = 10; }
            int c = Ui.mix(0xFF7A5CFF, 0xFF3DE0C0, (r - 1) / 9f);
            g.fill(x + a * s, y + r * s, x + b * s, y + (r + 1) * s, Ui.a(c, m));
        }
        g.fill(x + 3 * s, y + 2 * s, x + 8 * s, y + 3 * s + s / 2, Ui.a(0x55FFFFFF, m));
        g.fill(x + 4 * s, y + 3 * s, x + 6 * s, y + 9 * s, Ui.a(0xFFFFFFFF, m));
        g.fill(x + 4 * s, y + 7 * s, x + 9 * s, y + 9 * s, Ui.a(0xFFFFFFFF, m));
        g.fill(x + 8 * s, y + 3 * s, x + 10 * s, y + 5 * s, Ui.a(0xFFFFF07A, m));
    }
}