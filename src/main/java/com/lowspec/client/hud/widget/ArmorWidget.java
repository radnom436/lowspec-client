package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorWidget extends Widget {
    public enum Position {
        VERTICAL("Vertical (right)"),
        HORIZONTAL("Horizontal (hotbar)"),
        BOTTOM_LEFT("Bottom left"),
        BOTTOM_RIGHT("Bottom right");

        private final String label;

        Position(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum DisplayMode {
        PERCENTAGE("Percentage"),
        VALUE("Value");

        private final String label;

        DisplayMode(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private Position position = Position.VERTICAL;
    private DisplayMode mode = DisplayMode.PERCENTAGE;

    public ArmorWidget() {
        super("armor");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return 72;
    }

    @Override
    public int getRenderHeight(Font font) {
        return position == Position.VERTICAL ? 72 : 34;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        switch (position) {
            case VERTICAL:
                renderVertical(g, font, mc);
                break;
            case HORIZONTAL:
                renderHorizontal(g, font, mc);
                break;
            case BOTTOM_LEFT:
                renderBottomLeft(g, font, mc);
                break;
            case BOTTOM_RIGHT:
                renderBottomRight(g, font, mc);
                break;
        }
    }

    private void renderVertical(GuiGraphicsExtractor g, Font font, Minecraft mc) {
        int W = mc.getWindow().getGuiScaledWidth();
        int ax = W - 22;
        int ay = mc.getWindow().getGuiScaledHeight() / 2 - 38;

        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : slots) {
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                g.renderItem(stack, ax, ay);
                if (stack.isDamageableItem()) {
                    int pct = getDurabilityPercent(stack);
                    String text = pct + "%";
                    int col = pctColor(pct);
                    int tw = font.width(text);
                    g.text(font, text, ax - 3 - tw, ay + 4, col, true);
                }
            }
            ay += 18;
        }
    }

    private void renderHorizontal(GuiGraphicsExtractor g, Font font, Minecraft mc) {
        final EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        int W = mc.getWindow().getGuiScaledWidth();
        int x0 = W / 2 - 56;
        int y0 = mc.getWindow().getGuiScaledHeight() - 88;

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getItemBySlot(slots[i]);
            int x = x0 + i * 28 + 6;
            if (!stack.isEmpty()) {
                g.renderItem(stack, x, y0);
                if (stack.isDamageableItem()) {
                    int pct = getDurabilityPercent(stack);
                    String text = pct + "%";
                    int tw = font.width(text);
                    g.text(font, text, x + 8 - tw / 2, y0 + 17, pctColor(pct), true);
                }
            }
        }
    }

    private void renderBottomLeft(GuiGraphicsExtractor g, Font font, Minecraft mc) {
        final EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        int H = mc.getWindow().getGuiScaledHeight();
        int x0 = 6;
        int y0 = H - 33;
        int slotW = 28;

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getItemBySlot(slots[i]);
            int x = x0 + i * slotW + (slotW - 16) / 2;
            if (!stack.isEmpty()) {
                g.renderItem(stack, x, y0);
                if (stack.isDamageableItem()) {
                    int pct = getDurabilityPercent(stack);
                    String text = pct + "%";
                    int tw = font.width(text);
                    g.text(font, text, x + 8 - tw / 2, y0 + 17, pctColor(pct), true);
                }
            }
        }
    }

    private void renderBottomRight(GuiGraphicsExtractor g, Font font, Minecraft mc) {
        final EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        int H = mc.getWindow().getGuiScaledHeight();
        int x0 = mc.getWindow().getGuiScaledWidth() - 28 * 4 - 6;
        int y0 = H - 33;
        int slotW = 28;

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getItemBySlot(slots[i]);
            int x = x0 + i * slotW + (slotW - 16) / 2;
            if (!stack.isEmpty()) {
                g.renderItem(stack, x, y0);
                if (stack.isDamageableItem()) {
                    int pct = getDurabilityPercent(stack);
                    String text = pct + "%";
                    int tw = font.width(text);
                    g.text(font, text, x + 8 - tw / 2, y0 + 17, pctColor(pct), true);
                }
            }
        }
    }

    private int getDurabilityPercent(ItemStack stack) {
        int max = stack.getMaxDamage();
        return max <= 0 ? 100 : (max - stack.getDamageValue()) * 100 / max;
    }

    private int pctColor(int pct) {
        return pct > 60 ? 0xFF55FF55 : pct > 35 ? 0xFFFFFF55 : 0xFFFF5555;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void cyclePosition() {
        switch (position) {
            case VERTICAL: position = Position.HORIZONTAL; break;
            case HORIZONTAL: position = Position.BOTTOM_LEFT; break;
            case BOTTOM_LEFT: position = Position.BOTTOM_RIGHT; break;
            case BOTTOM_RIGHT: position = Position.VERTICAL; break;
        }
    }
}