package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayDeque;
import java.util.Deque;

public class ComboWidget extends Widget {
    private final Deque<Long> hits = new ArrayDeque<>();

    public ComboWidget() {
        super("combo");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("COMBO  0  Streak: 0");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        long now = System.nanoTime();
        cleanOldHits(now);

        int combo = hits.size();
        int streak = combo;

        int W = mc.getWindow().getGuiScaledWidth();
        int x = W - 6 - getRenderWidth(font);
        int y = 6;

        String text = String.format("COMBO  %d  Streak: %d", combo, streak);
        g.text(font, text, x, y, 0xFFFFFFFF, false);
    }

    public void recordHit() {
        hits.addLast(System.nanoTime());
    }

    public void reset() {
        hits.clear();
    }

    public int getCombo() {
        cleanOldHits(System.nanoTime());
        return hits.size();
    }

    private void cleanOldHits(long now) {
        long cutoff = now - 1_000_000_000L;
        while (!hits.isEmpty() && hits.peekFirst() < cutoff) {
            hits.pollFirst();
        }
    }
}