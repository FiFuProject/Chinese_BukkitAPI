package org.bukkit;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A RegionAccessor gives access to getting, modifying and spawning {@link Biome}, {@link BlockState} and {@link Entity},
 * as well as generating some basic structures.
 */
public interface RegionAccessor {

    /**
     * 获取指定{@link Location 位置}的{@link Biome 生物群系}.
     * <p>
     * 原文:Gets the {@link Biome} at the given {@link Location}.
     *
     * @param location 位置
     * @return 此位置处的生物群系
     */
    @NotNull
    Biome getBiome(@NotNull Location location);

    /**
     * 获取指定坐标的生物群系.
     * <p>
     * 原文:Gets the {@link Biome} at the given coordinates.
     *
     * @param x 方块的x坐标
     * @param y 方块的y坐标
     * @param z 方块的z坐标
     * @return 指定坐标位置处的生物群系
     */
    @NotNull
    Biome getBiome(int x, int y, int z);

    /**
     * 设置指定{@link Location 位置}的{@link Biome 生物群系}.
     * <p>
     * 原文:Sets the {@link Biome} at the given {@link Location}.
     *
     * @param location 位置
     * @param biome 此方块的新生物群系
     */
    void setBiome(@NotNull Location location, @NotNull Biome biome);

    /**
     * Sets the {@link Biome} for the given block coordinates
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @param biome New Biome type for this block
     */
    void setBiome(int x, int y, int z, @NotNull Biome biome);

    /**
     * Gets the {@link BlockState} at the given {@link Location}.
     *
     * @param location The location of the block state
     * @return Block state at the given location
     */
    @NotNull
    BlockState getBlockState(@NotNull Location location);

    /**
     * Gets the {@link BlockState} at the given coordinates.
     *
     * @param x X-coordinate of the block state
     * @param y Y-coordinate of the block state
     * @param z Z-coordinate of the block state
     * @return Block state at the given coordinates
     */
    @NotNull
    BlockState getBlockState(int x, int y, int z);

    /**
     * Gets the {@link BlockData} at the given {@link Location}.
     *
     * @param location The location of the block data
     * @return Block data at the given location
     */
    @NotNull
    BlockData getBlockData(@NotNull Location location);

    /**
     * Gets the {@link BlockData} at the given coordinates.
     *
     * @param x X-coordinate of the block data
     * @param y Y-coordinate of the block data
     * @param z Z-coordinate of the block data
     * @return Block data at the given coordinates
     */
    @NotNull
    BlockData getBlockData(int x, int y, int z);

    /**
     * Gets the type of the block at the given {@link Location}.
     *
     * @param location The location of the block
     * @return Material at the given coordinates
     */
    @NotNull
    Material getType(@NotNull Location location);

    /**
     * Gets the type of the block at the given coordinates.
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Material at the given coordinates
     */
    @NotNull
    Material getType(int x, int y, int z);

    /**
     * Sets the {@link BlockData} at the given {@link Location}.
     *
     * @param location The location of the block
     * @param blockData The block data to set the block to
     */
    void setBlockData(@NotNull Location location, @NotNull BlockData blockData);

    /**
     * Sets the {@link BlockData} at the given coordinates.
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @param blockData The block data to set the block to
     */
    void setBlockData(int x, int y, int z, @NotNull BlockData blockData);

    /**
     * Sets the {@link Material} at the given {@link Location}.
     *
     * @param location The location of the block
     * @param material The type to set the block to
     */
    void setType(@NotNull Location location, @NotNull Material material);

    /**
     * Sets the {@link Material} at the given coordinates.
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @param material The type to set the block to
     */
    void setType(int x, int y, int z, @NotNull Material material);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param location Location to spawn the tree
     * @param random Random to use to generated the tree
     * @param type Type of the tree to create
     * @return true if the tree was created successfully, otherwise false
     */
    boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type);

    /**
     * Creates a tree at the given {@link Location}
     * <p>
     * The provided consumer gets called for every block which gets changed
     * as a result of the tree generation. When the consumer gets called no
     * modifications to the world are done yet. Which means, that calling
     * {@link #getBlockState(Location)} in the consumer while return the state
     * of the block before the generation.
     * <p>
     * Modifications done to the {@link BlockState} in the consumer are respected,
     * which means that it is not necessary to call {@link BlockState#update()}
     *
     * @param location Location to spawn the tree
     * @param random Random to use to generated the tree
     * @param type Type of the tree to create
     * @param stateConsumer The consumer which should get called for every block which gets changed
     * @return true if the tree was created successfully, otherwise false
     */
    boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type, @Nullable Consumer<BlockState> stateConsumer);

    /**
     * 在指定的{@link Location 位置}创建一个实体.
     * <p>
     * 原文：
     * Creates a entity at the given {@link Location}
     *
     * @param location 生成实体的位置
     * @param type 生成的实体
     * @return 生成成功则返回此方法创建的实体
     */
    @NotNull
    Entity spawnEntity(@NotNull Location location, @NotNull EntityType type);

    /**
     * Creates a new entity at the given {@link Location}.
     *
     * @param loc the location at which the entity will be spawned.
     * @param type the entity type that determines the entity to spawn.
     * @param randomizeData whether or not the entity's data should be randomised
     *                      before spawning. By default entities are randomised
     *                      before spawning in regards to their equipment, age,
     *                      attributes, etc.
     *                      An example of this randomization would be the color of
     *                      a sheep, random enchantments on the equipment of mobs
     *                      or even a zombie becoming a chicken jockey.
     *                      If set to false, the entity will not be randomised
     *                      before spawning, meaning all their data will remain
     *                      in their default state and not further modifications
     *                      to the entity will be made.
     *                      Notably only entities that extend the
     *                      {@link org.bukkit.entity.Mob} interface provide
     *                      randomisation logic for their spawn.
     *                      This parameter is hence useless for any other type
     *                      of entity.
     * @return the spawned entity instance.
     */
    @NotNull
    public Entity spawnEntity(@NotNull Location loc, @NotNull EntityType type, boolean randomizeData);

    /**
     * Get a list of all entities in this RegionAccessor
     *
     * @return A List of all Entities currently residing in this world accessor
     */
    @NotNull
    List<Entity> getEntities();

    /**
     * Get a list of all living entities in this RegionAccessor
     *
     * @return A List of all LivingEntities currently residing in this world accessor
     */
    @NotNull
    List<LivingEntity> getLivingEntities();

    /**
     * Get a collection of all entities in this RegionAccessor matching the given
     * class/interface
     *
     * @param <T> an entity subclass
     * @param cls The class representing the type of entity to match
     * @return A List of all Entities currently residing in this world accessor
     *     that match the given class/interface
     */
    @NotNull
    <T extends Entity> Collection<T> getEntitiesByClass(@NotNull Class<T> cls);

    /**
     * Get a collection of all entities in this RegionAccessor matching any of the
     * given classes/interfaces
     *
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world accessor
     *     that match one or more of the given classes/interfaces
     */
    @NotNull
    Collection<Entity> getEntitiesByClasses(@NotNull Class<?>... classes);

    /**
     * 在指定的{@link Location 位置}根据给定的类生成一个实体.
     * <p>
     * 原文:Spawn an entity of a specific class at the given {@link Location}
     *
     * @param location 生成实体的{@link Location 位置}
     * @param clazz 生成{@link Entity 实体}的类
     * @param <T> 生成{@link Entity 实体}的类
     * @return 一个生成的{@link Entity 实体}实例
     * @throws IllegalArgumentException 如果参数为空或被要求生成的{@link Entity 实体}不能被生成则抛出错误
     */
    @NotNull
    <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz) throws IllegalArgumentException;

    /**
     * Spawn an entity of a specific class at the given {@link Location}, with
     * the supplied function run before the entity is added to the world.
     * <br>
     * Note that when the function is run, the entity will not be actually in
     * the world. Any operation involving such as teleporting the entity is undefined
     * until after this function returns.
     *
     * @param location the {@link Location} to spawn the entity at
     * @param clazz the class of the {@link Entity} to spawn
     * @param function the function to be run before the entity is spawned.
     * @param <T> the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     * @throws IllegalArgumentException if either parameter is null or the
     *     {@link Entity} requested cannot be spawned
     */
    @NotNull
    <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz, @Nullable Consumer<T> function) throws IllegalArgumentException;

    /**
     * Creates a new entity at the given {@link Location} with the supplied
     * function run before the entity is added to the world.
     * <br>
     * Note that when the function is run, the entity will not be actually in
     * the world. Any operation involving such as teleporting the entity is undefined
     * until after this function returns.
     * The passed function however is run after the potential entity's spawn
     * randomization and hence already allows access to the values of the mob,
     * whether or not those were randomized, such as attributes or the entity
     * equipment.
     *
     * @param location      the location at which the entity will be spawned.
     * @param clazz         the class of the {@link Entity} that is to be spawned.
     * @param <T>           the generic type of the entity that is being created.
     * @param randomizeData whether or not the entity's data should be randomised
     *                      before spawning. By default entities are randomised
     *                      before spawning in regards to their equipment, age,
     *                      attributes, etc.
     *                      An example of this randomization would be the color of
     *                      a sheep, random enchantments on the equipment of mobs
     *                      or even a zombie becoming a chicken jockey.
     *                      If set to false, the entity will not be randomised
     *                      before spawning, meaning all their data will remain
     *                      in their default state and not further modifications
     *                      to the entity will be made.
     *                      Notably only entities that extend the
     *                      {@link org.bukkit.entity.Mob} interface provide
     *                      randomisation logic for their spawn.
     *                      This parameter is hence useless for any other type
     *                      of entity.
     * @param function      the function to be run before the entity is spawned.
     * @return the spawned entity instance.
     * @throws IllegalArgumentException if either the world or clazz parameter are null.
     */
    @NotNull
    public <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz, boolean randomizeData, @Nullable Consumer<T> function) throws IllegalArgumentException;
}
