rootProject.name = "FastAsyncWorldEdit"

include("worldedit-libs")

listOf("bukkit", "powernukkit", "core").forEach {
    include("worldedit-libs:$it")
    include("worldedit-$it")
}
include("worldedit-libs:core:ap")
