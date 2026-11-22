package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.stream.Stream;

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

        var inventory = mc.player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }

        for (var slot : net.minecraft.world.entity.EquipmentSlot.ARMOR) {
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }

        ItemStack offhand = mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        if (offhand.is(Items.TOTEM_OF_UNDYING)) {
            count += offhand.getCount();
        }

        return count;
    }
}