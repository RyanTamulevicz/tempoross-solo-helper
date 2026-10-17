# Tempoross Solo Helper

A RuneLite Plugin Hub-style helper for multiple solo Tempoross methods. It presents one current action, highlights the relevant in-game target, and advances from observed inventory, HUD, NPC, and object state.

The plugin is guidance-only. It does not click, inject input, change menu entries, predict hazards, or send actions to the game.

## Method selector

Choose a strategy from the plugin setting named **Method**:

- **No cooking (max XP)** — the existing fast no-cook route.
- **Cooking Mix of XP and permits** — the cooking route based on the [OSRS Wiki solo strategy](https://oldschool.runescape.wiki/w/Tempoross/Strategies#Solo_cooking_(mix_of_XP_and_permits)).
- **Cooking + firefighting (max permits)** — the 17/19/19/2 route from the [OSRS Wiki max permits strategy](https://oldschool.runescape.wiki/w/Tempoross/Strategies#Solo_cooking_+_firefighting_(max_permits)).

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

## Cooking + firefighting (max permits)

Setup before entering: a rune pouch with runes for Humidify, a rope (unless wearing the full spirit angler outfit), a hammer (unless using the Imcando hammer), 19 free slots, and the rest filled with buckets of water.

### Phase 1

1. Fish the nearest spot. After 9 fish, cook at the shrine until a double fish spot appears, then fish it up to 17.
2. Cook all 17. If the colossal wave starts, tether to the totem pole.
3. Load exactly 17 into the closest crate. Tempoross's energy drops to 4%.
4. Douse the ship flame. Douse island fires too, or leave them to spread while watching your tile.
5. Catch 19 fish.
6. Cook all 19. Leave for the ship by 93% storm intensity (90% if fires block the path).
7. Load all 19.
8. Fish the spirit pool until Tempoross regains its energy.

### Phase 2

1. Catch 19 fish. Optionally take 5-10 buckets to douse spreading fires while waiting for double spots, then drop the extras.
2. Cook all 19 before the storm reaches 93%.
3. Load all 19.
4. Damage Tempoross below 60% essence, ideally below 50%.

### Phase 3

1. Fill your inventory with buckets from the dock crate, cast Humidify, and douse fires around the island.
2. Catch 2 fish before the storm gets too high. Cooking them is optional.
3. Load both fish, avoiding fires.
4. Finish Tempoross, then select **Leave**.

The panel shows the storm intensity from the Tempoross HUD during the 19-fish, firefighting, and final-fish steps. When it reaches 90% during a 19-fish catch or cook step, the instruction switches to heading for the ship.

If the storm forces you to load early, the helper counts the partial load from the fish or cook step, moves to the load step, and redirects the highlight to fishing or cooking until the shortfall is caught, cooked, and loaded. The final 2-fish load counts raw and cooked fish. The douse steps highlight the nearest fire anywhere, including the ship; they advance when you catch the first fish of the next batch, so fire tracking never blocks progression.

## Passive guidance

- Spawned double-fish spots always outrank normal fishing spots and use a separate configurable priority color.
- Active fires on the confirmed working shore remain passively highlighted. Fire detection never blocks route progression; the setup and post-kill bucket instructions are inventory guidance only.
- Wave and tether references are static reminders only; the plugin does not predict or detect wave timing.

## Panel and recovery

The panel shows the selected method, trip, method-local step progress, AUTO or MANUAL state, instruction, and observed progress. Right-click it for **Previous step**, **Next step**, or **Reset route**. Optional hotkeys are available for the same actions.

## Versioning

The Plugin Hub shows the `version` from `runelite-plugin.properties`, and `build.gradle` reads it from there, so it is the only place to change. Use semantic versioning (`MAJOR.MINOR.PATCH`); the build fails on any other format.

- **PATCH** — bug fixes and wording changes.
- **MINOR** — new methods or features that keep existing config working.
- **MAJOR** — changes that reset or break saved config.

Bump the version and add a `CHANGELOG.md` entry in the same commit as the change, before updating the commit hash in the Plugin Hub.

## Development

This project targets Java 11 and the current RuneLite release.

```sh
./gradlew clean test
./gradlew run
```

The `run` task launches a development RuneLite client. In-game behavior must be tested manually; never automate RuneScape input.
