package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

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

        List<MobEffectInstance> effects = mc.player.getActiveEffects();
        if (effects.isEmpty()) return;

        int W = mc.getWindow().getGuiScaledWidth();
        int x = W - 6 - getRenderWidth(font);
        int y = 6;

        for (MobEffectInstance instance : effects) {
            MobEffect effect = instance.getEffect();
            String name = effect.getDescriptionId().getString();
            if (instance.getAmplifier() > 0) {
                name += " " + (instance.getAmplifier() + 1);
            }

            boolean infinite = instance.isInfiniteDuration();
            String timeStr = infinite ? "inf" : formatTime(instance.getDuration());
            String line = name + "  " + timeStr;

            // Color based on remaining time
            int col;
            if (infinite || instance.getDuration() < 200) {
                col = 0xFFFF6B6B; // Red for low time
            } else {
                col = 0xFFFFFFFF; // White
            }

            int w = font.width(line) + 12;
            int ex = W - w - x;

            g.fill(ex, y, ex + w, y + 15, 0x70101820);
            g.fill(ex + 2, y + 2, ex + w - 2, y + 13, 0x55FFFFFF);
            g.drawString(font, line, ex + 6, y + 4, col, false);

            y += 18;
        }
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
            String name = instance.getEffect().getDescriptionId().getString();
            if (instance.getAmplifier() > 0) {
                name += " " + (instance.getAmplifier() + 1);
            }
            names.add(name);
        }
        return names;
    }
}