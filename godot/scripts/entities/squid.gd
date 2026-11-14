class_name Squid
extends Mob
## The boss: a giant squid that hangs at the right edge of the screen and
## launches rockets from the tips of its tentacles. It has no sprite and is
## drawn from code. The lower its health, the more tentacles fire at once.

const HP := 500.0
const SCORE := 300
## Hitbox of the head and mantle; the tentacles cannot be hit.
const BODY := Vector2(250, 170)
const TENTACLES := 6
const TENTACLE_LENGTH := 170.0
## How far apart the tips fan out, in pixels between neighbours.
const TENTACLE_SPREAD := 62.0
const TENTACLE_JOINTS := 14
## Where the body stops after swimming in from the right.
const HOME_X := 1024.0 - 265.0
const ENTER_SPEED := 2.0
## A tip glows for this long before its rocket leaves.
const CHARGE_TICKS := 30
const TOUCH_DAMAGE := 6.0
const TOUCH_DELAY := 15
const DEATH_TICKS := 170

const COLOR_BODY := Color(0.78, 0.27, 0.45)
const COLOR_DARK := Color(0.45, 0.12, 0.3)
const COLOR_SPOT := Color(0.9, 0.45, 0.6)

## True once the death animation is over: the level is won.
var defeated := false

var _age := 0
## Phase of the up-and-down swimming.
var _bob := 0.0
## Ticks left until each tentacle fires; -1 when it is not charging.
var _charge: Array[int] = []
var _attack_in := 90
var _touch_wait := 0
var _flash := 0
var _death_age := 0


func _init() -> void:
	super(Vector2(SCREEN.x + 10.0, 300.0), HP, TEAM_ENEMY)
	counts_as_kill = false
	show_hp = false
	for i in TENTACLES:
		_charge.append(-1)


func size() -> Vector2:
	return BODY


func tick() -> void:
	_age += 1
	queue_redraw()
	if _flash > 0:
		_flash -= 1
	if not alive:
		_tick_death()
		return

	if position.x > HOME_X:
		position.x = maxf(position.x - ENTER_SPEED, HOME_X)
	# Bobs up and down over most of the screen, faster when wounded.
	var bob_speed := 0.012 + 0.008 * (1.0 - hp / max_hp)
	_bob += bob_speed
	position.y = 300.0 + sin(_bob) * 215.0

	_tick_touch()
	if position.x > HOME_X:
		return
	_tick_tentacles()
	_attack_in -= 1
	if _attack_in <= 0:
		_attack()


func hurt(damage: float) -> void:
	super(damage)
	_flash = 5


## Rockets leave the tentacles that finished charging.
func _tick_tentacles() -> void:
	for i in TENTACLES:
		if _charge[i] < 0:
			continue
		_charge[i] -= 1
		if _charge[i] >= 0:
			continue
		var tip := position + _tentacle_point(i, 1.0)
		level.add_main(Bullet.rocket(tip - Vector2(ROCKET_WIDTH, 18.0), -1, team, false))
		Global.play_launch(false)


## Picks which tentacles fire next, depending on how hurt the squid is.
func _attack() -> void:
	var health := hp / max_hp
	if health > 0.66:
		# One rocket, from the tentacle closest to the player.
		_charge_tentacle(_tentacle_near_player(), 0)
		_attack_in = 75
	elif health > 0.33:
		# The closest tentacle and two random ones.
		_charge_tentacle(_tentacle_near_player(), 0)
		for n in 2:
			_charge_tentacle(randi() % TENTACLES, 8 * (n + 1))
		_attack_in = 95
	else:
		# A wave through all the tentacles, leaving one gap to slip through.
		var gap := randi() % TENTACLES
		var downwards := randf() < 0.5
		for i in TENTACLES:
			if i == gap:
				continue
			var order := i if downwards else TENTACLES - 1 - i
			_charge_tentacle(i, order * 7)
		_attack_in = 130


func _charge_tentacle(index: int, delay: int) -> void:
	if _charge[index] < 0:
		_charge[index] = CHARGE_TICKS + delay


func _tentacle_near_player() -> int:
	var target: float = level.player.position.y + level.player.size().y / 2.0
	var best := 0
	var best_distance := INF
	for i in TENTACLES:
		var distance := absf(position.y + _tentacle_point(i, 1.0).y - target)
		if distance < best_distance:
			best_distance = distance
			best = i
	return best


## Swimming into the squid hurts.
func _tick_touch() -> void:
	if _touch_wait > 0:
		_touch_wait -= 1
		return
	var player: Player = level.player
	if player.alive and collides(player):
		player.hurt(TOUCH_DAMAGE)
		_touch_wait = TOUCH_DELAY


## Explosions all over the body while it sinks.
func _tick_death() -> void:
	_death_age += 1
	position.y += 1.2
	if _death_age % 9 == 1:
		var at := position + Vector2(randf_range(0, BODY.x), randf_range(0, BODY.y))
		level.add_pop(Explosion.new(at))
		Global.play_explosion(_death_age % 2 == 0)
	if _death_age >= DEATH_TICKS:
		defeated = true


## A point on tentacle `index`, relative to the squid: `along` is 0 at the
## head and 1 at the tip.
func _tentacle_point(index: int, along: float) -> Vector2:
	var lane := index - (TENTACLES - 1) / 2.0
	var root := Vector2(34.0, BODY.y / 2.0 + lane * 13.0)
	var wave := sin(_age * 0.06 + index * 1.7 - along * 3.0) * 22.0 * along
	# Limp tentacles hang down once the squid is dead.
	var droop := _death_age * 0.5 * along * along
	return root + Vector2(-TENTACLE_LENGTH * along, lane * TENTACLE_SPREAD * along + wave + droop)


func _draw() -> void:
	var blink := Color.WHITE if _flash > 0 else COLOR_BODY
	var body := COLOR_BODY.lerp(blink, 0.6)
	var dark := COLOR_DARK.lerp(blink, 0.4)
	var middle := BODY.y / 2.0

	for i in TENTACLES:
		_draw_tentacle(i, body, dark)

	# Fins at the tail, then the mantle over them.
	draw_colored_polygon(PackedVector2Array([
		Vector2(175, middle - 30), Vector2(250, middle - 85), Vector2(235, middle - 10),
	]), dark)
	draw_colored_polygon(PackedVector2Array([
		Vector2(175, middle + 30), Vector2(250, middle + 85), Vector2(235, middle + 10),
	]), dark)
	_draw_ellipse(Vector2(160, middle), Vector2(96, 60), dark)
	_draw_ellipse(Vector2(160, middle), Vector2(91, 55), body)
	for spot: Vector2 in [Vector2(150, -25), Vector2(190, 5), Vector2(140, 22), Vector2(215, -18), Vector2(180, 32)]:
		draw_circle(Vector2(spot.x, middle + spot.y), 7.0, COLOR_SPOT.lerp(blink, 0.3))

	# Head with one big eye that follows the player.
	draw_circle(Vector2(62, middle), 54.0, dark)
	draw_circle(Vector2(62, middle), 49.0, body)
	var eye := Vector2(50, middle - 12)
	draw_circle(eye, 20.0, Color.WHITE)
	if alive:
		var look := Vector2.LEFT
		if level != null and level.player != null:
			look = (level.player.position - (position + eye)).normalized()
		draw_circle(eye + look * 8.0, 9.0, Color.BLACK)
		# An angry brow.
		draw_line(eye + Vector2(-24, -14), eye + Vector2(16, -26), dark, 7.0)
	else:
		draw_line(eye + Vector2(-10, -10), eye + Vector2(10, 10), Color.BLACK, 4.0)
		draw_line(eye + Vector2(-10, 10), eye + Vector2(10, -10), Color.BLACK, 4.0)


func _draw_tentacle(index: int, body: Color, dark: Color) -> void:
	for joint in TENTACLE_JOINTS + 1:
		var along := float(joint) / TENTACLE_JOINTS
		var radius := lerpf(13.0, 6.0, along)
		var at := _tentacle_point(index, along)
		draw_circle(at, radius + 2.0, dark)
	for joint in TENTACLE_JOINTS + 1:
		var along := float(joint) / TENTACLE_JOINTS
		draw_circle(_tentacle_point(index, along), lerpf(13.0, 6.0, along), body)

	# The launcher at the tip: heats up while the rocket is charging.
	var tip := _tentacle_point(index, 1.0)
	draw_circle(tip, 11.0, Color(0.2, 0.2, 0.25))
	var charge := _charge[index]
	if alive and charge >= 0 and charge <= CHARGE_TICKS:
		var heat := 1.0 - float(charge) / CHARGE_TICKS
		draw_circle(tip, 6.0 + 12.0 * heat, Color(1.0, 0.35, 0.1, 0.35))
		draw_circle(tip, 7.0, Color(1.0, 0.25 + 0.6 * heat, 0.1))
	else:
		draw_circle(tip, 6.0, Color(0.05, 0.05, 0.08))


func _draw_ellipse(center: Vector2, radii: Vector2, color: Color) -> void:
	var points := PackedVector2Array()
	for i in 32:
		var angle := TAU * i / 32.0
		points.append(center + Vector2(cos(angle), sin(angle)) * radii)
	draw_colored_polygon(points, color)
