package com.sk89q.worldedit.powernukkit;

import cn.nukkit.item.Item;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.blocks.BaseItemStack;

import javax.annotation.Nullable;

public class PowerNukkitItemStack extends BaseItemStack {
    private final Item stack;
    private boolean loadedNBT;
    
    public PowerNukkitItemStack(Item stack) {
        super(PowerNukkitItemTypeAdapter.asItemType(stack.getId()));
        this.stack = stack;
    }

    @Override
    public int getAmount() {
        return stack.getCount();
    }

    @Nullable
    @Override
    public Object getNativeItem() {
        return stack;
    }
    
    public Item getPowerNukkitItem() {
        return stack;
    }

    @Override
    public boolean hasNbtData() {
        if (!loadedNBT) {
            return stack.hasCompoundTag();
        }
        return super.hasNbtData();
    }

    @Nullable
    @Override
    public CompoundTag getNbtData() {
        if (!loadedNBT) {
            loadedNBT = true;
            setNbtData(PowerNukkitNbtAdapter.adapt(stack.getNamedTag()));
        }
        return super.getNbtData();
    }

    @Override
    public void setNbtData(@Nullable CompoundTag nbtData) {
        if (nbtData != null) {
            stack.setNamedTag(PowerNukkitNbtAdapter.toNative(nbtData, null));
        } else {
            stack.clearNamedTag();
        }
        super.setNbtData(nbtData);
    }


    @Override
    public void setAmount(int amount) {
        stack.setCount(amount);
    }
}
