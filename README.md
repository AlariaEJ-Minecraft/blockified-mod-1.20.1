# Blockified

A Fabric mod for Minecraft 1.20.1, based on the Blockified Mod on Modrinth. It adds a handful of self-contained material families — tar, engineered ice, bog hazards, magnetite, and more — each with its own blocks, tools, and gimmick, rather than one giant unified system.

- **Minecraft:** 1.20.1
- **Loader:** Fabric (requires Fabric API)
- **Java:** 17+
- **License:** MIT

## What's in it

### 🔥 Tar
- **Hot Tar** — a magma-block-style hazard with very high blast resistance, and a great furnace fuel (burns ~20% longer than a lava bucket).
- **Tar Ingot** / **Tarched Coal** — the smelting/repair material and an efficient fuel item for the set below.
- **Tarched Pickaxe** — mined ores come out already smelted, no furnace trip needed.
- **Tarched Armor Set** — sits between Diamond and Netherite in raw stats. Wear the full set and you get permanent Fire Resistance plus immunity to explosion damage.

### ❄️ Ice
- **Black Ice, Condensed Ice, Cold Ice, Hard Dense Ice** — four Packed Ice variants with escalating slipperiness. The three denser ones also give you a shove if you stand still on them, like a slow conveyor belt.
- **Ice Totem of Resistance** — a totem-flavored curiosity, still finding its purpose.

### 🪵 Bog
A Soul Sand-inspired trio of movement hazards:
- **Clay Bog** — slows you down like Soul Sand, cushions falls like Slime.
- **Bog Block** — even slower, with a shallow sunken surface.
- **Mud Bog** — the dangerous one. Sink in deep enough (3+ stacked layers) and you'll go fully under and start drowning. Grows naturally in Lush Caves and Mushroom Fields.

### 🧲 Magnetite
- **Magnetite Ore → Raw Magnetite → Magnetite Ingot** — an iron-tier material chain (smelt 1:1, or craft 3 raw into 1 ingot).
- **Magnetite Helmet** — grants **Phantom Protection**, a custom effect that cancels phantom attacks outright while worn. Take the helmet off and the effect drops immediately.
- **Magnetite Compass** — always points at the nearest *active* Magnetar, vanilla lodestone-compass style, updating live as Magnetars power on/off or as you travel.
- **Magnetar** — a directional redstone block (place it facing you, like a dispenser). Power it with any redstone signal and after a brief wind-up it switches fully on, force-powering every piece of redstone wire within an 8-block radius — no wires required. It's wireless redstone.

### 🍮 Oobleck
A proper non-Newtonian hazard, built from the ground up as quicksand:
- Comes in 6 depth levels, and generates naturally as short strips along **Desert riverbanks**.
- Stand still on it and you sink fast. Keep walking or sprinting through it and you'll stay near the surface instead.
- Always sticky (heavily dampened movement), and once you're more than half-sunk an extra Slowness kicks in on top — escaping takes sustained effort, not a single step.
- Bottle it up with a **Bucket of Oobleck** to place or pick up a full block anywhere.

### ⚔️ Combat & Weapons
- **Sword of Experience** / **Axe of Experience** — drain bonus XP (10–15 / 5–10) from non-player mobs on hit.
- **Crying Obsidian Sword** — Netherite-tier, and lands an invisible burning effect (Wither damage over time, no particles) on non-player targets.

## Building

```bash
./gradlew build
```

Requires network access to the Fabric/Mojang Maven repositories the first time, to resolve Minecraft, Yarn mappings, and Loom.

## Status

Actively evolving — expect some rough edges: a few textures are still placeholder art, and not every item has its final behavior locked in yet.
