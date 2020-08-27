package com.sk89q.worldedit.powernukkit;

import cn.nukkit.Player;
import cn.nukkit.nbt.tag.CompoundTag;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.NullWorld;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

import static com.google.common.base.Preconditions.checkNotNull;

public class PowerNukkitEntity implements Entity {
    private final WeakReference<cn.nukkit.entity.Entity> entityRef;
    private final String saveId;
    public PowerNukkitEntity(cn.nukkit.entity.Entity entity) {
        checkNotNull(entity);
        this.entityRef = new WeakReference<>(entity);
        saveId = entity.getSaveId();
    }

    @Override
    public Extent getExtent() {
        cn.nukkit.entity.Entity entity = entityRef.get();
        if (entity != null) {
            return PowerNukkitAdapter.adapt(entity.getLevel());
        } else {
            return NullWorld.getInstance();
        }
    }

    @Override
    public Location getLocation() {
        cn.nukkit.entity.Entity entity = entityRef.get();
        if (entity != null) {
            return PowerNukkitAdapter.adapt((cn.nukkit.level.Location) entity);
        } else {
            return new Location(NullWorld.getInstance());
        }
    }

    @Override
    public boolean setLocation(Location location) {
        cn.nukkit.entity.Entity entity = entityRef.get();
        if (entity == null) {
            return false;
        }
        return entity.teleport(PowerNukkitAdapter.adapt(location));
    }

    @Override
    public EntityType getType() {
        return EntityTypes.parse(saveId);
    }

    @Nullable
    @Override
    public BaseEntity getState() {
        cn.nukkit.entity.Entity entity = entityRef.get();
        if (entity == null || entity instanceof Player) {
            return null;
        }
        
        entity.saveNBT();
        CompoundTag namedTag = entity.namedTag.copy();

        return new BaseEntity(getType(), PowerNukkitNbtAdapter.adapt(namedTag));
    }

    @Override
    public boolean remove() {
        cn.nukkit.entity.Entity entity = entityRef.get();
        if (entity != null) {
            entity.close();
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getFacet(Class<? extends T> cls) {
        cn.nukkit.entity.Entity entity = entityRef.get();
        if (entity != null && EntityProperties.class.isAssignableFrom(cls)) {
            return (T) new PowerNukkitEntityProperties(entity);
        } else {
            return null;
        }
    }
}
