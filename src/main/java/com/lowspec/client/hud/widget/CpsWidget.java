package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsWidget extends Widget {
    private final Deque<Long> leftClicks = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();
    private long lastUpdateTime = 0;

    public CpsWidget() {
        super("cps");
        this.anchor = Anchor.TOP_LEFT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("CPS  L:0.0  R:0.0  Total:0.0");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        long now = System.nanoTime();
        if (now - lastUpdateTime > 500_000_000L) {
            cleanupClicks(now);
            lastUpdateTime = now;
        }

        int leftCps = leftClicks.size();
        int rightCps = rightClicks.size();
        int total = leftCps + rightCps;

        int x = getAbsoluteX(0);
        int y = getAbsoluteY(0, font);

        String text = String.format("CPS  L: %.1f  R: %.1f  Total: %.1f",
                leftCps / 100.0f, rightCps / 100.0f, total / 100.0f);
        g.text(font, text, x, y, 0xFFFFFFFF, false);
    }

    public void recordLeftClick() {
        leftClicks.addLast(System.nanoTime());
    }

    public void recordRightClick() {
        rightClicks.addLast(System.nanoTime());
    }

    private void cleanupClicks(long now) {
        long cutoff = now - 1_000_000_000L;
        while (!leftClicks.isEmpty() && leftClicks.peekFirst() < cutoff) {
            leftClicks.pollFirst();
        }
        while (!rightClicks.isEmpty() && rightClicks.peekFirst() < cutoff) {
            rightClicks.pollFirst();
        }
    }
}