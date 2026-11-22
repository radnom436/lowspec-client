package com.lowspec.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Properties;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class LowSpecClient implements ClientModInitializer {

    private static KeyMapping menuKey;
    private static KeyMapping zoomKey;

    // zoom + sprint state
    private static boolean zoomActive;
    private static int zoomBase;
    private static float zoomCur;
    private static boolean sprintForced;

    private static final HudElement NO_HUD = (graphics, delta) -> { };

    private static final Style LOGO_STYLE = Style.EMPTY.withFont(
            new FontDescription.Resource(Identifier.fromNamespaceAndPath("lowspec", "logo")));

    // ---------- used by the mixins ----------
    public static boolean nameTagOn() {
        return Settings.has(Mod.NAMETAG);
    }

    public static Component withLogo(Component name) {
        MutableComponent root = Component.empty();
        root.append(Component.literal("\uE000").withStyle(LOGO_STYLE));
        root.append(Component.literal(" "));
        root.append(name);
        return root;
    }

    @Override
    public void onInitializeClient() {
        Settings.load();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("lowspec", "main"));
        menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lowspec.menu", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, category));
        zoomKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lowspec.zoom", InputConstants.Type.KEYSYM, InputConstants.KEY_C, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.consumeClick()) {
                client.setScreenAndShow(new MenuScreen());
            }
            tickSprint(client);
            tickZoom(client);
        });

        // Hide the vanilla effect icons while our Effect timer module is on.
        HudElementRegistry.replaceElement(VanillaHudElements.MOB_EFFECTS,
                original -> Settings.has(Mod.EFFECTS) ? NO_HUD : original);

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("lowspec", "hud"),
                Hud::extract);

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof TitleScreen) {
                ScreenEvents.afterExtract(screen).register(Marks::title);
            } else if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
                ScreenEvents.afterExtract(screen).register(Marks::corner);
            }
        });
    }

    // ---------- Toggle sprint ----------
    private static void tickSprint(Minecraft mc) {
        if (mc.player == null) return;
        boolean forward = mc.options.keyUp.isDown();
        if (Settings.has(Mod.SPRINT) && forward && !mc.player.isUsingItem()) {
            mc.options.keySprint.setDown(true);
            sprintForced = true;
        } else if (sprintForced) {
            mc.options.keySprint.setDown(false);
            sprintForced = false;
        }
    }

    // ---------- Smooth zoom ----------
    private static void tickZoom(Minecraft mc) {
        var fov = mc.options.fov();
        boolean want = Settings.has(Mod.ZOOM) && zoomKey.isDown();
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

    // ---------- Modules ----------
    enum Mod {
        FPS("FPS counter", true),
        CPS("CPS counter", true),
        COORDS("Coordinates", false),
        MEMORY("Memory usage", false),
        KEYS("Keystrokes", true),
        ARMOR("Armor HUD", true),
        EFFECTS("Effect timer", true),
        SPRINT("Toggle sprint", false),
        ZOOM("Zoom", true),
        NAMETAG("Name tag + logo", true),
        FONT("Smooth font", false),
        PERF("Performance mode", false);

        final String label;
        final boolean def;

        Mod(String label, boolean def) {
            this.label = label;
            this.def = def;
        }
    }

    enum ArmorPos {
        VERTICAL("Vertical (right)"),
        HORIZONTAL("Horizontal (hotbar)"),
        BOTTOM_LEFT("Bottom left"),
        BOTTOM_RIGHT("Bottom right");

        final String label;

        ArmorPos(String label) {
            this.label = label;
        }
    }

    // ---------- Settings (saved to config/lowspec.properties) ----------
    static final class Settings {
        private static final boolean[] ON = new boolean[Mod.values().length];

        static {
            for (Mod m : Mod.values()) ON[m.ordinal()] = m.def;
        }

        static int armorPos = 0;

        static int prevRender = 12, prevSim = 12, prevBlend = 2;
        static boolean prevShadows = true;
        static double prevEntity = 1.0;

        private static final Path FILE =
                FabricLoader.getInstance().getConfigDir().resolve("lowspec.properties");

        static boolean has(Mod m) {
            return ON[m.ordinal()];
        }

        static void put(Mod m, boolean v) {
            ON[m.ordinal()] = v;
        }

        static void load() {
            try {
                if (!Files.exists(FILE)) return;
                Properties p = new Properties();
                try (var in = Files.newInputStream(FILE)) {
                    p.load(in);
                }
                for (Mod m : Mod.values()) {
                    ON[m.ordinal()] = Boolean.parseBoolean(
                            p.getProperty("mod." + m.name(), Boolean.toString(m.def)));
                }
                armorPos = Math.floorMod(Integer.parseInt(p.getProperty("armorPos", "0")), ArmorPos.values().length);
                prevRender = Integer.parseInt(p.getProperty("prevRender", "12"));
                prevSim = Integer.parseInt(p.getProperty("prevSim", "12"));
                prevBlend = Integer.parseInt(p.getProperty("prevBlend", "2"));
                prevShadows = Boolean.parseBoolean(p.getProperty("prevShadows", "true"));
                prevEntity = Double.parseDouble(p.getProperty("prevEntity", "1.0"));
            } catch (IOException | NumberFormatException ignored) {
            }
        }

        static void save() {
            try {
                Properties p = new Properties();
                for (Mod m : Mod.values()) {
                    p.setProperty("mod." + m.name(), Boolean.toString(ON[m.ordinal()]));
                }
                p.setProperty("armorPos", Integer.toString(armorPos));
                p.setProperty("prevRender", Integer.toString(prevRender));
                p.setProperty("prevSim", Integer.toString(prevSim));
                p.setProperty("prevBlend", Integer.toString(prevBlend));
                p.setProperty("prevShadows", Boolean.toString(prevShadows));
                p.setProperty("prevEntity", Double.toString(prevEntity));
                try (var out = Files.newOutputStream(FILE)) {
                    p.store(out, "LowSpec Client");
                }
            } catch (IOException ignored) {
            }
        }
    }

    // ---------- Performance mode ----------
    static final class Perf {
        private static final int LOW_RENDER = 6, LOW_SIM = 5;

        private static boolean looksLow(int render, int sim) {
            return render <= LOW_RENDER && sim <= LOW_SIM;
        }

        static void set(boolean enable) {
            Options o = Minecraft.getInstance().options;
            try {
                if (enable) {
                    int rd = o.renderDistance().get();
                    int sim = o.simulationDistance().get();
                    if (!looksLow(rd, sim)) {
                        // remember the normal settings
                        Settings.prevRender = rd;
                        Settings.prevSim = sim;
                        Settings.prevBlend = o.biomeBlendRadius().get();
                        Settings.prevShadows = o.entityShadows().get();
                        Settings.prevEntity = o.entityDistanceScaling().get();
                    } else if (looksLow(Settings.prevRender, Settings.prevSim)) {
                        // settings were already low and nothing sensible is stored: use vanilla defaults
                        Settings.prevRender = 12;
                        Settings.prevSim = 12;
                        Settings.prevBlend = 2;
                        Settings.prevShadows = true;
                        Settings.prevEntity = 1.0;
                    }
                    o.renderDistance().set(LOW_RENDER);
                    o.simulationDistance().set(LOW_SIM);
                    o.biomeBlendRadius().set(0);
                    o.entityShadows().set(false);
                    o.entityDistanceScaling().set(0.5);
                } else {
                    int rd = Settings.prevRender, sim = Settings.prevSim;
                    int blend = Settings.prevBlend;
                    boolean shadows = Settings.prevShadows;
                    double entity = Settings.prevEntity;
                    if (looksLow(rd, sim)) {
                        rd = 12;
                        sim = 12;
                        blend = 2;
                        shadows = true;
                        entity = 1.0;
                    }
                    o.renderDistance().set(rd);
                    o.simulationDistance().set(sim);
                    o.biomeBlendRadius().set(blend);
                    o.entityShadows().set(shadows);
                    o.entityDistanceScaling().set(entity);
                }
                o.save();
            } catch (RuntimeException ignored) {
            }
            Settings.put(Mod.PERF, enable);
        }
    }

    // ---------- Drawing helpers (kept cheap: few fills) ----------
    static final class Ui {
        static final Style STYLE = Style.EMPTY.withFont(
                new FontDescription.Resource(Identifier.fromNamespaceAndPath("lowspec", "glass")));
        private static final HashMap<String, Component> PLAIN = new HashMap<>();
        private static final HashMap<String, Component> STYLED = new HashMap<>();

        static Component t(String s) {
            boolean styled = Settings.has(Mod.FONT);
            HashMap<String, Component> map = styled ? STYLED : PLAIN;
            if (map.size() > 512) map.clear();
            return map.computeIfAbsent(s,
                    k -> styled ? Component.literal(k).withStyle(STYLE) : Component.literal(k));
        }

        static int tw(Font f, String s) {
            return f.width(t(s));
        }

        static void text(GuiGraphicsExtractor g, Font f, String s, int x, int y, int color, boolean shadow) {
            g.text(f, t(s), x, y, color, shadow);
        }

        static int a(int c, float m) {
            int al = Math.round(((c >>> 24) & 0xFF) * Math.max(0f, Math.min(1f, m)));
            return (al << 24) | (c & 0xFFFFFF);
        }

        static int mix(int c1, int c2, float t) {
            t = Math.max(0f, Math.min(1f, t));
            int r = 0;
            for (int sh = 24; sh >= 0; sh -= 8) {
                int p = (c1 >>> sh) & 0xFF, q = (c2 >>> sh) & 0xFF;
                r |= (p + Math.round((q - p) * t)) << sh;
            }
            return r;
        }

        /** Rectangle with 1px rounded corners, only 3 fills, no overlap. */
        static void rr(GuiGraphicsExtractor g, int x, int y, int w, int h, int c) {
            g.fill(x + 1, y, x + w - 1, y + 1, c);
            g.fill(x, y + 1, x + w, y + h - 1, c);
            g.fill(x + 1, y + h - 1, x + w - 1, y + h, c);
        }

        /** Lunar-style HUD panel: dark translucent box + thin light rim on top. */
        static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
            rr(g, x, y, w, h, 0x70101820);
            g.fill(x + 2, y, x + w - 2, y + 1, 0x55FFFFFF);
        }

        /** Richer frosted look, used only on the menu screen. */
        static void glass(GuiGraphicsExtractor g, int x, int y, int w, int h, float m) {
            rr(g, x + 1, y + 2, w, h, a(0x30000000, m));
            rr(g, x, y, w, h, a(0x700D1B2A, m));
            g.fill(x + 3, y, x + w - 3, y + 1, a(0x99FFFFFF, m));
            g.fill(x, y + 3, x + 1, y + h - 3, a(0x55FFFFFF, m));
            g.fill(x + w - 1, y + 3, x + w, y + h - 3, a(0x55FFFFFF, m));
            g.fill(x + 2, y + 1, x + w - 2, y + 1 + Math.max(2, h / 3), a(0x14FFFFFF, m));
        }
    }

    // ---------- Logo (drawn in code, 12x12 grid, s = pixels per cell) ----------
    static final class Logo {
        private static final int[][] RIM = {
                {2, 10}, {1, 11}, {0, 12}, {0, 12}, {0, 12}, {0, 12},
                {0, 12}, {0, 12}, {0, 12}, {0, 12}, {1, 11}, {2, 10}};

        static void draw(GuiGraphicsExtractor g, int x, int y, int s, float m) {
            for (int r = 0; r < 12; r++) {
                g.fill(x + RIM[r][0] * s, y + r * s, x + RIM[r][1] * s, y + (r + 1) * s, Ui.a(0xE6DFFBFF, m));
            }
            for (int r = 1; r <= 10; r++) {
                int a = 1, b = 11;
                if (r == 1 || r == 10) { a = 3; b = 9; }
                else if (r == 2 || r == 9) { a = 2; b = 10; }
                int c = Ui.mix(0xFF7A5CFF, 0xFF3DE0C0, (r - 1) / 9f);
                g.fill(x + a * s, y + r * s, x + b * s, y + (r + 1) * s, Ui.a(c, m));
            }
            g.fill(x + 3 * s, y + 2 * s, x + 8 * s, y + 3 * s + s / 2, Ui.a(0x55FFFFFF, m));
            g.fill(x + 4 * s, y + 3 * s, x + 6 * s, y + 9 * s, Ui.a(0xFFFFFFFF, m));
            g.fill(x + 4 * s, y + 7 * s, x + 9 * s, y + 9 * s, Ui.a(0xFFFFFFFF, m));
            g.fill(x + 8 * s, y + 3 * s, x + 10 * s, y + 5 * s, Ui.a(0xFFFFF07A, m));
        }
    }

    // ---------- Right Shift menu ----------
    static final class MenuScreen extends Screen {
        private static final Mod[] MODS = Mod.values();
        private final float[] on = new float[MODS.length];
        private final float[] hover = new float[MODS.length];
        private final float[] actionHover = new float[2];
        private float doneHover;
        private boolean listening;
        private long openNs, lastNs;
        private int px, py, pw, ph, cw, rows, doneY;

        MenuScreen() {
            super(Component.literal("LowSpec Client"));
        }

        private int cellX(int i) {
            return px + 10 + (i % 2) * (cw + 10);
        }

        private int cellY(int i) {
            return py + 40 + (i / 2) * 26;
        }

        private int actionY(int j) {
            return py + 40 + rows * 26 + j * 26;
        }

        @Override
        protected void init() {
            pw = 300;
            cw = (pw - 30) / 2;
            rows = (MODS.length + 1) / 2;
            ph = 40 + rows * 26 + 2 * 26 + 34;
            px = (this.width - pw) / 2;
            py = Math.max(2, (this.height - ph) / 2);
            listening = false;

            // Invisible vanilla buttons: they only handle clicks. Drawing is custom below.
            for (int i = 0; i < MODS.length; i++) {
                Mod mod = MODS[i];
                on[i] = Settings.has(mod) ? 1f : 0f;
                this.addRenderableWidget(Button.builder(Component.empty(), b -> {
                    boolean now = !Settings.has(mod);
                    if (mod == Mod.PERF) Perf.set(now);
                    else Settings.put(mod, now);
                    Settings.save();
                }).bounds(cellX(i), cellY(i), cw, 22).build());
            }

            // armor position: click to cycle
            this.addRenderableWidget(Button.builder(Component.empty(), b -> {
                Settings.armorPos = (Settings.armorPos + 1) % ArmorPos.values().length;
                Settings.save();
            }).bounds(px + 10, actionY(0), pw - 20, 22).build());

            // zoom key: click, then press the new key
            this.addRenderableWidget(Button.builder(Component.empty(), b -> listening = !listening)
                    .bounds(px + 10, actionY(1), pw - 20, 22).build());

            doneY = actionY(2) + 4;
            this.addRenderableWidget(Button.builder(Component.empty(), b -> this.onClose())
                    .bounds(px + 10, doneY, pw - 20, 22).build());

            openNs = lastNs = System.nanoTime();
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (listening) {
                int code = event.key();
                listening = false;
                if (code != InputConstants.KEY_ESCAPE) {
                    zoomKey.setKey(InputConstants.Type.KEYSYM.getOrCreate(code));
                    KeyMapping.resetMapping();
                    Minecraft.getInstance().options.save();
                }
                return true;
            }
            return super.keyPressed(event);
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
            long now = System.nanoTime();
            float dt = Math.min((now - lastNs) / 1e9f, 0.25f);
            lastNs = now;
            float k = 1f - (float) Math.exp(-dt * 16f);
            float p = Math.min(1f, (now - openNs) / 200_000_000f);
            float e = 1f - (1f - p) * (1f - p) * (1f - p);
            int oy = (int) ((1f - e) * 10);
            Font f = this.font;

            g.fill(0, 0, this.width, this.height, Ui.a(0x70000000, e));
            Ui.glass(g, px, py + oy, pw, ph, e);

            Logo.draw(g, px + 12, py + oy + 8, 2, e);
            Ui.text(g, f, "LowSpec Client", px + 12 + 30, py + oy + 16, Ui.a(0xFFFFFFFF, e), false);

            for (int i = 0; i < MODS.length; i++) {
                int x = cellX(i), y = cellY(i) + oy;
                boolean hov = mx >= x && mx < x + cw && my >= cellY(i) && my < cellY(i) + 22;
                hover[i] += ((hov ? 1f : 0f) - hover[i]) * k;
                on[i] += ((Settings.has(MODS[i]) ? 1f : 0f) - on[i]) * k;

                Ui.rr(g, x, y, cw, 22, Ui.a(Ui.mix(0x30FFFFFF, 0x55FFFFFF, hover[i]), e));
                Ui.text(g, f, MODS[i].label, x + 8, y + 7, Ui.a(0xFFFFFFFF, e), false);

                int sx = x + cw - 24 - 8;
                int sy = y + 5;
                Ui.rr(g, sx, sy, 24, 12, Ui.a(Ui.mix(0x66FFFFFF, 0xFF55FFAA, on[i]), e));
                Ui.rr(g, sx + 2 + (int) (on[i] * 12), sy + 2, 8, 8, Ui.a(0xFFFFFFFF, e));
            }

            // action rows: armor position + zoom key
            for (int j = 0; j < 2; j++) {
                int ay = actionY(j);
                boolean hov = mx >= px + 10 && mx < px + pw - 10 && my >= ay && my < ay + 22;
                actionHover[j] += ((hov ? 1f : 0f) - actionHover[j]) * k;
                Ui.rr(g, px + 10, ay + oy, pw - 20, 22, Ui.a(Ui.mix(0x30FFFFFF, 0x55FFFFFF, actionHover[j]), e));

                String label = j == 0 ? "Armor HUD position" : "Zoom key";
                String value;
                int vcol = 0xFF55FFAA;
                if (j == 0) {
                    value = ArmorPos.values()[Settings.armorPos].label;
                } else if (listening) {
                    value = "press a key...";
                    vcol = 0xFFFFE066;
                } else {
                    value = zoomKey.getTranslatedKeyMessage().getString();
                }
                Ui.text(g, f, label, px + 18, ay + oy + 7, Ui.a(0xFFFFFFFF, e), false);
                Ui.text(g, f, value, px + pw - 18 - Ui.tw(f, value), ay + oy + 7, Ui.a(vcol, e), false);
            }

            boolean dh = mx >= px + 10 && mx < px + pw - 10 && my >= doneY && my < doneY + 22;
            doneHover += ((dh ? 1f : 0f) - doneHover) * k;
            Ui.rr(g, px + 10, doneY + oy, pw - 20, 22, Ui.a(Ui.mix(0x44FFFFFF, 0x99FFFFFF, doneHover), e));
            int dw = Ui.tw(f, "Done");
            Ui.text(g, f, "Done", px + pw / 2 - dw / 2, doneY + oy + 7, Ui.a(0xFFFFFFFF, e), false);
        }
    }

    // ---------- HUD ----------
    static final class Hud {
        private static final int X = 6, Y = 6, KEY = 18, GAP = 2;
        private static final EquipmentSlot[] SLOTS = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

        private static final ArrayDeque<Long> leftClicks = new ArrayDeque<>();
        private static final ArrayDeque<Long> rightClicks = new ArrayDeque<>();
        private static boolean leftWas, rightWas;
        private static final float[] anim = new float[7]; // W A S D LMB RMB SPACE

        private static float deltaSum;
        private static int deltaCount;
        private static int shownFps;
        private static String fpsText = "FPS  --";
        private static String memText = "MEM  --";
        private static long windowStart = System.nanoTime();
        private static long lastNs = System.nanoTime();
        private static final String[] LINES = new String[4];

        static void extract(GuiGraphicsExtractor g, DeltaTracker delta) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            long now = System.nanoTime();

            float dt = Math.min((now - lastNs) / 1e9f, 0.25f);
            lastNs = now;
            float k = 1f - (float) Math.exp(-dt * 18f);

            // FPS from the real time between frames; memory text refreshed with it
            deltaSum += delta.getRealtimeDeltaTicks();
            deltaCount++;
            if (now - windowStart >= 500_000_000L) {
                if (deltaSum > 0) shownFps = Math.round(20f * deltaCount / deltaSum);
                fpsText = "FPS  " + shownFps;
                Runtime rt = Runtime.getRuntime();
                memText = "MEM  " + ((rt.totalMemory() - rt.freeMemory()) >> 20) + " / " + (rt.maxMemory() >> 20) + " MB";
                deltaSum = 0;
                deltaCount = 0;
                windowStart = now;
            }

            boolean left = mc.options.keyAttack.isDown();
            boolean right = mc.options.keyUse.isDown();
            track(leftClicks, left, leftWas, now);
            track(rightClicks, right, rightWas, now);
            leftWas = left;
            rightWas = right;

            int W = mc.getWindow().getGuiScaledWidth();
            int H = mc.getWindow().getGuiScaledHeight();

            // ----- info panel (FPS / CPS / coords / memory) -----
            int y = Y;
            int n = 0;
            if (Settings.has(Mod.FPS)) LINES[n++] = fpsText;
            if (Settings.has(Mod.CPS)) LINES[n++] = "CPS  " + leftClicks.size() + " | " + rightClicks.size();
            if (Settings.has(Mod.COORDS) && mc.player != null) {
                LINES[n++] = "XYZ  " + (int) Math.floor(mc.player.getX()) + "  "
                        + (int) Math.floor(mc.player.getY()) + "  " + (int) Math.floor(mc.player.getZ());
            }
            if (Settings.has(Mod.MEMORY)) LINES[n++] = memText;
            if (n > 0) {
                int w = 0;
                for (int i = 0; i < n; i++) w = Math.max(w, Ui.tw(font, LINES[i]));
                w += 14;
                int h = n * 11 + 6;
                Ui.panel(g, X, y, w, h);
                int ty = y + 4;
                for (int i = 0; i < n; i++) {
                    Ui.text(g, font, LINES[i], X + 7, ty, 0xFFFFFFFF, false);
                    ty += 11;
                }
                y += h + 4;
            }

            // ----- keystrokes -----
            if (Settings.has(Mod.KEYS)) {
                boolean[] d = {
                        mc.options.keyUp.isDown(), mc.options.keyLeft.isDown(),
                        mc.options.keyDown.isDown(), mc.options.keyRight.isDown(),
                        left, right, mc.options.keyJump.isDown()};
                for (int i = 0; i < anim.length; i++) {
                    anim[i] += ((d[i] ? 1f : 0f) - anim[i]) * k;
                }
                int row2 = y + KEY + GAP;
                key(g, font, "W", X + KEY + GAP, y, KEY, anim[0]);
                key(g, font, "A", X, row2, KEY, anim[1]);
                key(g, font, "S", X + KEY + GAP, row2, KEY, anim[2]);
                key(g, font, "D", X + 2 * (KEY + GAP), row2, KEY, anim[3]);
                int row3 = row2 + KEY + GAP;
                int total = 3 * KEY + 2 * GAP;
                int half = (total - GAP) / 2;
                key(g, font, "LMB", X, row3, half, anim[4]);
                key(g, font, "RMB", X + half + GAP, row3, total - half - GAP, anim[5]);
                key(g, font, "SPACE", X, row3 + KEY + GAP, total, anim[6]);
            }

            if (mc.player == null) return;

            if (Settings.has(Mod.ARMOR)) armor(g, font, mc, W, H);

            // ----- effect timer (top right) -----
            if (Settings.has(Mod.EFFECTS)) {
                int ey = Y;
                for (MobEffectInstance e : mc.player.getActiveEffects()) {
                    String name = e.getEffect().value().getDisplayName().getString();
                    if (e.getAmplifier() > 0) name += " " + (e.getAmplifier() + 1);
                    boolean inf = e.isInfiniteDuration();
                    String line = name + "  " + (inf ? "inf" : fmt(e.getDuration()));
                    int col = (!inf && e.getDuration() < 200) ? 0xFFFF6B6B : 0xFFFFFFFF;
                    int w = Ui.tw(font, line) + 12;
                    int ex = W - w - X;
                    Ui.panel(g, ex, ey, w, 15);
                    Ui.text(g, font, line, ex + 6, ey + 3, col, false);
                    ey += 18;
                }
            }
        }

        // ----- armor HUD -----
        private static void armor(GuiGraphicsExtractor g, Font font, Minecraft mc, int W, int H) {
            ArmorPos pos = ArmorPos.values()[Settings.armorPos];

            if (pos == ArmorPos.VERTICAL) {
                int ax = W - 22;
                int ay = H / 2 - 38;
                for (EquipmentSlot slot : SLOTS) {
                    ItemStack s = mc.player.getItemBySlot(slot);
                    if (!s.isEmpty()) {
                        g.item(s, ax, ay);
                        if (s.isDamageableItem()) {
                            String t = pct(s) + "%";
                            Ui.text(g, font, t, ax - 3 - Ui.tw(font, t), ay + 4, pctColor(s), true);
                        }
                    }
                    ay += 18;
                }
                return;
            }

            int slotW = 28;
            int total = 4 * slotW;
            int x0;
            int y0;
            if (pos == ArmorPos.HORIZONTAL) {
                x0 = W / 2 - total / 2;
                y0 = H - 88;
            } else if (pos == ArmorPos.BOTTOM_LEFT) {
                x0 = 6;
                y0 = H - 33;
            } else {
                x0 = W - total - 6;
                y0 = H - 33;
            }
            for (int i = 0; i < SLOTS.length; i++) {
                ItemStack s = mc.player.getItemBySlot(SLOTS[i]);
                int x = x0 + i * slotW + (slotW - 16) / 2;
                if (!s.isEmpty()) {
                    g.item(s, x, y0);
                    if (s.isDamageableItem()) {
                        String t = pct(s) + "%";
                        Ui.text(g, font, t, x + 8 - Ui.tw(font, t) / 2, y0 + 17, pctColor(s), true);
                    }
                }
            }
        }

        private static int pct(ItemStack s) {
            int max = s.getMaxDamage();
            return max <= 0 ? 100 : (max - s.getDamageValue()) * 100 / max;
        }

        private static int pctColor(ItemStack s) {
            int p = pct(s);
            return p > 60 ? 0xFF55FF55 : p > 35 ? 0xFFFFFF55 : 0xFFFF5555;
        }

        private static String fmt(int ticks) {
            int s = Math.max(0, ticks) / 20;
            int sec = s % 60;
            return (s / 60) + ":" + (sec < 10 ? "0" : "") + sec;
        }

        private static void track(ArrayDeque<Long> clicks, boolean down, boolean was, long now) {
            if (down && !was) clicks.addLast(now);
            long cutoff = now - 1_000_000_000L;
            while (!clicks.isEmpty() && clicks.peekFirst() < cutoff) clicks.pollFirst();
        }

        private static void key(GuiGraphicsExtractor g, Font font, String label, int x, int y, int w, float an) {
            g.fill(x, y, x + w, y + KEY, 0x70101820);
            if (an > 0.02f) g.fill(x, y, x + w, y + KEY, Ui.a(0xCCFFFFFF, an));
            int col = Ui.mix(0xFFFFFFFF, 0xFF101820, an);
            Ui.text(g, font, label, x + (w - Ui.tw(font, label)) / 2, y + (KEY - 9) / 2, col, false);
        }
    }

    // ---------- Watermarks (always on, no background box) ----------
    static final class Marks {
        private static final String NAME = "LowSpec Client";

        static void title(Screen screen, GuiGraphicsExtractor g, int mouseX, int mouseY, float tickDelta) {
            Font font = Minecraft.getInstance().font;
            int tw = Ui.tw(font, NAME);
            int total = 24 + 6 + tw;
            int rx = screen.width / 2 - total / 2;
            int ry = 84;
            Logo.draw(g, rx, ry, 2, 1f);
            Ui.text(g, font, NAME, rx + 30, ry + 8, 0xFFFFFFFF, true);
        }

        static void corner(Screen screen, GuiGraphicsExtractor g, int mouseX, int mouseY, float tickDelta) {
            Font font = Minecraft.getInstance().font;
            int tw = Ui.tw(font, NAME);
            int w = 12 + 5 + tw;
            int x = screen.width - w - 8;
            int y = screen.height - 12 - 6;
            Logo.draw(g, x, y, 1, 1f);
            Ui.text(g, font, NAME, x + 17, y + 2, 0xFFFFFFFF, true);
        }
    }
}
