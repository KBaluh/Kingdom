class_name Shield
extends Mob
## Bubble that follows a mob and soaks up damage for a few seconds.

const TEX := preload("res://assets/images/bubble_shield.png")
const HIT_POINTS := 20.0
const LIFETIME := 400

var _wearer: Mob
var _life := LIFETIME


func _init(wearer: Mob) -> void:
	super(Vector2.ZERO, HIT_POINTS, wearer.team)
	_wearer = wearer
	texture = TEX
	counts_as_kill = false
	show_hp = false
	_follow()


func tick() -> void:
	_follow()
	_life -= 1
	if _life <= 0:
		remove()


func hurt(damage: float) -> void:
	super(damage)
	if not alive:
		remove()


func _follow() -> void:
	position = _wearer.position + (_wearer.size() - size()) / 2.0
