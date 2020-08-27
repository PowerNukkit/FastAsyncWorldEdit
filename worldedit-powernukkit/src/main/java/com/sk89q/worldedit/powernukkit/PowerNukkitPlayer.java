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

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import com.boydti.fawe.config.Caption;
import com.boydti.fawe.config.Settings;
import com.boydti.fawe.object.RunnableVal;
import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PowerNukkitPlayer extends AbstractPlayerActor {
    
    private Player player;
    private WorldEditPlugin plugin;
    
    public PowerNukkitPlayer(Player player) {
        super(getExistingMap(WorldEditPlugin.getInstance(), player))
        this.plugin = WorldEditPlugin.getInstance();
        this.player = player;
    }

    public PowerNukkitPlayer(WorldEditPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        if (Settings.IMP.CLIPBOARD.USE_DISK) {
            loadClipboardFromDisk();
        }
    }

    private static Map<String, Object> getExistingMap(WorldEditPlugin plugin, Player player) {
        PowerNukkitPlayer cached = plugin.getCachedPlayer(player);
        if (cached != null) {
            return cached.getRawMeta();
        }
        return new ConcurrentHashMap<>();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public BaseItemStack getItemInHand(HandSide handSide) {
        Item item = handSide == HandSide.MAIN_HAND
                ? player.getInventory().getItemInHand()
                : player.getOffhandInventory().getItem(0);
        return PowerNukkitAdapter.adapt(item);
    }

    @Override
    public BaseBlock getBlockInHand(HandSide handSide) throws WorldEditException {
        Item item = handSide == HandSide.MAIN_HAND
                ? player.getInventory().getItemInHand()
                : player.getOffhandInventory().getItem(0);
        return PowerNukkitAdapter.asBlockState(item).toBaseBlock();
    }

    @Override
    public void giveItem(BaseItemStack itemStack) {
        final PlayerInventory inv = player.getInventory();
        Item newItem = PowerNukkitAdapter.adapt(itemStack);
        if (itemStack.getType().getId().equalsIgnoreCase(WorldEdit.getInstance().getConfiguration().wandItem)) {
            inv.remove(newItem);
        }
        final Item item = inv.getItemInHand();
        inv.setItemInHand(newItem);
        Item[] overflow = inv.addItem(item);
        if (overflow.length > 0) {
            TaskManager.IMP.sync(new RunnableVal<Object>() {
                @Override
                public void run(Object value) {
                    for (Item item : overflow) {
                        if (!item.isNull()) {
                            PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);
                            Server.getInstance().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                player.getLevel().dropItem(player.getLocation(), item);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            player.sendMessage(part);
        }
    }

    @Override
    public void print(String msg) {
        for (String part : msg.split("\n")) {
            player.sendMessage("\u00A7d" + part);
        }
    }

    @Override
    public void printDebug(String msg) {
        for (String part : msg.split("\n")) {
            player.sendMessage("\u00A77" + part);
        }
    }

    @Override
    public void printError(String msg) {
        for (String part : msg.split("\n")) {
            player.sendMessage("\u00A7c" + part);
        }
    }

    @Override
    public void print(Component component) {
        component = Caption.color(TranslatableComponent.of("prefix", component), getLocale());
        PowerNukkitTextAdapter.sendComponent(player, component);
    }

    @Override
    public boolean trySetPosition(Vector3 pos, float pitch, float yaw) {
        Level world = player.getLevel();
        if (pos instanceof com.sk89q.worldedit.util.Location) {
            com.sk89q.worldedit.util.Location loc = (com.sk89q.worldedit.util.Location) pos;
            Extent extent = loc.getExtent();
            if (extent instanceof World) {
                world = Server.getInstance().getLevelByName(((World) extent).getName());
            }
        }
        Level finalWorld = world;
        return TaskManager.IMP.sync(() -> player.teleport(new Location(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch, finalWorld)));
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public BlockBag getInventoryBlockBag() {
        return new PowerNukkitPlayerBlockBag(player);
    }

    @Override
    public GameMode getGameMode() {
        switch (player.getGamemode()) {
            case Player.SURVIVAL:
                return GameModes.SURVIVAL;
            case Player.CREATIVE:
                return GameModes.CREATIVE;
            case Player.ADVENTURE:
                return GameModes.ADVENTURE;
            case Player.SPECTATOR:
                return GameModes.SPECTATOR;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        if (gameMode.equals(GameModes.SURVIVAL)) {
            player.setGamemode(Player.SURVIVAL);
        } else if (gameMode.equals(GameModes.CREATIVE)) {
            player.setGamemode(Player.CREATIVE);
        } else if (gameMode.equals(GameModes.ADVENTURE)) {
            player.setGamemode(Player.ADVENTURE);
        } else if (gameMode.equals(GameModes.SPECTATOR)) {
            player.setGamemode(Player.SPECTATOR);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public void setPermission(String permission, boolean value) {
        player.addAttachment(plugin).setPermission(permission, value);
    }

    @Override
    public World getWorld() {
        return PowerNukkitAdapter.adapt(player.getLevel());
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
        // Not supported
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    protected boolean isAllowedToFly() {
        return player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT);
    }

    @Override
    protected void setFlying(boolean flying) {
        player.getAdventureSettings().set(AdventureSettings.Type.FLYING, true).update();
    }

    @Nullable
    @Override
    public BaseEntity getState() {
        throw new UnsupportedOperationException("Cannot create a state from this object");
    }

    @Override
    public com.sk89q.worldedit.util.Location getLocation() {
        Location location = player.getLocation();
        return new com.sk89q.worldedit.util.Location(
                PowerNukkitAdapter.adapt(location.getLevel()),
                PowerNukkitAdapter.asVector(location),
                location.getYaw(),
                location.getPitch()
        );
    }
}
