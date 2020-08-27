/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.sk89q.worldedit.powernukkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.plugin.PluginBase;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Plugin for PowerNukkit.
 */
public class WorldEditPlugin extends PluginBase {
    static {
        if (!"PowerNukkit".equals(Server.getInstance().getCodename())) {
            throw new IllegalStateException(String.format(
                    "\n**********************************************\n"
                            + "** This Nukkit server (%s) is not supported by this version of WorldEdit.\n"
                            + "** This WorldEdit version requires PowerNukkit to work.\n"
                            + "** Please download an OLDER version of WorldEdit or upgrade to PowerNukkit.\n"
                            + "**********************************************\n", 
                    Server.getInstance().getVersion()+" - "+Server.getInstance().getCodename()));
        }
        int[] v = Arrays.stream(Server.getInstance().getVersion().split("-")[0].split("\\."))
                .mapToInt(Integer::parseInt).toArray();
        if (v[0] != 1 || v[1] < 2 || v[1] == 3 && (v[2] == 0 || v[2] == 1 && v[3] < 4)) {
            throw new IllegalStateException(String.format(
                    "\n**********************************************\n"
                            + "** This PowerNukkit version (%s) is not supported by this version of WorldEdit.\n"
                            + "** Please download an OLDER version of WorldEdit which does.\n"
                            + "**********************************************\n",
                    Server.getInstance().getVersion()
            ));
        }
    }

    public static final String CUI_PLUGIN_CHANNEL = "worldedit:cui";
    private static WorldEditPlugin INSTANCE;

    @Override
    public void onLoad() {
        if (INSTANCE != null) {
            return;
        }
        INSTANCE = this;
    }
    
    /**
     * Gets the instance of this plugin.
     *
     * @return an instance of the plugin
     * @throws NullPointerException if the plugin hasn't been enabled
     */
    public static WorldEditPlugin getInstance() {
        return checkNotNull(INSTANCE);
    }

    public PowerNukkitPlayer getCachedPlayer(Player player) {
        List<MetadataValue> meta = player.getMetadata("WE");
        if (meta.isEmpty()) {
            return null;
        }
        return (PowerNukkitPlayer) meta.get(0).value();
    }
}
