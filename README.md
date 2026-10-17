
# Tempoross Solo Helper

A RuneLite Plugin Hub-style helper supporting multiple solo Tempoross strategies.

The plugin provides step-by-step guidance, highlights relevant in-game targets, and automatically advances through each strategy using observed inventory, HUD, NPC, and object state.

It is designed to make solo Tempoross easier to follow without automating gameplay.

**The plugin is guidance-only.** It does not click, inject input, modify menu entries, predict hazards, or send actions to the game.

## Features

- Multiple selectable solo Tempoross strategies
- Automatic progression through fishing, cooking, loading, and spirit-pool phases
- Highlighting of relevant fishing spots, cooking shrines, cannons, and other targets
- Prioritisation of double-fishing spots
- Configurable final fish amount for the cooking strategy
- Automatic splitting of the final fish batch between both cannons
- Inventory-based fish tracking and progress indicators
- Manual step controls for recovery if automatic progression fails
- Optional hotkeys for controlling the route
- Passive fire highlighting and bucket reminders

## Method Selector

Choose your preferred strategy from the plugin setting named **Method**.

Two methods are available:

1. **No cooking (max XP)** — a fast route focused on Fishing experience.
2. **Cooking Mix of XP and permits** — a cooking route based on the [OSRS Wiki solo strategy](https://oldschool.runescape.wiki/w/Tempoross/Strategies#Solo_cooking_(mix_of_XP_and_permits)).

Changing the selected method resets the current route.

The selected strategy starts automatically when the Tempoross encounter HUD appears after entering a game.

---

## Method 1: No Cooking (Max XP)

This method prioritises Fishing experience by loading raw harpoonfish without cooking them.

### Trip 1

1. Catch 25 or 26 harpoonfish, depending on available inventory space.
2. Prioritise double-fishing spots whenever available.
3. Load the full batch into the highlighted hopper.

### Trip 2

1. Catch 27 harpoonfish.
2. Load all 27 fish.
3. Fish from the spirit pool until it closes.

### Trip 3

1. Catch 27 harpoonfish.
2. Load exactly 3 fish, leaving 24.
3. Fish from the spirit pool until Tempoross is at approximately 10% essence.
4. Load the remaining 24 fish.

### Trip 4

1. Catch and load the final 27 fish.
2. Finish Tempoross at the spirit pool.
3. Select **Leave**.

---

## Method 2: Cooking Mix of XP and Permits

This strategy balances Fishing experience and reward permits.

It follows the opening **17/19/19** rotation, with a configurable final fishing batch.

The original strategy uses 28 fish for the final batch. The modified plugin supports alternative targets, including 22.

### Preparation

Before entering Tempoross:

- Bring 5 buckets of water.
- Bring your harpoon.
- Ensure you have sufficient inventory space.

### Trip 1 — 17 Fish

1. Catch 17 harpoonfish.
2. After catching approximately 8 fish, temporarily switch to cooking while waiting for a double-fishing spot.
3. Return to fishing when a double-fishing spot appears.
4. Cook all 17 fish.
5. Load all 17 cooked fish into the cannon.

Tempoross should now be at low energy.

Fires may appear during this phase. The plugin provides passive highlighting for detected fires.

### Trip 2 — 19 Fish

1. Catch 19 harpoonfish.
2. After catching approximately 8 fish, cook while waiting for the next double-fishing spot.
3. Return to fishing when a double spot appears.
4. Cook all 19 fish.
5. Load all 19 cooked fish.
6. Attack Tempoross at the spirit pool.

Continue loading the full batch even if Tempoross becomes vulnerable before you finish.

### Trip 3 — 19 Fish

1. Catch 19 harpoonfish.
2. Use the same fishing and cooking approach as Trip 2.
3. Cook all 19 fish.
4. Load all 19 cooked fish.
5. Attack Tempoross at the spirit pool.

Again, continue loading the full batch even if Tempoross becomes vulnerable before loading finishes.

### Trip 4 — Configurable Final Batch

The final fishing target is controlled by the **Final fish amount** setting.

For example, with a target of 22 fish:

1. Catch 22 harpoonfish.
2. Prioritise double-fishing spots.
3. When a double-fishing spot expires, switch to cooking.
4. Return to fishing when the next double spot appears.
5. Continue until the configured number of fish has been caught.
6. Cook the full batch.
7. Load the fish into both cannons.
8. Attack Tempoross at the spirit pool to finish the encounter.

The plugin automatically splits the final batch between both cannons.

For a final target of 22:

- First cannon: 11 fish
- Second cannon: 11 fish

For the original target of 28:

- First cannon: 14 fish
- Second cannon: 14 fish

For odd-numbered targets, the extra fish is allocated to the second cannon.

If Tempoross survives the first final spirit-pool phase, wait for the loaded cannons to bring it down again and finish the encounter.

### After the Kill

Before leaving:

1. Collect 5 buckets.
2. Fill them with water.
3. Prepare for the next encounter.

---

## Configurable Final Fish Amount

The **Final fish amount** setting allows players to customise the final batch of the cooking strategy.

The current modified version uses a default of **22 fish**.

The original strategy uses **28 fish**.

The supported configuration range must match the limits defined in the plugin's Java settings.

Changing the final fish amount updates:

- The final fishing target
- The final cooking target
- The cannon-loading targets
- The progress display
- The route instructions

Changing this setting resets the current route.

### Example Configurations

| Final Target | First Cannon | Second Cannon |
|---|---|---|
| 19 | 9 | 10 |
| 20 | 10 | 10 |
| 22 | 11 | 11 |
| 24 | 12 | 12 |
| 26 | 13 | 13 |
| 28 | 14 | 14 |

These are loading splits, not guarantees that every target will successfully complete the encounter. Different targets may affect energy management, encounter timing, and the ability to finish Tempoross.

---

## Automatic Fishing and Cooking Guidance

For the first three fishing batches (17/19/19), the helper temporarily highlights the cooking shrine after approximately 8 fish have been caught.

When a double-fishing spot appears, fishing receives priority again.

For the final batch, the helper uses a different cycle:

1. Fish at the highlighted double spot.
2. Switch to cooking when the spot expires.
3. Return to fishing when another double spot appears.
4. Repeat until the configured target is reached.

### Exact Fish Tracking

Fishing and cooking checkpoints count only raw and cooked harpoonfish toward the configured totals.

Crystallised harpoonfish do not contribute to these checkpoints.

Hopper-loading progress advances using observed decreases in cooked fish during confirmed loading.

If a double catch causes the player to exceed an exact fishing target, the helper pauses fishing guidance and displays how many extra raw fish must be removed.

---

## Target Highlighting

The plugin highlights relevant objects and locations based on the current strategy step.

### Fishing Spots

- Double-fishing spots receive priority over standard fishing spots.
- Double spots use a separately configurable highlight colour.
- When double spots disappear, normal fishing guidance resumes as appropriate.

### Cooking Shrines

Cooking shrines are highlighted when the current strategy requires cooking.

### Cannons

The appropriate cannon is highlighted during loading.

During the final batch, the plugin tracks the configured amount for each cannon and switches guidance once the first cannon's target has been reached.

### Fires

Active fires on the confirmed working shore receive passive highlighting.

Fire detection does not block route progression.

### Waves and Tethering

Wave and tether references are informational reminders only.

The plugin does not predict incoming waves or automate tethering.

---

## Progress Panel and Recovery

The panel displays:

- Selected method
- Current trip
- Current step
- Automatic or manual progression state
- Current instruction
- Observed progress

### Manual Controls

Right-click the panel to access:

- **Previous step**
- **Next step**
- **Reset route**

Optional hotkeys are available for the same actions.

These controls allow players to recover from missed detections or unexpected gameplay situations.

---

## Development

This project targets Java 11 compatibility and the current RuneLite release.

**Build environment:** Gradle 9.7.1 requires Java 17 or newer to run. Java 21 can be used to execute Gradle, while the project's configured Java toolchain must remain available for compilation.

### Run Tests

On Windows:

```powershell
.\gradlew.bat clean test
```

On Linux or macOS:

```bash
./gradlew clean test
```

### Launch the Development Client

On Windows:

```powershell
.\gradlew.bat run
```

On Linux or macOS:

```bash
./gradlew run
```

The `run` task launches a RuneLite development client containing the locally modified plugin.

### Testing

Automated tests should verify:

- Correct route progression
- Configurable final fish targets
- Accurate cooking and loading checkpoints
- Correct cannon splitting for even and odd targets
- Inventory overshoot handling
- No unintended changes to the original 17/19/19 opening rotation

Passing automated tests does not guarantee correct in-game behaviour.

The plugin should also be tested manually during Tempoross encounters.

---

## Contributing

Contributions, bug reports, and improvements are welcome, subject to the repository maintainer's contribution requirements.

Suggested areas for improvement include:

- Additional Tempoross strategies
- More flexible fish targets
- Improved stage detection and recovery
- Better highlighting and progress tracking
- Additional tests for unusual encounter states
- Improvements to fishing and cooking efficiency

When contributing, please keep the plugin guidance-only and compatible with RuneLite and Jagex's rules.

The plugin must never automate RuneScape input.

---

## Credits

Based on the original Tempoross Solo Helper project by [RyanTamulevicz](https://github.com/RyanTamulevicz/tempoross-solo-helper).

The cooking strategy references the [OSRS Wiki Tempoross strategies guide](https://oldschool.runescape.wiki/w/Tempoross/Strategies).

Additional development introduces configurable final fishing targets and associated route guidance.
