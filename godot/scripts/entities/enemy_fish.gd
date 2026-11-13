class_name EnemyFish
extends Mob
## Hunter and green fish: swim left and fire rockets at random intervals.

const KINDS := {
	"hunter": {
		"left": preload("res://assets/images/hunterFishLeft.png"),
		"right": preload("res://assets/images/hunterFishRight.png"),
		"hp": 20.0,
		"score": 8,
		# Ticks between shots: 1..7 seconds.
		"shoot_min": 60,
		"shoot_extra": 360,
	},
	"green": {
		"left": preload("res://assets/images/greenFishLeft.png"),
		"right": preload("res://assets/images/greenFishRight.png"),
		"hp": 35.0,
		"score": 14,
		# Ticks between shots: 0.75..4.75 seconds.
		"shoot_min": 45,
		"shoot_extra": 240,
	},
}

var _score: int
var _shoot_min: int
var _shoot_extra: int
var _shoot_in: int


func _init(kind: String, pos: Vector2) -> void:
	var def: Dictionary = KINDS[kind]
	super(pos, def["hp"], TEAM_ENEMY)
	speed = 2
	texture = def["left"]
	_score = def["score"]
	_shoot_min = def["shoot_min"]
	_shoot_extra = def["shoot_extra"]
	_shoot_in = _next_shot()


func tick() -> void:
	super()
	if not alive:
		return
	_shoot_in -= 1
	if _shoot_in <= 0:
		fire()
		_shoot_in = _next_shot()


func score_value() -> int:
	@warning_ignore("integer_division")
	return _score + randi() % (_score / 2)


func _next_shot() -> int:
	return _shoot_min + randi() % _shoot_extra
