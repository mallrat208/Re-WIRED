package com.mr208.rewired.common.handlers;

import java.util.Random;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemBlueprint;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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

	public static VillagerRegistry.VillagerCareer chopperCareer;
	public static VillagerRegistry.VillagerCareer runnerCareer;
	public static VillagerRegistry.VillagerCareer deckerCareer;
	public static VillagerRegistry.VillagerCareer bladeCareer;

	public static void onPreInit()
	{
		cyberwareVillager = new VillagerRegistry.VillagerProfession("rewired:runner",
				"rewired:textures/entities/villagers/runner.png",
				"rewired:textures/entities/villagers/zombie_runner.png");

		runnerCareer = new VillagerRegistry.VillagerCareer(cyberwareVillager, "runner");
		//chopperCareer = new VillagerRegistry.VillagerCareer(cyberwareVillager, "chopper");
		//deckerCareer = new VillagerRegistry.VillagerCareer(cyberwareVillager, "decker");
		//bladeCareer = new VillagerRegistry.VillagerCareer(cyberwareVillager, "blade");

		ItemStack[] cyberEquipment = {
				new ItemStack(CyberwareContent.trenchcoat),
				new ItemStack(CyberwareContent.jacket),
				new ItemStack(CyberwareContent.shades),
				new ItemStack(CyberwareContent.shades2)};

		//Runner - Level 1 Trades
		RandomItemsForEmeralds clothingTrade1 = new RandomItemsForEmeralds(new EntityVillager.PriceInfo(11,17),cyberEquipment.clone());

		runnerCareer.addTrade(1, clothingTrade1);
		runnerCareer.addTrade(1, new EntityVillager.EmeraldForItems(CyberwareContent.neuropozyne, new EntityVillager.PriceInfo(1,2)));

		//Runner - Level 2 Trades
		RandomCyberwareForEmeralds cyberwareTrade2 = new RandomCyberwareForEmeralds(new EntityVillager.PriceInfo(6,13),
				CyberwareAPI.QUALITY_SCAVENGED,
				new ItemStack(CyberwareContent.eyeUpgrades,1,0),
				new ItemStack(CyberwareContent.cybereyeUpgrades,1,3),
				new ItemStack(CyberwareContent.brainUpgrades, 1,4),
				new ItemStack(CyberwareContent.lungsUpgrades, 1,1),
				new ItemStack(CyberwareContent.skinUpgrades,1,1),
				new ItemStack(CyberwareContent.muscleUpgrades,1,0),
				new ItemStack(CyberwareContent.handUpgrades, 1,1));

		runnerCareer.addTrade(2, cyberwareTrade2);
		runnerCareer.addTrade(2, new EmeraldsForRandomItem(new EntityVillager.PriceInfo(3,8), new ItemStack(CyberwareContent.katana)));

		//Runner - Level 3 Trades
		EmeraldsForRandomCyberware cyberwareTrade3 = new EmeraldsForRandomCyberware(new EntityVillager.PriceInfo(6,13),
				CyberwareAPI.QUALITY_MANUFACTURED,
				new ItemStack(CyberwareContent.eyeUpgrades,1,0),
				new ItemStack(CyberwareContent.cybereyeUpgrades,1,3),
				new ItemStack(CyberwareContent.brainUpgrades, 1,4),
				new ItemStack(CyberwareContent.lungsUpgrades, 1,1),
				new ItemStack(CyberwareContent.skinUpgrades,1,1),
				new ItemStack(CyberwareContent.muscleUpgrades,1,0),
				new ItemStack(CyberwareContent.handUpgrades, 1,1));

		runnerCareer.addTrade(3, cyberwareTrade3);
		runnerCareer.addTrade(3,
				new RandomBlueprintsForEmeralds(new EntityVillager.PriceInfo(9,12),
						new ItemStack(CyberwareContent.brainUpgrades,1,2),
						new ItemStack(ReWIREDContent.skinAugments,1,0),
						new ItemStack(ReWIREDContent.handAugments, 1,1)));
		runnerCareer.addTrade(3,
				new RandomEnchantedItemForEmeralds(new EntityVillager.PriceInfo(11,19), 35, cyberEquipment.clone()));
	}

	@SubscribeEvent
	public static void registerVillager(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event)
	{
		//event.getRegistry().register(chopperProfession);
		event.getRegistry().register(cyberwareVillager);
	}

	static class RandomEnchantedItemForEmeralds implements EntityVillager.ITradeList
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
				enchantedOut = EnchantmentHelper.addRandomEnchantment(random, this.items[random.nextInt(this.items.length)], this.level, true);

			recipeList.add(new MerchantRecipe(emeraldsIn, enchantedOut));
		}
	}

	static class EmeraldsForRandomItem implements EntityVillager.ITradeList
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

	static class EmeraldsForRandomCyberware implements EntityVillager.ITradeList
	{
		ItemStack[] cyberware;
		EntityVillager.PriceInfo price;
		ICyberware.Quality quality;

		public EmeraldsForRandomCyberware(EntityVillager.PriceInfo priceInfo, ICyberware.Quality quality, ItemStack... cyberware)
		{
			this.cyberware = cyberware;
			this.price = priceInfo;
			this.quality = quality;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;
			if(this.price!=null)
				i = this.price.getPrice(random);

			ItemStack emeraldsOut = new ItemStack(Items.EMERALD, i,0);
			ItemStack cyberwareIn;

			if(this.cyberware.length==1)
				cyberwareIn = CyberwareAPI.writeQualityTag(this.cyberware[0],this.quality).copy();
			else
				cyberwareIn = CyberwareAPI.writeQualityTag(this.cyberware[random.nextInt(this.cyberware.length)], this.quality).copy();

			recipeList.add(new MerchantRecipe(cyberwareIn, emeraldsOut));
		}
	}

	static class RandomItemsForEmeralds implements EntityVillager.ITradeList
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

	static class RandomBlueprintsForEmeralds implements EntityVillager.ITradeList
	{
		ItemStack[] cyberware;
		EntityVillager.PriceInfo price;

		public RandomBlueprintsForEmeralds(EntityVillager.PriceInfo price, ItemStack... cyberware)
		{
			this.cyberware = cyberware;
			this.price = price;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;

			if(this.price!=null)
				i = this.price.getPrice(random);

			ItemStack emeraldsIn = new ItemStack(Items.EMERALD,i,0).copy();
			ItemStack blueprintItem;

			if(this.cyberware.length==1)
				blueprintItem = this.cyberware[0];
			else
				blueprintItem = this.cyberware[random.nextInt(this.cyberware.length)].copy();

			ItemStack blueprintOut = ItemBlueprint.getBlueprintForItem(blueprintItem);

			recipeList.add(new MerchantRecipe(emeraldsIn,blueprintOut));

		}
	}

	static class RandomCyberwareForEmeralds implements EntityVillager.ITradeList
	{
		ItemStack[] cyberware;
		EntityVillager.PriceInfo price;
		ICyberware.Quality quality;

		public RandomCyberwareForEmeralds(EntityVillager.PriceInfo priceInfo, ICyberware.Quality quality, ItemStack... cyberware)
		{
			this.cyberware = cyberware;
			this.price = priceInfo;
			this.quality = quality;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;

			if(this.price!=null)
				i = this.price.getPrice(random);

			ItemStack emeraldsIn = new ItemStack(Items.EMERALD, i, 0).copy();
			ItemStack cyberStack;

			if(this.cyberware.length==1)
				cyberStack = this.cyberware[0].copy();
			else
				cyberStack = this.cyberware[random.nextInt(this.cyberware.length)].copy();

			ItemStack qualityStackOut = CyberwareAPI.writeQualityTag(cyberStack, this.quality);

			recipeList.add(new MerchantRecipe(emeraldsIn, qualityStackOut));
		}
	}
}
