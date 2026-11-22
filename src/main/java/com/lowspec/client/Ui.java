package com.lowspec.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import java.util.HashMap;
import java.util.Map;

public class Ui {
    public static final Style STYLE = Style.EMPTY.withFont(
            new net.minecraft.network.chat.FontDescription.Resource(
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("lowspec", "glass")));

    private static final Map<String, Component> PLAIN = new HashMap<>();
    private static final Map<String, Component> STYLED = new HashMap<>();

    public static Component t(String s) {
        boolean styled = LowSpecClient.getConfigManager().get().defaultProfile.getModule("font").enabled;
        Map<String, Component> map = styled ? STYLED : PLAIN;
        if (map.size() > 512) map.clear();
        return map.computeIfAbsent(s,
                k -> styled ? Component.literal(k).withStyle(STYLE) : Component.literal(k));
    }

    public static int tw(Font f, String s) {
        return f.width(t(s));
    }

    public static void text(GuiGraphicsExtractor g, Font f, String s, int x, int y, int color, boolean shadow) {
        g.text(f, Component.literal(s), x, y, color, shadow);
    }

    public static int a(int c, float m) {
        int al = Math.round(((c >>> 24) & 0xFF) * Math.max(0f, Math.min(1f, m)));
        return (al << 24) | (c & 0xFFFFFF);
    }

    public static int mix(int c1, int c2, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = 0;
        for (int sh = 24; sh >= 0; sh -= 8) {
            int p = (c1 >>> sh) & 0xFF, q = (c2 >>> sh) & 0xFF;
            r |= (p + Math.round((q - p) * t)) << sh;
        }
        return r;
    }

    public static void rr(GuiGraphicsExtractor g, int x, int y, int w, int h, int c) {
        g.fill(x + 1, y, x + w - 1, y + 1, c);
        g.fill(x, y + 1, x + w, y + h - 1, c);
        g.fill(x + 1, y + h - 1, x + w - 1, y + h, c);
    }

    public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        rr(g, x, y, w, h, 0x70101820);
        g.fill(x + 2, y, x + w - 2, y + 1, 0x55FFFFFF);
    }

    public static void glass(GuiGraphicsExtractor g, int x, int y, int w, int h, float m) {
        rr(g, x + 1, y + 2, w, h, a(0x30000000, m));
        rr(g, x, y, w, h, a(0x700D1B2A, m));
        g.fill(x + 3, y, x + w - 3, y + 1, a(0x99FFFFFF, m));
        g.fill(x, y + 3, x + 1, y + h - 3, a(0x55FFFFFF, m));
        g.fill(x + w - 1, y + 3, x + w, y + h - 3, a(0x55FFFFFF, m));
        g.fill(x + 2, y + 1, x + w - 2, y + 1 + Math.max(2, h / 3), a(0x14FFFFFF, m));
    }
}