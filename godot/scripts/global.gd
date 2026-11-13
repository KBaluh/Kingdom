extends Node
## Autoload "Global": user settings, input map, shared sound effects and
## the level a new run should start from.

const SETTINGS_PATH := "user://settings.cfg"
const MENU_SCENE := "res://scenes/main_menu.tscn"
const GAME_SCENE := "res://scenes/game.tscn"
const EXPLOSION_VARIANTS := 4
## How far a stick must be pushed before the fish starts to move.
const STICK_DEADZONE := 0.4
const ALL_DEVICES := -1

var volume := 0.8
var fullscreen := false
var start_level := 0

var _rocket_boom: AudioStreamPlayer
var _mine_boom: AudioStreamPlayer


func _ready() -> void:
	process_mode = Node.PROCESS_MODE_ALWAYS
	_register_actions()
	_load_settings()
	apply_settings()
	_rocket_boom = _make_boom_player(1.0, -4.0)
	_mine_boom = _make_boom_player(0.7, -1.0)


## Plays a random rocket explosion; `heavy` gives the deeper sea-mine blast.
func play_explosion(heavy: bool = false) -> void:
	if heavy:
		_mine_boom.play()
	else:
		_rocket_boom.play()


func set_volume(value: float) -> void:
	volume = clampf(value, 0.0, 1.0)
	apply_settings()


func set_fullscreen(enabled: bool) -> void:
	fullscreen = enabled
	apply_settings()


func toggle_fullscreen() -> void:
	set_fullscreen(not fullscreen)


## Lets the player hear the new volume after releasing the slider.
func preview_volume(_value_changed: bool = true) -> void:
	play_explosion()


func apply_settings() -> void:
	var bus := AudioServer.get_bus_index("Master")
	AudioServer.set_bus_mute(bus, volume <= 0.001)
	AudioServer.set_bus_volume_db(bus, linear_to_db(maxf(volume, 0.001)))

	var mode := DisplayServer.WINDOW_MODE_FULLSCREEN if fullscreen else DisplayServer.WINDOW_MODE_WINDOWED
	if DisplayServer.window_get_mode() != mode:
		DisplayServer.window_set_mode(mode)


func save_settings() -> void:
	var config := ConfigFile.new()
	config.set_value("audio", "volume", volume)
	config.set_value("video", "fullscreen", fullscreen)
	config.save(SETTINGS_PATH)


func _load_settings() -> void:
	var config := ConfigFile.new()
	if config.load(SETTINGS_PATH) != OK:
		return
	volume = clampf(float(config.get_value("audio", "volume", volume)), 0.0, 1.0)
	fullscreen = bool(config.get_value("video", "fullscreen", fullscreen))


func _make_boom_player(pitch: float, volume_db: float) -> AudioStreamPlayer:
	var variants := AudioStreamRandomizer.new()
	for i in EXPLOSION_VARIANTS:
		var stream: AudioStream = load("res://assets/sounds/rocket_explosion%d.wav" % (i + 1))
		variants.add_stream(-1, stream)
	variants.random_pitch = 1.12
	variants.random_volume_offset_db = 2.0

	var player := AudioStreamPlayer.new()
	player.stream = variants
	player.max_polyphony = 6
	player.pitch_scale = pitch
	player.volume_db = volume_db
	add_child(player)
	return player


func _register_actions() -> void:
	# Keyboard keys, gamepad buttons, gamepad [axis, direction] pairs.
	_add_action("move_left", [KEY_A, KEY_LEFT], [JOY_BUTTON_DPAD_LEFT], [[JOY_AXIS_LEFT_X, -1.0]])
	_add_action("move_right", [KEY_D, KEY_RIGHT], [JOY_BUTTON_DPAD_RIGHT], [[JOY_AXIS_LEFT_X, 1.0]])
	_add_action("move_up", [KEY_W, KEY_UP], [JOY_BUTTON_DPAD_UP], [[JOY_AXIS_LEFT_Y, -1.0]])
	_add_action("move_down", [KEY_S, KEY_DOWN], [JOY_BUTTON_DPAD_DOWN], [[JOY_AXIS_LEFT_Y, 1.0]])
	_add_action("fire", [KEY_CTRL, KEY_J], [JOY_BUTTON_A], [[JOY_AXIS_TRIGGER_RIGHT, 1.0]])
	_add_action("shield", [KEY_SPACE], [JOY_BUTTON_X, JOY_BUTTON_LEFT_SHOULDER])
	_add_action("pause", [KEY_ESCAPE, KEY_P], [JOY_BUTTON_START])
	# Menus: the built-in actions already navigate with the d-pad and stick,
	# but have no gamepad buttons for confirm and back.
	_add_action("ui_accept", [], [JOY_BUTTON_A])
	_add_action("ui_cancel", [], [JOY_BUTTON_B])


## True when at least one gamepad is plugged in.
func has_gamepad() -> bool:
	return not Input.get_connected_joypads().is_empty()


func _add_action(action: StringName, keys: Array, joy_buttons: Array = [], joy_axes: Array = []) -> void:
	if not InputMap.has_action(action):
		InputMap.add_action(action, STICK_DEADZONE)
	for key in keys:
		var event := InputEventKey.new()
		event.physical_keycode = key
		InputMap.action_add_event(action, event)
	for button in joy_buttons:
		var event := InputEventJoypadButton.new()
		event.device = ALL_DEVICES
		event.button_index = button
		if not InputMap.action_has_event(action, event):
			InputMap.action_add_event(action, event)
	for axis in joy_axes:
		var event := InputEventJoypadMotion.new()
		event.device = ALL_DEVICES
		event.axis = axis[0]
		event.axis_value = axis[1]
		InputMap.action_add_event(action, event)
