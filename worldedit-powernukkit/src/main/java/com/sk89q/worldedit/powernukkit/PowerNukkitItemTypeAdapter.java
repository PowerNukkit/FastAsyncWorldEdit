package com.sk89q.worldedit.powernukkit;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import com.sk89q.worldedit.world.block.BlockID;
import com.sk89q.worldedit.world.item.ItemType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerNukkitItemTypeAdapter {
    private static final Int2ObjectMap<ItemType> ID_TO_TYPE = new Int2ObjectOpenHashMap<>();
    private static final Map<String, ItemType> KEY_TO_TYPE = new HashMap<>();
    private static final Map<ItemType, int[]> TYPE_TO_ID_META = new HashMap<>();
    
    public static ItemType asItemType(int id) {
        return ID_TO_TYPE.computeIfAbsent(id, PowerNukkitItemTypeAdapter::registerUniversal);
    }
    
    public static void init() {
        itemIdStream()
                .mapToInt(PowerNukkitItemTypeAdapter::readStaticValue)
                .distinct()
                .forEachOrdered(PowerNukkitItemTypeAdapter::registerItem);
        blockIdStream()
                .mapToInt(PowerNukkitItemTypeAdapter::readStaticValue)
                .distinct()
                .forEachOrdered(PowerNukkitItemTypeAdapter::registerBlock);
    }

    private static ItemType registerUniversal(int universal) {
        if (universal < 0) {
            int blockId = -universal + 255;
            return registerBlock(blockId);
        } else if (universal <= 255) {
            return registerBlock(universal);
        } else {
            return registerItem(universal);
        }
    }
    
    private static Stream<Field> itemIdStream() {
        return Arrays.stream(ItemID.class.getFields())
                .filter(field -> field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
                .filter(field -> field.getType().equals(Integer.TYPE));
    }

    private static ItemType registerItem(int itemId) {
        List<ItemType> registration = itemIdStream()
                .filter(field -> readStaticValue(field) == itemId)
                .map(field -> register(itemId, "minecraft:" + field.getName().toLowerCase()))
                .collect(Collectors.toList());
        if (registration.isEmpty()) {
            return null;
        }
        return registration.get(0);
    }
    
    private static Stream<Field> blockIdStream() {
        return Arrays.stream(BlockID.class.getFields())
                .filter(field -> field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
                .filter(field -> field.getType().equals(Integer.TYPE));
    }

    private static ItemType registerBlock(int blockId) {
        int universal = blockIdToUniversal(blockId);
        List<ItemType> registration = blockIdStream()
                .filter(field -> readStaticValue(field) == blockId)
                .map(field -> register(universal, "minecraft:" + field.getName().toLowerCase()))
                .collect(Collectors.toList());
        if (registration.isEmpty()) {
            return null;
        }
        return registration.get(0);
    }
    
    private static ItemType register(int universal, final String key) {
        if (ID_TO_TYPE.containsKey(universal)) {
            ItemType type = ID_TO_TYPE.get(universal);
            KEY_TO_TYPE.putIfAbsent(key, type);
            return type;
        }
        
        ItemType registered = ItemType.REGISTRY.register(key, new ItemType(key));
        ID_TO_TYPE.put(universal, registered);
        KEY_TO_TYPE.put(key, registered);
        TYPE_TO_ID_META.put(registered, new int[]{universal, 0});
        return registered;
    }

    private static int blockIdToUniversal(int blockId) {
        if (blockId <= 255) {
            return blockId;
        } else {
            return 255 - blockId;
        }
    }
    
    private static int readStaticValue(Field field) {
        try {
            return field.getInt(null);
        } catch (IllegalAccessException e) {
            throw new InternalError(e);
        }
    }

    @Nullable
    public static int[] asItemIdMeta(ItemType type) {
        final int[] idMeta = TYPE_TO_ID_META.get(type);
        if (idMeta == null) {
            return null;
        } else {
            return idMeta.clone(); 
        }
    }

    public static ItemType asItemType(Item item) {
        return asItemType(item.getId());
    }
}
