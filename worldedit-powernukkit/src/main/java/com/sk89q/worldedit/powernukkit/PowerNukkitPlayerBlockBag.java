package com.sk89q.worldedit.powernukkit;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extent.inventory.*;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockState;

public class PowerNukkitPlayerBlockBag extends BlockBag implements SlottableBlockBag {
    
    private Player player;
    private Item[] items;

    /**
     * Construct the object.
     *
     * @param player the player
     */
    public PowerNukkitPlayerBlockBag(Player player) {
        this.player = player;
    }


    /**
     * Loads inventory on first use.
     */
    private void loadInventory() {
        if (items == null) {
            PlayerInventory inventory = player.getInventory();
            int size = inventory.getSize();
            items = new Item[size];
            for (int i = 0; i < size; i++) {
                items[i] = inventory.getItem(i);
            }
        }
    }

    /**
     * Get the player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public void fetchBlock(BlockState blockState) throws BlockBagException {
        if (blockState.getBlockType().getMaterial().isAir()) {
            throw new IllegalArgumentException("Can't fetch air block");
        }

        loadInventory();

        boolean found = false;

        for (int slot = 0; slot < items.length; ++slot) {
            Item powerNukkitItem = items[slot];

            if (powerNukkitItem == null) {
                continue;
            }

            if (!PowerNukkitAdapter.equals(blockState.getBlockType(), powerNukkitItem)) {
                // Type id doesn't fit
                continue;
            }

            int currentAmount = powerNukkitItem.getCount();
            if (currentAmount < 0) {
                // Unlimited
                return;
            }

            if (currentAmount > 1) {
                powerNukkitItem.setCount(currentAmount - 1);
                found = true;
            } else {
                items[slot] = null;
                found = true;
            }

            break;
        }

        if (!found) {
            throw new OutOfBlocksException();
        }
    }

    @Override
    public void storeBlock(BlockState blockState, int amount) throws BlockBagException {
        if (blockState.getBlockType().getMaterial().isAir()) {
            throw new IllegalArgumentException("Can't store air block");
        }
        if (!blockState.getBlockType().hasItemType()) {
            throw new IllegalArgumentException("This block cannot be stored");
        }

        loadInventory();

        int freeSlot = -1;

        for (int slot = 0; slot < items.length; ++slot) {
            Item powerNukkitItem = items[slot];

            if (powerNukkitItem == null) {
                // Delay using up a free slot until we know there are no stacks
                // of this item to merge into

                if (freeSlot == -1) {
                    freeSlot = slot;
                }
                continue;
            }

            if (!PowerNukkitAdapter.equals(blockState.getBlockType(), powerNukkitItem)) {
                // Type id doesn't fit
                continue;
            }

            int currentAmount = powerNukkitItem.getCount();
            if (currentAmount < 0) {
                // Unlimited
                return;
            }
            if (currentAmount >= 64) {
                // Full stack
                continue;
            }

            int spaceLeft = 64 - currentAmount;
            if (spaceLeft >= amount) {
                powerNukkitItem.setCount(currentAmount + amount);
                return;
            }

            powerNukkitItem.setCount(64);
            amount -= spaceLeft;
        }

        if (freeSlot > -1) {
            items[freeSlot] = PowerNukkitAdapter.adapt(new BaseItemStack(blockState.getBlockType().getItemType(), amount));
            return;
        }

        throw new OutOfSpaceException(blockState.getBlockType());
    }

    @Override
    public void flushChanges() {
        if (items != null) {
            PlayerInventory inventory = player.getInventory();
            int size = size();
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, items[i]);
            }
            items = null;
        }
    }

    @Override
    public void addSourcePosition(Location pos) {
    }

    @Override
    public void addSingleSourcePosition(Location pos) {
    }

    @Override
    public BaseItem getItem(int slot) {
        loadInventory();
        return PowerNukkitAdapter.adapt(items[slot]);
    }

    @Override
    public void setItem(int slot, BaseItem block) {
        loadInventory();
        BaseItemStack stack = block instanceof BaseItemStack ? (BaseItemStack) block : new BaseItemStack(block.getType(), block.getNbtData(), 1);
        items[slot] = PowerNukkitAdapter.adapt(stack);
    }

}
