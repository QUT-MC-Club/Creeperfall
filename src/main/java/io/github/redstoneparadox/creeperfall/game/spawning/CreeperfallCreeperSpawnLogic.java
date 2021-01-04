package io.github.redstoneparadox.creeperfall.game.spawning;

import io.github.redstoneparadox.creeperfall.game.CreeperfallConfig;
import io.github.redstoneparadox.creeperfall.game.util.Timer;
import io.github.redstoneparadox.creeperfall.hooks.CreeperHooks;
import io.github.redstoneparadox.creeperfall.mixin.MobEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.plasmid.game.GameSpace;

import java.util.Objects;
import java.util.Random;

public class CreeperfallCreeperSpawnLogic {
	private final GameSpace gameSpace;
	private final CreeperfallConfig config;
	private final int maxCreepers;
	private final Random random;
	private final Timer spawnTimer;
	private final Timer creeperIncreaseTimer;
	private int currentCreepers = 1;

	public CreeperfallCreeperSpawnLogic(GameSpace gameSpace, CreeperfallConfig config) {
		this.gameSpace = gameSpace;
		this.config = config;
		this.maxCreepers = gameSpace.getPlayerCount() * config.maxCreepersPerPlayer;
		int stageLength = config.stageLengthSeconds * 20;
		int spawnDelay = config.creeperSpawnDelaySeconds * 20;
		this.random = new Random();
		this.spawnTimer = Timer.createRepeating(spawnDelay, this::spawnCreepers);
		this.creeperIncreaseTimer = Timer.createRepeating(stageLength, () -> {
			if (currentCreepers < maxCreepers) {
				currentCreepers += 1;
			}
		});
	}

	public void tick() {
		spawnTimer.tick();
		creeperIncreaseTimer.tick();
	}

	private void spawnCreepers() {
		int count = random.nextInt(currentCreepers) + 1;

		for (int i = 0; i < count; i++) {
			spawnCreeper();
		}
	}

	private void spawnCreeper() {
		ServerWorld world = gameSpace.getWorld();
		CreeperEntity entity = EntityType.CREEPER.create(world);

		int size = config.mapConfig.size;
		int radius = size/2;
		int x = random.nextInt(size) - radius;
		int y = 85;
		int z = random.nextInt(size) - radius;

		Objects.requireNonNull(entity).setPos(0, 105, 0);
		entity.updatePosition(x, y, z);
		entity.setVelocity(Vec3d.ZERO);

		entity.prevX = x;
		entity.prevY = y;
		entity.prevZ = z;

		entity.addStatusEffect(new StatusEffectInstance(
				StatusEffects.SLOW_FALLING,
				4096,
				1,
				true,
				false));
		entity.setHealth(0.5f);
		((MobEntityAccessor) entity).setExperiencePoints(0);
		((CreeperHooks) entity).setCreeperfallCreeper(true);
		entity.initialize(world, world.getLocalDifficulty(new BlockPos(0, 0, 0)), SpawnReason.NATURAL, null, null);
		world.spawnEntity(entity);
	}
}
