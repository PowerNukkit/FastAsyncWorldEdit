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

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.*;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.Registries;
import org.enginehub.piston.CommandManager;

import javax.annotation.Nullable;
import java.util.*;

public class PowerNukkitServerInterface extends AbstractPlatform implements MultiUserPlatform {
    @Override
    public Collection<Actor> getConnectedUsers() {
        List<Actor> users = new ArrayList<>();
        Server.getInstance().getOnlinePlayers().values().stream().forEach(WorldEditPlugin.getInstance()::wrapPlayer);
        return null;
    }

    @Override
    public Registries getRegistries() {
        return null;
    }

    @Override
    public int getDataVersion() {
        return 0;
    }

    @Override
    public boolean isValidMobType(String type) {
        return false;
    }

    @Override
    public void reload() {

    }

    @Nullable
    @Override
    public Player matchPlayer(Player player) {
        return null;
    }

    @Nullable
    @Override
    public World matchWorld(World world) {
        return null;
    }

    @Override
    public void registerCommands(CommandManager commandManager) {

    }

    @Override
    public void registerGameHooks() {

    }

    @Override
    public LocalConfiguration getConfiguration() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getPlatformName() {
        return null;
    }

    @Override
    public String getPlatformVersion() {
        return null;
    }

    @Override
    public Map<Capability, Preference> getCapabilities() {
        return null;
    }

    @Override
    public Set<SideEffect> getSupportedSideEffects() {
        return null;
    }
}
