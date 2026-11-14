extends Node2D
## A run of the game. Owns the three entity layers, ticks them at 60 Hz,
## applies the level rules (battles, kills, missed fish) and shows the HUD,
## the pause menu and the end-of-level screens.

enum State { PLAYING, PAUSED, ENDED }

const MAX_BATTLES := 3
const MAX_SKIPPED := 10
const BATTLE_TEXT_TICKS := 120

## Spawners are [kind, base interval in ticks].
const LEVELS := [
	{
		"background": preload("res://assets/images/background.jpg"),
		"max_hp": 100.0,
		"first_kills": 5,
		"spawners": [["bubble", 20], ["medicine", 1500], ["hunter", 110]],
	},
	{
		"background": preload("res://assets/images/background2.jpg"),
		"max_hp": 150.0,
		"first_kills": 10,
		"spawners": [
			["bubble", 20], ["medicine", 900], ["hunter", 110], ["green", 400],
			["sea_mine", 250], ["top_fish", 900],
		],
	},
	{
		"background": preload("res://assets/images/background3.jpg"),
		"max_hp": 180.0,
		"first_kills": 12,
		"spawners": [
			["bubble", 20], ["medicine", 850], ["green", 420], ["green", 140],
			["sea_mine", 200], ["top_fish_mine", 900],
		],
	},
	# The boss fight: no battles and no fish to miss, only the squid.
	{
		"background": preload("res://assets/images/background3.jpg"),
		"background_tint": Color(0.5, 0.55, 0.8),
		"max_hp": 200.0,
		"first_kills": 1,
		"boss": true,
		"spawners": [["bubble", 20], ["medicine", 650]],
	},
]

var player: Player
## The squid on the boss level, null on the others.
var boss: Squid
var level_index := 0
var battle := 1
var kills := 0
var kills_needed := 5
var skipped := 0
## Total for the whole run, carried from level to level.
var score := 0

var _state := State.PLAYING
var _score_at_level_start := 0
## Kills needed for the following battles: doubles after every battle won.
var _kills_step := 5
var _spawners: Array[Spawner] = []
var _battle_text_ticks := 0

var _background: Sprite2D
var _back: Node2D
var _main: Node2D
var _pop: Node2D

var _info: Label
var _hp_bar: ProgressBar
var _hp_fill: StyleBoxFlat
var _hp_text: Label
var _shield_bar: ProgressBar
var _battle_label: Label
var _boss_box: HBoxContainer
var _boss_bar: ProgressBar
var _hud: CanvasLayer
## Covers whatever a screen wider or taller than the play field shows around it.
var _frame: Array[ColorRect] = []
## On-screen controls; null when not playing on a touch screen.
var _touch: TouchControls

var _overlay: CanvasLayer
var _shade: ColorRect
var _pause_panel: MenuPanel
var _settings_panel: MenuPanel
var _end_panel: MenuPanel


func _ready() -> void:
	# Keeps receiving input while the tree is paused, to be able to unpause.
	process_mode = Node.PROCESS_MODE_ALWAYS

	_background = Sprite2D.new()
	_background.centered = false
	add_child(_background)
	_back = _add_layer()
	_main = _add_layer()
	_pop = _add_layer()
	_build_frame()
	_build_hud()
	_build_touch_controls()
	_build_overlay()
	get_viewport().size_changed.connect(_fit_screen)
	_fit_screen()
	_start_level(clampi(Global.start_level, 0, LEVELS.size() - 1))


func _physics_process(_delta: float) -> void:
	if _state == State.PLAYING:
		_tick()


func _unhandled_input(event: InputEvent) -> void:
	var pause_pressed := event.is_action_pressed("pause")
	# In the pause menu the gamepad's B button (ui_cancel) also goes back.
	var back_pressed := _state == State.PAUSED and event.is_action_pressed("ui_cancel")
	if (pause_pressed or back_pressed) and _go_back():
		get_viewport().set_input_as_handled()


func _notification(what: int) -> void:
	if not is_node_ready():
		return
	match what:
		# The back button on Android.
		NOTIFICATION_WM_GO_BACK_REQUEST:
			if not _go_back():
				_to_menu()
		# The player switched to another app or locked the phone.
		NOTIFICATION_APPLICATION_PAUSED, NOTIFICATION_APPLICATION_FOCUS_OUT:
			if _state == State.PLAYING and Global.is_touch():
				_pause()


## Pauses the game, or steps one screen back in the pause menu.
## False on the end-of-level screens, where there is nothing to go back to.
func _go_back() -> bool:
	if _state == State.PLAYING:
		_pause()
	elif _state == State.PAUSED:
		if _settings_panel.visible:
			Global.save_settings()
			_show_panel(_pause_panel)
		else:
			_resume()
	else:
		return false
	return true


# --- Entity layers (called by entities and spawners) ---

func add_back(entity: Entity) -> void:
	_add_entity(_back, entity)


func add_main(entity: Entity) -> void:
	_add_entity(_main, entity)


func add_pop(entity: Entity) -> void:
	_add_entity(_pop, entity)


func _add_entity(layer: Node2D, entity: Entity) -> void:
	entity.level = self
	layer.add_child(entity)


func _add_layer() -> Node2D:
	var layer := Node2D.new()
	add_child(layer)
	return layer


# --- Level flow ---

func _start_level(index: int) -> void:
	level_index = index
	_score_at_level_start = score

	for layer: Node2D in [_back, _main, _pop]:
		for node in layer.get_children():
			(node as Entity).remove()

	var def: Dictionary = LEVELS[index]
	_background.texture = def["background"]
	_background.modulate = def.get("background_tint", Color.WHITE)
	battle = 1
	kills = 0
	skipped = 0
	kills_needed = def["first_kills"]
	_kills_step = 5

	_spawners.clear()
	for spawner: Array in def["spawners"]:
		_spawners.append(Spawner.new(spawner[0], spawner[1]))

	player = Player.new(Vector2(100, 200), def["max_hp"])
	add_main(player)

	boss = null
	if def.get("boss", false):
		boss = Squid.new()
		add_main(boss)
	_boss_box.visible = boss != null

	_show_battle_text()
	_set_state(State.PLAYING)
	_update_hud()


func _tick() -> void:
	if skipped >= MAX_SKIPPED or player.hp <= 0.0:
		_game_over()
		return

	if _battle_text_ticks > 0:
		_battle_text_ticks -= 1
		if _battle_text_ticks == 0:
			_battle_label.text = ""

	if boss != null:
		if boss.defeated:
			score += Squid.SCORE
			_level_done()
			return
	elif kills >= kills_needed:
		_battle_won()
		if battle > MAX_BATTLES:
			_level_done()
			return

	for spawner in _spawners:
		spawner.tick(self)

	_tick_layer(_back, false)
	_tick_layer(_main, true)
	_tick_layer(_pop, false)
	Global.set_rockets_flying(_rockets_flying())
	_update_hud()


func _battle_won() -> void:
	battle += 1
	if battle > MAX_BATTLES:
		return
	_kills_step *= 2
	kills_needed = _kills_step
	kills = 0
	skipped = 0
	player.heal(player.max_hp)
	# Hunters come faster with every battle.
	for spawner in _spawners:
		if spawner.kind == "hunter":
			spawner.base_interval -= 20
	_show_battle_text()


func _tick_layer(layer: Node2D, gameplay: bool) -> void:
	for node in layer.get_children():
		var entity := node as Entity
		if entity == null or entity.removed:
			continue
		entity.tick()
		if entity.removed or _left_screen(entity) or not gameplay:
			continue
		if entity is Bullet:
			_hit_mobs(entity as Bullet)
		elif entity is MedicineChest and entity.collides(player):
			player.heal(MedicineChest.BONUS)
			entity.remove()


## The player's rockets currently on screen, for the in-flight sound.
## Enemy rockets are left out: there is almost always one in the water,
## which would turn the loop into constant background noise.
func _rockets_flying() -> int:
	var count := 0
	for node in _main.get_children():
		var bullet := node as Bullet
		if bullet == null or bullet.removed or not bullet.leaves_trail:
			continue
		if bullet.team != Entity.TEAM_PLAYER:
			continue
		if bullet.position.x < Entity.SCREEN.x:
			count += 1
	return count


## Removes entities that swam off the screen; a fish that gets away is "missed".
func _left_screen(entity: Entity) -> bool:
	if entity is Player or entity is Shield or entity is Squid:
		return false
	# The sprite, not the hitbox: a sea mine's hitbox is smaller than its
	# sprite, so at its spawn point on the bottom edge it is already "below".
	var r := Rect2(entity.position, entity.size())
	var screen := Entity.SCREEN
	var inside := r.position.x >= -r.size.x and r.position.x <= screen.x + r.size.x \
			and r.position.y >= -r.size.y and r.position.y <= screen.y
	if inside:
		return false
	if entity is Mob:
		skipped += 1
	entity.remove()
	return true


func _hit_mobs(bullet: Bullet) -> void:
	for node in _main.get_children():
		var mob := node as Mob
		if mob == null or mob.removed or not mob.alive:
			continue
		if mob.team == bullet.team or not mob.collides(bullet):
			continue
		mob.hurt(bullet.damage)
		bullet.hit()
		bullet.remove()
		if not mob.alive and mob.counts_as_kill:
			score += mob.score_value()
			kills += 1
			mob.remove()
		return


func _game_over() -> void:
	var reason := "Вашу рибку знищено" if player.hp <= 0.0 else "Забагато риб прорвалося в королівство"
	_show_end("Гру закінчено", [reason, "Рахунок: %d" % score], [
		["Спробувати ще раз", _restart_level],
		["Головне меню", _to_menu],
	])


func _level_done() -> void:
	if level_index + 1 < LEVELS.size():
		_show_end("Рівень %d пройдено!" % (level_index + 1), ["Рахунок: %d" % score], [
			["Наступний рівень", _start_level.bind(level_index + 1)],
			["Головне меню", _to_menu],
		])
	else:
		_show_end("Перемога!", ["Королівство врятовано", "Рахунок: %d" % score], [
			["Грати знову", _new_run],
			["Головне меню", _to_menu],
		])


func _restart_level() -> void:
	score = _score_at_level_start
	_start_level(level_index)


func _new_run() -> void:
	score = 0
	_start_level(0)


func _to_menu() -> void:
	get_tree().paused = false
	get_tree().change_scene_to_file(Global.MENU_SCENE)


func _pause() -> void:
	_set_state(State.PAUSED)
	_show_panel(_pause_panel)


func _resume() -> void:
	_set_state(State.PLAYING)


func _set_state(state: State) -> void:
	_state = state
	var playing := state == State.PLAYING
	get_tree().paused = not playing
	_shade.visible = not playing
	if _touch != null:
		_touch.visible = playing
	if not playing:
		Global.set_rockets_flying(0)
	if playing:
		_show_panel(null)


# --- HUD ---

# --- Screen fit ---

func _build_frame() -> void:
	var layer := CanvasLayer.new()
	add_child(layer)
	for i in 4:
		var bar := ColorRect.new()
		bar.color = Color(0, 0.02, 0.05)
		layer.add_child(bar)
		_frame.append(bar)


func _build_touch_controls() -> void:
	if not Global.is_touch():
		return
	var layer := CanvasLayer.new()
	layer.layer = 3
	add_child(layer)
	_touch = TouchControls.new()
	_touch.pause_pressed.connect(_go_back)
	layer.add_child(_touch)


## Centers the 4:3 play field on the screen and frames it. On a wide phone
## the bars at the sides are where the thumbs and the touch controls go.
func _fit_screen() -> void:
	var screen := get_viewport().get_visible_rect().size
	var field := Entity.SCREEN
	var origin := ((screen - field) / 2).max(Vector2.ZERO).floor()
	position = origin
	_hud.offset = origin

	var right := origin.x + field.x
	var bottom := origin.y + field.y
	var bars: Array[Rect2] = [
		Rect2(0, 0, origin.x, screen.y),
		Rect2(right, 0, screen.x - right, screen.y),
		Rect2(origin.x, 0, field.x, origin.y),
		Rect2(origin.x, bottom, field.x, screen.y - bottom),
	]
	for i in 4:
		_frame[i].position = bars[i].position
		_frame[i].size = bars[i].size.max(Vector2.ZERO)


# --- HUD ---

func _build_hud() -> void:
	_hud = CanvasLayer.new()
	_hud.layer = 2
	add_child(_hud)
	var hud := _hud
	# A phone screen is small: the same text has to be bigger to be read.
	var font_size := 20 if Global.is_touch() else 16

	_info = MenuPanel.make_label("", font_size)
	_info.position = Vector2(10, 6)
	hud.add_child(_info)

	var bars := HBoxContainer.new()
	bars.position = Vector2(10, 14 + font_size * 1.25)
	bars.add_theme_constant_override("separation", 8)
	hud.add_child(bars)

	bars.add_child(MenuPanel.make_label("Життя", font_size))
	_hp_fill = MenuPanel.flat_box(Color(0.3, 0.9, 0.4), Color.TRANSPARENT, 5, 0)
	_hp_bar = _make_bar(220, _hp_fill)
	bars.add_child(_hp_bar)
	_hp_text = MenuPanel.make_label("", font_size)
	_hp_text.custom_minimum_size.x = 44
	bars.add_child(_hp_text)

	bars.add_child(MenuPanel.make_label("Щит", font_size))
	_shield_bar = _make_bar(120, MenuPanel.flat_box(Color(0.45, 0.8, 1.0), Color.TRANSPARENT, 5, 0))
	_shield_bar.max_value = Player.SHIELD_COOLDOWN
	bars.add_child(_shield_bar)

	_battle_label = MenuPanel.make_label("", 40)
	# As wide as the play field, not the screen: the HUD layer moves with the field.
	_battle_label.position = Vector2(0, 170)
	_battle_label.size.x = Entity.SCREEN.x
	hud.add_child(_battle_label)

	# The boss's health, along the bottom of the play field.
	_boss_box = HBoxContainer.new()
	_boss_box.position = Vector2(170, Entity.SCREEN.y - 34)
	_boss_box.add_theme_constant_override("separation", 8)
	hud.add_child(_boss_box)
	_boss_box.add_child(MenuPanel.make_label("Кальмар", font_size))
	_boss_bar = _make_bar(580, MenuPanel.flat_box(Color(0.85, 0.3, 0.5), Color.TRANSPARENT, 5, 0))
	_boss_bar.max_value = Squid.HP
	_boss_box.add_child(_boss_bar)


func _make_bar(width: float, fill: StyleBoxFlat) -> ProgressBar:
	var bar := ProgressBar.new()
	bar.show_percentage = false
	bar.custom_minimum_size = Vector2(width, 16)
	bar.size_flags_vertical = Control.SIZE_SHRINK_CENTER
	bar.add_theme_stylebox_override("background",
			MenuPanel.flat_box(Color(0, 0.05, 0.1, 0.6), Color(1, 1, 1, 0.35), 5, 0))
	bar.add_theme_stylebox_override("fill", fill)
	return bar


func _update_hud() -> void:
	if boss != null:
		_info.text = "Рівень %d    Бос    Рахунок: %d" % [level_index + 1, score]
		_boss_bar.value = boss.hp
	else:
		_info.text = "Рівень %d    Бій %d/%d    Пропущено: %d/%d    Знищено: %d/%d    Рахунок: %d" % [
			level_index + 1, mini(battle, MAX_BATTLES), MAX_BATTLES,
			skipped, MAX_SKIPPED, kills, kills_needed, score,
		]
	_hp_bar.max_value = player.max_hp
	_hp_bar.value = player.hp
	_hp_text.text = str(int(player.hp))
	var ratio := player.hp / player.max_hp
	if ratio <= 0.3:
		_hp_fill.bg_color = Color.RED
	elif ratio <= 0.8:
		_hp_fill.bg_color = Color.YELLOW
	else:
		_hp_fill.bg_color = Color(0.3, 0.9, 0.4)
	_shield_bar.value = player.shield_delay
	if _touch != null:
		_touch.shield_charge = float(player.shield_delay) / Player.SHIELD_COOLDOWN


func _show_battle_text() -> void:
	_battle_text_ticks = BATTLE_TEXT_TICKS
	if boss != null:
		_battle_label.text = "Рівень %d: бос!" % (level_index + 1)
	else:
		_battle_label.text = "Рівень %d, бій %d" % [level_index + 1, battle]


# --- Pause menu and end screens ---

func _build_overlay() -> void:
	_overlay = CanvasLayer.new()
	_overlay.layer = 10
	add_child(_overlay)

	_shade = ColorRect.new()
	_shade.color = Color(0, 0.03, 0.08, 0.55)
	_shade.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	_overlay.add_child(_shade)

	_pause_panel = MenuPanel.new("Пауза")
	_pause_panel.add_button("Продовжити", _resume)
	_pause_panel.add_button("Почати рівень заново", _restart_level)
	_pause_panel.add_button("Налаштування", _open_settings)
	_pause_panel.add_button("Головне меню", _to_menu)
	_overlay.add_child(_pause_panel)

	_settings_panel = MenuPanel.settings(_pause)
	_overlay.add_child(_settings_panel)

	_show_panel(null)


func _open_settings() -> void:
	_show_panel(_settings_panel)


## Shows one overlay panel and hides the others; null hides them all.
func _show_panel(panel: MenuPanel) -> void:
	for other in [_pause_panel, _settings_panel, _end_panel]:
		if other != null:
			other.visible = false
	if panel != null:
		panel.open()


## `buttons` is a list of [text, Callable].
func _show_end(title: String, lines: Array, buttons: Array) -> void:
	if _end_panel != null:
		_end_panel.queue_free()
	_end_panel = MenuPanel.new(title)
	for line: String in lines:
		_end_panel.add_label(line)
	for button: Array in buttons:
		_end_panel.add_button(button[0], button[1])
	_overlay.add_child(_end_panel)
	_set_state(State.ENDED)
	_show_panel(_end_panel)
