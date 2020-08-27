package com.sk89q.worldedit.powernukkit;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.IBlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.NotABlockException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class PowerNukkitAdapter {
    private PowerNukkitAdapter() {
        throw new UnsupportedOperationException();
    }


    public static BaseItemStack adapt(Item item) {
        checkNotNull(item);
        return new PowerNukkitItemStack(item);
    }
    
    public static BlockState asBlockState(IBlockState block, IBlockState... layers) {
        if (layers.length == 0 || layers.length == 1 && layers[0].getBlockId() == BlockID.AIR) {
            return BlockState.get(blockStateString(block));
        }
        
        int len = layers.length;
        if (len > 1) {
            for (int i = len - 1; i >= 0; i--) {
                if (layers[i].getBlockId() == BlockID.AIR) {
                    len--;
                }
            }
        }
        
        if (len <= 0) {
            return BlockState.get(blockStateString(block));
        }

        StringBuilder builder = new StringBuilder(blockStateString(block));
        if (builder.charAt(builder.length() - 1) == ']') {
            builder.setLength(builder.length() - 1);
        } else {
            builder.append('[');
        }
        for (int i = 1; i <= len; i++) {
            builder.append("layer").append(i).append('=')
                    .append(escapeBlockStateString(blockStateString(layers[i-1])))
                    .append(',');
        }
        builder.setLength(builder.length()-1);
        return BlockState.get(builder.append(']').toString());
    }
    
    private static String unescapeBlockStateString(String blockState) {
        int last = blockState.indexOf('%');
        if (last == -1) {
            return blockState;
        }
        int len = blockState.length();
        
        StringBuilder builder = new StringBuilder(blockState.length());
        builder.append(blockState, 0, last);
        while (true) {
            if (last + 3 >= len) {
                builder.append(blockState, last, len);
                break;
            }
            char code1 = blockState.charAt(last + 1);
            char code2 = blockState.charAt(last + 2);
            int next = last + 1;
            if (code1 == '5') {
                if (code2 == 'B') {
                    builder.append('[');
                    next += 2;
                } else if (code2 == 'D') {
                    builder.append(']');
                    next += 2;
                }
            } else if (code1 == '3' && code2 == 'D') {
                builder.append('=');
                next += 2;
            } else if (code1 == '2') {
                if (code2 == '5') {
                    builder.append('%');
                    next += 2;
                } else if (code2 == 'C') {
                    builder.append(',');
                    next += 2;
                }
            }
            
            next = blockState.indexOf('%', next);
            if (next == -1) {
                builder.append(blockState, last, len);
                break;
            }
            last = next;
        }
        
        return builder.toString();
    }
    
    private static String escapeBlockStateString(String blockState) {
        int start = blockState.indexOf('[');
        if (start == -1 && blockState.indexOf('%') == -1) {
            return blockState;
        }
        StringBuilder builder = new StringBuilder(blockState.length() + 20);
        for (char c: blockState.toCharArray()) {
            switch (c) {
                case '[':
                    builder.append("%5B");
                    break;
                case ']':
                    builder.append("%5D");
                    break;
                case '=':
                    builder.append("%3D");
                    break;
                case '%':
                    builder.append("%25");
                    break;
                case ',':
                    builder.append("%2C");
                    break;
                default:
                    builder.append(c);
            }
        }
        return builder.toString();
    }
    
    private static String blockStateString(IBlockState block) {
        Set<String> nameSet = block.getProperties().getNames();
        if (nameSet.isEmpty()) {
            return block.getPersistenceName();
        }

        List<String> names = new ArrayList<>(nameSet);
        Collections.sort(names);
        StringBuilder sb = new StringBuilder(block.getPersistenceName()).append('[');
        names.forEach(name-> sb.append(name).append('=').append(block.getPropertyValue(name)).append(','));
        sb.setLength(sb.length()-1);
        return sb.append(']').toString();
    }
    
    public static BlockState asBlockState(Item item) {
        checkNotNull(item);
        Block block = item.getBlock();
        if (block.getId() == BlockID.AIR) {
            throw new NotABlockException();
        }
        
        return asBlockState(block);
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
    
    public static Level adapt(World world) {
        checkNotNull(world);
        if (world instanceof PowerNukkitWorld) {
            return ((PowerNukkitWorld) world).getLevel();
        }
        Level level = Server.getInstance().getLevelByName(world.getName());
        if (level == null) {
            throw new IllegalArgumentException("Can't find a PowerNukkit world for " + world);
        }
        return level;
    }

    public static Vector3 asVector(cn.nukkit.math.Vector3 location) {
        checkNotNull(location);
        return Vector3.at(location.getX(), location.getY(), location.getZ());
    }

    public static BlockVector3 asBlockVector(cn.nukkit.math.Vector3 vector3) {
        checkNotNull(vector3);
        return BlockVector3.at(vector3.getX(), vector3.getY(), vector3.getZ());
    }

    public static com.sk89q.worldedit.entity.Entity adapt(Entity entity) {
        checkNotNull(entity);
        return new PowerNukkitEntity(entity);
    }

    public static Location adapt(cn.nukkit.level.Location location) {
        checkNotNull(location);
        Vector3 position = asVector(location);
        return new Location(
                adapt(location.getLevel()),
                position,
                (float)location.getYaw(),
                (float)location.getPitch());
    }
    
    public static cn.nukkit.level.Location adapt(Location location) {
        checkNotNull(location);
        return new cn.nukkit.level.Location(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                adapt((World) location.getExtent()));
    }

    public static cn.nukkit.math.BlockVector3 adapt(BlockVector3 position) {
        checkNotNull(position);
        return new cn.nukkit.math.BlockVector3(position.getX(), position.getY(), position.getZ());
    }

    public static cn.nukkit.math.Vector3 adapt(Vector3 position) {
        checkNotNull(position);
        return new cn.nukkit.math.Vector3(position.getX(), position.getY(), position.getZ());
    }

    public static cn.nukkit.math.Vector3 asVector3(BlockVector3 position) {
        checkNotNull(position);
        return new cn.nukkit.math.Vector3(position.getX(), position.getY(), position.getZ());
    }
}
