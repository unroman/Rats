package com.github.alexthe666.rats.data.ratlantis.tags;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatlantisEntityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RatlantisEntityTags extends EntityTypeTagsProvider {
	public RatlantisEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, RatsMod.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(Tags.EntityTypes.BOSSES).add(
				RatlantisEntityRegistry.DUTCHRAT.get(), RatlantisEntityRegistry.NEO_RATLANTEAN.get(),
				RatlantisEntityRegistry.RAT_BARON.get(), RatlantisEntityRegistry.RATLANTEAN_AUTOMATON.get());

		tag(EntityTypeTags.IMPACT_PROJECTILES).add(
				RatlantisEntityRegistry.LASER_BEAM.get(),
				RatlantisEntityRegistry.RATLANTIS_ARROW.get(), RatlantisEntityRegistry.RATTLING_GUN_BULLET.get());

		tag(EntityTypeTags.ARROWS).add(RatlantisEntityRegistry.RATLANTIS_ARROW.get());

		tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(
				RatlantisEntityRegistry.DUTCHRAT.get(), RatlantisEntityRegistry.GHOST_PIRAT.get(),
				RatlantisEntityRegistry.RAT_PROTECTOR.get(), RatlantisEntityRegistry.RATLANTEAN_AUTOMATON.get(),
				RatlantisEntityRegistry.RATLANTEAN_RATBOT.get(), RatlantisEntityRegistry.RATLANTEAN_SPIRIT.get());
	}
}
