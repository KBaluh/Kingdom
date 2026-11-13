class_name Entity
extends Sprite2D
## Base for everything that lives in a level. Entities do not process on
## their own: the level (game.gd) calls tick() 60 times a second, in order,
## and resolves collisions with plain rectangles.

const SCREEN := Vector2(1024, 768)
const TEAM_PLAYER := 0
const TEAM_ENEMY := 1

## The game.gd node this entity was added to.
var level
var removed := false


func _init() -> void:
	centered = false


func tick() -> void:
	pass


func remove() -> void:
	if not removed:
		removed = true
		queue_free()


func size() -> Vector2:
	return texture.get_size() if texture != null else Vector2.ZERO


func rect() -> Rect2:
	return Rect2(position, size())


func collides(other: Entity) -> bool:
	return rect().intersects(other.rect())
