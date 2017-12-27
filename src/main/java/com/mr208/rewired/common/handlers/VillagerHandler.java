package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.handler.CyberwareDataHandler;
import flaxbeard.cyberware.common.item.ItemArmorCyberware;
import flaxbeard.cyberware.common.item.ItemBlueprint;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Random;

@Mod.EventBusSubscriber(modid = ReWIRED.MOD_ID)
public class VillagerHandler
{
	public static VillagerRegistry.VillagerProfession chopperProfession;
	public static VillagerRegistry.VillagerProfession runnerProfession;

	public static VillagerRegistry.VillagerCareer chopperCareer;
	public static VillagerRegistry.VillagerCareer runnerCarrer;

	public static void onPreInit()
	{
		//chopperProfession = new VillagerRegistry.VillagerProfession("rewired:chopper",
		//		"rewired:textures/entities/villagers/chopper.png",
		//		"rewired:textures/entities/villagers/zombie_chopper.png");
		runnerProfession = new VillagerRegistry.VillagerProfession("rewired:runner",
				"rewired:textures/entities/villagers/runner.png",
				"rewired:textures/entities/villagers/zombie_runner.png");
		//chopperCareer = new VillagerRegistry.VillagerCareer(chopperProfession, "chopper");
		runnerCarrer = new VillagerRegistry.VillagerCareer(runnerProfession, "runner");

		//Level 1 - Equipment
		runnerCarrer.addTrade(1,
				new EntityVillager.ListItemForEmeralds(CyberwareContent.trenchcoat,new EntityVillager.PriceInfo(3,6)));
		runnerCarrer.addTrade(1,
				new EntityVillager.ListItemForEmeralds(CyberwareContent.jacket,new EntityVillager.PriceInfo(3,6)));
		runnerCarrer.addTrade(1,
				new EntityVillager.ListItemForEmeralds(CyberwareContent.shades,new EntityVillager.PriceInfo(3,6)));
		runnerCarrer.addTrade(1,
				new EntityVillager.ListItemForEmeralds(CyberwareContent.shades2,new EntityVillager.PriceInfo(3,6)));
		//Level 2 - Scavenged Augs
		runnerCarrer.addTrade(2,
				new CyberwareForEmeralds(CyberwareContent.eyeUpgrades,0,CyberwareAPI.QUALITY_SCAVENGED,new EntityVillager.PriceInfo(5,12)));
		runnerCarrer.addTrade(2,
				new CyberwareForEmeralds(CyberwareContent.cybereyeUpgrades,3,CyberwareAPI.QUALITY_SCAVENGED, new EntityVillager.PriceInfo(6,13)));
		runnerCarrer.addTrade(2,
				new CyberwareForEmeralds(CyberwareContent.brainUpgrades, 4, CyberwareAPI.QUALITY_SCAVENGED, new EntityVillager.PriceInfo(6,13)));
		runnerCarrer.addTrade(2,
				new CyberwareForEmeralds(CyberwareContent.lungsUpgrades, 1, CyberwareAPI.QUALITY_SCAVENGED, new EntityVillager.PriceInfo(6,13)));
		runnerCarrer.addTrade(2,
				new CyberwareForEmeralds(CyberwareContent.skinUpgrades,1, CyberwareAPI.QUALITY_SCAVENGED, new EntityVillager.PriceInfo(6,13)));
		runnerCarrer.addTrade(2,
				new CyberwareForEmeralds(CyberwareContent.muscleUpgrades,0, CyberwareAPI.QUALITY_SCAVENGED, new EntityVillager.PriceInfo(6,13)));
		runnerCarrer.addTrade(2,
				new CyberwareForEmeralds(CyberwareContent.handUpgrades, 1, CyberwareAPI.QUALITY_SCAVENGED, new EntityVillager.PriceInfo(6,13)));
		//Level 3 - Weapons?
		runnerCarrer.addTrade(3,
				new EntityVillager.ListEnchantedItemForEmeralds(CyberwareContent.katana, new EntityVillager.PriceInfo(12,19)));

		//Level 4 - Blueprints! PKB, DERPS,
		runnerCarrer.addTrade(4,
				new BlueprintsForEmeralds(ReWIREDContent.handAugments,1 ,new EntityVillager.PriceInfo(9,12)));
		runnerCarrer.addTrade(4,
				new BlueprintsForEmeralds(ReWIREDContent.skinAugments,0 ,new EntityVillager.PriceInfo(9,12)));
		runnerCarrer.addTrade(4,
				new BlueprintsForEmeralds(CyberwareContent.brainUpgrades,2 ,new EntityVillager.PriceInfo(9,12)));


	}

	@SubscribeEvent
	public static void registerVillager(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event)
	{
		//event.getRegistry().register(chopperProfession);
		event.getRegistry().register(runnerProfession);
	}

	private static class ItemsForComponents implements EntityVillager.ITradeList
	{
		public ItemStack buyingItemStack;
		public ComponentPrice price;

		public ItemsForComponents(ItemStack itemStackIn, ComponentPrice componentPrice)
		{
			this.buyingItemStack = itemStackIn;
			this.price = componentPrice;
		}

		public ItemsForComponents(Item itemIn, ComponentPrice componentPrice)
		{
			this.buyingItemStack = new ItemStack(itemIn);
			this.price = componentPrice;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;
			if(this.price != null)
				i = this.price.getPrice(random);

			ItemStack tradeStack = this.buyingItemStack.copy();
			ItemStack componentStack = this.price.getComponent(random).copy();

			if(i<0)
			{
				componentStack.setCount(1);
				tradeStack.setCount(i);

				recipeList.add(new MerchantRecipe(tradeStack, componentStack));
			}
			else
			{
				recipeList.add(new MerchantRecipe(componentStack, tradeStack));
			}
		}
	}

	static class CyberwareForEmeralds implements EntityVillager.ITradeList
	{
		public Item cyberware;
		public int meta;
		public ICyberware.Quality quality;
		public EntityVillager.PriceInfo price;

		public CyberwareForEmeralds(Item cyberwareItem, int meta, ICyberware.Quality quality, EntityVillager.PriceInfo priceInfo)
		{
			this.cyberware = cyberwareItem;
			this.meta = meta;
			this.quality = quality;
			this.price = priceInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;

			if(this.price != null)
				i = this.price.getPrice(random);

			ItemStack emeraldIn = new ItemStack(Items.EMERALD, i, 0);
			ItemStack cyberStack = new ItemStack(this.cyberware, 1, this.meta);
			CyberwareAPI.writeQualityTag(cyberStack, this.quality);

			recipeList.add(new MerchantRecipe(emeraldIn, cyberStack));
		}
	}

	static class BlueprintsForEmeralds implements EntityVillager.ITradeList
	{
		public EntityVillager.PriceInfo price;
		public ItemStack blueprint;

		public BlueprintsForEmeralds(Item cyberwareItem, int meta, EntityVillager.PriceInfo priceInfo)
		{
			this(new ItemStack(cyberwareItem,1, meta), priceInfo);
		}

		public BlueprintsForEmeralds(ItemStack cyberwareStack, EntityVillager.PriceInfo priceInfo)
		{
			this.price = priceInfo;
			this.blueprint =  ItemBlueprint.getBlueprintForItem(cyberwareStack);
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
		{
			int i = 1;

			if(this.price != null)
				i = this.price.getPrice(random);

			ItemStack blueprintOut = this.blueprint.copy();
			ItemStack emeraldIn = new ItemStack(Items.EMERALD,i,0);

			recipeList.add(new MerchantRecipe(emeraldIn, blueprintOut));
		}
	}

	public static class ComponentPrice extends Tuple<Integer, Integer>
	{
		public Type component;

		public ComponentPrice(int min, int max)
		{
			this(Type.NONE, min, max);
		}

		public ComponentPrice(Type component, int min, int max)
		{
			super(min, max);
			this.component = component;

			if(max < min)
				ReWIRED.LOGGER.warn("ComponentPrice({}. {}) is invalid. {} is smaller than {}", min, max, max, min);
		}

		public int getPrice(Random rand)
		{
			return this.getFirst() >= this.getSecond() ? this.getFirst(): this.getFirst() + rand.nextInt( this.getSecond()-this.getFirst() + 1);
		}

		public ItemStack getComponent(Random rand)
		{
			int meta;
			if(component == Type.NONE)
			{
				meta = rand.nextInt(10);
			}
			else
			{
				meta = this.component.ordinal();
			}

			return new ItemStack(CyberwareContent.component, getPrice(rand), meta);
		}

		public enum Type
		{
			NONE,
			ACTUATOR,
			BIOREACTOR,
			TITANIUM_MESH,
			SOLIDSTATE_CIRCUITRY,
			CHROME_PLATING,
			FIBER_OPTICS,
			FULLERENE_MICROSTRUCTURES,
			SYNTHETIC_NERVES,
			STORAGE_CELL,
			MICROELECTRIC_CELLS;
		}
	}
}
