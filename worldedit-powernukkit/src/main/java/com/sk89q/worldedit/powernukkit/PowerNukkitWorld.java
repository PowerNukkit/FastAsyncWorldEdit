package com.sk89q.worldedit.powernukkit;

import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.generator.object.tree.*;
import cn.nukkit.math.NukkitRandom;
import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.TreeGenerator.TreeType;
import com.sk89q.worldedit.world.AbstractWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldedit.world.weather.WeatherTypes;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class PowerNukkitWorld extends AbstractWorld {
    private static final Map<TreeType, PowerNukkitGenerator> treeTypeGenerator = new EnumMap<>(TreeType.class);
    static {
        Block jungleLog = BlockState.of(BlockID.WOOD).withProperty(BlockWood.OLD_LOG_TYPE, WoodType.JUNGLE).getBlock();
        Block jungleLeaves = BlockState.of(BlockID.LEAVES).withProperty(BlockLeaves.OLD_LEAF_TYPE, WoodType.JUNGLE).getBlock();
        treeTypeGenerator.put(TreeType.TREE, (level, position) -> { new ObjectOakTree().placeObject(level, position.x, position.y, position.z, new NukkitRandom()); return true; });
        treeTypeGenerator.put(TreeType.BIG_TREE, (level, position) -> { new ObjectOakTree().placeObject(level, position.x, position.y, position.z, new NukkitRandom()); return true; });
        treeTypeGenerator.put(TreeType.REDWOOD, (level, position) -> { new ObjectSpruceTree().placeObject(level, position.x, position.y, position.z, new NukkitRandom()); return true; });
        treeTypeGenerator.put(TreeType.TALL_REDWOOD, (level, position) -> { new ObjectBigSpruceTree(3/ 4f, 4).placeObject(level, position.x, position.y, position.z, new NukkitRandom()); return true; });
        treeTypeGenerator.put(TreeType.MEGA_REDWOOD, (level, position) -> { new ObjectBigSpruceTree(1/ 4f, 5).placeObject(level, position.x, position.y, position.z, new NukkitRandom()); return true; });
        treeTypeGenerator.put(TreeType.BIRCH, (level, position) -> { new ObjectBirchTree().placeObject(level, position.x, position.y, position.z, new NukkitRandom()); return true; });
        treeTypeGenerator.put(TreeType.TALL_BIRCH, (level, position) -> { new ObjectTallBirchTree().placeObject(level, position.x, position.y, position.z, new NukkitRandom()); return true; });
        treeTypeGenerator.put(TreeType.JUNGLE, (level, position) -> new ObjectJungleBigTree(10, 20, jungleLog, jungleLeaves).generate(level, new NukkitRandom(), position.asVector3()));
        treeTypeGenerator.put(TreeType.SMALL_JUNGLE, (level, position) -> new NewJungleTree(4, 7).generate(level, new NukkitRandom(), position.asVector3()));
        treeTypeGenerator.put(TreeType.JUNGLE_BUSH, (level, position) -> new NewJungleTree(1, 0).generate(level, new NukkitRandom(), position.asVector3()));
        treeTypeGenerator.put(TreeType.RED_MUSHROOM, (level, position) -> new BigMushroom(1).generate(level, new NukkitRandom(), position.asVector3()));
        treeTypeGenerator.put(TreeType.BROWN_MUSHROOM, (level, position) -> new BigMushroom(0).generate(level, new NukkitRandom(), position.asVector3()));
        treeTypeGenerator.put(TreeType.SWAMP, (level, position) -> new ObjectSwampTree().generate(level, new NukkitRandom(), position.asVector3()));
        treeTypeGenerator.put(TreeType.ACACIA, (level, position) -> new ObjectSavannaTree().generate(level, new NukkitRandom(), position.asVector3()));
        treeTypeGenerator.put(TreeType.DARK_OAK, (level, position) -> new ObjectDarkOakTree().generate(level, new NukkitRandom(), position.asVector3()));

        // Other mappings for WE-specific values
        treeTypeGenerator.put(TreeType.SHORT_JUNGLE, treeTypeGenerator.get(TreeType.SMALL_JUNGLE));
        treeTypeGenerator.put(TreeType.RANDOM, treeTypeGenerator.get(TreeType.BROWN_MUSHROOM));
        treeTypeGenerator.put(TreeType.RANDOM_REDWOOD, treeTypeGenerator.get(TreeType.REDWOOD));
        treeTypeGenerator.put(TreeType.PINE, treeTypeGenerator.get(TreeType.REDWOOD));
        treeTypeGenerator.put(TreeType.RANDOM_BIRCH, treeTypeGenerator.get(TreeType.BIRCH));
        treeTypeGenerator.put(TreeType.RANDOM_JUNGLE, treeTypeGenerator.get(TreeType.JUNGLE));
        treeTypeGenerator.put(TreeType.RANDOM_MUSHROOM, treeTypeGenerator.get(TreeType.BROWN_MUSHROOM));
        for (TreeGenerator.TreeType type : TreeGenerator.TreeType.values()) {
            if (treeTypeGenerator.get(type) == null) {
                WorldEdit.logger.error("No TreeType mapping for TreeGenerator.TreeType." + type);
            }
        }
    }
    
    public static PowerNukkitGenerator toPowerNukkitTreeGenerator(TreeType type) {
        return treeTypeGenerator.get(type);
    }
    
    private WeakReference<Level> levelRef;
    private final String levelName;
    
    public PowerNukkitWorld(Level level) {
        this.levelRef = new WeakReference<>(level);
        this.levelName = level.getName();
    }

    /**
     * Get the world handle.
     *
     * @return the world
     */
    public Level getLevel() {
        Level lvl = levelRef.get();
        if (lvl == null) {
            lvl = Server.getInstance().getLevelByName(levelName);
            if (lvl != null) {
                levelRef = new WeakReference<>(lvl);
            }
        }
        return checkNotNull(lvl, "The world was unloaded and the reference is unavailable");
    }

    protected Level getLevelChecked() throws WorldEditException {
        Level level = levelRef.get();
        if (level == null) {
            throw new WorldUnloadedException();
        }
        return level;
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        Level level = getLevel();

        return Arrays.stream(level.getEntities())
                .filter(e-> region.contains(PowerNukkitAdapter.asBlockVector(e)))
                .map(PowerNukkitAdapter::adapt)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<? extends Entity> getEntities() {
        return Arrays.stream(getLevel().getEntities())
                .map(PowerNukkitAdapter::adapt)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return getLevel().getName();
    }

    @Override
    public Path getStoragePath() {
        Level level = getLevel();
        return Paths.get(Server.getInstance().getDataPath())
                .resolve("worlds")
                .resolve(level.getFolderName());
    }

    @Override
    public int getBlockLightLevel(BlockVector3 position) {
        return getLevel().getBlockLightAt(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public boolean regenerate(Region region, EditSession editSession) {
        // TODO
        return false;
    }

    @Override
    public boolean clearContainerBlockContents(BlockVector3 position) {
        BlockEntity blockEntity = getLevel().getBlockEntity(PowerNukkitAdapter.adapt(position));
        if (!(blockEntity instanceof InventoryHolder)) {
            return false;
        }
        Inventory inventory = ((InventoryHolder) blockEntity).getInventory();
        if (inventory == null) {
            return false;
        }
        inventory.clearAll();
        return true;
    }

    @Override
    public boolean generateTree(TreeType type, EditSession editSession, BlockVector3 position) throws MaxChangedBlocksException {
        Level level = getLevel();
        PowerNukkitGenerator generator = toPowerNukkitTreeGenerator(type);
        // TODO Add support to a future BlockChangeDelegate
        return generator != null && generator.generate(level, PowerNukkitAdapter.adapt(position));
    }

    @Override
    public void dropItem(Vector3 position, BaseItemStack item) {
        getLevel().dropItem(PowerNukkitAdapter.adapt(position), PowerNukkitAdapter.adapt(item));
    }

    @Override
    public void checkLoadedChunk(BlockVector3 pt) {
        Level level = getLevel();
        int chunkX = pt.getBlockX() >> 4;
        int chunkZ = pt.getBlockZ() >> 4;
        level.getChunk(chunkX, chunkZ, true);
    }

    @Override
    public boolean equals(Object o) {
        final Level level = levelRef.get();
        if (o == null || level == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof PowerNukkitWorld) {
            Level otherLevel = ((PowerNukkitWorld) o).levelRef.get();
            return level.equals(otherLevel);
        }
        if (o instanceof World) {
            return ((World) o).getName().equals(level.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return levelName.hashCode();
    }

    @Override
    public WeatherType getWeather() {
        Level level = getLevel();
        if (level.isThundering()) {
            return WeatherTypes.THUNDER_STORM;
        } else if (level.isRaining()) {
            return WeatherTypes.RAIN;
        }
        
        return WeatherTypes.CLEAR;
    }

    @Override
    public long getRemainingWeatherDuration() {
        Level level = getLevel();
        if (level.isThundering()) {
            return level.getThunderTime();
        }
        return level.getRainTime();
    }

    @Override
    public void setWeather(WeatherType weatherType) {
        Level level = getLevel();
        if (weatherType == WeatherTypes.THUNDER_STORM) {
            level.setThundering(true);
        } else if (weatherType == WeatherTypes.RAIN) {
            level.setRaining(true);
        } else {
            level.setThundering(false);
            level.setRaining(false);
        }
    }

    @Override
    public void setWeather(WeatherType weatherType, long duration) {
        Level level = getLevel();
        if (weatherType == WeatherTypes.THUNDER_STORM) {
            level.setThundering(true);
            level.setThunderTime((int) duration);
            level.setRainTime((int) duration);
        } else if (weatherType == WeatherTypes.RAIN) {
            level.setRaining(true);
            level.setRainTime((int) duration);
        } else {
            level.setThundering(false);
            level.setRaining(false);
            level.setRainTime((int) duration);
        }
    }

    @Override
    public BlockVector3 getSpawnPosition() {
        return PowerNukkitAdapter.asBlockVector(getLevel().getSpawnLocation());
    }

    @Override
    public void simulateBlockMine(BlockVector3 position) {
        getLevel().useBreakOn(PowerNukkitAdapter.asVector3(position), null, null, true);
    }

    @Override
    public com.sk89q.worldedit.world.block.BlockState getBlock(int x, int y, int z) {
        Level level = getLevel();
        BlockState[] layers = new BlockState[level.getProvider().getMaximumLayer() - 1];
        BlockState main = level.getBlockStateAt(x, y, z, 0);
        for (int layer = 1; layer <= layers.length; layer++) {
            layers[layer - 1] = level.getBlockStateAt(x, y, z, layer);
        }
        com.sk89q.worldedit.world.block.BlockState blockState = PowerNukkitAdapter.asBlockState(main, layers);
        return blockState;
    }
}
