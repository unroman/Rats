package com.github.alexthe666.rats.server.entity.rat;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.events.ForgeClientEvents;
import com.github.alexthe666.rats.client.render.RatsRenderType;
import com.github.alexthe666.rats.registry.*;
import com.github.alexthe666.rats.server.block.RatCageBlock;
import com.github.alexthe666.rats.server.block.RatTubeBlock;
import com.github.alexthe666.rats.server.entity.RatMount;
import com.github.alexthe666.rats.server.entity.RatMountBase;
import com.github.alexthe666.rats.server.entity.ai.goal.*;
import com.github.alexthe666.rats.server.entity.ai.navigation.control.*;
import com.github.alexthe666.rats.server.entity.ai.navigation.navigation.EtherealRatNavigation;
import com.github.alexthe666.rats.server.entity.ai.navigation.navigation.RatFlightNavigation;
import com.github.alexthe666.rats.server.entity.ai.navigation.navigation.RatNavigation;
import com.github.alexthe666.rats.server.entity.ratlantis.RatBiplaneMount;
import com.github.alexthe666.rats.server.items.OreRatNuggetItem;
import com.github.alexthe666.rats.server.items.RatSackItem;
import com.github.alexthe666.rats.server.items.RatStaffItem;
import com.github.alexthe666.rats.server.items.upgrades.*;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.*;
import com.github.alexthe666.rats.server.message.ManageRatStaffPacket;
import com.github.alexthe666.rats.server.message.RatsNetworkHandler;
import com.github.alexthe666.rats.server.message.SetDancingRatPacket;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.github.alexthe666.rats.server.misc.RatUtils;
import com.github.alexthe666.rats.server.misc.RatVariant;
import com.github.alexthe666.rats.server.misc.RatVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TamedRat extends InventoryRat {

	private static final EntityDataAccessor<Boolean> TOGA = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> VISUAL_FLAG = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DYED = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Byte> DYE_COLOR = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> DANCING = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> DANCE_MOVES = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HELD_RF = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> RESPAWN_COUNTDOWN = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Optional<GlobalPos>> PICKUP_POS = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.OPTIONAL_GLOBAL_POS);
	private static final EntityDataAccessor<Optional<GlobalPos>> DEPOSIT_POS = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.OPTIONAL_GLOBAL_POS);
	private static final EntityDataAccessor<Boolean> IS_IN_WHEEL = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<String> SPECIAL_DYE = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Integer> MOUNT_RESPAWN_COOLDOWN = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(TamedRat.class, EntityDataSerializers.BOOLEAN);

	public Direction depositFacing = Direction.UP;
	public boolean crafting = false;
	public boolean climbingTube = false;
	public int cookingProgress = 0;
	public int coinCooldown = 0;
	public int breedCooldown = 0;
	public BlockPos jukeboxPos;
	public FluidStack transportingFluid = FluidStack.EMPTY;
	private Goal harvestGoal;
	private Goal pickupGoal;
	private Goal depositGoal;
	private Goal attackGoal;
	/*
	   0 = tamed navigator
	   1 = flight navigator
	   2 = tube navigator
	   3 = aquatic navigator
	   4 = ethereal navigator
	   5 = cage navigator
	 */
	protected int navigatorType;
	public int rangedAttackCooldown = 0;
	public int visualCooldown = 0;
	private int poopCooldown = 0;
	public int randomEffectCooldown = 0;
	private int updateNavigationCooldown;
	public boolean isCurrentlyWorking;

	public TamedRat(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
		this.xpReward = 0;
		this.updateNavigationCooldown = 100;
		this.setupDynamicAI();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 8.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.35D)
				.add(Attributes.FLYING_SPEED, 0.35D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.FOLLOW_RANGE, 12.0D);
	}

	@Override
	protected void registerGoals() {
		this.harvestGoal = new RatHarvestCropsGoal(this);
		this.pickupGoal = new RatPickupGoal(this, RatPickupGoal.PickupType.INVENTORY);
		this.depositGoal = new RatDepositGoal(this, RatDepositGoal.DepositType.INVENTORY);
		this.attackGoal = new RatMeleeAttackGoal(this, 1.45D, true);
		this.goalSelector.addGoal(0, new RatFloatGoal(this));
		this.goalSelector.addGoal(1, this.attackGoal);
		this.goalSelector.addGoal(2, new RatFollowOwnerGoal(this, 1.0D, 10.0F, 3.0F));
		this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(3, this.harvestGoal);
		this.goalSelector.addGoal(4, this.depositGoal);
		this.goalSelector.addGoal(5, this.pickupGoal);
		this.goalSelector.addGoal(6, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(7, new RatWanderGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new RatPatrolGoal(this));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new RatTargetItemsGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Animal.class, true, entity -> EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(entity) && !entity.isBaby() && TamedRat.this.canMove() && TamedRat.this.shouldHuntAnimal()));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, true, entity -> entity instanceof Enemy && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(entity) && TamedRat.this.canMove() && TamedRat.this.shouldHuntMonster()));
		this.targetSelector.addGoal(2, new RatOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new RatOwnerHurtTargetGoal(this));
	}

	protected void setupDynamicAI() {
		AtomicReference<Goal> newHarvest = new AtomicReference<>(new RatHarvestCropsGoal(this));
		AtomicReference<Goal> newDeposit = new AtomicReference<>(new RatDepositGoal(this, RatDepositGoal.DepositType.INVENTORY));
		AtomicReference<Goal> newPickup = new AtomicReference<>(new RatPickupGoal(this, RatPickupGoal.PickupType.INVENTORY));
		AtomicReference<Goal> newAttack = new AtomicReference<>(new RatMeleeAttackGoal(this, 1.45D, true));

		RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof ChangesAIUpgrade, stack ->
				((ChangesAIUpgrade) stack.getItem()).addNewWorkGoals(this).forEach(goal -> {
					if (!(goal instanceof RatWorkGoal workGoal)) {
						throw new IllegalArgumentException("Rat Goals must implement the interface RatWorkGoal! Goal" + goal.getClass().getName() + "doesnt do this!");
					}
					switch (workGoal.getRatTaskType()) {
						case ATTACK -> newAttack.set(goal);
						case DEPOSIT -> newDeposit.set(goal);
						case PICKUP -> newPickup.set(goal);
						case HARVEST -> newHarvest.set(goal);
					}
				})
		);

		if (!this.getLevel().isClientSide()) {
			this.goalSelector.removeGoal(this.harvestGoal);
			this.goalSelector.removeGoal(this.depositGoal);
			this.goalSelector.removeGoal(this.pickupGoal);
			this.goalSelector.removeGoal(this.attackGoal);

			this.attackGoal = newAttack.get();
			this.depositGoal = newDeposit.get();
			this.pickupGoal = newPickup.get();
			this.harvestGoal = newHarvest.get();

			this.goalSelector.addGoal(1, this.attackGoal);
			this.goalSelector.addGoal(3, this.depositGoal);
			this.goalSelector.addGoal(4, this.pickupGoal);
			this.goalSelector.addGoal(5, this.harvestGoal);

			if (this.hasFlightUpgrade()) {
				this.switchNavigator(1);
			} else if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_AQUATIC.get())) {
				this.switchNavigator(3);
			} else if (RatUpgradeUtils.hasUpgrade(this, RatlantisItemRegistry.RAT_UPGRADE_ETHEREAL.get())) {
				this.switchNavigator(4);
			} else {
				this.switchNavigator(this.isInCage() ? 5 : this.isInTube() ? 2 : 0);
			}

		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(TOGA, false);
		this.getEntityData().define(DANCING, false);
		this.getEntityData().define(DANCE_MOVES, 0);
		this.getEntityData().define(HELD_RF, 0);
		this.getEntityData().define(RESPAWN_COUNTDOWN, 0);
		this.getEntityData().define(VISUAL_FLAG, false);
		this.getEntityData().define(DYED, false);
		this.getEntityData().define(FLYING, false);
		this.getEntityData().define(DYE_COLOR, (byte) 0);
		this.getEntityData().define(DEPOSIT_POS, Optional.empty());
		this.getEntityData().define(PICKUP_POS, Optional.empty());
		this.getEntityData().define(IS_IN_WHEEL, false);
		this.getEntityData().define(SPECIAL_DYE, "rainbow");
		this.getEntityData().define(MOUNT_RESPAWN_COOLDOWN, 20);
	}

	public void switchNavigator(int type) {
		if (type == 0) { //tamed
			this.moveControl = new RatMoveControl(this);
			this.navigation = new RatNavigation(this, this.getLevel());
			this.navigatorType = 0;
		} else if (type == 1) { //flying
			this.moveControl = new RatFlightMoveControl(this, 1.0F);
			this.navigation = new RatFlightNavigation(this, this.getLevel());
			this.navigatorType = 1;
		} else if (type == 2) { //tube
			this.moveControl = new RatTubeMoveControl(this);
			this.navigation = new RatNavigation(this, this.getLevel());
			this.navigatorType = 2;
		} else if (type == 3) { //aquatic
			this.moveControl = new SmoothSwimmingMoveControl(this, 360, 360, 10.0F, 1.0F, true);
			this.navigation = new AmphibiousPathNavigation(this, this.getLevel());
			this.navigatorType = 3;
		} else if (type == 4) { //ethereal
			this.moveControl = new EtherealRatMoveControl(this);
			this.navigation = new EtherealRatNavigation(this, this.getLevel());
			this.navigatorType = 4;
		} else if (type == 5) { //cage
			this.moveControl = new RatCageMoveControl(this);
			this.navigation = new RatNavigation(this, this.getLevel());
			this.navigatorType = 5;
		}
	}

	@Override
	public boolean isHoldingFood() {
		return RatUpgradeUtils.forEachUpgradeBool(this, (stack) -> stack.isRatHoldingFood(this), true) && super.isHoldingFood();
	}

	@Override
	protected boolean isVisuallySitting() {
		return super.isVisuallySitting() || this.isDancing();
	}

	@Override
	public boolean isHoldingItemInHands() {
		return (this.isEating() || (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.cookingProgress > 0) || this.holdsItemInHandUpgrade() || this.getMBTransferRate() > 0) && this.sleepProgress <= 0.0F;
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (this.getVehicle() instanceof LivingEntity living && this.isRidingSpecialMount()) {
			return living.doHurtTarget(entity);
		}
		boolean flag = entity.hurt(this.damageSources().mobAttack(this), (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
		if (flag) {
			this.doEnchantDamageEffects(this, entity);
			RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof PostAttackUpgrade, stack -> ((PostAttackUpgrade) stack.getItem()).afterHit(this, (LivingEntity) entity));
		}
		return flag;
	}

	public boolean isInCage() {
		return this.getLevel().getBlockState(this.blockPosition()).getBlock() instanceof RatCageBlock;
	}

	public boolean isInTube() {
		return this.getLevel().getBlockState(this.blockPosition()).getBlock() instanceof RatTubeBlock;
	}

	@Override
	public boolean onClimbable() {
		return this.isInTube() ? this.climbingTube : super.onClimbable();
	}

	@Override
	public int getArmorValue() {
		return super.getArmorValue() * 3;
	}

	@Override
	public boolean removeWhenFarAway(double dist) {
		return false;
	}

	@Override
	public boolean canDrownInFluidType(FluidType type) {
		return type == ForgeMod.WATER_TYPE.get() && (!RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_AQUATIC.get()) || !RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_UNDERWATER.get()));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("NavCooldown", this.updateNavigationCooldown);
		tag.putInt("CookingProgress", this.cookingProgress);
		tag.putInt("BreedCooldown", this.breedCooldown);
		tag.putInt("CoinCooldown", this.coinCooldown);
		tag.putInt("MountCooldown", this.getMountCooldown());
		tag.putInt("TransportingRF", this.getHeldRF());
		tag.putInt("RespawnCountdown", this.getRespawnCountdown());
		tag.putInt("Command", this.getCommandInteger());
		tag.putBoolean("VisualFlag", this.getVisualFlag());
		tag.putBoolean("Dancing", this.isDancing());
		tag.putBoolean("Toga", this.hasToga());
		tag.putBoolean("Dyed", this.isDyed());
		tag.putByte("DyeColor", (byte) this.getDyeColor());
		tag.putString("SpecialDye", this.getSpecialDye());
		this.getDepositPos().flatMap(pos -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).resultOrPartial(RatsMod.LOGGER::error)).ifPresent(tag1 -> tag.put("DepositPos", tag1));
		this.getPickupPos().flatMap(pos -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).resultOrPartial(RatsMod.LOGGER::error)).ifPresent(tag1 -> tag.put("PickupPos", tag1));
		tag.putInt("RandomEffectCooldown", this.randomEffectCooldown);
		if (this.transportingFluid != null) {
			CompoundTag fluidTag = new CompoundTag();
			this.transportingFluid.writeToNBT(fluidTag);
			tag.put("TransportingFluid", fluidTag);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.updateNavigationCooldown = tag.getInt("NavCooldown");
		this.cookingProgress = tag.getInt("CookingProgress");
		this.breedCooldown = tag.getInt("BreedCooldown");
		this.coinCooldown = tag.getInt("CoinCooldown");
		this.setMountCooldown(tag.getInt("MountCooldown"));
		this.randomEffectCooldown = tag.getInt("RandomEffectCooldown");
		this.setHeldRF(tag.getInt("TransportingRF"));
		this.setRespawnCountdown(tag.getInt("RespawnCountdown"));
		this.setCommandInteger(tag.getInt("Command"));
		this.setDancing(tag.getBoolean("Dancing"));
		this.setVisualFlag(tag.getBoolean("VisualFlag"));
		this.setToga(tag.getBoolean("Toga"));
		this.setDyed(tag.getBoolean("Dyed"));
		this.setDyeColor((tag.getByte("DyeColor")));
		this.setSpecialDye(tag.getString("SpecialDye"));
		if (tag.contains("DepositPos")) {
			this.setDepositPos(GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("DepositPos")).resultOrPartial(RatsMod.LOGGER::error).orElse(null));
		}
		if (tag.contains("PickupPos")) {
			this.setPickupPos(GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("PickupPos")).resultOrPartial(RatsMod.LOGGER::error).orElse(null));
		}
		if (tag.contains("DepositFacing")) {
			this.depositFacing = Direction.values()[tag.getInt("DepositFacing")];
		}
		if (tag.contains("TransportingFluid")) {
			CompoundTag fluidTag = tag.getCompound("TransportingFluid");
			if (!fluidTag.isEmpty()) {
				this.transportingFluid = FluidStack.loadFluidStackFromNBT(fluidTag);
			}
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.isBaby() && this.getCommand() != RatCommand.SIT) {
			this.setCommand(RatCommand.SIT);
		}
		if (this.breedCooldown > 0) {
			this.breedCooldown--;
		}
		if (this.isOrderedToSit() && this.getTarget() != null) {
			this.setTarget(null);
		}

		if (this.getTarget() != null && this.getMountCooldown() > 0) {
			this.setTarget(null);
		}

		if (this.getRespawnCountdown() > 0) {
			this.setRespawnCountdown(this.getRespawnCountdown() - 1);
		}

		if (!this.getLevel().isClientSide() && this.getMountEntityType() != null && !this.isPassenger() && this.getMountCooldown() == 0) {
			Entity entity = this.getMountEntityType().create(this.getLevel());
			entity.copyPosition(this);
			if (entity instanceof Mob mob && this.getLevel() instanceof ServerLevelAccessor accessor) {
				ForgeEventFactory.onFinalizeSpawn(mob, accessor, this.getLevel().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
			}
			this.getLevel().addFreshEntity(entity);

			this.getLevel().broadcastEntityEvent(this, (byte) 127);
			this.startRiding(entity, true);
		}

		RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof TickRatUpgrade, stack -> ((TickRatUpgrade) stack.getItem()).tick(this));

		if (RatConfig.upgradeRegenRate > 0) {
			RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof StatBoostingUpgrade, stack -> {
				if (((StatBoostingUpgrade) stack.getItem()).regeneratesHealth() && this.getHealth() < this.getMaxHealth() && this.tickCount % RatConfig.upgradeRegenRate == 0) {
					this.heal(1.0F);
				}
			});
		}

		if (this.updateNavigationCooldown-- == 0) {
			this.updateNavigationCooldown = 60;
			int savedNav = this.navigatorType;
			if (this.isInCage()) {
				this.switchNavigator(5);
			} else if (this.isInTube()) {
				this.switchNavigator(2);
			} else if (this.hasFlightUpgrade()) {
				this.switchNavigator(1);
			} else {
				this.switchNavigator(savedNav);
			}
		}

		this.setNoGravity(this.isFlying());
		if (this.isFlying()) {
			if (this.isOrderedToSit() || this.verticalCollisionBelow || this.isOnGround()) {
				this.setFlying(false);
			}
			if (Math.abs(this.getDeltaMovement().x()) < 0.01D && Math.abs(this.getDeltaMovement().z()) < 0.01D) {
				if (Math.abs(this.getDeltaMovement().y()) > 0.0D) {
					this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.1D, 1.0D));
				}
			}
		}

		if (this.getMountCooldown() > 0) {
			this.setMountCooldown(this.getMountCooldown() - 1);
		}
		if (this.rangedAttackCooldown > 0) {
			this.rangedAttackCooldown--;
		}
		if (this.visualCooldown > 0) {
			this.visualCooldown--;
		}
		if (this.poopCooldown > 0) {
			this.poopCooldown--;
		}
		if (this.isDancing() && this.getAnimation() != this.getDanceAnimation()) {
			this.setAnimation(this.getDanceAnimation());
		}
		if (this.isDancing() && (this.jukeboxPos == null || this.jukeboxPos.distToCenterSqr(this.getX(), this.getY(), this.getZ()) > 256.0D || !this.getLevel().getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX))) {
			this.setDancing(false);
		}
		if (!this.getLevel().isClientSide() && this.getLevel().getBlockState(this.blockPosition()).is(RatsBlockRegistry.RAT_QUARRY_PLATFORM.get()) && this.getLevel().isEmptyBlock(this.blockPosition().above())) {
			this.setPos(this.getX(), this.getY() + 1, this.getZ());
			this.getNavigation().stop();
		}

		if (this.jumping && !this.getLevel().isClientSide() && this.getLevel().getBlockState(this.blockPosition().above()).is(RatsBlockRegistry.RAT_QUARRY_PLATFORM.get()) && this.getLevel().isEmptyBlock(this.blockPosition().above(2))) {
			this.setPos(this.getX(), this.getY() + 1, this.getZ());
			this.getNavigation().stop();
		}
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return !RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_QUARRY.get()) && !RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_AQUATIC.get()) && super.isPushedByFluid(type);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isBaby() || this.isInvulnerableTo(source) || (source.is(DamageTypes.IN_WALL) && this.isPassenger())) {
			return false;
		} else {
			if (this.getVehicle() != null && this.isRidingSpecialMount()) {
				this.getVehicle().hurt(source, amount);
				this.invulnerableTime = 20;
				return false;
			}

			Entity entity = source.getEntity();

			if (entity != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
				amount = (amount + 1.0F) / 2.0F;
			}

			return super.hurt(source, amount);
		}
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(ForgeSpawnEggItem.fromEntityType(RatsEntityRegistry.RAT.get()));
	}

	public void setFlying(boolean flying) {
		this.getEntityData().set(FLYING, flying);
	}

	public boolean isFlying() {
		return this.getEntityData().get(FLYING);
	}

	public void setToga(boolean toga) {
		this.getEntityData().set(TOGA, toga);
	}

	public boolean hasToga() {
		return this.getEntityData().get(TOGA);
	}

	public boolean getVisualFlag() {
		return this.getEntityData().get(VISUAL_FLAG);
	}

	public void setVisualFlag(boolean flag) {
		this.getEntityData().set(VISUAL_FLAG, flag);
	}

	public boolean isDancing() {
		return this.getEntityData().get(DANCING);
	}

	public void setDancing(boolean dancing) {
		this.getEntityData().set(DANCING, dancing);
	}

	public int getDanceMoves() {
		return this.getEntityData().get(DANCE_MOVES);
	}

	public void setDanceMoves(int moves) {
		this.getEntityData().set(DANCE_MOVES, moves);
	}

	public int getHeldRF() {
		return this.getEntityData().get(HELD_RF);
	}

	public void setHeldRF(int rf) {
		this.getEntityData().set(HELD_RF, rf);
	}

	public int getRespawnCountdown() {
		return this.getEntityData().get(RESPAWN_COUNTDOWN);
	}

	public void setRespawnCountdown(int respawn) {
		this.getEntityData().set(RESPAWN_COUNTDOWN, respawn);
	}

	public boolean isInWheel() {
		return this.getEntityData().get(IS_IN_WHEEL);
	}

	public void setInWheel(boolean wheel) {
		this.getEntityData().set(IS_IN_WHEEL, wheel);
	}

	public boolean isDyed() {
		return this.getEntityData().get(DYED);
	}

	public void setDyed(boolean dyed) {
		this.getEntityData().set(DYED, dyed);
	}

	public int getDyeColor() {
		return this.getEntityData().get(DYE_COLOR);
	}

	public void setDyeColor(int color) {
		this.getEntityData().set(DYE_COLOR, (byte) (color));
	}

	public String getSpecialDye() {
		return this.getEntityData().get(SPECIAL_DYE);
	}

	public void setSpecialDye(String keyword) {
		this.getEntityData().set(SPECIAL_DYE, keyword);
	}


	public Optional<GlobalPos> getPickupPos() {
		return this.getEntityData().get(PICKUP_POS);
	}

	public void setPickupPos(@Nullable GlobalPos pos) {
		this.getEntityData().set(PICKUP_POS, Optional.ofNullable(pos));
	}

	public Optional<GlobalPos> getDepositPos() {
		return this.getEntityData().get(DEPOSIT_POS);
	}

	public void setDepositPos(@Nullable GlobalPos pos) {
		this.getEntityData().set(DEPOSIT_POS, Optional.ofNullable(pos));
	}

	public int getMountCooldown() {
		return this.getEntityData().get(MOUNT_RESPAWN_COOLDOWN);
	}

	public void setMountCooldown(int cooldown) {
		this.getEntityData().set(MOUNT_RESPAWN_COOLDOWN, cooldown);
	}

	@Override
	public boolean isEating() {
		return super.isEating() && this.getCommand().allowsEating;
	}

	@Override
	public void onItemEaten() {
		ItemStack handCopy = this.getMainHandItem().copy();
		this.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
		if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_ORE_DOUBLING.get()) && OreDoublingRatUpgradeItem.isProcessable(handCopy)) {
			ItemStack nugget = OreRatNuggetItem.saveResourceToNugget(this.getLevel(), handCopy, true).copyWithCount(2);
			if (RatConfig.ratFartNoises) {
				this.playSound(RatsSoundRegistry.RAT_POOP.get(), 0.5F + this.getRandom().nextFloat() * 0.5F, 1.0F + this.getRandom().nextFloat() * 0.5F);
			}
			if (!this.getLevel().isClientSide()) {
				this.spawnAtLocation(nugget, 0.0F);
			}
		} else if (this.getRandom().nextFloat() <= 0.05F) {
			ItemStack nugget = new ItemStack(RatsItemRegistry.RAT_NUGGET.get());
			if (RatConfig.ratFartNoises) {
				this.playSound(RatsSoundRegistry.RAT_POOP.get(), 0.5F + this.getRandom().nextFloat() * 0.5F, 1.0F + this.getRandom().nextFloat() * 0.5F);
			}
			if (!this.getLevel().isClientSide()) {
				this.spawnAtLocation(nugget, 0.0F);
			}
		}
	}

	public void createBabiesFrom(TamedRat mother, TamedRat father) {
		TamedRat baby = new TamedRat(RatsEntityRegistry.TAMED_RAT.get(), this.getLevel());
		baby.setMale(this.getRandom().nextBoolean());
		RatVariant babyColor;
		if ((father.getColorVariant().isBreedingExclusive() || mother.getColorVariant().isBreedingExclusive()) && this.getRandom().nextInt(6) == 0) {
			babyColor = RatVariants.getRandomBreedingExclusiveVariant(this.getRandom());
		} else {
			if (this.getRandom().nextInt(10) == 0) {
				babyColor = RatVariants.getRandomVariant(this.getRandom(), true);
			} else {
				babyColor = this.getRandom().nextBoolean() ? father.getColorVariant() : mother.getColorVariant();
			}
		}
		baby.setColorVariant(babyColor);
		baby.setPos(mother.getX() - 0.5F + mother.getRandom().nextFloat(), mother.getY(), mother.getZ() - 0.5F + mother.getRandom().nextFloat());
		baby.setAge(-24000);
		baby.setCommand(RatCommand.SIT);
		if (mother.isTame()) {
			baby.setTame(true);
			baby.setOwnerUUID(mother.getOwnerUUID());
		} else if (father.isTame()) {
			baby.setTame(true);
			baby.setOwnerUUID(father.getOwnerUUID());
		}
		this.getLevel().addFreshEntity(baby);
	}

	public boolean isPickable() {
		return !this.isPassenger();
	}

	public ItemStack getResultForRecipe(RecipeType<? extends SingleItemRecipe> recipe, ItemStack stack) {
		Optional<? extends SingleItemRecipe> optional = this.getLevel().getRecipeManager().getRecipeFor(recipe, new SimpleContainer(stack), this.getLevel());
		if (optional.isPresent()) {
			ItemStack itemstack = optional.get().getResultItem(this.getLevel().registryAccess());
			if (!itemstack.isEmpty()) {
				ItemStack itemstack1 = itemstack.copy();
				itemstack1.setCount(stack.getCount() * itemstack.getCount());
				return itemstack1;
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean tryDepositItemInContainers(ItemStack burntItem) {
		if (getLevel().getBlockEntity(this.blockPosition()) != null) {
			BlockEntity te = getLevel().getBlockEntity(this.blockPosition());
			if (te != null) {
				LazyOptional<IItemHandler> handler = te.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
				if (handler.resolve().isPresent()) {
					if (ItemHandlerHelper.insertItem(handler.resolve().get(), burntItem, true).isEmpty()) {
						ItemHandlerHelper.insertItem(handler.resolve().get(), burntItem, false);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected void doPush(Entity entity) {
		if (!this.crafting) {
			entity.push(this);
		}
	}

	public Animation getDanceAnimation() {
		if (this.getDanceMoves() == 0) {
			return ANIMATION_DANCE;
		}
		return NO_ANIMATION;
	}

	public boolean shouldDropExperience() {
		return super.shouldDropExperience() && !RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_ANGEL.get());
	}

	@Override
	protected boolean shouldDropLoot() {
		return this.shouldDropExperience();
	}

	@Override
	protected void dropEquipment() {
		if (!RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_ANGEL.get())) {
			super.dropEquipment();
		}
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean playerKill) {
		if (this.hasToga()) {
			this.spawnAtLocation(new ItemStack(RatlantisItemRegistry.RAT_TOGA.get()), 0.0F);
		}
		super.dropCustomDeathLoot(source, looting, playerKill);
	}

	@Override
	protected void handleBeforeRemoval() {
		this.spawnAngelCopy();
	}

	public void spawnAngelCopy() {
		if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_ANGEL.get())) {
			TamedRat copy = RatsEntityRegistry.TAMED_RAT.get().create(this.getLevel());
			CompoundTag tag = new CompoundTag();
			this.addAdditionalSaveData(tag);
			tag.putBoolean("NoAI", false);
			tag.putShort("HurtTime", (short) 0);
			tag.putInt("HurtByTimestamp", 0);
			tag.putShort("DeathTime", (short) 0);
			copy.readAdditionalSaveData(tag);
			copy.setHealth(copy.getMaxHealth());
			copy.copyPosition(this);
			copy.setRespawnCountdown(1200);
			this.getLevel().addFreshEntity(copy);
		}
	}

	@Override
	public boolean canBeAffected(MobEffectInstance instance) {
		if (instance.getEffect() == MobEffects.POISON && (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_POISON.get()) || RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_DAMAGE_PROTECTION.get()))) {
			return false;
		}
		return super.canBeAffected(instance);
	}

	@Override
	public void updateRiding(Entity riding) {
		super.updateRiding(riding);
		if (riding.isVehicle() && riding instanceof Strider strider) {
			riding.clearFire();
			this.setPos(riding.getX(), riding.getY() + strider.getPassengersRidingOffset() + 0.15F, riding.getZ());
			strider.boost();
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (this.getRespawnCountdown() > 0 || itemstack.getItem() instanceof SpawnEggItem) {
			return InteractionResult.PASS;
		}
		if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_CARRAT.get())) {
			if (player.getFoodData().needsFood()) {
				player.getFoodData().eat(1, 0.1F);
				player.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
				for (int i = 0; i < 8; i++) {
					double d0 = this.getRandom().nextGaussian() * 0.02D;
					double d1 = this.getRandom().nextGaussian() * 0.02D;
					double d2 = this.getRandom().nextGaussian() * 0.02D;
					this.getLevel().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.CARROT)), this.getX() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.getRandom().nextFloat() * this.getBbHeight() * 2.0F) - (double) this.getBbHeight(), this.getZ() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
				}
				return InteractionResult.SUCCESS;
			}
		}
		if (!this.isBaby() && this.isOwnedBy(player)) {
			if (player.isSecondaryUseActive() && !this.isPassenger()) {
				if (player.getPassengers().size() < 3) {
					this.startRiding(player, true);
					player.displayClientMessage(Component.translatable("entity.rats.rat.dismount_instructions"), true);
				}
				return InteractionResult.sidedSuccess(this.getLevel().isClientSide());
			}

			if (itemstack.is(RatlantisItemRegistry.RAT_TOGA.get())) {
				if (!this.hasToga()) {
					if (!player.isCreative()) {
						itemstack.shrink(1);
					}
				} else {
					if (!this.getLevel().isClientSide()) {
						this.spawnAtLocation(new ItemStack(RatlantisItemRegistry.RAT_TOGA.get()), 0.0F);
					}
				}
				this.setToga(!this.hasToga());
				this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1F, 1.5F);
			}

			if (itemstack.is(RatsBlockRegistry.DYE_SPONGE.get().asItem()) && this.isDyed()) {
				this.setDyed(false);
				this.setDyeColor(0);
				for (int i = 0; i < 8; i++) {
					double d0 = this.getRandom().nextGaussian() * 0.02D;
					double d1 = this.getRandom().nextGaussian() * 0.02D;
					double d2 = this.getRandom().nextGaussian() * 0.02D;
					this.getLevel().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(RatsBlockRegistry.DYE_SPONGE.get())), this.getX() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.getRandom().nextFloat() * this.getBbHeight() * 2.0F) - (double) this.getBbHeight(), this.getZ() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
				}
				this.playSound(RatsSoundRegistry.DYE_SPONGE_USED.get(), this.getSoundVolume(), this.getVoicePitch());
				return InteractionResult.SUCCESS;
			}
			if (itemstack.is(RatsItemRegistry.RATBOW_ESSENCE.get())) {
				if (this.applySpecialDyeIfPossible(itemstack)) {
					return InteractionResult.SUCCESS;
				}
			}
			if (this.applyNormalDyeIfPossible(itemstack)) {
				return InteractionResult.SUCCESS;
			}
			if (itemstack.is(RatsItemRegistry.RAT_SACK.get())) {
				RatSackItem.packRatIntoSack(itemstack, this, RatSackItem.getRatsInStack(itemstack) + 1);
				this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1, 1);
				this.discard();
				player.swing(hand);
				return InteractionResult.SUCCESS;
			} else if (itemstack.getItem() instanceof RatStaffItem) {
				player.getCapability(RatsCapabilityRegistry.SELECTED_RAT).ifPresent(cap -> cap.setSelectedRat(this));
				player.swing(hand);
				if (!this.getLevel().isClientSide() && player instanceof ServerPlayer sp) {
					RatsNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new ManageRatStaffPacket(this.getId(), BlockPos.ZERO, Direction.NORTH.ordinal(), false, false));
				}
				player.displayClientMessage(Component.translatable("entity.rats.rat.staff.bind", this.getName()), true);
				return InteractionResult.SUCCESS;
			} else if (itemstack.getItem() == Items.ARROW) {
				itemstack.shrink(1);
				ItemStack ratArrowStack = new ItemStack(RatsItemRegistry.RAT_ARROW.get());
				CompoundTag tag = new CompoundTag();
				CompoundTag ratTag = new CompoundTag();
				this.addAdditionalSaveData(ratTag);
				tag.put("Rat", ratTag);
				ratArrowStack.setTag(tag);
				if (itemstack.isEmpty()) {
					player.setItemInHand(hand, ratArrowStack);
				} else if (!player.getInventory().add(ratArrowStack)) {
					player.drop(ratArrowStack, false);
				}
				this.playSound(RatsSoundRegistry.RAT_HURT.get(), 1, 1);
				player.swing(hand);
				this.discard();
				return InteractionResult.SUCCESS;
			}
		}
		return super.mobInteract(player, hand);
	}

	public boolean applyNormalDyeIfPossible(ItemStack stack) {
		if (stack.getItem() instanceof DyeItem item && this.getDyeColor() != item.getDyeColor().getId()) {
			if (!this.isDyed()) {
				this.setDyed(true);
			}
			this.setDyeColor(item.getDyeColor().getId());
			for (int i = 0; i < 8; i++) {
				double d0 = this.getRandom().nextGaussian() * 0.02D;
				double d1 = this.getRandom().nextGaussian() * 0.02D;
				double d2 = this.getRandom().nextGaussian() * 0.02D;
				this.getLevel().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), this.getX() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.getRandom().nextFloat() * this.getBbHeight() * 2.0F) - (double) this.getBbHeight(), this.getZ() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
			}
			this.playSound(RatsSoundRegistry.ESSENCE_APPLIED.get(), this.getSoundVolume(), this.getVoicePitch());
			stack.shrink(1);
			return true;
		}
		return false;
	}

	public boolean applySpecialDyeIfPossible(ItemStack stack) {
		if (!this.isDyed()) {
			this.setDyed(true);
		}
		String name = stack.getHoverName().getString();
		if ((RatsRenderType.GlintType.getRenderTypeBasedOnKeyword(name) == null && this.getDyeColor() != 100) || RatsRenderType.GlintType.getRenderTypeBasedOnKeyword(name) != RatsRenderType.GlintType.getRenderTypeBasedOnKeyword(this.getSpecialDye())) {
			this.setDyeColor(100);
			this.setSpecialDye(name);
			for (int i = 0; i < 8; i++) {
				double d0 = this.getRandom().nextGaussian() * 0.02D;
				double d1 = this.getRandom().nextGaussian() * 0.02D;
				double d2 = this.getRandom().nextGaussian() * 0.02D;
				this.getLevel().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(RatsItemRegistry.RATBOW_ESSENCE.get())), this.getX() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.getRandom().nextFloat() * this.getBbHeight() * 2.0F) - (double) this.getBbHeight(), this.getZ() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
			}
			this.playSound(RatsSoundRegistry.ESSENCE_APPLIED.get(), this.getSoundVolume(), this.getVoicePitch());
			stack.shrink(1);
			return true;
		}
		return false;
	}

	@Override
	public void setTame(boolean tamed) {
		if (tamed) {
			Arrays.fill(this.armorDropChances, 1.1F);
			Arrays.fill(this.handDropChances, 1.1F);
		}
		super.setTame(tamed);
	}

	@Override
	public int getAmbientSoundInterval() {
		return RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_CHRISTMAS.get()) ? 1000 : 200;
	}

	@Override
	public boolean causeFallDamage(float dist, float mult, DamageSource source) {
		if (!this.isInvulnerableTo(source) && !this.isPassenger()) {
			return super.causeFallDamage(dist, mult, source);
		}
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
		if (!this.hasFlightUpgrade()) {
			super.checkFallDamage(y, onGround, state, pos);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 85) {
			this.crafting = true;
		} else if (id == 86) {
			this.crafting = false;
		} else if (id == 127) {
			for (int k = 0; k < 20; ++k) {
				double d2 = this.getRandom().nextGaussian() * 0.02D;
				double d0 = this.getRandom().nextGaussian() * 0.02D;
				double d1 = this.getRandom().nextGaussian() * 0.02D;
				this.getLevel().addParticle(ParticleTypes.POOF, this.getX() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.getRandom().nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d2, d0, d1);
			}
		} else {
			super.handleEntityEvent(id);
		}
	}

	protected SoundEvent getAmbientSound() {
		if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_CHRISTMAS.get())) {
			return RatsSoundRegistry.RAT_SANTA.get();
		}
		if (RatsMod.ICEANDFIRE_LOADED && RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_DRAGON.get())) {
			SoundEvent possibleDragonSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("iceandfire", "firedragon_child_idle"));
			if (possibleDragonSound != null) {
				return possibleDragonSound;
			}
		}
		return super.getAmbientSound();
	}

	protected SoundEvent getDeathSound() {
		if (RatsMod.ICEANDFIRE_LOADED && RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_DRAGON.get())) {
			SoundEvent possibleDragonSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("iceandfire", "firedragon_child_death"));
			if (possibleDragonSound != null) {
				return possibleDragonSound;
			}
		}
		return super.getDeathSound();
	}

	protected SoundEvent getHurtSound(DamageSource source) {
		if (RatsMod.ICEANDFIRE_LOADED && RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_DRAGON.get())) {
			SoundEvent possibleDragonSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("iceandfire", "firedragon_child_hurt"));
			if (possibleDragonSound != null) {
				return possibleDragonSound;
			}
		}
		return super.getHurtSound(source);
	}

	public boolean onHearFlute(Player player, RatCommand ratCommand) {
		if (this.isOwnedBy(player) && !this.isBaby() && !RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_NO_FLUTE.get())) {
			this.setCommand(ratCommand);
			return true;
		}
		return false;
	}

	public boolean canRatPickupItem(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		if ((RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_BLACKLIST.get()) || RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_WHITELIST.get())) && !RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_MINER.get())) {
			CompoundTag tag;
			if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_BLACKLIST.get())) {
				tag = RatUpgradeUtils.getUpgrade(this, RatsItemRegistry.RAT_UPGRADE_BLACKLIST.get()).getTag();
			} else {
				tag = RatUpgradeUtils.getUpgrade(this, RatsItemRegistry.RAT_UPGRADE_WHITELIST.get()).getTag();
			}
			String ourItemID = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
			if (tag != null && tag.contains("Items", 9)) {
				ListTag list = tag.getList("Items", 10);
				if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_BLACKLIST.get())) {
					for (int i = 0; i < list.size(); ++i) {
						String itemID = list.getCompound(i).getString("id");
						if (ourItemID.equals(itemID)) {
							return false;
						}
					}
					return true;
				} else {
					//whitelist
					for (int i = 0; i < list.size(); ++i) {
						String itemID = list.getCompound(i).getString("id");
						if (ourItemID.equals(itemID)) {
							return true;
						}
					}
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public MobType getMobType() {
		if (this.getInventory() != null) {
			if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_UNDEAD.get())) {
				return MobType.UNDEAD;
			}
			if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_AQUATIC.get())) {
				return MobType.WATER;
			}
		}
		return super.getMobType();
	}

	@Override
	public boolean isCurrentlyGlowing() {
		if (this.getLevel().isClientSide() && ForgeClientEvents.isRatSelectedOnStaff(this)) return true;
		return super.isCurrentlyGlowing();
	}

	public void attemptTeleport(double x, double y, double z) {
		double d0 = this.getX();
		double d1 = this.getY();
		double d2 = this.getZ();
		this.setPos(x, y, z);
		this.getLevel().broadcastEntityEvent(this, (byte) 84);
		boolean flag = false;
		BlockPos blockpos = this.blockPosition();

		if (this.getLevel().isLoaded(blockpos)) {
			boolean flag1 = false;

			while (!flag1 && blockpos.getY() > 0) {
				BlockPos blockpos1 = blockpos.below();
				BlockState state = this.getLevel().getBlockState(blockpos1);

				if (state.getMaterial().blocksMotion()) {
					flag1 = true;
				} else {
					this.setPos(this.getX(), this.getY() - 1, this.getZ());
					blockpos = blockpos1;
				}
			}

			if (flag1) {
				this.teleportTo(this.getX(), this.getY(), this.getZ());

				if (this.getLevel().noCollision(this) && !this.getLevel().containsAnyLiquid(this.getBoundingBox())) {
					flag = true;
				}
			}
		}

		if (!flag) {
			this.teleportTo(d0, d1, d2);
		} else {
			this.playSound(RatsSoundRegistry.RAT_TELEPORT.get(), 1, 1);
		}
	}

	public boolean isDirectPathBetweenPoints(Vec3 target) {
		BlockHitResult result = this.getLevel().clip(new ClipContext(this.position(), target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		BlockPos pos = result.getBlockPos();
		BlockPos sidePos = result.getBlockPos().relative(result.getDirection());
		if (!this.getLevel().isEmptyBlock(pos) || !this.getLevel().isEmptyBlock(sidePos)) {
			return true;
		} else {
			return result.getType() == HitResult.Type.MISS;
		}
	}

	@Override
	public boolean canBeSeenAsEnemy() {
		return !RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_UNDEAD.get()) || super.canBeSeenAsEnemy();
	}

	@Override
	public boolean isNoAi() {
		return super.isNoAi() || this.getRespawnCountdown() > 0;
	}

	public boolean holdsItemInHandUpgrade() {
		boolean bool = RatUpgradeUtils.forEachUpgradeBool(this, stack -> stack instanceof HoldsItemUpgrade upgrade && !upgrade.isFakeHandRender(), false);

		return !this.isInWheel() && bool;
	}

	@Override
	public boolean shouldPlayIdleAnimations() {
		boolean bool = RatUpgradeUtils.forEachUpgradeBool(this, (stack) -> stack.playIdleAnimation(this), true);
		return super.shouldPlayIdleAnimations() && !this.isInTube() && !this.isInWheel() && this.cookingProgress <= 0 && bool;
	}

	public boolean hasAnyUpgrades() {
		for (EquipmentSlot slot : RatUpgradeUtils.UPGRADE_SLOTS) {
			if (!this.getItemBySlot(slot).isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public void onUpgradeChanged() {
		this.setupDynamicAI();

		Entity vehicle = this.getVehicle();
		if (!this.getLevel().isClientSide() && vehicle instanceof RatMount mount) {
			if (!RatUpgradeUtils.hasUpgrade(this, mount.getUpgradeItem())) {
				this.getLevel().broadcastEntityEvent(this, (byte) 127);
				this.stopRiding();
				vehicle.discard();
			}
		}

		AttributeSupplier defaultAttributes = TamedRat.createAttributes().build();
		List<Attribute> attributeList = new ArrayList<>();

		RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof StatBoostingUpgrade, stack ->
				((StatBoostingUpgrade) stack.getItem()).getAttributeBoosts().forEach((attribute, aDouble) -> {
					this.tryIncreaseStat(attribute, aDouble);
					attributeList.add(attribute);
				}));

		for (Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues()) {
			if (!attributeList.contains(attribute) && this.getAttribute(attribute) != null) {
				Objects.requireNonNull(this.getAttribute(attribute)).setBaseValue(defaultAttributes.getBaseValue(attribute));
			}
		}

		if (this.getHeldRF() > this.getRFTransferRate()) {
			this.setHeldRF(0);
		}
		this.heal(this.getMaxHealth());
	}

	public void tryIncreaseStat(Attribute stat, double value) {
		double prev = Objects.requireNonNull(this.getAttribute(stat)).getValue();
		if (prev < value) {
			Objects.requireNonNull(this.getAttribute(stat)).setBaseValue(value);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		if (this.getRespawnCountdown() > 0) {
			return true;
		}
		AtomicBoolean upgradePrevented = new AtomicBoolean(false);
		RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof DamageImmunityUpgrade, stack -> {
			if (((DamageImmunityUpgrade) stack.getItem()).isImmuneToDamageSource(this, source)) {
				upgradePrevented.set(true);
			}
		});

		if (RatUpgradeUtils.hasUpgrade(this, RatsItemRegistry.RAT_UPGRADE_CREATIVE.get())) {
			return source.getEntity() == null || source.getEntity() instanceof LivingEntity living && !this.isOwnedBy(living);
		}
		return upgradePrevented.get() || super.isInvulnerableTo(source);
	}

	@Override
	public boolean isInvulnerable() {
		return super.isInvulnerable() || this.getRespawnCountdown() > 0;
	}

	@Override
	public void setRecordPlayingNearby(BlockPos pos, boolean partying) {
		int moves = this.getRandom().nextInt(4);
		if (!this.isDancing() && partying) {
			this.setDanceMoves(moves);
		}
		this.setDancing(partying);
		this.jukeboxPos = pos;
		if (this.getLevel().isClientSide()) {
			RatsNetworkHandler.CHANNEL.sendToServer(new SetDancingRatPacket(this.getId(), partying, pos.asLong(), moves));
		}
	}

	public boolean shouldDepositItem(ItemStack item) {
		return RatUpgradeUtils.forEachUpgradeBool(this, (stack) -> stack.shouldDepositItem(this, item), true);
	}

	public int getRFTransferRate() {
		AtomicInteger energy = new AtomicInteger();
		RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof EnergyRatUpgradeItem, stack ->
				energy.set(((EnergyRatUpgradeItem) stack.getItem()).getRFTransferRate()));

		return energy.get();
	}

	public int getMBTransferRate() {
		AtomicInteger fluid = new AtomicInteger();
		RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof BucketRatUpgradeItem, stack ->
				fluid.set(((BucketRatUpgradeItem) stack.getItem()).getMbTransferRate()));

		return fluid.get();
	}

	@Override
	public boolean isAlliedTo(Entity entity) {
		LivingEntity livingentity = this.getOwner();
		if (entity == livingentity) {
			return true;
		} else if (entity instanceof RatMountBase mount) {
			return mount.getRat() != null && this.isAlliedTo(mount.getRat());
		} else if (livingentity != null && entity instanceof TamableAnimal animal) {
			return animal.isOwnedBy(livingentity);
		} else {
			return livingentity != null ? livingentity.isAlliedTo(entity) : super.isAlliedTo(entity);
		}
	}

	@Nullable
	private EntityType<?> getMountEntityType() {
		AtomicReference<EntityType<?>> type = new AtomicReference<>(null);
		RatUpgradeUtils.forEachUpgrade(this, item -> item instanceof MountRatUpgradeItem, stack -> type.set(((MountRatUpgradeItem<?>) stack.getItem()).getEntityType()));
		return type.get();
	}

	public double getRatDistanceCenterSq(double x, double y, double z) {
		double d0 = this.getX() - x - 0.5D;
		double d1 = this.getY() - y - 0.5D;
		double d2 = this.getZ() - z - 0.5D;
		if (this.getVehicle() != null && getMountEntityType() != null && this.getVehicle().getType() == getMountEntityType()) {
			d0 = this.getVehicle().getX() - x - 0.5D;
			d1 = this.getVehicle().getY() - y - 0.5D;
			d2 = this.getVehicle().getZ() - z - 0.5D;
		}
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public double getRatDistanceSq(double x, double y, double z) {
		double d0 = this.getX() - x;
		double d1 = this.getY() - y;
		double d2 = this.getZ() - z;
		if (this.getVehicle() != null && this.getMountEntityType() != null && this.getVehicle().getType() == this.getMountEntityType()) {
			d0 = this.getVehicle().getX() - x;
			d1 = this.getVehicle().getY() - y;
			d2 = this.getVehicle().getZ() - z;
		}
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	@Override
	public double distanceToSqr(Vec3 vec) {
		double d0 = this.getX() - vec.x();
		double d1 = this.getY() - vec.y();
		double d2 = this.getZ() - vec.z();
		if (this.getVehicle() != null && this.getMountEntityType() != null && this.getVehicle().getType() == this.getMountEntityType()) {
			d0 = this.getVehicle().getX() - vec.x();
			d1 = this.getVehicle().getY() - vec.y();
			d2 = this.getVehicle().getZ() - vec.z();
		}
		return d0 * d0 + d1 * d1 + d2 * d2;
	}


	public boolean isRidingSpecialMount() {
		boolean ret = false;
		if (this.getVehicle() != null && this.getMountEntityType() != null) {
			ret = this.getVehicle().getType().equals(this.getMountEntityType());
		}
		return ret;
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		return !this.isRidingSpecialMount() || !RatUtils.isRidingOrBeingRiddenBy(this, target);
	}

	public double getRatHarvestDistance(double expansion) {
		return (3.5F + expansion) * this.getRatDistanceModifier();
	}

	public double getRatDistanceModifier() {
		if (this.isRidingSpecialMount()) {
			Entity entity = this.getVehicle();
			if (entity != null) {
				if (entity instanceof RatBiplaneMount) {
					return 3.95D;
				}
				return 1.5D;
			}

		}
		return 1.0D;
	}

	public boolean hasFlightUpgrade() {
		boolean bool = RatUpgradeUtils.forEachUpgradeBool(this, (stack) -> stack.canFly(this), false);
		return bool || (RatUpgradeUtils.hasUpgrade(this, RatlantisItemRegistry.RAT_UPGRADE_BIPLANE_MOUNT.get()) && this.isRidingSpecialMount());
	}
}
