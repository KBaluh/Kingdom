class_name MenuPanel
extends Control
## A centered, titled panel with a vertical stack of labels and buttons.
## Shared by the main menu, the pause menu and the end-of-game screens.

const BUTTON_SIZE := Vector2(340, 54)
## Taller buttons and sliders where they are pressed with a finger.
const TOUCH_BUTTON_HEIGHT := 70.0
const TOUCH_SLIDER_HEIGHT := 48.0

var box: VBoxContainer
var _first_focus: Control


func _init(title: String = "") -> void:
	set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)

	var center := CenterContainer.new()
	center.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	add_child(center)

	var panel := PanelContainer.new()
	panel.add_theme_stylebox_override("panel",
			flat_box(Color(0.02, 0.1, 0.2, 0.86), Color(0.35, 0.75, 0.95, 0.45), 16, 22))
	center.add_child(panel)

	box = VBoxContainer.new()
	box.add_theme_constant_override("separation", 12)
	panel.add_child(box)

	if title != "":
		add_label(title, 34)


## Shows the panel and gives keyboard focus to its first button.
func open() -> void:
	visible = true
	if _first_focus != null:
		_first_focus.grab_focus.call_deferred()


func add_label(text: String, font_size: int = 22, color: Color = Color.WHITE) -> Label:
	var label := make_label(text, font_size, color)
	box.add_child(label)
	return label


func add_button(text: String, action: Callable) -> Button:
	var button := Button.new()
	button.text = text
	button.custom_minimum_size = BUTTON_SIZE
	if Global.is_touch():
		button.custom_minimum_size.y = TOUCH_BUTTON_HEIGHT
	button.add_theme_font_size_override("font_size", 22)
	button.add_theme_stylebox_override("normal",
			flat_box(Color(0.06, 0.27, 0.42, 0.95), Color(0.35, 0.75, 0.95, 0.5)))
	button.add_theme_stylebox_override("hover",
			flat_box(Color(0.1, 0.4, 0.58, 1.0), Color(0.55, 0.9, 1.0, 0.9)))
	button.add_theme_stylebox_override("pressed",
			flat_box(Color(0.04, 0.2, 0.32, 1.0), Color(0.55, 0.9, 1.0, 0.9)))
	button.add_theme_stylebox_override("focus",
			flat_box(Color(0, 0, 0, 0), Color(1, 1, 1, 0.9)))
	button.pressed.connect(action)
	box.add_child(button)
	if _first_focus == null:
		_first_focus = button
	return button


## Adds a titled 0..1 slider; `on_change` receives the new value.
func add_slider(title: String, value: float, on_change: Callable) -> HSlider:
	add_label(title, 20)
	var slider := HSlider.new()
	slider.min_value = 0.0
	slider.max_value = 1.0
	slider.step = 0.05
	slider.value = value
	slider.custom_minimum_size = Vector2(BUTTON_SIZE.x, TOUCH_SLIDER_HEIGHT if Global.is_touch() else 28.0)
	slider.value_changed.connect(on_change)
	box.add_child(slider)
	return slider


## Volume, fullscreen and vibration panel; `on_back` is called after the settings are saved.
static func settings(on_back: Callable) -> MenuPanel:
	var panel := MenuPanel.new("Налаштування")

	var volume := panel.add_slider("Загальна гучність", Global.volume, Global.set_volume)
	volume.drag_ended.connect(Global.preview_volume)
	panel.add_slider("Музика", Global.music_volume, Global.set_music_volume)

	if not Global.is_touch():
		var fullscreen := panel.add_button(_fullscreen_text(), Global.toggle_fullscreen)
		fullscreen.pressed.connect(func() -> void: fullscreen.text = _fullscreen_text())

	var vibration := panel.add_button(_vibration_text(), Global.toggle_vibration)
	vibration.pressed.connect(func() -> void: vibration.text = _vibration_text())

	var back := panel.add_button("Назад", Global.save_settings)
	back.pressed.connect(on_back)
	return panel


static func _fullscreen_text() -> String:
	return "Повний екран: %s" % ("увімк." if Global.fullscreen else "вимк.")


static func _vibration_text() -> String:
	var title := "Вібрація" if Global.is_touch() else "Вібрація геймпада"
	return "%s: %s" % [title, "увімк." if Global.vibration else "вимк."]


static func make_label(text: String, font_size: int = 22, color: Color = Color.WHITE) -> Label:
	var label := Label.new()
	label.text = text
	label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	label.add_theme_font_size_override("font_size", font_size)
	label.add_theme_color_override("font_color", color)
	label.add_theme_color_override("font_outline_color", Color(0, 0.05, 0.1, 0.8))
	label.add_theme_constant_override("outline_size", maxi(4, font_size / 6))
	return label


static func flat_box(fill: Color, border: Color = Color.TRANSPARENT,
		radius: int = 10, margin: int = 10) -> StyleBoxFlat:
	var style := StyleBoxFlat.new()
	style.bg_color = fill
	style.set_corner_radius_all(radius)
	style.set_content_margin_all(margin)
	if border.a > 0.0:
		style.border_color = border
		style.set_border_width_all(2)
	return style
