class_name Bullet
extends Entity
## Rockets and mines. A bullet damages the first mob of another team it
## touches, then explodes.

const TEX_ROCKET_LEFT := preload("res://assets/images/RocketBulletLeft.png")
const TEX_ROCKET_RIGHT := preload("res://assets/images/RocketBulletRight.png")
const TEX_SEA_MINE := preload("res://assets/images/SeaMine.png")
const TEX_TOP_MINE := preload("res://assets/images/SeaMineTop.png")

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


## Floats up from the sea floor and blows up enemy fish.
static func sea_mine(pos: Vector2) -> Bullet:
	var bullet := Bullet.new()
	bullet.position = pos
	bullet.texture = TEX_SEA_MINE
	bullet.heading = Vector2.UP
	bullet.speed = 3.0
	bullet.team = TEAM_PLAYER
	bullet.damage = 40.0
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
		if accelerates:
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
	return Rect2(position, size()).grow(-hitbox_shrink)


## Called by the level when the bullet has damaged a mob.
func hit() -> void:
	var boom := Explosion.new(rect().get_center())
	if explodes_on_top_layer:
		level.add_pop(boom)
	else:
		level.add_main(boom)
	Global.play_explosion(heavy_boom)
