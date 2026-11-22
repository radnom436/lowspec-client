package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TotemWidget extends Widget {
    public TotemWidget() {
        super("totem");
        this.anchor = Anchor.TOP_RIGHT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return 18;
    }

    @Override
    public int getRenderHeight(Font font) {
        return 18;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int count = countTotems();

        int W = mc.getWindow().getGuiScaledWidth();
        int x = W - 6 - 18;
        int y = 6;

        if (count > 0) {
            g.fill(x, y, x + 18, y + 18, 0x70101820);
            String text = String.valueOf(count);
            int tw = font.width(text);
            g.text(font, text, x + 9 - tw / 2, y + 1, 0xFFFFFFFF, true);
        } else {
            String text = "0";
            g.text(font, text, x, y, 0x555555, false);
        }
    }

    private int countTotems() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        int count = 0;

        NonNullList<ItemStack> inventory = mc.player.getInventory().getNonExposedStorage();
        for (ItemStack stack : inventory) {
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }

        for (EquipmentSlot slot : EquipmentSlot.ARMOR) {
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }

        ItemStack offhand = mc.player.getItemBySlot(EquipmentSlot.OFFHAND);
        if (offhand.is(Items.TOTEM_OF_UNDYING)) {
            count += offhand.getCount();
        }

        return count;
    }
}