package com.mr208.rewired.common;

import java.util.ArrayList;
import java.util.List;

import com.mr208.rewired.common.blocks.BlockECG;
import com.mr208.rewired.common.crafting.RecipeColorableEquipment;
import com.mr208.rewired.common.entities.EntityCyberSkeleton;
import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.handlers.ConfigHandler.Augments;
import com.mr208.rewired.common.handlers.ConfigHandler.Entities;
import com.mr208.rewired.common.handlers.ConfigHandler.Equipment;
import com.mr208.rewired.common.handlers.ConfigHandler.General;
import com.mr208.rewired.common.items.ItemReWIRED;
import com.mr208.rewired.common.items.ItemReWIREDFood;
import com.mr208.rewired.common.items.equipment.ItemRiotShield;
import com.mr208.rewired.common.items.equipment.ItemTechVisor;
import com.mr208.rewired.common.items.augments.*;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.CyberwareContent.ZombieItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;

import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.misc.NNLUtil;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.entities.EntityRailRider;

@Mod.EventBusSubscriber(modid = ReWIRED.MOD_ID)
public class ReWIREDContent
{
	@GameRegistry.ObjectHolder("cyberware:component")
	public static final Item component = null;

	public static ArrayList<Item> registeredItems = new ArrayList<>();
	public static ArrayList<Block> registeredBlocks = new ArrayList<>();
	public static ArrayList<EntityEntry> registeredEntityEntries = new ArrayList<>();

	public static Item.ToolMaterial POLYMER_MATERIAL;
	public static Item.ToolMaterial CARBON_MATERIAL;
	public static Item.ToolMaterial PLASTEEL_MATERIAL;

	public static ItemReWIRED itemReWIRED;
	
	public static ItemRiotShield itemShieldPolymer;
	public static ItemRiotShield itemShieldCarbon;
	public static ItemRiotShield itemShieldPlasteel;

	public static ItemReWIRED itemBatonPolymer;
	public static ItemReWIRED itemBatonCarbon;
	public static ItemReWIRED itemBatonPlasteel;
	
	public static Block electroChromicGlass;
	public static Block ecglassWhite;
	public static Block ecglassOrange;
	public static Block ecglassMagenta;
	public static Block ecglassLightBlue;
	public static Block ecglassYellow;
	public static Block ecglassLime;
	public static Block ecglassPink;
	public static Block ecglassGray;
	public static Block ecglassSilver;
	public static Block ecglassCyan;
	public static Block ecglassPurple;
	public static Block ecglassBlue;
	public static Block ecglassBrown;
	public static Block ecglassGreen;
	public static Block ecglassRed;
	public static Block ecglassBlack;

	public static ItemAugment eyeAugments;
	public static ItemAugment craniumAugments;
	public static ItemAugment heartAugments;
	public static ItemAugment lungsAugments;
	public static ItemAugment torsoAugments;
	public static ItemAugment skinAugments;
	public static ItemAugment muscleAugments;
	public static ItemAugment boneAugments;
	public static ItemAugment armAugments;
	public static ItemAugment handAugments;
	public static ItemAugment legAugments;
	public static ItemAugment footAugments;
	
	public static ItemTechVisor armorTechVisor;
	
	public static ItemReWIREDFood foodPowerbar;
	public static ItemReWIREDFood foodSilverGorgon;

	public static EntityEntry entryRailRider;
	public static EntityEntry entryCyberSkeleton;
	public static EntityEntry entryGreyGoo;

	public static List<CyberwareContent.ZombieItem> cyberSkeletonItems;

	public static void onPreInit()
	{
		itemReWIRED = new ItemReWIRED("stub");
		itemReWIRED.setMetaHidden(0);
		
		if(General.BLOCKS.enableECG)
		{
			electroChromicGlass = new BlockECG(null);
			//ecglassWhite = new BlockECG(EnumDyeColor.WHITE);
			//ecglassOrange = new BlockECG(EnumDyeColor.ORANGE);
			//ecglassMagenta = new BlockECG(EnumDyeColor.MAGENTA);
			//ecglassLightBlue = new BlockECG(EnumDyeColor.LIGHT_BLUE);
			//ecglassYellow = new BlockECG(EnumDyeColor.YELLOW);
			//ecglassLime = new BlockECG(EnumDyeColor.LIME);
			//ecglassPink = new BlockECG(EnumDyeColor.PINK);
			//ecglassGray = new BlockECG(EnumDyeColor.GRAY);
			//ecglassSilver = new BlockECG(EnumDyeColor.SILVER);
			//ecglassCyan = new BlockECG(EnumDyeColor.CYAN);
			//ecglassPurple = new BlockECG(EnumDyeColor.PURPLE);
			//ecglassBlue = new BlockECG(EnumDyeColor.BLUE);
			//ecglassGreen = new BlockECG(EnumDyeColor.GREEN);
			//ecglassRed = new BlockECG(EnumDyeColor.RED);
			//ecglassBlack = new BlockECG(EnumDyeColor.BLACK);
			
		}
		
		armorTechVisor = new ItemTechVisor(ArmorMaterial.CHAIN);
		
		POLYMER_MATERIAL = EnumHelper.addToolMaterial("POLYMER",
				1,
				131,
				5.0F,
				1.5F,
				6);
		POLYMER_MATERIAL.setRepairItem(new ItemStack(Items.IRON_INGOT));
		
		CARBON_MATERIAL = EnumHelper.addToolMaterial("CARBON",
				2,
				250,
				7.0F,
				2.5F,
				8);
		CARBON_MATERIAL.setRepairItem(new ItemStack(Items.IRON_INGOT));
		
		PLASTEEL_MATERIAL = EnumHelper.addToolMaterial("PLASTEEL",
				3,
				1561,
				9.0F,
				3.5F,
				10);
		PLASTEEL_MATERIAL.setRepairItem(new ItemStack(Items.IRON_INGOT));
		
		if(Equipment.shields.enableShields)
		{
			itemShieldPolymer=new ItemRiotShield(POLYMER_MATERIAL);
			itemShieldPolymer.setComponents(NNLUtil.fromArray(new ItemStack[]{
					new ItemStack(Blocks.GLASS,2),
					new ItemStack(Items.IRON_INGOT,2),
					Comp.FULLERENE_MICROSTRUCTURES.numb(2),
					Comp.TITANIUM_MESH.numb(4)}));
			
			itemShieldCarbon=new ItemRiotShield(CARBON_MATERIAL);
			itemShieldCarbon.setComponents(NNLUtil.fromArray(new ItemStack[]{
					new ItemStack(Blocks.GLASS,2),
					new ItemStack(Items.IRON_INGOT,1),
					new ItemStack(Blocks.OBSIDIAN,1),
					Comp.FULLERENE_MICROSTRUCTURES.numb(2),
					Comp.TITANIUM_MESH.numb(4)}));
			
			itemShieldPlasteel=new ItemRiotShield(PLASTEEL_MATERIAL);
			itemShieldPlasteel.setComponents(NNLUtil.fromArray(new ItemStack[]{
					new ItemStack(Blocks.GLASS,2),
					new ItemStack(Blocks.OBSIDIAN,1),
					new ItemStack(Items.DIAMOND,1),
					Comp.CHROME_PLATING.numb(2),
					Comp.TITANIUM_MESH.numb(4)}));
		}

		handAugments = new ItemHandAugment("hand",
				ICyberware.EnumSlot.HAND,
				new String[]{"plasteel_fist","projected_kinetic_barrier"});
		handAugments.setEssenceCost(ConfigHandler.Augments.plasteelFist.TOLERANCE_COST,
				ConfigHandler.Augments.pkb.TOLERANCE_COST);
		handAugments.setWeights(ConfigHandler.Augments.plasteelFist.RARITY,
				ConfigHandler.Augments.pkb.RARITY);
		handAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[] { Comp.ACTUATOR.numb(2),
						Comp.TITANIUM_MESH.numb(1),
						Comp.CHROME_PLATING.numb(1),
						Comp.FULLERENE_MICROSTRUCTURES.numb(2)}),
				NNLUtil.fromArray(new ItemStack[] { Comp.SYNTHETIC_NERVES.numb(1),
						Comp.MICROELECTRIC_CELLS.numb(3),
						Comp.FULLERENE_MICROSTRUCTURES.numb(2)})
		);

		footAugments = new ItemFootAugment("foot",
				ICyberware.EnumSlot.FOOT,
				new String[]{"rail_riders"});
		footAugments.setEssenceCost(ConfigHandler.Augments.RailRider.TOLERANCE_COST);
		footAugments.setWeights(ConfigHandler.Augments.RailRider.RARITY);
		footAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[]{ Comp.ACTUATOR.numb(2),
						Comp.CHROME_PLATING.numb(1),
						new ItemStack(Items.IRON_INGOT,2)})
		);

		skinAugments = new ItemSkinAugment("skin", ICyberware.EnumSlot.SKIN,
				new String[]{"camo"});
		skinAugments.setEssenceCost(ConfigHandler.Augments.TOC.TOLERANCE_COST);
		skinAugments.setWeights(ConfigHandler.Augments.TOC.RARITY);
		skinAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[] { Comp.SYNTHETIC_NERVES.numb(2),
						Comp.FIBER_OPTICS.numb(2),
						Comp.BIOREACTOR.numb(1),
						Comp.CHROME_PLATING.numb(1)})
		);

		torsoAugments = new ItemTorsoAugment("torso",
				ICyberware.EnumSlot.LOWER_ORGANS,
				new String[]{"derps","cyberstomach"});
		torsoAugments.setEssenceCost(ConfigHandler.Augments.derps.TOLERANCE_COST,
				ConfigHandler.Augments.cyberStomach.TOLERANCE_COST);
		torsoAugments.setWeights(ConfigHandler.Augments.derps.RARITY,
				ConfigHandler.Augments.cyberStomach.RARITY);
		torsoAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[]{
						new ItemStack(Items.ENDER_PEARL, 2),
						Comp.SOLID_STATE_CIRCUITRY.numb(2),
						Comp.SYNTHETIC_NERVES.numb(3),
						Comp.FIBER_OPTICS.numb(1),
						Comp.CHROME_PLATING.numb(2)}),
				NNLUtil.fromArray(new ItemStack[]{
						Comp.SOLID_STATE_CIRCUITRY.numb(1),
						Comp.STORAGE_CELL.numb(8),
						Comp.TITANIUM_MESH.numb(1),
						Comp.CHROME_PLATING.numb(4)})
		);

		craniumAugments = new ItemCraniumAugment("cranium",
				ICyberware.EnumSlot.CRANIUM,
				new String[]{"ecd"});
		craniumAugments.setEssenceCost(ConfigHandler.Augments.ecd.TOLERANCE_COST);
		craniumAugments.setWeights(ConfigHandler.Augments.ecd.RARITY);
		craniumAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[] {new ItemStack(Items.ENDER_PEARL),
						Comp.MICROELECTRIC_CELLS.numb(2),
						Comp.SOLID_STATE_CIRCUITRY.numb(1),
						Comp.SYNTHETIC_NERVES.numb(1),
						Comp.BIOREACTOR.numb(1)})
		);
		
		foodPowerbar = new ItemReWIREDFood("powerbar",
				EnumAction.EAT,
				Augments.cyberStomach.FOOD_POWERBAR_AMT,
				(float)Augments.cyberStomach.FOOD_POWERBAR_SAT,
				Augments.cyberStomach.FOOD_POWERBAR_NRG,
				64);
		
		foodSilverGorgon = new ItemReWIREDFood("energydrink",
				EnumAction.DRINK,
				Augments.cyberStomach.FOOD_ENERGYDRINK_AMT,
				(float)Augments.cyberStomach.FOOD_ENERGYDRINK_SAT,
				Augments.cyberStomach.FOOD_ENERGYDRINK_NRG,
				64);

		//ENTITIES
		entryRailRider = EntityEntryBuilder.create()
				.entity(EntityRailRider.class)
				.name("railrider")
				.id(new ResourceLocation(ReWIRED.MOD_ID,"rail_rider"), 0)
				.tracker(32,1,true)
				.build();
		registeredEntityEntries.add(entryRailRider);
		
		if(Entities.enableCyberskeleton)
		{
			entryCyberSkeleton=EntityEntryBuilder.create()
					.entity(EntityCyberSkeleton.class)
					.name("cyberskeleton")
					.id(new ResourceLocation(ReWIRED.MOD_ID, "cyber_skeleton"), 1)
					.egg(0x616161, 0x343434)
					.tracker(32, 1, false)
					.build();
			registeredEntityEntries.add(entryCyberSkeleton);
		}
	}
	
	public static void onInit()
	{
		addCyberSkeletonItems();
		
		/*
		VillagerRegistry villageRegistry=VillagerRegistry.instance();
		villageRegistry.registerVillageCreationHandler(new VillageRunnersHouse.VillageCreationRunner());
		
		try
		{
		 	MapGenStructureIO.registerStructureComponent(VillageRunnersHouse.class, ReWIRED.MOD_ID+":runnershouse");
		} catch(Throwable ignored)
		{
			ReWIRED.LOGGER.warn("Failed to register VillageRunnersHouse");
		}
		*/
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
		for(EntityEntry entry:registeredEntityEntries)
		{
			event.getRegistry().register(entry);
		}
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		event.getRegistry().register(new RecipeColorableEquipment());
	}

	private static void addCyberSkeletonItems()
	{
		cyberSkeletonItems = new ArrayList<>();
		cyberSkeletonItems.add(new ZombieItem(15,new ItemStack(CyberwareContent.brainUpgrades,1,1)));
		cyberSkeletonItems.add(new ZombieItem(15,new ItemStack(CyberwareContent.brainUpgrades,1,4)));
		cyberSkeletonItems.add(new ZombieItem(15,new ItemStack(skinAugments, 1, 0)));
		cyberSkeletonItems.add(new ZombieItem(20,new ItemStack(CyberwareContent.boneUpgrades, 1,0)));
		cyberSkeletonItems.add(new ZombieItem(20,new ItemStack(CyberwareContent.boneUpgrades, 1,1)));
		cyberSkeletonItems.add(new ZombieItem(20,new ItemStack(CyberwareContent.boneUpgrades, 1,2)));
		cyberSkeletonItems.add(new ZombieItem(25,new ItemStack(CyberwareContent.handUpgrades, 1,0)));
		cyberSkeletonItems.add(new ZombieItem(25,new ItemStack(CyberwareContent.handUpgrades, 1,1)));
		cyberSkeletonItems.add(new ZombieItem(18,new ItemStack(CyberwareContent.legUpgrades, 1,0)));
		cyberSkeletonItems.add(new ZombieItem(18,new ItemStack(CyberwareContent.legUpgrades, 1,1)));
		cyberSkeletonItems.add(new ZombieItem(15,new ItemStack(footAugments, 1,0 )));
		cyberSkeletonItems.add(new ZombieItem(20,new ItemStack(CyberwareContent.cybereyeUpgrades, 1, 3)));
		cyberSkeletonItems.add(new ZombieItem(20,new ItemStack(CyberwareContent.cybereyeUpgrades,1,0)));
		cyberSkeletonItems.add(new ZombieItem(20,new ItemStack(CyberwareContent.cybereyeUpgrades, 1,2)));
	}
	
	protected enum Comp {
		
		ACTUATOR(0),
		BIOREACTOR(1),
		TITANIUM_MESH(2),
		SOLID_STATE_CIRCUITRY(3),
		CHROME_PLATING(4),
		FIBER_OPTICS(5),
		FULLERENE_MICROSTRUCTURES(6),
		SYNTHETIC_NERVES(7),
		STORAGE_CELL(8),
		MICROELECTRIC_CELLS(9);
		
		private final int meta;
		private final Item component = CyberwareContent.component;
		Comp(int meta)
		{
			this.meta = meta;
		}
		
		public ItemStack numb(int quantity)	{
			
			return new ItemStack(component,quantity,meta);
		}
		
	}

}
