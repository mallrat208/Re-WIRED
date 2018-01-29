package com.mr208.rewired.common.handlers;

import java.util.ArrayList;
import java.util.Random;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import com.mr208.rewired.common.handlers.ConfigHandler.Entities;
import com.mr208.rewired.common.handlers.ConfigHandler.Equipment;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.CyberwareContent.ZombieItem;
import flaxbeard.cyberware.common.item.ItemBlueprint;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod.EventBusSubscriber(modid = ReWIRED.MOD_ID)
public class VillagerHandler
{
	public static VillagerRegistry.VillagerProfession cyberwareVillager;
	
	public static VillagerRegistry.VillagerCareer runnerCareer;

	public static void onPreInit()
	{
		cyberwareVillager = new VillagerRegistry.VillagerProfession("rewired:runner",
				"rewired:textures/entities/villagers/runner.png",
				"rewired:textures/entities/villagers/zombie_runner.png");

		runnerCareer = new VillagerRegistry.VillagerCareer(cyberwareVillager, "runner");

		//Runner - Level 1 Trades
		runnerCareer.addTrade(1, new RandomItemsForEmeralds(new PriceInfo(11,17),getTradeEquipment()));
		runnerCareer.addTrade(1, new RandomItemsForEmeralds(new PriceInfo(3,6), getTradeConsumables()));

		//Runner - Level 2 Trades
		runnerCareer.addTrade(2, new RandomCyberwareForEmeralds(CyberwareTrade.SCAVENGED));
		runnerCareer.addTrade(2, new EmeraldsForRandomItem(new PriceInfo(1,3), new ItemStack(CyberwareContent.neuropozyne)));

		//Runner - Level 3 Trades
		runnerCareer.addTrade(3, new RandomCyberwareForEmeralds(CyberwareTrade.MANUFACTURED));
		runnerCareer.addTrade(3, new RandomCyberwareForEmeralds(CyberwareTrade.BLUEPRINT));
		runnerCareer.addTrade(3,
				new RandomEnchantedItemForEmeralds(new PriceInfo(11,19), 35, getTradeEquipment()));
	}

	@SubscribeEvent
	public static void registerVillager(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event)
	{
		//event.getRegistry().register(chopperProfession);
		if(Entities.enableVillager)
		{
			event.getRegistry().register(cyberwareVillager);
		}
	}
	
	static class RandomCyberwareForEmeralds implements ITradeList
	{
		CyberwareTrade tradeType;
		
		public RandomCyberwareForEmeralds(CyberwareTrade tradeType)
		{
			this.tradeType = tradeType;
		}
		
		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int emeraldCost = 0;
			ItemStack itemStackOut;
			ItemStack emeraldsIn;
			
			ZombieItem randomZombieItem = WeightedRandom.getRandomItem(random, CyberwareContent.zombieItems);
			
			ItemStack randomItem = randomZombieItem.stack.copy();
			randomItem.setCount(1);
			
			int itemWeight = randomZombieItem.itemWeight;
			
			if(this.tradeType == CyberwareTrade.BLUEPRINT)
			{
				itemStackOut = ItemBlueprint.getBlueprintForItem(randomItem);
				emeraldCost =(int)Math.floor(48f - (48f * ((float) itemWeight / 100f)));
				emeraldsIn = new ItemStack(Items.EMERALD, Math.max(1,emeraldCost),0);
				
				recipeList.add(new MerchantRecipe(emeraldsIn, itemStackOut));
				
			} else if(this.tradeType == CyberwareTrade.SCAVENGED)
			{
				itemStackOut = CyberwareAPI.writeQualityTag(randomItem, CyberwareAPI.QUALITY_SCAVENGED);
				emeraldCost =(int)Math.floor(32f - (32f * ((float) itemWeight / 100f)));
				emeraldsIn = new ItemStack(Items.EMERALD, Math.max(1,emeraldCost), 0);
				
				recipeList.add(new MerchantRecipe(emeraldsIn, itemStackOut));
			
			} else if(this.tradeType == CyberwareTrade.MANUFACTURED)
			{
				itemStackOut = CyberwareAPI.writeQualityTag(randomItem, CyberwareAPI.QUALITY_MANUFACTURED);
				emeraldCost =(int)Math.floor(64f - (64f * ((float) itemWeight / 100f)));
				emeraldsIn = new ItemStack(Items.EMERALD, Math.max(1,emeraldCost), 0);
				
				recipeList.add(new MerchantRecipe(emeraldsIn, itemStackOut));
			}
		}
	}

	static class RandomEnchantedItemForEmeralds implements ITradeList
	{
		ItemStack[] items;
		EntityVillager.PriceInfo price;
		int level;

		public RandomEnchantedItemForEmeralds(EntityVillager.PriceInfo priceInfo, int level, ItemStack... items)
		{
			this.items = items;
			this.price = priceInfo;
			this.level = level;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;
			if(this.price!=null)
				i = this.price.getPrice(random);

			ItemStack emeraldsIn = new ItemStack(Items.EMERALD, i, 0);
			ItemStack enchantedOut;
			if(this.items.length==1)
				enchantedOut = EnchantmentHelper.addRandomEnchantment(random, this.items[0], this.level, true);
			else
				enchantedOut = EnchantmentHelper.addRandomEnchantment(random, this.items[random.nextInt(this.items.length)].copy(), this.level, true);

			recipeList.add(new MerchantRecipe(emeraldsIn, enchantedOut));
		}
	}

	static class EmeraldsForRandomItem implements ITradeList
	{
		ItemStack[] items;
		EntityVillager.PriceInfo price;

		public EmeraldsForRandomItem(EntityVillager.PriceInfo priceInfo, ItemStack... items)
		{
			this.items = items;
			this.price = priceInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;
			if(this.price!=null)
				i = this.price.getPrice(random);

			ItemStack emeraldsOut = new ItemStack(Items.EMERALD, i, 0);

			ItemStack itemIn;
			if(this.items.length==0)
				itemIn = this.items[0].copy();
			else
				itemIn = this.items[random.nextInt(this.items.length)].copy();

			recipeList.add(new MerchantRecipe(itemIn, emeraldsOut));
		}
	}

	static class RandomItemsForEmeralds implements ITradeList
	{
		EntityVillager.PriceInfo price;
		ItemStack[] items;

		public RandomItemsForEmeralds(EntityVillager.PriceInfo priceInfo, ItemStack... items)
		{
			this.price = priceInfo;
			this.items = items;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			ItemStack emeraldsIn;
			ItemStack itemOut;

			int i = 1;
			if(this.price!=null)
				i = this.price.getPrice(random);

			emeraldsIn = new ItemStack(Items.EMERALD, i, 0);

			if(this.items.length==1)
				itemOut = this.items[0];
			else
				itemOut = this.items[random.nextInt(this.items.length)].copy();

			recipeList.add(new MerchantRecipe(emeraldsIn, itemOut));
		}
	}
	
	enum CyberwareTrade
	{
		BLUEPRINT,
		SCAVENGED,
		MANUFACTURED
	}
	
	protected static ItemStack[] getTradeEquipment()
	{
		ArrayList<ItemStack> cyberEquipment = new ArrayList<>();
		
		cyberEquipment.add(new ItemStack(CyberwareContent.trenchcoat));
		cyberEquipment.add(new ItemStack(CyberwareContent.jacket));
		cyberEquipment.add(new ItemStack(CyberwareContent.shades));
		cyberEquipment.add(new ItemStack(CyberwareContent.shades2));
		
		if(Equipment.shields.enableShields)
		{
			cyberEquipment.add(new ItemStack(ReWIREDContent.itemShieldCarbon));
		}
		
		return cyberEquipment.toArray(new ItemStack[cyberEquipment.size()]);
	}
	
	protected static ItemStack[] getTradeConsumables()
	{
		ArrayList<ItemStack> consumables = new ArrayList<>();
		
		consumables.add(new ItemStack(CyberwareContent.neuropozyne));
		consumables.add(new ItemStack(ReWIREDContent.foodPowerbar));
		consumables.add(new ItemStack(ReWIREDContent.foodSilverGorgon));
		
		return consumables.toArray(new ItemStack[consumables.size()]);
	}
}
