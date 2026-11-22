# LowSpec Client

**The lightweight all-in-one client for Minecraft on Fabric**

[![Modrinth Downloads](https://api.modrinth.com/v1/project/4s4Y6W92/stats)](https://modrinth.com/mod/lowspec-client)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)

## Overview

LowSpec Client is a lightweight, open-source, ad-free, telemetry-free Minecraft Fabric mod designed for players on weak hardware. Get the Lunar/Badlion feature set at a fraction of the cost.

## Features

### HUD Widgets
- **FPS Counter** - Current, average, 1% low, frame time
- **CPS Counter** - Clicks per second tracking
- **Coordinates** - XYZ display with Nether conversion and facing direction
- **Keystrokes** - WASD, mouse buttons, jump indicators
- **Armor HUD** - Durability percentage with 4 position presets
- **Mob Effect Timer** - Potion duration countdown
- **Ping** - Server latency (async, rate-limited)
- **Speed** - Current movement speed
- **Memory** - RAM usage with optional GPU memory

### PvP Features
- **Reach** - Last hit distance indicator
- **Combo Counter** - Hit streak tracking
- **Hit Counter** - Total hits
- **Totem Counter** - Totem of Undying count
- **Potion Counters** - Gapple detection

### Gameplay Modules
- **Zoom** - Smooth zoom with scroll adjustment
- **Toggle Sprint/Sneak** - True toggle behavior (not auto-sprint)
- **Freelook** - Separate camera rotation (server-gated)
- **Low Fire/Low Shield** - Reduced fire/shield animation opacity
- **Fullbright** - Max brightness toggle
- **Crosshair** - Customizable crosshair with dynamic reactions

### Performance
- **Performance Mode** - Reduces render distance, entity distance, shadows
- **Performance Center** - Hardware detection, preset recommendations
- **Dynamic Profile** - Auto-tune settings for target FPS
- **Built-in Benchmark** - FPS testing tool

### Customization
- **HUD Editor** - Drag, drop, resize, grid snap
- **Profiles** - Save/load layouts (PvP, Survival, Streaming)
- **Theme System** - Global and per-widget styling
- **Import/Export** - Share configurations

### Server Safety
- **Compliance Profiles** - Strict/Standard/Open mode
- **Server Detection** - Auto-apply safe settings per server
- **Feature Labels** - Clear server-risk indicators

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) (>= 0.19.1)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release from [Modrinth](https://modrinth.com/mod/lowspec-client)
4. Place the `.jar` file in your Minecraft `mods` folder

## Configuration

Access the menu by pressing **Right Shift**. Features can be toggled and configured from the drag-and-drop HUD editor.

## Development

```bash
./gradlew build
```

The project uses Java 25, Fabric Loom, and Minecraft 26.2.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines.

## License

MIT License - see [LICENSE](LICENSE) for details.

## Credits

- Original author: You
- Poppins font: SIL Open Font License
- Inspired by Lunar Client, Badlion, AxolotlClient

## Support

- [GitHub Issues](https://github.com/you/lowspec-client/issues)
- [Modrinth Page](https://modrinth.com/mod/lowspec-client)