package com.mr208.rewired.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mr208.rewired.common.entities.EntityCyberSkeleton;
import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.items.augments.*;
import flaxbeard.cyberware.common.CyberwareContent;
import net.minecraft.block.Block;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraft.util.WeightedRandom;
import net.minecraft.village.MerchantRecipeList;
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
import com.mr208.rewired.common.items.armor.ItemReWIREDShield;
import com.mr208.rewired.common.entities.EntityRailRider;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod.EventBusSubscriber(modid = ReWIRED.MOD_ID)
public class ReWIREDContent
{
	public static final int VERY_RARE = 1;
	public static final int RARE = 10;
	public static final int UNCOMMON = 25;
	public static final int COMMON = 50;
	public static final int VERY_COMMON = 100;

	@GameRegistry.ObjectHolder("cyberware:component")
	public static final Item component = null;

	public static ArrayList<Item> registeredItems = new ArrayList<>();
	public static ArrayList<Block> registeredBlocks = new ArrayList<>();
	public static ArrayList<EntityEntry> registeredEntityEntries = new ArrayList<>();

	public static Item.ToolMaterial POLYMER_MATERIAL;
	public static Item.ToolMaterial CARBON_MATERIAL;
	public static Item.ToolMaterial PLASTEEL_MATERIAL;

	public static Item itemShieldPolymer;
	public static Item itemShieldCarbon;
	public static Item itemShieldPlasteel;

	public static Item itemBatonPolymer;
	public static Item itemBatonCarbon;
	public static Item itemBatonPlasteel;

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

	public static EntityEntry entryRailRider;
	public static EntityEntry entryCyberSkeleton;
	public static EntityEntry entryGreyGoo;

	public static List<CyberwareContent.ZombieItem> cyberSkeletonItems;

	public static void onPreInit()
	{
		POLYMER_MATERIAL = EnumHelper.addToolMaterial("POLYMER",
				1,
				131,
				5.0F,
				1.5F,
				6);
		CARBON_MATERIAL = EnumHelper.addToolMaterial("CARBON",
				2,
				250,
				7.0F,
				2.5F,
				8);
		PLASTEEL_MATERIAL = EnumHelper.addToolMaterial("PLASTEEL",
				3,
				1561,
				9.0F,
				3.5F,
				10);

		itemShieldPolymer = new ItemReWIREDShield(POLYMER_MATERIAL);
		itemShieldCarbon = new ItemReWIREDShield(CARBON_MATERIAL);
		itemShieldPlasteel = new ItemReWIREDShield(PLASTEEL_MATERIAL);

		handAugments = new ItemHandAugment("hand", ICyberware.EnumSlot.HAND,new String[]{"plasteel_fist","projected_kinetic_barrier"});
		handAugments.setEssenceCost(ConfigHandler.Augments.plasteelFist.TOLERANCE_COST,ConfigHandler.Augments.pkb.TOLERANCE_COST);
		handAugments.setWeights(ConfigHandler.Augments.plasteelFist.RARITY,ConfigHandler.Augments.pkb.RARITY);
		handAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[] { new ItemStack(component, 2, 0), new ItemStack(component, 1, 2), new ItemStack(component, 1, 4), new ItemStack(component, 2, 6)}),
				NNLUtil.fromArray(new ItemStack[] { new ItemStack(component, 1, 7), new ItemStack(component, 3, 9), new ItemStack(component, 2, 6)})
		);

		footAugments = new ItemFootAugment("foot", ICyberware.EnumSlot.FOOT,new String[]{"rail_riders"});
		footAugments.setEssenceCost(ConfigHandler.Augments.RailRider.TOLERANCE_COST);
		footAugments.setWeights(ConfigHandler.Augments.RailRider.RARITY);
		footAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[]{new ItemStack(component,2,0)})
		);

		skinAugments = new ItemSkinAugment("skin", ICyberware.EnumSlot.SKIN, new String[]{"camo"});
		skinAugments.setEssenceCost(ConfigHandler.Augments.TOC.TOLERANCE_COST);
		skinAugments.setWeights(ConfigHandler.Augments.TOC.RARITY);
		skinAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[] { new ItemStack(Items.APPLE)})
		);

		torsoAugments = new ItemTorsoAugment("torso", ICyberware.EnumSlot.LOWER_ORGANS, new String[]{"derps"});
		torsoAugments.setEssenceCost(ConfigHandler.Augments.derps.TOLERANCE_COST);
		torsoAugments.setWeights(ConfigHandler.Augments.derps.RARITY);
		torsoAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[] {new ItemStack(Items.APPLE)})
		);

		craniumAugments = new ItemCraniumAugment("cranium", ICyberware.EnumSlot.CRANIUM, new String[]{"ecd"});
		craniumAugments.setEssenceCost(ConfigHandler.Augments.ecd.TOLERANCE_COST);
		craniumAugments.setWeights(ConfigHandler.Augments.ecd.RARITY);
		craniumAugments.setComponents(
				NNLUtil.fromArray(new ItemStack[] {new ItemStack(Items.APPLE)})
		);

		//ENTITIES
		entryRailRider = EntityEntryBuilder.create()
				.entity(EntityRailRider.class)
				.name("railrider")
				.id(new ResourceLocation(ReWIRED.MOD_ID,"rail_rider"), 0)
				.tracker(32,1,true)
				.build();
		registeredEntityEntries.add(entryRailRider);

		entryCyberSkeleton = EntityEntryBuilder.create()
				.entity(EntityCyberSkeleton.class)
				.name("cyberskeleton")
				.id(new ResourceLocation(ReWIRED.MOD_ID, "cyber_skeleton"), 1)
				.egg(0x616161, 0x343434)
				.tracker(32,1,false)
				.build();
		registeredEntityEntries.add(entryCyberSkeleton);
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
		for(EntityEntry entry:registeredEntityEntries)
		{
			event.getRegistry().register(entry);
		}
	}

	private void addCyberSkeletonItems()
	{

	}

}
