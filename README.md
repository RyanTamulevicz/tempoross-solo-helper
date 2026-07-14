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

Setup before entering: have **5 buckets of water in your inventory**.

1. Fish 17 harpoonfish. Cook when you've caught the first 8 or so, and cook until a double fish spot comes up.
2. Cook all the fish.
3. Load all 17 fish.
4. Fires should come up now. Usually 4 appear, but sometimes 1 spreads; drop buckets after this.
5. Fish 19 harpoonfish. Do a few, then cook until a double fish spot comes up.
6. Cook all.
7. Load all 19 fish. Tempoross will go down, but just keep loading it all.
8. Attack Tempoross.
9. Fish 19 harpoonfish. Do a few, then cook until a double fish spot comes up.
10. Cook all.
11. Load all 19 fish. Tempoross will go down, but just keep loading it all.
12. Attack Tempoross.
13. Fish until a double fish spot comes up, cook after the spot expires, and fish the double spot again when it spawns.
14. Cook all.
15. Load 14 fish into the first cannon, then load 14 into the second cannon.
16. Attack Tempoross. If bad RNG prevents the kill, wait for the two cannons to redown him and finish him off.
17. Once he dies, take 5 buckets and fill them with water before getting kicked out.

The 17/19/19 fishing steps temporarily switch the primary highlight to the cooking shrine after the first 8 fish. A spawned double-fish spot immediately ends that early cooking break and restores fishing priority. Step 13 uses a different cycle: it keeps fishing through the double spot, switches to cooking when that spot expires, and returns to fishing for the next double spawn.

Cooking checkpoints count only raw and cooked harpoonfish toward the 17/19/19/28 totals. Crystallised fish do not count for this method and do not advance its exact hopper-load checkpoints. Only cooked-fish decreases observed during confirmed hopper loading advance the exact load totals. If a double catch overshoots an exact target, fishing highlighting pauses and the panel reports how many extra raw fish must be removed.

On the final load, the helper counts 14 fish in the first hopper, then moves the highlight to the other hopper for the remaining 14. Fire activity never gates progression: step 4 keeps the fishing target available and advances when the first fish of the next batch is caught.

## Passive guidance

- Spawned double-fish spots always outrank normal fishing spots and use a separate configurable priority color.
- Active fires on the confirmed working shore remain passively highlighted. Fire detection never blocks route progression; the setup and post-kill bucket instructions are inventory guidance only.
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
