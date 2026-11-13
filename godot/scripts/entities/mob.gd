class_name Mob
extends Entity
## Anything with hit points: the player, enemy fish, the bubble shield.

const SHOOT_DELAY := 30
const ROCKET_WIDTH := 65.0

var team: int
## -1 swims left, 1 swims right.
var dir := -1
var speed := 3
var max_hp: float
var hp: float
var alive := true
## False for the player and the shield: destroying them is not a "kill".
var counts_as_kill := true
var show_hp := true

var _cooldown := 0


func _init(pos: Vector2, hit_points: float, mob_team: int) -> void:
	super()
	position = pos
	max_hp = hit_points
	hp = hit_points
	team = mob_team


func tick() -> void:
	if not alive:
		return
	tick_weapon()
	position.x += dir * speed


func tick_weapon() -> void:
	if _cooldown > 0:
		_cooldown -= 1


## Launches a rocket in the facing direction if the gun has reloaded.
func fire(chaotic: bool = false) -> void:
	if _cooldown > 0:
		return
	_cooldown = SHOOT_DELAY
	var body := size()
	var x := position.x + ROCKET_WIDTH
	if dir < 0:
		x -= floorf(body.x / 2.0)
	var from := Vector2(x, position.y + floorf(body.y / 2.0))
	level.add_main(Bullet.rocket(from, dir, team, chaotic))


func hurt(damage: float) -> void:
	if not alive:
		return
	hp -= damage
	if hp <= 0.0:
		hp = 0.0
		alive = false
		_death_bubbles()
	queue_redraw()


func heal(amount: float) -> void:
	if alive:
		hp = minf(hp + amount, max_hp)
		queue_redraw()


func score_value() -> int:
	return 0


## Remaining hit points under the mob once it is noticeably damaged.
func _draw() -> void:
	if not show_hp or hp >= max_hp * 0.8:
		return
	var color := Color.YELLOW if hp >= max_hp * 0.3 else Color.RED
	var font := ThemeDB.fallback_font
	var text := str(int(hp))
	var at := Vector2(size().x / 2.0, size().y + 2.0)
	draw_string_outline(font, at, text, HORIZONTAL_ALIGNMENT_LEFT, -1, 14, 4, Color(0, 0, 0, 0.7))
	draw_string(font, at, text, HORIZONTAL_ALIGNMENT_LEFT, -1, 14, color)


func _death_bubbles() -> void:
	if level == null:
		return
	var center := position + size() / 2.0
	for i in 20:
		var at := center + Vector2(randf_range(-25, 25), randf_range(-50, 50))
		level.add_pop(Bubble.new(at, randi() % 2, 2, 2, 25))
