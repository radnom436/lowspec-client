package com.lowspec.client;

import com.lowspec.client.config.ConfigManager;
import com.lowspec.client.core.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class LowSpecClient implements ClientModInitializer {
    private static KeyMapping menuKey;
    private static KeyMapping zoomKey;
    private static KeyMapping freelookKey;

    private static final ModuleManager moduleManager = new ModuleManager();
    private static final ConfigManager configManager = new ConfigManager();

    private static boolean zoomActive;
    private static int zoomBase;
    private static float zoomCur;
    private static boolean sprintForced;

    private static final HudElement NO_HUD = (graphics, delta) -> { };

    private static final Style LOGO_STYLE = Style.EMPTY.withFont(
            new FontDescription.Resource(Identifier.fromNamespaceAndPath("lowspec", "logo")));

    @Override
    public void onInitializeClient() {
        migrateLegacyConfig();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("lowspec", "main"));

        menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lowspec.menu", KeyMapping.Type.KEYSYM, KeyMapping.DEFAULT_KEYBOARD_BIND_RSHIFT, category));
        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lowspec.zoom", KeyMapping.Type.KEYSYM, KeyMapping.DEFAULT_KEYBOARD_BIND_C, category));
        freelookKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lowspec.freelook", KeyMapping.Type.KEYSYM, KeyMapping.DEFAULT_KEYBOARD_BIND_LEFT_ALT, category));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        HudElementRegistry.replaceElement(VanillaHudElements.MOB_EFFECTS,
                original -> isEnabled("effects") ? NO_HUD : original);

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("lowspec", "hud"),
                this::renderHud);

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof TitleScreen) {
                ScreenEvents.afterExtract(screen).register(this::titleWatermark);
            } else if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
                ScreenEvents.afterExtract(screen).register(this::cornerWatermark);
            }
        });
    }

    private void onClientTick(Minecraft mc) {
        while (menuKey.consumeClick()) {
            mc.setScreenAndShow(new MenuScreen());
        }
        tickSprint(mc);
        tickZoom(mc);
    }

    private void tickSprint(Minecraft mc) {
        if (mc.player == null) return;
        boolean forward = mc.options.keyUp.isDown();
        boolean sprintEnabled = isEnabled("sprint") && !isRestricted("sprint");
        if (sprintEnabled && forward && !mc.player.isUsingItem()) {
            mc.options.keySprint.setDown(true);
            sprintForced = true;
        } else if (sprintForced) {
            mc.options.keySprint.setDown(false);
            sprintForced = false;
        }
    }

    private void tickZoom(Minecraft mc) {
        var fov = mc.options.fov();
        boolean want = isZoomKeyDown();
        if (want) {
            if (!zoomActive) {
                zoomActive = true;
                zoomBase = fov.get();
                zoomCur = zoomBase;
            }
            zoomCur += (30f - zoomCur) * 0.4f;
            fov.set(Math.round(zoomCur));
        } else if (zoomActive) {
            zoomCur += (zoomBase - zoomCur) * 0.4f;
            if (Math.abs(zoomBase - zoomCur) < 0.8f) {
                fov.set(zoomBase);
                zoomActive = false;
            } else {
                fov.set(Math.round(zoomCur));
            }
        }
    }

    private void renderHud(GuiGraphicsExtractor g, float delta) {
        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        int y = 6;
        int x = 6;

        if (isEnabled("fps")) {
            String fpsText = getFpsText();
            g.text(mc.font, fpsText, x, y, 0xFFFFFFFF, false);
            y += 16;
        }

        if (isEnabled("cps")) {
            String cpsText = getCpsText();
            g.text(mc.font, cpsText, x, y, 0xFFFFFFFF, false);
            y += 16;
        }

        if (isEnabled("coords") && mc.player != null) {
            String coordsText = String.format("XYZ  %d  %d  %d",
                    (int)Math.floor(mc.player.getX()),
                    (int)Math.floor(mc.player.getY()),
                    (int)Math.floor(mc.player.getZ()));
            g.text(mc.font, coordsText, x, y, 0xFFFFFFFF, false);
            y += 16;
        }

        if (isEnabled("memory")) {
            Runtime rt = Runtime.getRuntime();
            int used = (int)((rt.totalMemory() - rt.freeMemory()) >> 20);
            int max = (int)(rt.maxMemory() >> 20);
            String memText = String.format("MEM  %d / %d MB", used, max);
            g.text(mc.font, memText, x, y, 0xFFFFFFFF, false);
        }
    }

    private String getFpsText() {
        return "FPS  --";
    }

    private String getCpsText() {
        return "CPS  --";
    }

    public static boolean isEnabled(String moduleId) {
        return configManager.get().defaultProfile.getModule(moduleId).enabled;
    }

    public static void setEnabled(String moduleId, boolean enabled) {
        configManager.get().defaultProfile.getModule(moduleId).enabled = enabled;
        configManager.save();
    }

    public static boolean isRestricted(String moduleId) {
        return false;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static boolean nameTagOn() {
        return isEnabled("nametag");
    }

    public static Component withLogo(Component name) {
        MutableComponent root = Component.empty();
        root.append(Component.literal("").withStyle(LOGO_STYLE));
        root.append(Component.literal(" "));
        root.append(name);
        return root;
    }

    public static boolean isZoomKeyDown() {
        return zoomKey != null && zoomKey.isDown();
    }

    public static boolean isFreelookKeyDown() {
        return freelookKey != null && freelookKey.isDown();
    }

    private void migrateLegacyConfig() {
        Path legacyFile = FabricLoader.getInstance().getConfigDir().resolve("lowspec.properties");
        if (!Files.exists(legacyFile)) return;

        try {
            Properties p = new Properties();
            try (var in = Files.newInputStream(legacyFile)) {
                p.load(in);
            }

            String[] moduleIds = {"fps", "cps", "coords", "memory", "keys", "armor", "effects", "sprint", "zoom", "nametag", "font", "perf"};
            boolean[] defaults = {true, true, false, false, true, true, true, false, true, true, false, false};

            for (int i = 0; i < moduleIds.length; i++) {
                String id = moduleIds[i];
                boolean legacyState = Boolean.parseBoolean(p.getProperty("mod." + id.toUpperCase(), Boolean.toString(defaults[i])));
                if (legacyState != defaults[i]) {
                    configManager.get().defaultProfile.getModule(id).enabled = legacyState;
                }
            }

            configManager.save();

            Path archive = legacyFile.resolveSibling(legacyFile.getFileName() + ".bak");
            Files.move(legacyFile, archive);
        } catch (Exception e) {
        }
    }

    private void titleWatermark(Screen screen, GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int tw = font.width("LowSpec Client");
        int total = 24 + 6 + tw;
        int rx = screen.width / 2 - total / 2;
        int ry = 84;
        Logo.draw(g, rx, ry, 2, 1f);
        Ui.text(g, font, "LowSpec Client", rx + 30, ry + 16, 0xFFFFFFFF, true);
    }

    private void cornerWatermark(Screen screen, GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int tw = font.width("LowSpec Client");
        int w = 12 + 5 + tw;
        int x = screen.width - w - 8;
        int y = screen.height - 12 - 6;
        Logo.draw(g, x, y, 1, 1f);
        Ui.text(g, font, "LowSpec Client", x + 17, y + 2, 0xFFFFFFFF, true);
    }
}