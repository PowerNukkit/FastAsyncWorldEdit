package com.sk89q.worldedit.powernukkit;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;

@FunctionalInterface
public interface PowerNukkitGenerator {
    boolean generate(Level level, BlockVector3 position);
}
