# Tempoross Solo Helper

A RuneLite Plugin Hub-style helper for the no-cook solo Tempoross catch-26 route. It presents one current action, highlights the relevant in-game target, and advances from observed inventory, HUD, NPC, and object state.

The plugin is guidance-only. It does not click, inject input, change menu entries, predict future hazards, or send actions to the game.

## Route

1. Catch 26 harpoonfish.
2. Load all 26; tether during the wave and douse the ship fire with the starting bucket.
3. Take five more buckets, leaving six total.
4. Cast Humidify (or use the water pump), then douse fires.
5. Drop every bucket and catch 27 fish.
6. Load all 27 and harpoon the spirit pool until it closes.
7. Catch 27, load exactly five, and harpoon Tempoross to 12% essence.
8. Load the remaining 22.
9. Catch 27, load all, and finish Tempoross.
10. Take one bucket before leaving so the next run starts with 26 fish slots.

## Recommended setup

- Crystal harpoon for the XP-focused route
- Full spirit angler outfit so no rope occupies an inventory slot
- Rune pouch with the runes for Humidify
- One starting bucket of water
- 27 free inventory slots after the rune pouch; the starting bucket reduces the first catch to 26

## Controls and recovery

At the Tempoross lobby, right-click the route panel and choose **Start route**. This explicit activation keeps the encounter guidance manually triggered. The helper remains armed and resets automatically for each game until **Stop route** is selected.

Right-click the route panel for **Previous step**, **Next step**, or **Reset route**. Optional hotkeys for those navigation actions are available in the plugin configuration.

Automatic progression intentionally waits at timing-sensitive steps. If an unusual sequence or missed interaction leaves the helper on the wrong step, use the panel controls instead of restarting the game.

## Development

This project targets Java 11 and the current RuneLite release.

```sh
./gradlew clean test
./gradlew run
```

The `run` task launches a development RuneLite client. In-game behavior must be tested manually; never automate RuneScape input.
