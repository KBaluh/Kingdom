class_name Bullet
extends Entity
## Rockets and mines. A bullet damages the first mob of another team it
## touches, then explodes.

const TEX_ROCKET_LEFT := preload("res://assets/images/RocketBulletLeft.png")
const TEX_ROCKET_RIGHT := preload("res://assets/images/RocketBulletRight.png")
const TEX_SEA_MINE := preload("res://assets/images/SeaMine.png")
const TEX_TOP_MINE := preload("res://assets/images/SeaMineTop.png")
## Enough to sink any fish in one hit.
const HOMING_DAMAGE := 40.0
## Pixels per move; a homing rocket speeds up from the first to the second.
const HOMING_START_SPEED := 6.0
const HOMING_MAX_SPEED := 22.0
const HOMING_ACCELERATION := 1.5

var team: int
var damage: float
var heading := Vector2.ZERO
var speed := 1.0
## Rockets gain `speed` on every move; mines move at a constant speed.
var accelerates := false
## Wobble up and down while flying (the player's chaotic rocket).
var chaotic := false
var leaves_trail := false
## Pixels trimmed from each side of the sprite to get the hitbox.
var hitbox_shrink := 0.0
var explodes_on_top_layer := false
var heavy_boom := false
## When above zero, the player loses this share of their full hit points
## instead of `damage`.
var player_hp_share := 0.0

## Fish a homing rocket steers at; once it is gone the rocket flies straight on.
var target: Mob

var _velocity := Vector2.ZERO
var _wait := 1
var _trail_tick := 0


static func rocket(pos: Vector2, dir_x: int, rocket_team: int, is_chaotic: bool) -> Bullet:
	var bullet := Bullet.new()
	bullet.position = pos
	bullet.texture = TEX_ROCKET_LEFT if dir_x < 0 else TEX_ROCKET_RIGHT
	bullet.heading = Vector2(dir_x, 0)
	bullet.team = rocket_team
	bullet.damage = 14.0
	bullet.accelerates = true
	bullet.chaotic = is_chaotic
	bullet.leaves_trail = true
	return bullet


## Rocket of the volley gun: chases one fish wherever it swims.
## Drawn centered so it can turn; `pos` is its middle, not its corner.
static func homing(pos: Vector2, prey: Mob, rocket_team: int) -> Bullet:
	var bullet := Bullet.new()
	bullet.centered = true
	bullet.position = pos
	bullet.texture = TEX_ROCKET_RIGHT
	bullet.heading = Vector2.RIGHT
	bullet.speed = HOMING_START_SPEED
	bullet.team = rocket_team
	bullet.damage = HOMING_DAMAGE
	bullet.leaves_trail = true
	bullet.target = prey
	bullet._seek()
	return bullet


## Floats up from the sea floor and blows up whoever touches it first:
## it sinks a fish outright and costs the player 30% of their hit points.
static func sea_mine(pos: Vector2) -> Bullet:
	var bullet := Bullet.new()
	bullet.position = pos
	bullet.texture = TEX_SEA_MINE
	bullet.heading = Vector2.UP
	bullet.speed = 3.0
	bullet.team = TEAM_NEUTRAL
	bullet.damage = 40.0
	bullet.player_hp_share = 0.3
	bullet.hitbox_shrink = 10.0
	bullet.heavy_boom = true
	return bullet


## Dropped on the player by mine-laying top fish.
static func top_mine(pos: Vector2) -> Bullet:
	var bullet := Bullet.new()
	bullet.position = pos
	bullet.texture = TEX_TOP_MINE
	bullet.heading = Vector2.DOWN
	bullet.speed = 7.0
	bullet.team = TEAM_ENEMY
	bullet.damage = 10.0
	bullet.explodes_on_top_layer = true
	bullet.heavy_boom = true
	return bullet


func tick() -> void:
	# Bullets move on every second tick.
	if _wait > 0:
		_wait -= 1
	else:
		_wait = 1
		if target != null:
			_seek()
			speed = minf(speed + HOMING_ACCELERATION, HOMING_MAX_SPEED)
			_velocity = heading * speed
		elif accelerates:
			_velocity += heading * speed
		else:
			_velocity = heading * speed
		position += _velocity

	if chaotic:
		position.y += roundf(cos(floorf(position.x / 25.0)))

	if leaves_trail:
		_trail_tick += 1
		if _trail_tick >= 3:
			_trail_tick = 0
			var at := position + Vector2(0, randi() % 30 + 8)
			level.add_main(Bubble.new(at, Bubble.SMALL, 4, 2, 2 + randi() % 5))


func rect() -> Rect2:
	var corner := position - size() / 2.0 if centered else position
	return Rect2(corner, size()).grow(-hitbox_shrink)


## Turns a homing rocket towards its fish while the fish is still there.
func _seek() -> void:
	if not is_instance_valid(target) or target.removed or not target.alive:
		return
	var to_target := target.rect().get_center() - position
	if to_target.is_zero_approx():
		return
	heading = to_target.normalized()
	rotation = heading.angle()


## Hit points this bullet takes off `mob`.
func damage_to(mob: Mob) -> float:
	if player_hp_share > 0.0 and mob is Player:
		return mob.max_hp * player_hp_share
	return damage


## Called by the level when the bullet has damaged a mob.
func hit() -> void:
	var boom := Explosion.new(rect().get_center())
	if explodes_on_top_layer:
		level.add_pop(boom)
	else:
		level.add_main(boom)
	Global.play_explosion(heavy_boom)
