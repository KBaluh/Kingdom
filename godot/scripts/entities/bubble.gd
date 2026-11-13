class_name Bubble
extends Entity
## Decorative bubble that rises. Background bubbles live until they leave
## the screen; rocket-trail and death bubbles pop after `life` moves.

enum { SMALL, MIDDLE, BIG }

const TEXTURES: Array[Texture2D] = [
	preload("res://assets/images/bubble_small.png"),
	preload("res://assets/images/bubble_middle.png"),
	preload("res://assets/images/bubble_large.png"),
]

var _speed: int
var _move_every: int
var _life: int
var _wait := 0


## `move_every`: rise once per that many ticks. `life`: moves before the
## bubble pops, or -1 to live until it leaves the screen.
func _init(pos: Vector2, type: int, rise_speed: int, move_every: int = 1, life: int = -1) -> void:
	super()
	position = pos
	texture = TEXTURES[type]
	_speed = rise_speed
	_move_every = move_every
	_life = life


func tick() -> void:
	_wait += 1
	if _wait < _move_every:
		return
	_wait = 0
	position.y -= _speed
	if _life >= 0:
		_life -= 1
		if _life < 0:
			remove()
