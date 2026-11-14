extends Control
## Main menu: new game, level select, settings, quit.

const BACKGROUND := preload("res://assets/images/background.jpg")
const BUBBLE := preload("res://assets/images/bubble_middle.png")
const LEVEL_COUNT := 3

var _main: MenuPanel
var _levels: MenuPanel
var _settings: MenuPanel
var _hint: Label
var _bubbles: CPUParticles2D


func _ready() -> void:
	get_tree().paused = false

	var background := TextureRect.new()
	background.texture = BACKGROUND
	background.expand_mode = TextureRect.EXPAND_IGNORE_SIZE
	background.stretch_mode = TextureRect.STRETCH_KEEP_ASPECT_COVERED
	background.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	add_child(background)
	_bubbles = _make_bubbles()
	add_child(_bubbles)
	_place_bubbles()
	resized.connect(_place_bubbles)

	var header := VBoxContainer.new()
	header.set_anchors_and_offsets_preset(Control.PRESET_TOP_WIDE)
	header.offset_top = 30
	header.add_child(MenuPanel.make_label("Kingdom", 84, Color(0.75, 0.95, 1.0)))
	header.add_child(MenuPanel.make_label("Захисти рибне королівство від хижаків", 22))
	add_child(header)

	_hint = MenuPanel.make_label("", 18)
	_hint.set_anchors_and_offsets_preset(Control.PRESET_BOTTOM_WIDE)
	_hint.offset_top = -48
	_hint.offset_bottom = -18
	add_child(_hint)
	_update_hint()
	Input.joy_connection_changed.connect(_update_hint)

	_main = MenuPanel.new()
	_main.add_button("Нова гра", _start.bind(0))
	_main.add_button("Вибір рівня", _show.bind("levels"))
	_main.add_button("Налаштування", _show.bind("settings"))
	# Phone apps are left with the home button, not closed from a menu.
	if not Global.is_touch():
		_main.add_button("Вихід", get_tree().quit)

	_levels = MenuPanel.new("Вибір рівня")
	for i in LEVEL_COUNT:
		_levels.add_button("Рівень %d" % (i + 1), _start.bind(i))
	_levels.add_button("Назад", _show.bind("main"))

	_settings = MenuPanel.settings(_show.bind("main"))

	for panel: MenuPanel in [_main, _levels, _settings]:
		# Keep the panels clear of the title and, on phones, of the hint.
		panel.offset_top = 150
		if Global.is_touch():
			panel.offset_bottom = -40
		add_child(panel)
	_show("main")


func _unhandled_input(event: InputEvent) -> void:
	if event.is_action_pressed("ui_cancel") and not _main.visible:
		Global.save_settings()
		_show("main")


func _notification(what: int) -> void:
	# The back button on Android: back to the main panel, or out of the game.
	if what == NOTIFICATION_WM_GO_BACK_REQUEST:
		if _main.visible:
			get_tree().quit()
		else:
			Global.save_settings()
			_show("main")


## Shows gamepad controls while a gamepad is connected, otherwise the touch
## or keyboard ones.
func _update_hint(_device: int = 0, _connected: bool = false) -> void:
	if Global.has_gamepad():
		_hint.text = "Стік / хрестовина — рух     A / RT — ракета     X / LB — щит     Start — пауза"
	elif Global.is_touch():
		_hint.text = "Ліва половина екрана — рух     кнопки праворуч — ракета і щит"
	else:
		_hint.text = "WASD / стрілки — рух     Ctrl — ракета     Пробіл — щит     Esc — пауза"


func _show(panel_name: String) -> void:
	var panels := {"main": _main, "levels": _levels, "settings": _settings}
	for key: String in panels:
		panels[key].visible = false
	panels[panel_name].open()


func _start(level: int) -> void:
	Global.start_level = level
	get_tree().change_scene_to_file(Global.GAME_SCENE)


func _make_bubbles() -> CPUParticles2D:
	var bubbles := CPUParticles2D.new()
	bubbles.texture = BUBBLE
	bubbles.amount = 40
	bubbles.lifetime = 9.0
	bubbles.preprocess = 9.0
	bubbles.emission_shape = CPUParticles2D.EMISSION_SHAPE_RECTANGLE
	bubbles.direction = Vector2.UP
	bubbles.spread = 6.0
	bubbles.gravity = Vector2.ZERO
	bubbles.initial_velocity_min = 70.0
	bubbles.initial_velocity_max = 130.0
	bubbles.scale_amount_min = 0.5
	bubbles.scale_amount_max = 2.0
	return bubbles


## Bubbles rise from under the whole bottom edge, however wide the screen is.
func _place_bubbles() -> void:
	_bubbles.position = Vector2(size.x / 2, size.y + 32)
	_bubbles.emission_rect_extents = Vector2(size.x / 2, 10)
