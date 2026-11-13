class_name Explosion
extends Entity
## Five-frame explosion animation centered on the point of impact.

const FRAMES: Array[Texture2D] = [
	preload("res://assets/images/expl1.png"),
	preload("res://assets/images/expl2.png"),
	preload("res://assets/images/expl3.png"),
	preload("res://assets/images/expl4.png"),
	preload("res://assets/images/expl5.png"),
]
const TICKS_PER_FRAME := 4

var _frame := 0
var _frame_age := 0


func _init(center: Vector2) -> void:
	super()
	centered = true
	position = center
	texture = FRAMES[0]


func tick() -> void:
	_frame_age += 1
	if _frame_age < TICKS_PER_FRAME:
		return
	_frame_age = 0
	_frame += 1
	if _frame >= FRAMES.size():
		remove()
	else:
		texture = FRAMES[_frame]


func rect() -> Rect2:
	return Rect2(position - size() / 2.0, size())
