package com.lowspec.client.perf;

import com.lowspec.client.LowSpecClient;
import com.lowspec.client.Ui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PerformanceCenterScreen extends Screen {
    public enum Preset {
        LOW("Low", "Optimized for weak hardware"),
        BALANCED("Balanced", "Balanced performance and quality"),
        QUALITY("Quality", "Maximum quality");

        private final String label;
        private final String description;

        Preset(String label, String description) {
            this.label = label;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }
    }

    public PerformanceCenterScreen() {
        super(Component.literal("Performance Center"));
    }

    @Override
    protected void init() {
        int pw = 300;
        int ph = 200;
        int px = (this.width - pw) / 2;
        int py = (this.height - ph) / 2;

        Button lowBtn = Button.builder(Component.literal("Low Preset"), b -> applyPreset(Preset.LOW))
                .bounds(px + 20, py + 40, 120, 20).build();
        this.addRenderableWidget(lowBtn);

        Button balBtn = Button.builder(Component.literal("Balanced"), b -> applyPreset(Preset.BALANCED))
                .bounds(px + 160, py + 40, 120, 20).build();
        this.addRenderableWidget(balBtn);

        Button qualBtn = Button.builder(Component.literal("Quality"), b -> applyPreset(Preset.QUALITY))
                .bounds(px + 20, py + 70, 260, 20).build();
        this.addRenderableWidget(qualBtn);

        Button restoreBtn = Button.builder(Component.literal("Restore Previous"), b -> restorePrevious())
                .bounds(px + 20, py + 100, 260, 20).build();
        this.addRenderableWidget(restoreBtn);

        Button closeBtn = Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(px + 20, py + 130, 260, 20).build();
        this.addRenderableWidget(closeBtn);
    }

    private void applyPreset(Preset preset) {
        Minecraft mc = Minecraft.getInstance();
        switch (preset) {
            case LOW:
                mc.options.renderDistance().set(6);
                mc.options.simulationDistance().set(5);
                mc.options.biomeBlendRadius().set(0);
                mc.options.entityShadows().set(false);
                mc.options.entityDistanceScaling().set(0.5f);
                break;
            case BALANCED:
                mc.options.renderDistance().set(10);
                mc.options.simulationDistance().set(8);
                mc.options.biomeBlendRadius().set(2);
                mc.options.entityShadows().set(true);
                mc.options.entityDistanceScaling().set(1.0f);
                break;
            case QUALITY:
                mc.options.renderDistance().set(12);
                mc.options.simulationDistance().set(12);
                mc.options.biomeBlendRadius().set(2);
                mc.options.entityShadows().set(true);
                mc.options.entityDistanceScaling().set(1.0f);
                break;
        }
        mc.options.save();
    }

    private void restorePrevious() {
        Minecraft mc = Minecraft.getInstance();
        mc.options.renderDistance().set(12);
        mc.options.simulationDistance().set(12);
        mc.options.biomeBlendRadius().set(2);
        mc.options.entityShadows().set(true);
        mc.options.entityDistanceScaling().set(1.0f);
        mc.options.save();
    }

    @Override
    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        Font f = mc.font;

        g.fill(0, 0, this.width, this.height, 0x80000000);

        int pw = 300;
        int ph = 200;
        int px = (this.width - pw) / 2;
        int py = (this.height - ph) / 2;
        Ui.panel(g, px, py, pw, ph);

        Ui.text(g, f, "Performance Center", px + 20, py + 10, 0xFFFFFFFF, false);

        String hwInfo = String.format("Render Distance: %d", mc.options.renderDistance().get());
        Ui.text(g, f, hwInfo, px + 20, py + 35, 0xFFAAAAAA, false);

        super.render(g, mouseX, mouseY, delta);
    }
}