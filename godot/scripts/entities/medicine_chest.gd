class_name MedicineChest
extends Entity
## Floats up from the bottom; heals the player on touch.

const TEX := preload("res://assets/images/MedicineChest.png")
const BONUS := 35.0
const SPEED := 3.0


func _init(pos: Vector2) -> void:
	super()
	position = pos
	texture = TEX


func tick() -> void:
	position.y -= SPEED
