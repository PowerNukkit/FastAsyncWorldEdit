package com.sk89q.worldedit.powernukkit;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.EntityProjectile;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.entity.metadata.EntityProperties;

import static com.google.common.base.Preconditions.checkNotNull;

public class PowerNukkitEntityProperties implements EntityProperties {
    private final Entity entity;
    
    public PowerNukkitEntityProperties(Entity entity) {
        checkNotNull(entity);
        this.entity = entity;
    }

    @Override
    public boolean isPlayerDerived() {
        return entity instanceof EntityHuman;
    }

    @Override
    public boolean isProjectile() {
        return entity instanceof EntityProjectile;
    }

    @Override
    public boolean isItem() {
        return entity instanceof EntityItem;
    }

    @Override
    public boolean isFallingBlock() {
        return entity instanceof EntityFallingBlock;
    }

    @Override
    public boolean isPainting() {
        return entity instanceof EntityPainting;
    }

    @Override
    public boolean isItemFrame() {
        return false;
    }

    @Override
    public boolean isBoat() {
        return entity instanceof EntityBoat;
    }

    @Override
    public boolean isMinecart() {
        return entity instanceof EntityMinecartAbstract;
    }

    @Override
    public boolean isTNT() {
        return entity instanceof EntityPrimedTNT || entity instanceof EntityMinecartTNT;
    }

    @Override
    public boolean isExperienceOrb() {
        return entity instanceof EntityXPOrb;
    }

    @Override
    public boolean isLiving() {
        return entity instanceof EntityLiving;
    }

    @Override
    public boolean isAnimal() {
        return entity instanceof EntityAnimal;
    }

    @Override
    public boolean isAmbient() {
        return entity instanceof EntityBat;
    }

    @Override
    public boolean isNPC() {
        return entity instanceof EntityVillager || entity instanceof EntityVillagerV1;
    }

    @Override
    public boolean isGolem() {
        return entity.getSaveId().equals("IronGolem");
    }

    @Override
    public boolean isTamed() {
        return entity instanceof EntityTameable && ((EntityTameable) entity).isTamed();
    }

    @Override
    public boolean isTagged() {
        return entity instanceof EntityLiving && entity.hasCustomName();
    }

    @Override
    public boolean isArmorStand() {
        return entity instanceof EntityArmorStand;
    }

    @Override
    public boolean isPasteable() {
        return !(entity instanceof Player || entity.getSaveId().equals("EnderDragon"));
    }
}
