# Tempoross Solo Helper

A RuneLite Plugin Hub-style helper for multiple solo Tempoross methods. It presents one current action, highlights the relevant in-game target, and advances from observed inventory, HUD, NPC, and object state.

The plugin is guidance-only. It does not click, inject input, change menu entries, predict hazards, or send actions to the game.

## Method selector

Choose a strategy from the plugin setting named **Method**:

- **No cooking (max XP)** — the existing fast no-cook route.
- **Cooking Mix of XP and permits** — the cooking route based on the [OSRS Wiki solo strategy](https://oldschool.runescape.wiki/w/Tempoross/Strategies#Solo_cooking_(mix_of_XP_and_permits)).

Changing the method resets the current route. The selected method starts automatically when the Tempoross encounter HUD appears after zoning into a game.

## No cooking (max XP)

### Trip 1

1. Catch 25 or 26 harpoonfish, selected automatically from available inventory space. Double-fish spots take priority.
2. Load the full batch into the highlighted hopper.

### Trip 2

1. Catch 27 fish and load all 27.
2. Fish from the spirit pool until it closes.

### Trip 3

1. Catch 27 fish.
2. Load exactly three, leaving 24.
3. Fish from the pool to about 10% essence.
4. Load the remaining fish.

### Trip 4

1. Catch and load the final 27 fish.
2. Finish Tempoross at the spirit pool and select **Leave**.

## Cooking Mix of XP and permits

### Trip 1 — 17 cooked

1. Catch about eight raw harpoonfish and begin cooking at the highlighted shrine.
2. If a double spot appears during the opening cook, it immediately takes priority so fishing can continue.
3. Reach exactly 17 raw/cooked fish, cook all 17, then load exactly 17 cooked fish. Crystallised fish do not count for this method.

### Trip 2 — 19 cooked

1. Catch, cook, and load exactly 19 fish.
2. Fish from the spirit pool until it closes.

The panel carries the strategy's static 93% contingency note. It never reads storm intensity to change targets or issue an automatic warning; use **Next** and **Previous** for manual recovery if the in-game HUD reaches that threshold.

### Trip 3 — 19 cooked

1. Catch, cook, and load another exact batch of 19.
2. Fish from the spirit pool until it closes again.

### Trip 4 — 28 cooked

1. Catch, cook, and load 28 fish cumulatively. If fewer than 28 inventory slots are open, the helper automatically splits the target across multiple cycles.
2. If time is tight, the panel notes the optional six-fish second-hopper contingency.
3. Finish Tempoross and select **Leave**.

Cooking checkpoints count only raw and cooked harpoonfish toward the 17/19/19/28 totals. Crystallised fish do not count for this method and do not advance its exact hopper-load checkpoints. Only cooked-fish decreases observed during confirmed hopper loading advance the exact load totals. If a double catch overshoots an exact target, fishing highlighting pauses and the panel reports how many extra raw fish must be removed.

After an exact 17- or 19-cooked load, one optional raw fish may be loaded immediately afterward. It is never required and does not count toward the cooked checkpoint.

## Passive guidance

- Spawned double-fish spots always outrank normal fishing spots and use a separate configurable priority color.
- Active fires on the confirmed working shore remain highlighted, but fire, dousing, bucket, filling, pump, and Humidify actions are never route stages and never block progression.
- Wave and tether references are static reminders only; the plugin does not predict or detect wave timing.

## Panel and recovery

The panel shows the selected method, trip, method-local step progress, AUTO or MANUAL state, instruction, and observed progress. Right-click it for **Previous step**, **Next step**, or **Reset route**. Optional hotkeys are available for the same actions.

## Development

This project targets Java 11 and the current RuneLite release.

```sh
./gradlew clean test
./gradlew run
```

The `run` task launches a development RuneLite client. In-game behavior must be tested manually; never automate RuneScape input.
