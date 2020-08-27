package com.sk89q.worldedit.powernukkit;

import com.sk89q.jnbt.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PowerNukkitNbtAdapter {
    private PowerNukkitNbtAdapter() {
        throw new UnsupportedOperationException();
    }
    
    public static ByteTag adapt(cn.nukkit.nbt.tag.ByteTag tag) {
        return new ByteTag((byte) tag.data);
    }
    
    public static ShortTag adapt(cn.nukkit.nbt.tag.ShortTag tag) {
        return new ShortTag((short) tag.data);
    }
    
    public static IntTag adapt(cn.nukkit.nbt.tag.IntTag tag) {
        return new IntTag(tag.data);
    } 
    
    public static LongTag adapt(cn.nukkit.nbt.tag.LongTag tag) {
        return new LongTag(tag.data);
    }
    
    public static FloatTag adapt(cn.nukkit.nbt.tag.FloatTag tag) {
        return new FloatTag(tag.data);
    }
    
    public static DoubleTag adapt(cn.nukkit.nbt.tag.DoubleTag tag) {
        return new DoubleTag(tag.data);
    }
    
    public static StringTag adapt(cn.nukkit.nbt.tag.StringTag tag) {
        return new StringTag(tag.data);
    }
    
    public static IntArrayTag adapt(cn.nukkit.nbt.tag.IntArrayTag tag) {
        return new IntArrayTag(tag.data.clone());
    }
    
    public static ByteArrayTag adapt(cn.nukkit.nbt.tag.ByteArrayTag tag) {
        return new ByteArrayTag(tag.data.clone());
    }
    
    public static CompoundTag adapt(cn.nukkit.nbt.tag.CompoundTag tag) {
        Map<String, Tag> map = new HashMap<>();
        for (cn.nukkit.nbt.tag.Tag child : tag.getAllTags()) {
            map.put(child.getName(), adapt(child));
        }
        return new CompoundTag(map);
    }
    
    public static ListTag adapt(cn.nukkit.nbt.tag.ListTag<?> tag) {
        if (tag.size() == 0) {
            return new ListTag(EndTag.class, Collections.emptyList());
        }
        cn.nukkit.nbt.tag.Tag child = tag.get(0);
        Class<? extends Tag> type;
        switch (child.getId()) {
            case cn.nukkit.nbt.tag.Tag.TAG_Byte:
                type = ByteTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Short:
                type = ShortTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Int:
                type = IntTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Long:
                type = LongTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Double:
                type = DoubleTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Float:
                type = FloatTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_String:
                type = StringTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Byte_Array:
                type = ByteArrayTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Int_Array:
                type = IntArrayTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_Compound:
                type = CompoundTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_List:
                type = ListTag.class;
                break;
            case cn.nukkit.nbt.tag.Tag.TAG_End:
                type = EndTag.class;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        int size = tag.size();
        Tag[] tags = new Tag[size];
        for (int i = 0; i < size; i++) {
            tags[i] = adapt(tag.get(i));
        }
        return new ListTag(type, Arrays.asList(tags));
    }
    
    public static Tag adapt(cn.nukkit.nbt.tag.Tag tag) {
        switch (tag.getId()) {
            case cn.nukkit.nbt.tag.Tag.TAG_Byte:
                return adapt((cn.nukkit.nbt.tag.ByteTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Short:
                return adapt((cn.nukkit.nbt.tag.ShortTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Int:
                return adapt((cn.nukkit.nbt.tag.IntTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Long:
                return adapt((cn.nukkit.nbt.tag.LongTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Double:
                return adapt((cn.nukkit.nbt.tag.DoubleTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Float:
                return adapt((cn.nukkit.nbt.tag.FloatTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_String:
                return adapt((cn.nukkit.nbt.tag.StringTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Byte_Array:
                return adapt((cn.nukkit.nbt.tag.ByteArrayTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Int_Array:
                return adapt((cn.nukkit.nbt.tag.IntArrayTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_Compound:
                return adapt((cn.nukkit.nbt.tag.CompoundTag) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_List:
                return adapt((cn.nukkit.nbt.tag.ListTag<?>) tag);
            case cn.nukkit.nbt.tag.Tag.TAG_End:
                return new EndTag();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static cn.nukkit.nbt.tag.ByteTag toNative(ByteTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.ByteTag(name, tag.getValue());
    }
    
    public static cn.nukkit.nbt.tag.ShortTag toNative(ShortTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.ShortTag(name, tag.getValue());
    }


    public static cn.nukkit.nbt.tag.IntTag toNative(IntTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.IntTag(name, tag.getValue());
    }


    public static cn.nukkit.nbt.tag.LongTag toNative(LongTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.LongTag(name, tag.getValue());
    }


    public static cn.nukkit.nbt.tag.DoubleTag toNative(DoubleTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.DoubleTag(name, tag.getValue());
    }


    public static cn.nukkit.nbt.tag.FloatTag toNative(FloatTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.FloatTag(name, tag.getValue());
    }


    public static cn.nukkit.nbt.tag.StringTag toNative(StringTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.StringTag(name, tag.getValue());
    }

    public static cn.nukkit.nbt.tag.ByteArrayTag toNative(ByteArrayTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.ByteArrayTag(name, tag.getValue().clone());
    }

    public static cn.nukkit.nbt.tag.IntArrayTag toNative(IntArrayTag tag, @Nullable String name) {
        return new cn.nukkit.nbt.tag.IntArrayTag(name, tag.getValue().clone());
    }

    public static cn.nukkit.nbt.tag.CompoundTag toNative(CompoundTag tag, @Nullable String name) {
        Map<String, cn.nukkit.nbt.tag.Tag> tags = new HashMap<>();
        tag.getValue().forEach((childName, child)-> tags.put(childName, toNative(child, childName)));
        return new cn.nukkit.nbt.tag.CompoundTag(name, tags);
    }
    
    public static cn.nukkit.nbt.tag.ListTag<?> toNative(ListTag tag, @Nullable String name) {
        cn.nukkit.nbt.tag.ListTag<cn.nukkit.nbt.tag.Tag> listTag = new cn.nukkit.nbt.tag.ListTag<>(name);
        tag.getValue().forEach(child -> listTag.add(toNative(child, null)));
        return listTag;
    }

    public static cn.nukkit.nbt.tag.Tag toNative(Tag tag, @Nullable String name) {
        switch (tag.getTypeCode()) {
            case NBTConstants.TYPE_BYTE: 
                return toNative((ByteTag) tag, name);
            case NBTConstants.TYPE_SHORT:
                return toNative((ShortTag) tag, name);
            case NBTConstants.TYPE_INT:
                return toNative((IntTag) tag, name);
            case NBTConstants.TYPE_LONG:
                return toNative((LongTag) tag, name);
            case NBTConstants.TYPE_FLOAT:
                return toNative((FloatTag) tag, name);
            case NBTConstants.TYPE_DOUBLE:
                return toNative((DoubleTag) tag, name);
            case NBTConstants.TYPE_STRING:
                return toNative((StringTag) tag, name);
            case NBTConstants.TYPE_BYTE_ARRAY:
                return toNative((ByteArrayTag) tag, name);
            case NBTConstants.TYPE_INT_ARRAY:
                return toNative((IntArrayTag) tag, name);
            case NBTConstants.TYPE_COMPOUND:
                return toNative((CompoundTag) tag, name);
            case NBTConstants.TYPE_LIST:
                return toNative((ListTag) tag, name);
            default:
            case NBTConstants.TYPE_LONG_ARRAY:
                throw new UnsupportedOperationException();    
        }
    }
}
