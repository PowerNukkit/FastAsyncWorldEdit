package com.sk89q.worldedit.powernukkit;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.inventory.InventoryHolder;
import com.sk89q.worldedit.world.registry.BlockMaterial;

public class PowerNukkitMaterial implements BlockMaterial {
    
    private final Block block;

    public PowerNukkitMaterial(Block block) {
        this.block = block.clone();
    }

    @Override
    public boolean isAir() {
        return block.getId() == BlockID.AIR;
    }

    @Override
    public boolean isFullCube() {
        return block.isFullBlock();
    }

    @Override
    public boolean isOpaque() {
        return !block.isTransparent();
    }

    @Override
    public boolean isPowerSource() {
        return block.isPowerSource();
    }

    @Override
    public boolean isLiquid() {
        return block instanceof BlockLiquid;
    }

    @Override
    public boolean isSolid() {
        return block.isSolid();
    }

    @Override
    public float getHardness() {
        return (float) block.getHardness();
    }

    @Override
    public float getResistance() {
        return (float) block.getResistance();
    }

    @Override
    public float getSlipperiness() {
        return (float) block.getFrictionFactor();
    }

    @Override
    public int getLightValue() {
        return block.getLightLevel();
    }

    @Override
    public int getLightOpacity() {
        return Block.lightFilter[block.getId()];
    }

    @Override
    public boolean isFragileWhenPushed() {
        return block.breaksWhenMoved();
    }

    @Override
    public boolean isUnpushable() {
        return !block.sticksToPiston();
    }

    @Override
    public boolean isTicksRandomly() {
        switch (block.getId()) {
            case BlockID.GRASS:
            case BlockID.FARMLAND:
            case BlockID.MYCELIUM:
            case BlockID.SAPLING:
            case BlockID.LEAVES:
            case BlockID.LEAVES2:
            case BlockID.SNOW_LAYER:
            case BlockID.ICE:
            case BlockID.LAVA:
            case BlockID.STILL_LAVA:
            case BlockID.CACTUS:
            case BlockID.BEETROOT_BLOCK:
            case BlockID.CARROT_BLOCK:
            case BlockID.POTATO_BLOCK:
            case BlockID.MELON_STEM:
            case BlockID.PUMPKIN_STEM:
            case BlockID.WHEAT_BLOCK:
            case BlockID.SUGARCANE_BLOCK:
            case BlockID.RED_MUSHROOM:
            case BlockID.BROWN_MUSHROOM:
            case BlockID.NETHER_WART_BLOCK:
            case BlockID.FIRE:
            case BlockID.GLOWING_REDSTONE_ORE:
            case BlockID.COCOA_BLOCK:
            case BlockID.CORAL_FAN:
            case BlockID.CORAL_FAN_DEAD:
            case BlockID.BLOCK_KELP:
            case BlockID.SWEET_BERRY_BUSH:
            case BlockID.TURTLE_EGG:
            case BlockID.BAMBOO:
            case BlockID.BAMBOO_SAPLING:
            case BlockID.CRIMSON_NYLIUM:
            case BlockID.WARPED_NYLIUM:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isMovementBlocker() {
        return block.isSolid() && block.getBoundingBox() != null;
    }

    @Override
    public boolean isBurnable() {
        return block.getBurnAbility() != 0;
    }

    @Override
    public boolean isToolRequired() {
        return block.getToolType() != 0;
    }

    @Override
    public boolean isReplacedDuringPlacement() {
        return block.canBeReplaced();
    }

    @Override
    public boolean isTranslucent() {
        return block.isTransparent();
    }

    @Override
    public boolean hasContainer() {
        return block instanceof BlockEntityHolder<?> 
                && ((BlockEntityHolder<?>) block).getBlockEntityClass().isAssignableFrom(InventoryHolder.class);
    }

    @Override
    public int getMapColor() {
        return block.getColor().getRGB();
    }
}
