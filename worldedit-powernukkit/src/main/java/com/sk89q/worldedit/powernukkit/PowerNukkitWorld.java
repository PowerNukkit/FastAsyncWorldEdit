package com.sk89q.worldedit.powernukkit;

import cn.nukkit.level.Level;
import com.sk89q.worldedit.extent.Extent;

import java.lang.ref.WeakReference;

public class PowerNukkitWorld implements Extent {
    private WeakReference<Level> worldRef;
    private final String worldNameRef;
    
    public PowerNukkitWorld(Level level) {
    }
}
