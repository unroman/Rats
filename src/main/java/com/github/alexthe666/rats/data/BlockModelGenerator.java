package com.github.alexthe666.rats.data;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatlantisBlockRegistry;
import com.github.alexthe666.rats.registry.RatsBlockRegistry;
import com.github.alexthe666.rats.server.block.DutchratBellBlock;
import com.github.alexthe666.rats.server.block.RatAttractorBlock;
import com.github.alexthe666.rats.server.block.RatCageBlock;
import com.github.alexthe666.rats.server.block.RatHoleBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockModelGenerator extends BlockStateProvider {

	public BlockModelGenerator(PackOutput output, ExistingFileHelper helper) {
		super(output, RatsMod.MODID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.builtinEntity(RatsBlockRegistry.AUTO_CURDLER.get(), "minecraft:block/cauldron_side");
		this.simpleBlock(RatsBlockRegistry.BLOCK_OF_BLUE_CHEESE.get());
		this.simpleBlock(RatsBlockRegistry.BLOCK_OF_NETHER_CHEESE.get());
		this.simpleBlock(RatsBlockRegistry.BLOCK_OF_CHEESE.get());
		this.simpleBlock(RatsBlockRegistry.CHEESE_CAULDRON.get(), this.models().withExistingParent("cauldron_cheese", new ResourceLocation(RatsMod.MODID, "block/template_cauldron_with_transforms"))
				.texture("bottom", new ResourceLocation("block/cauldron_bottom"))
				.texture("content", this.blockPrefix("block_of_cheese"))
				.texture("inside", new ResourceLocation("block/cauldron_inner"))
				.texture("particle", new ResourceLocation("block/cauldron_side"))
				.texture("side", new ResourceLocation("block/cauldron_side"))
				.texture("top", new ResourceLocation("block/cauldron_top")));
		this.simpleBlock(RatsBlockRegistry.BLUE_CHEESE_CAULDRON.get(), this.models().withExistingParent("cauldron_blue_cheese", new ResourceLocation(RatsMod.MODID, "block/template_cauldron_with_transforms"))
				.texture("bottom", new ResourceLocation("block/cauldron_bottom"))
				.texture("content", this.blockPrefix("block_of_blue_cheese"))
				.texture("inside", new ResourceLocation("block/cauldron_inner"))
				.texture("particle", new ResourceLocation("block/cauldron_side"))
				.texture("side", new ResourceLocation("block/cauldron_side"))
				.texture("top", new ResourceLocation("block/cauldron_top")));
		this.simpleBlock(RatsBlockRegistry.NETHER_CHEESE_CAULDRON.get(), this.models().withExistingParent("cauldron_nether_cheese", new ResourceLocation(RatsMod.MODID, "block/template_cauldron_with_transforms"))
				.texture("bottom", new ResourceLocation("block/cauldron_bottom"))
				.texture("content", this.blockPrefix("block_of_nether_cheese"))
				.texture("inside", new ResourceLocation("block/cauldron_inner"))
				.texture("particle", new ResourceLocation("block/cauldron_side"))
				.texture("side", new ResourceLocation("block/cauldron_side"))
				.texture("top", new ResourceLocation("block/cauldron_top")));
		this.simpleBlock(RatsBlockRegistry.MILK_CAULDRON.get(), this.models().withExistingParent("cauldron_milk", new ResourceLocation(RatsMod.MODID, "block/template_cauldron_with_transforms"))
				.texture("bottom", new ResourceLocation("block/cauldron_bottom"))
				.texture("content", new ResourceLocation("forge", "block/milk_still"))
				.texture("inside", new ResourceLocation("block/cauldron_inner"))
				.texture("particle", new ResourceLocation("block/cauldron_side"))
				.texture("side", new ResourceLocation("block/cauldron_side"))
				.texture("top", new ResourceLocation("block/cauldron_top")));
		this.simpleBlock(RatsBlockRegistry.COMPRESSED_GARBAGE.get());
		this.simpleBlock(RatsBlockRegistry.CURSED_GARBAGE.get());
		this.simpleBlock(RatsBlockRegistry.DYE_SPONGE.get());
		this.simpleBlock(RatsBlockRegistry.FISH_BARREL.get(), this.models().cube("fish_barrel",
						new ResourceLocation("block/barrel_bottom"), this.blockPrefix("fish_barrel_top"),
						new ResourceLocation("block/barrel_side"), new ResourceLocation("block/barrel_side"),
						new ResourceLocation("block/barrel_side"), new ResourceLocation("block/barrel_side"))
				.texture("particle", new ResourceLocation("block/barrel_side")));
		ConfiguredModel[] TRASH = new ConfiguredModel[8];
		for (int i = 0; i < TRASH.length; i++) {
			TRASH[i] = ConfiguredModel.builder().weight(i == 0 ? 10 : 1).modelFile(this.models().cubeAll("garbage_" + i, this.blockPrefix("garbage_" + i))).buildLast();
		}
		this.simpleBlock(RatsBlockRegistry.GARBAGE_PILE.get(), TRASH);
		this.horizontalBlock(RatsBlockRegistry.JACK_O_RATERN.get(), this.models().orientable("jack_o_ratern",
				new ResourceLocation("block/pumpkin_side"),
				this.blockPrefix("jack_o_ratern"),
				new ResourceLocation("block/pumpkin_top")));
		this.trapdoorBlockWithRenderType((TrapDoorBlock) RatsBlockRegistry.MANHOLE.get(), this.blockPrefix("manhole"), true, "translucent");
		this.simpleBlock(RatsBlockRegistry.MARBLED_CHEESE_RAW.get());
		this.simpleBlock(RatsBlockRegistry.PIED_GARBAGE.get());
		this.simpleBlock(RatsBlockRegistry.PIED_WOOL.get());
		this.simpleBlock(RatsBlockRegistry.PURIFIED_GARBAGE.get());
		this.getVariantBuilder(RatsBlockRegistry.RAT_ATTRACTOR.get()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(this.models().getExistingFile(this.blockPrefix("rat_attractor" +
						(state.getValue(RatAttractorBlock.POWERED) ? "_active" : "") +
						(state.getValue(RatAttractorBlock.CONNECTED_UP) ? "" : "_no_chain")))).build());
		this.cageBlock(RatsBlockRegistry.RAT_CAGE.get());
		this.cageBlock(RatsBlockRegistry.RAT_CAGE_BREEDING_LANTERN.get());
		this.cageBlock(RatsBlockRegistry.RAT_CAGE_DECORATED.get());
		this.cageBlock(RatsBlockRegistry.RAT_CAGE_WHEEL.get());
		this.simpleBlock(RatsBlockRegistry.RAT_CRAFTING_TABLE.get(), this.models().cube("rat_crafting_table",
						this.blockPrefix("block_of_cheese"), this.blockPrefix("rat_crafting_table_top"),
						this.blockPrefix("rat_crafting_table_side"), this.blockPrefix("rat_crafting_table_side"),
						this.blockPrefix("rat_crafting_table"), this.blockPrefix("rat_crafting_table"))
				.texture("particle", this.blockPrefix("rat_crafting_table")));
		this.getMultipartBuilder(RatsBlockRegistry.RAT_HOLE.get())
				.part().modelFile(models().getExistingFile(new ResourceLocation(RatsMod.MODID, "block/rat_hole"))).addModel().end()
				.part().modelFile(models().getExistingFile(new ResourceLocation(RatsMod.MODID, "block/rat_hole_plug"))).addModel().condition(RatHoleBlock.NORTH, false).end()
				.part().modelFile(models().getExistingFile(new ResourceLocation(RatsMod.MODID, "block/rat_hole_plug"))).rotationY(180).addModel().condition(RatHoleBlock.SOUTH, false).end()
				.part().modelFile(models().getExistingFile(new ResourceLocation(RatsMod.MODID, "block/rat_hole_plug"))).rotationY(900).addModel().condition(RatHoleBlock.EAST, false).end()
				.part().modelFile(models().getExistingFile(new ResourceLocation(RatsMod.MODID, "block/rat_hole_plug"))).rotationY(270).addModel().condition(RatHoleBlock.WEST, false).end();

		this.simpleBlock(RatsBlockRegistry.RAT_QUARRY.get(), this.models().cube("rat_quarry",
						this.blockPrefix("rat_quarry_bottom"), this.blockPrefix("rat_quarry_top"),
						this.blockPrefix("rat_quarry_side"), this.blockPrefix("rat_quarry_side"),
						this.blockPrefix("rat_quarry_side"), this.blockPrefix("rat_quarry_side"))
				.texture("particle", this.blockPrefix("rat_quarry_side")));
		this.simpleBlock(RatsBlockRegistry.RAT_QUARRY_PLATFORM.get(), this.models().getExistingFile(this.blockPrefix("rat_quarry_platform")));
		this.simpleBlock(RatsBlockRegistry.RAT_TRAP.get(), this.models().getExistingFile(new ResourceLocation("block/oak_planks")));
		this.simpleBlock(RatsBlockRegistry.RAT_UPGRADE_BLOCK.get());
		this.simpleBlock(RatsBlockRegistry.TRASH_CAN.get(), this.models().cubeAll("trash_can", new ResourceLocation("block/cauldron_side")));
		this.simpleBlock(RatsBlockRegistry.UPGRADE_COMBINER.get(), this.models().getExistingFile(this.blockPrefix("upgrade_combiner")));
		this.simpleBlock(RatsBlockRegistry.UPGRADE_SEPARATOR.get(), this.models().getExistingFile(this.blockPrefix("upgrade_separator")));

		this.simpleBlock(RatlantisBlockRegistry.AIR_RAID_SIREN.get(), this.models().getExistingFile(this.blockPrefix("air_raid_siren")));
		this.simpleBlock(RatlantisBlockRegistry.BLACK_MARBLED_CHEESE.get());
		this.simpleBlock(RatlantisBlockRegistry.BRAIN_BLOCK.get(), this.models().cube("brain_block",
						this.blockPrefix("brain_bottom"), this.blockPrefix("brain_top"),
						this.blockPrefix("brain_front"), this.blockPrefix("brain_back"),
						this.blockPrefix("brain_side"), this.blockPrefix("brain_side"))
				.texture("particle", this.blockPrefix("brain_side")));
		this.simpleBlock(RatlantisBlockRegistry.CHEESE_ORE.get());
		this.simpleBlock(RatlantisBlockRegistry.CHUNKY_CHEESE_TOKEN.get(), this.models().cubeAll("chunky_cheese_token", this.itemPrefix("chunky_cheese_token")).renderType(new ResourceLocation("cutout")));
		this.horizontalBlock(RatlantisBlockRegistry.COMPRESSED_RAT.get(), this.models().cube("compressed_rat",
						this.blockPrefix("compressed_rat_top"), this.blockPrefix("compressed_rat_top"),
						this.blockPrefix("compressed_rat_front"), this.blockPrefix("compressed_rat_back"),
						this.blockPrefix("compressed_rat_other_side"), this.blockPrefix("compressed_rat_side"))
				.texture("particle", this.blockPrefix("compressed_rat_side")));

		this.getVariantBuilder(RatlantisBlockRegistry.DUTCHRAT_BELL.get()).forAllStates(state -> {
			ModelFile type = switch (state.getValue(DutchratBellBlock.ATTACHMENT)) {
				case FLOOR ->
						this.models().withExistingParent("dutchrat_bell_floor", new ResourceLocation("block/bell_floor"))
								.texture("particle", this.blockPrefix("pirat_log"))
								.texture("bar", this.blockPrefix("pirat_log"))
								.texture("post", this.blockPrefix("pirat_log"));
				case CEILING ->
						this.models().withExistingParent("dutchrat_bell_ceiling", new ResourceLocation("block/bell_ceiling"))
								.texture("particle", this.blockPrefix("pirat_log"))
								.texture("bar", this.blockPrefix("pirat_log"));
				case SINGLE_WALL ->
						this.models().withExistingParent("dutchrat_bell_wall", new ResourceLocation("block/bell_wall"))
								.texture("particle", this.blockPrefix("pirat_log"))
								.texture("bar", this.blockPrefix("pirat_log"));
				case DOUBLE_WALL ->
						this.models().withExistingParent("dutchrat_bell_between_walls", new ResourceLocation("block/bell_between_walls"))
								.texture("particle", this.blockPrefix("pirat_log"))
								.texture("bar", this.blockPrefix("pirat_log"));
			};
			return ConfiguredModel.builder().modelFile(type).rotationY((int) state.getValue(DutchratBellBlock.FACING).toYRot() + (state.getValue(DutchratBellBlock.ATTACHMENT) == BellAttachType.FLOOR ? 0 : 90)).build();
		});
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE.get());
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_BRICK.get());
		this.stairsBlock((StairBlock) RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_STAIRS.get(), this.blockPrefix("marbled_cheese_brick"));
		this.slabBlock((SlabBlock) RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_SLAB.get(), this.blockPrefix("marbled_cheese_brick"), this.blockPrefix("marbled_cheese_brick"));
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_CHISELED.get());
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_CRACKED.get());
		this.stairsBlock((StairBlock) RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_CRACKED_STAIRS.get(), this.blockPrefix("marbled_cheese_brick_cracked"));
		this.slabBlock((SlabBlock) RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_CRACKED_SLAB.get(), this.blockPrefix("marbled_cheese_brick_cracked"), this.blockPrefix("marbled_cheese_brick_cracked"));
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_MOSSY.get());
		this.stairsBlock((StairBlock) RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_MOSSY_STAIRS.get(), this.blockPrefix("marbled_cheese_brick_mossy"));
		this.slabBlock((SlabBlock) RatlantisBlockRegistry.MARBLED_CHEESE_BRICK_MOSSY_SLAB.get(), this.blockPrefix("marbled_cheese_brick_mossy"), this.blockPrefix("marbled_cheese_brick_mossy"));
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_CHISELED.get());
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_DIRT.get(), this.models().withExistingParent("marbled_cheese_dirt", this.blockPrefix("overlay_block"))
				.texture("particle", new ResourceLocation("block/dirt")).texture("tex", new ResourceLocation("block/dirt"))
				.texture("overlay", this.blockPrefix("marbled_cheese_overlay")));
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_GOLEM_CORE.get());
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_GRASS.get(), this.models().withExistingParent("marbled_cheese_grass", this.blockPrefix("grass_overlay_double"))
				.texture("particle", new ResourceLocation("block/dirt")).texture("side", new ResourceLocation("block/grass_block_side"))
				.texture("bottom", new ResourceLocation("block/dirt")).texture("top", new ResourceLocation("block/grass_block_top"))
				.texture("overlay", new ResourceLocation("block/grass_block_side_overlay")).texture("overlay2", this.blockPrefix("marbled_cheese_overlay")));
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_PILLAR.get(), this.models().cubeColumn("marbled_cheese_pillar", this.blockPrefix("marbled_cheese_pillar"), this.blockPrefix("marbled_cheese_pillar_top")));
		this.horizontalBlock(RatlantisBlockRegistry.MARBLED_CHEESE_RAT_HEAD.get(), this.models().getExistingFile(this.blockPrefix("marbled_cheese_rat_head")));
		this.stairsBlock((StairBlock) RatlantisBlockRegistry.MARBLED_CHEESE_STAIRS.get(), this.blockPrefix("marbled_cheese"));
		this.slabBlock((SlabBlock) RatlantisBlockRegistry.MARBLED_CHEESE_SLAB.get(), this.blockPrefix("marbled_cheese"), this.blockPrefix("marbled_cheese"));
		this.simpleBlock(RatlantisBlockRegistry.MARBLED_CHEESE_TILE.get());
		this.simpleBlock(RatlantisBlockRegistry.ORATCHALCUM_BLOCK.get());
		this.simpleBlock(RatlantisBlockRegistry.ORATCHALCUM_ORE.get());
		this.buttonBlock((ButtonBlock) RatlantisBlockRegistry.PIRAT_BUTTON.get(), this.blockPrefix("pirat_planks"));
		this.doorBlock((DoorBlock) RatlantisBlockRegistry.PIRAT_DOOR.get(), this.blockPrefix("pirat_door_bottom"), this.blockPrefix("pirat_door_top"));
		this.fenceBlock((FenceBlock) RatlantisBlockRegistry.PIRAT_FENCE.get(), this.blockPrefix("pirat_planks"));
		this.fenceGateBlock((FenceGateBlock) RatlantisBlockRegistry.PIRAT_FENCE_GATE.get(), this.blockPrefix("pirat_planks"));
		this.logBlock((RotatedPillarBlock) RatlantisBlockRegistry.PIRAT_LOG.get());
		this.simpleBlock(RatlantisBlockRegistry.PIRAT_PLANKS.get());
		this.pressurePlateBlock((PressurePlateBlock) RatlantisBlockRegistry.PIRAT_PRESSURE_PLATE.get(), this.blockPrefix("pirat_planks"));
		this.slabBlock((SlabBlock) RatlantisBlockRegistry.PIRAT_SLAB.get(), this.blockPrefix("pirat_planks"), this.blockPrefix("pirat_planks"));
		this.stairsBlock((StairBlock) RatlantisBlockRegistry.PIRAT_STAIRS.get(), this.blockPrefix("pirat_planks"));
		this.trapdoorBlock((TrapDoorBlock) RatlantisBlockRegistry.PIRAT_TRAPDOOR.get(), this.blockPrefix("pirat_trapdoor"), true);
		this.axisBlock((RotatedPillarBlock) RatlantisBlockRegistry.PIRAT_WOOD.get(), this.blockPrefix("pirat_log"), this.blockPrefix("pirat_log"));
		this.simpleBlock(RatlantisBlockRegistry.RATGLOVE_FLOWER.get(), this.models().cross("ratglove_flower", this.blockPrefix("ratglove_flower")).renderType(new ResourceLocation("cutout")));
		this.simpleBlock(RatlantisBlockRegistry.POTTED_RATGLOVE_FLOWER.get(), models().withExistingParent(RatlantisBlockRegistry.POTTED_RATGLOVE_FLOWER.getId().getPath(), "block/flower_pot_cross").renderType(new ResourceLocation("cutout")).texture("plant", blockTexture(RatlantisBlockRegistry.RATGLOVE_FLOWER.get())));
		this.simpleBlock(RatlantisBlockRegistry.RATLANTEAN_GEM_ORE.get());
		this.simpleBlock(RatlantisBlockRegistry.RATLANTIS_PORTAL.get(), this.models().cubeAll("ratlantis_portal", new ResourceLocation("block/yellow_stained_glass")));
		this.simpleBlock(RatlantisBlockRegistry.RATLANTIS_REACTOR.get());
		this.logBlock((RotatedPillarBlock) RatlantisBlockRegistry.STRIPPED_PIRAT_LOG.get());
		this.axisBlock((RotatedPillarBlock) RatlantisBlockRegistry.STRIPPED_PIRAT_WOOD.get(), this.blockPrefix("stripped_pirat_log"), this.blockPrefix("stripped_pirat_log"));

	}

	private void builtinEntity(Block b, String particle) {
		simpleBlock(b, models().getBuilder(ForgeRegistries.BLOCKS.getKey(b).getPath())
				.parent(new ModelFile.UncheckedModelFile("builtin/entity"))
				.texture("particle", particle));
	}

	private void cageBlock(Block block) {
		this.getMultipartBuilder(block)
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_top_tube"))).uvLock(false).addModel().condition(RatCageBlock.UP, 2).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bottom_tube"))).uvLock(false).addModel().condition(RatCageBlock.DOWN, 2).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars_tube"))).uvLock(false).rotationY(180).addModel().condition(RatCageBlock.SOUTH, 2).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars_tube"))).uvLock(false).rotationY(90).addModel().condition(RatCageBlock.EAST, 2).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars_tube"))).uvLock(false).rotationY(270).addModel().condition(RatCageBlock.WEST, 2).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars_tube"))).uvLock(false).addModel().condition(RatCageBlock.NORTH, 2).end()

				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_top"))).uvLock(false).addModel().condition(RatCageBlock.UP, 0).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bottom"))).uvLock(false).addModel().condition(RatCageBlock.DOWN, 0).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars"))).uvLock(false).rotationY(180).addModel().condition(RatCageBlock.SOUTH, 0).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars"))).uvLock(false).rotationY(90).addModel().condition(RatCageBlock.EAST, 0).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars"))).uvLock(false).rotationY(270).addModel().condition(RatCageBlock.WEST, 0).end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bars"))).uvLock(false).addModel().condition(RatCageBlock.NORTH, 0).end()

				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bottom_right"))).uvLock(false).addModel().nestedGroup().condition(RatCageBlock.DOWN, 0, 2).condition(RatCageBlock.EAST, 0, 2).end().end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bottom_left"))).uvLock(false).addModel().nestedGroup().condition(RatCageBlock.DOWN, 0, 2).condition(RatCageBlock.WEST, 0, 2).end().end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bottom_left"))).uvLock(false).rotationY(90).addModel().nestedGroup().condition(RatCageBlock.DOWN, 0, 2).condition(RatCageBlock.NORTH, 0, 2).end().end()
				.part().modelFile(this.models().getExistingFile(this.blockPrefix("rat_cage_bottom_right"))).uvLock(false).rotationY(90).addModel().nestedGroup().condition(RatCageBlock.DOWN, 0, 2).condition(RatCageBlock.SOUTH, 0, 2).end().end();
	}

	private ResourceLocation blockPrefix(String name) {
		return new ResourceLocation(RatsMod.MODID, "block/" + name);
	}

	private ResourceLocation itemPrefix(String name) {
		return new ResourceLocation(RatsMod.MODID, "item/" + name);
	}
}
