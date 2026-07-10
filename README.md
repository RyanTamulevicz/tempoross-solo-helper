# Tempoross Solo Helper

A RuneLite Plugin Hub-style helper for the no-cook solo Tempoross route. It presents one current action, highlights the relevant in-game target, and advances from observed inventory, HUD, NPC, and object state.

The plugin is guidance-only. It does not click, inject input, change menu entries, predict future hazards, or send actions to the game.

## Route

### Trip 1

1. Run to the island and catch 25 or 26 harpoonfish, selected automatically from available inventory space. Double-fish spots take priority.
2. Load the full batch into the highlighted hopper and tether if needed.

### Trip 2

1. Return to the island and catch 27 fish.
2. Load all 27, then fish from the spirit pool until it closes.

### Trip 3

1. Catch 27 fish.
2. Load exactly three, leaving 24, then click off and return to the spirit pool.
3. Fish from the pool to about 10% essence.
4. Load the remaining fish. Loading one extra by mistake will not strand the route.

### Trip 4

1. Catch and load the final 27 fish, then finish Tempoross at the spirit pool.
2. Select **Leave** on a Spirit Angler NPC.

## Recommended setup

- Crystal harpoon for the XP-focused route
- Enough open inventory space for the 25/26-fish opening trip and each 27-fish trip

## Controls and recovery

The helper starts and resets automatically when the Tempoross encounter HUD appears after zoning into a game. No panel interaction is required. Right-click the route panel for **Previous step**, **Next step**, or **Reset route** if recovery is needed. Optional hotkeys for those navigation actions are available in the plugin configuration.

The helper learns the working island shore from the exact ammunition hopper that successfully loads fish. It keeps that shore locked for passive fire guidance, while the route itself proceeds independently of every fire.

During fishing steps, a spawned double-fish spot is always highlighted ahead of nearer normal spots.

The panel groups the fishing, loading, pool, and departure actions into four trips, shows overall route progress, and reports AUTO or MANUAL tracking state. Double-fish spots use a separate configurable priority color.

Automatic progression intentionally waits at timing-sensitive steps. If an unusual sequence or missed interaction leaves the helper on the wrong step, use the panel controls instead of restarting the game.

## Development

This project targets Java 11 and the current RuneLite release.

```sh
./gradlew clean test
./gradlew run
```

The `run` task launches a development RuneLite client. In-game behavior must be tested manually; never automate RuneScape input.
