package com.github.alexthe666.rats.server.entity.ratlantis;

import com.github.alexthe666.rats.registry.RatlantisEntityRegistry;
import com.github.alexthe666.rats.registry.RatlantisItemRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.ai.goal.PiratWanderGoal;
import com.github.alexthe666.rats.server.entity.rat.AbstractRat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Pirat extends AbstractRat implements RangedAttackMob, Ratlanteans, Enemy, Pirats {

	private final RangedAttackGoal fireCannonballGoal = new RangedAttackGoal(this, 1.0D, 32, 70, 16.0F);
	private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.45D, false);
	private int attackCooldown = 70;

	public Pirat(EntityType<? extends AbstractRat> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		Arrays.fill(this.armorDropChances, 0.2F);
		Arrays.fill(this.handDropChances, 0.2F);
		this.moveControl = new MoveControl(this);
		this.navigation = new WaterBoundPathNavigation(this, this.getLevel());
		this.setCombatTask();
	}

	public static boolean checkPiratSpawnRules(EntityType<Pirat> type, LevelAccessor accessor, MobSpawnType reason, BlockPos pos, RandomSource random) {
		boolean flag = accessor.getDifficulty() != Difficulty.PEACEFUL && isDarkEnough(accessor, pos) && (reason == MobSpawnType.SPAWNER || accessor.getFluidState(pos).is(FluidTags.WATER));
		return random.nextInt(150) == 0 && flag;
	}

	public static boolean isDarkEnough(LevelAccessor accessor, BlockPos pos) {
		return accessor.getBrightness(LightLayer.SKY, pos) > 10;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(2, new PiratWanderGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.66D));
		this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 5, true, false, entity -> !(entity instanceof Ratlanteans) && !(entity instanceof Pirats) && entity != null && !entity.isAlliedTo(Pirat.this) && (!(entity instanceof Player player) || !player.isCreative())));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 30.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 5.0D)
				.add(Attributes.FOLLOW_RANGE, 16.0D);
	}

	public void setCombatTask() {
		if (!this.getLevel().isClientSide()) {
			this.goalSelector.removeGoal(this.meleeAttackGoal);
			this.goalSelector.removeGoal(this.fireCannonballGoal);
			if (this.isPassenger()) {
				this.goalSelector.addGoal(1, this.fireCannonballGoal);
			} else {
				this.goalSelector.addGoal(1, this.meleeAttackGoal);
			}
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.attackCooldown > 0) {
			this.attackCooldown--;
		}
	}

	@Override
	public boolean isHoldingItemInHands() {
		return true;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return true;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setCombatTask();
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(accessor, difficulty, type, data, tag);
		this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(RatlantisItemRegistry.PIRAT_CUTLASS.get()));
		this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(RatsItemRegistry.PIRAT_HAT.get()));
		if (!this.isPassenger()) {
			PiratBoat boat = new PiratBoat(RatlantisEntityRegistry.PIRAT_BOAT.get(), this.getLevel());
			boat.copyPosition(this);
			if (!this.getLevel().isClientSide()) {
				this.getLevel().addFreshEntity(boat);
			}
			this.startRiding(boat, true);
		}
		this.setCombatTask();
		return data;
	}

	@Override
	public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
		BlockPos pos = this.blockPosition();
		BlockState state = this.getLevel().getBlockState(pos.below());
		return this.getLevel().getDifficulty() != Difficulty.PEACEFUL && this.isValidLightLevel() && state.getMaterial() == Material.WATER && this.getRandom().nextFloat() < 0.1F;
	}

	@Override
	public boolean isTame() {
		return false;
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		boolean flag = super.startRiding(entity, force);
		this.setCombatTask();
		return flag;
	}

	@Override
	public void stopRiding() {
		super.stopRiding();
		this.setCombatTask();
	}

	@Override
	public void updateRiding(Entity riding) {
		super.updateRiding(riding);
		this.setPos(riding.getX(), riding.getY() + 0.5D, riding.getZ());
	}

	@Override
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		if (this.attackCooldown == 0) {
			this.lookAt(target, 180, 180);
			double d0 = target.getX() - this.getX();
			double d2 = target.getZ() - this.getZ();
			float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
			this.setYRot(f % 360);
			this.yBodyRot = getYRot();
			if (this.getVehicle() != null && this.getVehicle() instanceof PiratBoat) {
				((PiratBoat) this.getVehicle()).shoot(this);
			}
			this.attackCooldown = 70;
		}
	}
}
