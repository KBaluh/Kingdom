class_name Player
extends Mob

const TEX_LEFT := preload("res://assets/images/player_left.png")
const TEX_RIGHT := preload("res://assets/images/player_right.png")
const SHIELD_COOLDOWN := 600

var shield: Shield
## Ticks since the shield was last raised; ready at SHIELD_COOLDOWN.
var shield_delay := SHIELD_COOLDOWN


func _init(pos: Vector2, hit_points: float) -> void:
	super(pos, hit_points, TEAM_PLAYER)
	speed = 4
	dir = 1
	counts_as_kill = false
	texture = TEX_RIGHT


func tick() -> void:
	if not alive:
		return

	var input := Vector2(
			Input.get_axis("move_left", "move_right"),
			Input.get_axis("move_up", "move_down")).sign()
	if input.x != 0.0:
		dir = int(input.x)
		texture = TEX_LEFT if dir < 0 else TEX_RIGHT
	position = (position + input * speed).clamp(Vector2.ZERO, SCREEN - size())

	tick_weapon()
	if Input.is_action_pressed("fire") and fire(true):
		Global.rumble(0.3, 0.6, 0.12)

	shield_delay = mini(shield_delay + 1, SHIELD_COOLDOWN)
	if Input.is_action_just_pressed("shield"):
		_raise_shield()


func hurt(damage: float) -> void:
	if shield_active():
		shield.hurt(damage)
	else:
		super(damage)


func shield_active() -> bool:
	return is_instance_valid(shield) and not shield.removed


func _raise_shield() -> void:
	if shield_delay < SHIELD_COOLDOWN or shield_active():
		return
	shield = Shield.new(self)
	level.add_main(shield)
	shield_delay = 0
