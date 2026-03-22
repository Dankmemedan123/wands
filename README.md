# Elemental Wands — Forge 1.20.1

Three powerful wands that bring elemental combat to Minecraft.

---

## The Wands

| Wand | Effect | Cooldown | Recipe |
|------|--------|----------|--------|
| 🔥 **Inferno Wand** | Ignites all enemies in a 10-block forward cone for 5 seconds | 2 seconds | 2× Blaze Rod + Fire Charge (vertical) |
| ❄️ **Glacial Wand** | Applies Slowness IV + Mining Fatigue II + full visual freeze to all enemies within 8 blocks | 8 seconds | 2× Packed Ice + Diamond (vertical) |
| ⚡ **Storm Wand** | Calls lightning at the block your crosshair is pointing at (up to 50 blocks) | 5 seconds | Lightning Rod + Gold Ingot + Iron Ingot (vertical) |

All wands display an action bar message on use and show Minecraft's built-in cooldown overlay in the hotbar.

**Note:** No pets (tamed wolves, cats, etc.) owned by the casting player are affected by any wand.

---

## Requirements

- **Java JDK 17** — [adoptium.net](https://adoptium.net/temurin/releases/?version=17)
- **Forge 1.20.1** — [files.minecraftforge.net](https://files.minecraftforge.net)

---

## Building (Recommended — GitHub Actions)

1. Push the project to a GitHub repository
2. The included `.github/workflows/main.yml` builds the `.jar` automatically on every push
3. Go to **Actions** → click the latest green run → scroll to **Artifacts** → download `elementalwands-mod`
4. Unzip — inside is `elementalwands-1.20.1-1.0.0.jar`

**Important:** The GitHub Actions workflow uses `gradle/actions/setup-gradle` and calls `gradle build` directly. Do NOT change this to `./gradlew build` — the wrapper jar is not included and will fail.

## Building Locally

If you have Gradle 8.1+ installed:
```
gradle build
```

If you want to use the wrapper (requires Gradle to bootstrap first):
```
./gradlew build          # Linux/Mac
gradlew.bat build        # Windows
```

Output: `build/libs/elementalwands-1.20.1-1.0.0.jar`

---

## Installing

1. Install Forge 1.20.1 from [files.minecraftforge.net](https://files.minecraftforge.net)
2. Copy the `.jar` to your `.minecraft/mods/` folder (or CurseForge instance mods folder)
3. Launch with the Forge 1.20.1 profile

---

## Project Structure

```
elementalwands/
├── src/main/java/com/example/elementalwands/
│   ├── ElementalWandsMod.java     ← @Mod entry point
│   ├── ModItems.java              ← DeferredRegister for all three wands
│   ├── ModCreativeTabs.java       ← Custom "Elemental Wands" creative tab
│   └── item/
│       ├── InfernoWand.java       ← Cone fire attack
│       ├── GlacialWand.java       ← Area freeze/slow
│       └── StormWand.java         ← Targeted lightning strike
└── src/main/resources/
    ├── assets/elementalwands/
    │   ├── lang/en_us.json
    │   ├── models/item/           ← item/generated model JSONs
    │   └── textures/item/         ← 16×16 PNG textures (pixel art)
    └── data/elementalwands/
        └── recipes/               ← Shaped crafting recipes
```
