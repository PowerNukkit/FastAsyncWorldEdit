package com.sk89q.worldedit.powernukkit;

import cn.nukkit.Player;
import com.sk89q.worldedit.util.formatting.text.Component;

public class PowerNukkitTextAdapter {
    private PowerNukkitTextAdapter() {
        throw new UnsupportedOperationException();
    }

    public static void sendComponent(Player player, Component component) {
        //TODO
        player.sendMessage(component.toString());
    }
    
    
}
