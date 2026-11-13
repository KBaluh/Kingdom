class_name Spawner
extends RefCounted
## Adds one kind of entity to the level at randomized intervals (in ticks).

var kind: String
var base_interval: int
var _interval: int
var _timer := 0


func _init(spawn_kind: String, interval: int) -> void:
	kind = spawn_kind
	base_interval = interval
	_interval = interval


func tick(level) -> void:
	_timer += 1
	if _timer < _interval:
		return
	_timer = 0
	_spawn(level)
	# Next one comes after 1x..1.5x the base interval.
	@warning_ignore("integer_division")
	_interval = base_interval + randi() % maxi(base_interval / 2, 1)


func _spawn(level) -> void:
	var screen := Entity.SCREEN
	match kind:
		"bubble":
			var speed := randi_range(1, 3)
			var type := speed - 1
			var bubble := Bubble.new(Vector2(randi() % int(screen.x), screen.y), type, speed)
			# Small bubbles drift behind the fish, big ones in front.
			if type == Bubble.SMALL or (type == Bubble.MIDDLE and randf() < 0.5):
				level.add_back(bubble)
			else:
				level.add_pop(bubble)
		"hunter", "green":
			var y := randi() % int(screen.y - 100)
			level.add_main(EnemyFish.new(kind, Vector2(screen.x + 10, y)))
		"sea_mine":
			level.add_main(Bullet.sea_mine(Vector2(randi() % int(screen.x - 78), screen.y)))
		"medicine":
			level.add_main(MedicineChest.new(Vector2(randi() % int(screen.x - 50), screen.y)))
		"top_fish":
			level.add_main(TopFish.new(Vector2(screen.x, 5), false))
		"top_fish_mine":
			level.add_main(TopFish.new(Vector2(screen.x, 5), true))
		_:
			push_error("Unknown spawner kind: %s" % kind)
