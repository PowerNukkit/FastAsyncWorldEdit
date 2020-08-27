package com.sk89q.worldedit.powernukkit;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.NotABlockException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BlockID;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class PowerNukkitAdapter {
    private PowerNukkitAdapter() {
        throw new UnsupportedOperationException();
    }


    public static BaseItemStack adapt(Item item) {
        checkNotNull(item);
        return new PowerNukkitItemStack(item);
    }

    public static BlockState asBlockState(Item item) {
        checkNotNull(item);
        Block block = item.getBlock();
        if (block.getId() == BlockID.AIR) {
            throw new NotABlockException();
        }

        Set<String> nameSet = block.getProperties().getNames();
        if (nameSet.isEmpty()) {
            return BlockState.get(block.getPersistenceName());
        }
        
        List<String> names = new ArrayList<>(nameSet);
        Collections.sort(names);
        StringBuilder sb = new StringBuilder(block.getPersistenceName()).append('[');
        names.forEach(name-> sb.append(name).append('=').append(block.getPropertyValue(name)).append(','));
        sb.setLength(sb.length()-1);
        return BlockState.get(sb.append(']').toString());
    }

    /**
     * Create a PowerNukkit ItemStack from a WorldEdit BaseItemStack
     *
     * @param itemStack The WorldEdit BaseItemStack
     * @return The PowerNukkit ItemStack
     */
    public static Item adapt(BaseItemStack itemStack) {
        checkNotNull(itemStack);
        if (itemStack instanceof PowerNukkitItemStack) return ((PowerNukkitItemStack) itemStack).getPowerNukkitItem();
        final int[] idMeta = PowerNukkitItemTypeAdapter.asItemIdMeta(itemStack.getType());
        if (idMeta == null) {
            throw new IllegalArgumentException("Unsupported item type: "+itemStack.getType());
        }
        final Item item = new Item(idMeta[0], idMeta[1], itemStack.getAmount());
        if (itemStack.hasNbtData()) {
            CompoundTag nbtData = itemStack.getNbtData();
            if (nbtData != null) {
                item.setCompoundTag(PowerNukkitNbtAdapter.toNative(nbtData, null));
            }
        }
        return item;
    }

    public static boolean equals(BlockType blockType, Item powerNukkitItem) {
        return blockType == PowerNukkitItemTypeAdapter.asItemType(powerNukkitItem).getBlockType();
    }

    public static Extent adapt(Level level) {
        checkNotNull(level);
        return new PowerNukkitWorld(level);
    }

    public static Vector3 asVector(cn.nukkit.math.Vector3 location) {
        checkNotNull(location);
        return Vector3.at(location.getX(), location.getY(), location.getZ());
    }
}
