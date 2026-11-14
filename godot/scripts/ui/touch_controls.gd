class_name TouchControls
extends Control
## On-screen controls for phones: a floating stick on the left half of the
## screen, rocket, volley and shield buttons on the right, pause in the top corner.
## They press the same input actions as the keyboard and the gamepad.

signal pause_pressed

enum Role { STICK, FIRE, SHIELD, VOLLEY }

const ACTIONS: Array[StringName] = [
	&"move_left", &"move_right", &"move_up", &"move_down", &"fire", &"shield",
	&"volley",
]
const STICK_RADIUS := 95.0
const KNOB_RADIUS := 42.0
## How far the knob must be dragged, as a share of the radius, to move the fish.
const STICK_THRESHOLD := 0.3
const FIRE_RADIUS := 84.0
const SHIELD_RADIUS := 62.0
const VOLLEY_RADIUS := 62.0
const PAUSE_RADIUS := 34.0
## Fingers are imprecise: buttons react a little outside their circle too.
const HIT_SLACK := 1.25
const ROCKET_ICON := preload("res://assets/images/RocketBulletRight.png")
const SHIELD_ICON := preload("res://assets/images/bubble_shield.png")
const TINT := Color(0.75, 0.95, 1.0)

## 0..1, how far the shield has recharged; shown as a ring around its button.
var shield_charge := 1.0:
	set(value):
		if not is_equal_approx(value, shield_charge):
			shield_charge = value
			queue_redraw()

## 0..1, how far the volley gun has reloaded; a ring around its button too.
var volley_charge := 1.0:
	set(value):
		if not is_equal_approx(value, volley_charge):
			volley_charge = value
			queue_redraw()

## Finger index -> Role.
var _fingers := {}
var _stick_origin := Vector2.ZERO
var _stick_offset := Vector2.ZERO


func _init() -> void:
	set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	mouse_filter = Control.MOUSE_FILTER_IGNORE
	# Stops with the game, so fingers on the pause menu do not steer the fish.
	process_mode = Node.PROCESS_MODE_PAUSABLE
	resized.connect(queue_redraw)
	visibility_changed.connect(release_all)


func _notification(what: int) -> void:
	if what == NOTIFICATION_PAUSED or what == NOTIFICATION_EXIT_TREE:
		release_all()


func _input(event: InputEvent) -> void:
	if not is_visible_in_tree():
		return
	if event is InputEventScreenTouch:
		if event.pressed:
			_touch_down(event.index, event.position)
		else:
			_touch_up(event.index)
	elif event is InputEventScreenDrag and _fingers.get(event.index) == Role.STICK:
		_move_stick(event.position)


## Lets go of everything; lifted fingers are not reported while the game is paused.
func release_all() -> void:
	_fingers.clear()
	_stick_offset = Vector2.ZERO
	for action: StringName in ACTIONS:
		Input.action_release(action)
	queue_redraw()


func _touch_down(index: int, at: Vector2) -> void:
	if at.distance_to(_pause_center()) <= PAUSE_RADIUS * HIT_SLACK * 1.3:
		pause_pressed.emit()
	elif at.distance_to(_fire_center()) <= FIRE_RADIUS * HIT_SLACK:
		_fingers[index] = Role.FIRE
		Input.action_press(&"fire")
	elif at.distance_to(_shield_center()) <= SHIELD_RADIUS * HIT_SLACK:
		_fingers[index] = Role.SHIELD
		Input.action_press(&"shield")
	elif at.distance_to(_volley_center()) <= VOLLEY_RADIUS * HIT_SLACK:
		_fingers[index] = Role.VOLLEY
		Input.action_press(&"volley")
	elif at.x < size.x * 0.5 and not _fingers.values().has(Role.STICK):
		_fingers[index] = Role.STICK
		_stick_origin = at
		_stick_offset = Vector2.ZERO
	queue_redraw()


func _touch_up(index: int) -> void:
	if not _fingers.has(index):
		return
	match _fingers[index]:
		Role.FIRE:
			Input.action_release(&"fire")
		Role.SHIELD:
			Input.action_release(&"shield")
		Role.VOLLEY:
			Input.action_release(&"volley")
		Role.STICK:
			_stick_offset = Vector2.ZERO
			_apply_stick()
	_fingers.erase(index)
	queue_redraw()


func _move_stick(at: Vector2) -> void:
	var offset := at - _stick_origin
	# Dragged past the rim: the stick follows the finger instead of losing it.
	if offset.length() > STICK_RADIUS:
		_stick_origin = at - offset.limit_length(STICK_RADIUS)
		offset = at - _stick_origin
	_stick_offset = offset
	_apply_stick()
	queue_redraw()


func _apply_stick() -> void:
	var push := _stick_offset / STICK_RADIUS
	_set_action(&"move_left", push.x < -STICK_THRESHOLD)
	_set_action(&"move_right", push.x > STICK_THRESHOLD)
	_set_action(&"move_up", push.y < -STICK_THRESHOLD)
	_set_action(&"move_down", push.y > STICK_THRESHOLD)


func _set_action(action: StringName, pressed: bool) -> void:
	if pressed == Input.is_action_pressed(action):
		return
	if pressed:
		Input.action_press(action)
	else:
		Input.action_release(action)


func _fire_center() -> Vector2:
	return size - Vector2(125, 125)


func _shield_center() -> Vector2:
	return size - Vector2(125, 310)


func _volley_center() -> Vector2:
	return size - Vector2(310, 140)


func _pause_center() -> Vector2:
	return Vector2(size.x - 60, 56)


func _stick_rest() -> Vector2:
	return Vector2(165, size.y - 165)


func _draw() -> void:
	var roles := _fingers.values()

	var stick_held := roles.has(Role.STICK)
	var origin := _stick_origin if stick_held else _stick_rest()
	_draw_disc(origin, STICK_RADIUS, stick_held, 0.5)
	draw_circle(origin + _stick_offset, KNOB_RADIUS, Color(TINT, 0.55 if stick_held else 0.3))

	_draw_disc(_fire_center(), FIRE_RADIUS, roles.has(Role.FIRE))
	_draw_icon(ROCKET_ICON, _fire_center(), FIRE_RADIUS * 1.2)

	var shield := _shield_center()
	_draw_disc(shield, SHIELD_RADIUS, roles.has(Role.SHIELD), 0.35)
	_draw_icon(SHIELD_ICON, shield, SHIELD_RADIUS * 1.3, 1.0 if shield_charge >= 1.0 else 0.35)
	draw_arc(shield, SHIELD_RADIUS, -PI / 2, -PI / 2 + TAU * shield_charge, 48,
			Color(0.45, 0.8, 1.0, 0.95), 5.0, true)

	var volley := _volley_center()
	var volley_alpha := 1.0 if volley_charge >= 1.0 else 0.35
	_draw_disc(volley, VOLLEY_RADIUS, roles.has(Role.VOLLEY), 0.35)
	# Three rockets fanning out: one for every fish.
	for turn: float in [-0.5, 0.0, 0.5]:
		draw_set_transform(volley, turn)
		_draw_icon(ROCKET_ICON, Vector2(12, 0), VOLLEY_RADIUS * 0.85, volley_alpha)
	draw_set_transform(Vector2.ZERO)
	draw_arc(volley, VOLLEY_RADIUS, -PI / 2, -PI / 2 + TAU * volley_charge, 48,
			Color(1.0, 0.6, 0.25, 0.95), 5.0, true)

	var pause := _pause_center()
	_draw_disc(pause, PAUSE_RADIUS, false)
	for x: float in [-13.0, 5.0]:
		draw_rect(Rect2(pause + Vector2(x, -13), Vector2(8, 26)), Color(1, 1, 1, 0.8))


func _draw_disc(center: Vector2, radius: float, held: bool, rim_alpha: float = 0.7) -> void:
	draw_circle(center, radius, Color(0.02, 0.1, 0.2, 0.55 if held else 0.3))
	if held:
		draw_circle(center, radius, Color(TINT, 0.25))
	draw_arc(center, radius, 0, TAU, 48, Color(TINT, rim_alpha), 3.0, true)


## Draws a texture centered and scaled to fit a square of `fit` pixels.
func _draw_icon(icon: Texture2D, center: Vector2, fit: float, alpha: float = 1.0) -> void:
	var icon_size := icon.get_size()
	icon_size *= fit / maxf(icon_size.x, icon_size.y)
	draw_texture_rect(icon, Rect2(center - icon_size / 2, icon_size), false, Color(1, 1, 1, alpha))
