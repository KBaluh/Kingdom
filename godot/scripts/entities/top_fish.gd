class_name TopFish
extends Mob
## Fast one-hit fish that crosses along the surface. The mine-laying
## variant drops a mine once it is above the player.

const TEX := preload("res://assets/images/TopFish.png")

var drops_mine: bool
var _dropped := false


func _init(pos: Vector2, lays_mines: bool) -> void:
	super(pos, 1.0, TEAM_ENEMY)
	speed = 4
	texture = TEX
	drops_mine = lays_mines


func tick() -> void:
	super()
	if drops_mine and alive and not _dropped and position.x <= level.player.position.x:
		_dropped = true
		var body := size()
		level.add_main(Bullet.top_mine(position + Vector2(body.x / 2.0 - 20.0, body.y - 5.0)))


func score_value() -> int:
	return 10
