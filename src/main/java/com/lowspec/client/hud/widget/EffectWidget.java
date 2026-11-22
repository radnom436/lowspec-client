package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class EffectWidget extends Widget {
    public EffectWidget() {
        super("effects");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("Speed I  0:45");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 18;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        for (MobEffectInstance instance : mc.player.getActiveEffects()) {
            String name = instance.getEffect().value().getDisplayName().getString();
            if (instance.getAmplifier() > 0) {
                name += " " + (instance.getAmplifier() + 1);
            }

            boolean infinite = instance.isInfiniteDuration();
            String timeStr = infinite ? "inf" : formatTime(instance.getDuration());
            String line = name + "  " + timeStr;

            int col;
            if (infinite || instance.getDuration() < 200) {
                col = 0xFFFF6B6B;
            } else {
                col = 0xFFFFFFFF;
            }

            int w = font.width(line) + 12;
            int W = mc.getWindow().getGuiScaledWidth();
            int ex = W - w - 6;
            int y = 6 + (getActiveEffectsBefore(instance) * 18);

            g.fill(ex, y, ex + w, y + 15, 0x70101820);
            g.fill(ex + 2, y + 2, ex + w - 2, y + 13, 0x55FFFFFF);
            g.text(font, line, ex + 6, y + 4, col, false);
        }
    }

    private int getActiveEffectsBefore(MobEffectInstance target) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        int count = 0;
        for (MobEffectInstance instance : mc.player.getActiveEffects()) {
            if (instance == target) break;
            count++;
        }
        return count;
    }

    private String formatTime(int ticks) {
        int s = Math.max(0, ticks) / 20;
        int sec = s % 60;
        return (s / 60) + ":" + (sec < 10 ? "0" : "") + sec;
    }

    public List<String> getEffectNames() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return new ArrayList<>();

        List<String> names = new ArrayList<>();
        for (MobEffectInstance instance : mc.player.getActiveEffects()) {
            String name = instance.getEffect().value().getDisplayName().getString();
            if (instance.getAmplifier() > 0) {
                name += " " + (instance.getAmplifier() + 1);
            }
            names.add(name);
        }
        return names;
    }
}