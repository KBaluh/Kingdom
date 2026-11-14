# Kingdom

A 2D arcade game: you are a fish that defends the fish kingdom from predators
with rockets and a bubble shield. Each level is three battles of growing size.

The game is a **Godot 4 project (GDScript)** in `godot/`. It was ported from
the old Java Swing version in `src/`, which is kept only for reference: do not
change it, and do not build or run it to test the game.

## Running

Godot 4.7 or newer is required. A local copy of the engine may be in
`.tools/godot/` (ignored by git); otherwise use a `godot` from PATH.

```
godot --path godot                                  # play
godot -e --path godot                               # open in the editor
godot --headless --path godot --import              # import new assets
godot --headless --path godot --quit-after 120      # check that scripts load
godot --headless --path godot --export-release "Windows Desktop"
```

Export presets are `Windows Desktop` (`godot/build/Kingdom.exe`) and `Android`
(`godot/build/Kingdom.apk`). `godot/build/` is ignored by git.

## Where things are

| Path | What |
| --- | --- |
| `godot/project.godot` | Project settings: 1024x768 viewport, `Global` autoload, main scene |
| `godot/scenes/` | `main_menu.tscn`, `game.tscn`: only a root node with a script |
| `godot/scripts/global.gd` | Autoload `Global`: settings, input actions, sounds, music, rumble |
| `godot/scripts/main_menu.gd` | Main menu, level select |
| `godot/scripts/game.gd` | A run of the game: level data (`LEVELS`), rules, HUD, pause and end screens |
| `godot/scripts/spawner.gd` | Spawns entities at randomized intervals |
| `godot/scripts/entities/` | `Entity` -> `Mob` (player, fish, shield), `Bullet` (rockets, mines), bubbles, explosion, medicine chest |
| `godot/scripts/ui/` | `MenuPanel` (all menus and dialogs), `TouchControls` (phones) |
| `godot/assets/` | Images and sounds |
| `tools/GenSounds.java` | Synthesizes every sound and the music: `java tools/GenSounds.java` |
| `src/`, `resources/` | Original Java version and its art |

## How the game works

- Entities do not process on their own. `game.gd` calls `tick()` on each one
  60 times a second from `_physics_process` and resolves collisions with plain
  rectangles (`Entity.rect()`); there are no physics bodies or areas.
- Speeds, cooldowns and intervals are therefore in ticks and pixels per tick.
- Entities live in three layers: back, main (gameplay) and pop (in front).
- The UI is built in code, not in scenes. Text shown to the player is in
  Ukrainian; code, comments and commit messages are in English.
- Input goes through actions registered in `global.gd` (`move_*`, `fire`,
  `shield`, `pause`) for keyboard, gamepad and touch alike.

## Conventions

- Commit the `.import` and `.uid` files Godot generates next to assets and
  scripts; `godot/.godot/` is ignored.
- Sounds are generated, not hand-edited: change `tools/GenSounds.java`, rerun
  it, then run the `--import` command above.
- There is no test suite. To check a change, run the game headless as shown
  above and look for `SCRIPT ERROR` in the output, then play it.
- On exit Godot prints `1 resources still in use at exit`. It is a known,
  harmless message.
