extends Control
## Main menu: new game, level select, settings, quit.

const BACKGROUND := preload("res://assets/images/background.jpg")
const BUBBLE := preload("res://assets/images/bubble_middle.png")
const LEVEL_COUNT := 3

var _main: MenuPanel
var _levels: MenuPanel
var _settings: MenuPanel


func _ready() -> void:
	get_tree().paused = false

	var background := TextureRect.new()
	background.texture = BACKGROUND
	background.expand_mode = TextureRect.EXPAND_IGNORE_SIZE
	background.stretch_mode = TextureRect.STRETCH_SCALE
	background.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	add_child(background)
	add_child(_make_bubbles())

	var header := VBoxContainer.new()
	header.set_anchors_and_offsets_preset(Control.PRESET_TOP_WIDE)
	header.offset_top = 30
	header.add_child(MenuPanel.make_label("Kingdom", 84, Color(0.75, 0.95, 1.0)))
	header.add_child(MenuPanel.make_label("Захисти рибне королівство від хижаків", 22))
	add_child(header)

	var hint := MenuPanel.make_label(
			"WASD / стрілки — рух     Ctrl — ракета     Пробіл — щит     Esc — пауза", 18)
	hint.set_anchors_and_offsets_preset(Control.PRESET_BOTTOM_WIDE)
	hint.offset_top = -48
	hint.offset_bottom = -18
	add_child(hint)

	_main = MenuPanel.new()
	_main.add_button("Нова гра", _start.bind(0))
	_main.add_button("Вибір рівня", _show.bind("levels"))
	_main.add_button("Налаштування", _show.bind("settings"))
	_main.add_button("Вихід", get_tree().quit)

	_levels = MenuPanel.new("Вибір рівня")
	for i in LEVEL_COUNT:
		_levels.add_button("Рівень %d" % (i + 1), _start.bind(i))
	_levels.add_button("Назад", _show.bind("main"))

	_settings = MenuPanel.settings(_show.bind("main"))

	for panel: MenuPanel in [_main, _levels, _settings]:
		# Keep the panels clear of the title.
		panel.offset_top = 150
		add_child(panel)
	_show("main")


func _unhandled_input(event: InputEvent) -> void:
	if event.is_action_pressed("ui_cancel") and not _main.visible:
		Global.save_settings()
		_show("main")


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
	bubbles.position = Vector2(512, 800)
	bubbles.amount = 40
	bubbles.lifetime = 9.0
	bubbles.preprocess = 9.0
	bubbles.emission_shape = CPUParticles2D.EMISSION_SHAPE_RECTANGLE
	bubbles.emission_rect_extents = Vector2(512, 10)
	bubbles.direction = Vector2.UP
	bubbles.spread = 6.0
	bubbles.gravity = Vector2.ZERO
	bubbles.initial_velocity_min = 70.0
	bubbles.initial_velocity_max = 130.0
	bubbles.scale_amount_min = 0.5
	bubbles.scale_amount_max = 2.0
	return bubbles
