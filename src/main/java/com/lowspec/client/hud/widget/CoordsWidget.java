package com.lowspec.client.hud.widget;

import com.lowspec.client.hud.widget.Anchor;
import com.lowspec.client.hud.widget.Point;
import com.lowspec.client.hud.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class CoordsWidget extends Widget {
    public CoordsWidget() {
        super("coords");
        this.anchor = Anchor.TOP_LEFT;
        this.offset.set(6, 6);
    }

    @Override
    public int getRenderWidth(Font font) {
        return font.width("XYZ  0  0  0  BIOME  Nether: 0,0");
    }

    @Override
    public int getRenderHeight(Font font) {
        return 16;
    }

    @Override
    public void render(GuiGraphicsExtractor g, Font font, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        int x = getAbsoluteX(0);
        int y = getAbsoluteY(0, font);

        double px = Math.floor(player.getX());
        double py = Math.floor(player.getY());
        double pz = Math.floor(player.getZ());

        String facing = getFacingString(player);

        String text = String.format("XYZ  %d  %d  %d  %s", (int)px, (int)py, (int)pz, facing);

        g.text(font, text, x, y, 0xFFFFFFFF, false);
    }

    private String getFacingString(Player player) {
        if (player == null) return "N/A";
        net.minecraft.client.player.LocalPlayer local = (net.minecraft.client.player.LocalPlayer) player;
        float yaw = local.getYRot();
        if (yaw < 0) yaw += 360;
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round(yaw / 45) % 8;
        return dirs[index];
    }
}