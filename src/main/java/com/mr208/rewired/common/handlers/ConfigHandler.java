package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;


public class ConfigHandler
{
	@Config(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME+ "/General", category = "General")
	public static class General
	{
		@Name("Blocks")
		@Comment("Various Blocks added by the Mod")
		public static Blocks BLOCKS = new Blocks();
		
		public static class Blocks
		{
			@Comment("Enables Electrochromic Glass. Turns Opaque when powered by Redstone")
			@Name("Enable Electrochromic Glass")
			public boolean enableECG = true;
		}

	}
	

	@Config(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME+ "/Equipment", category = "Equipment")
	public static class Equipment
	{
		@Name("Shields")
		@Comment("Shield Item. Found primarily as mob drops or rarely as loot")
		public static SHIELDS shields = new SHIELDS();
		
		public static class SHIELDS
		{
			@Comment("Enables Riot Shields")
			@Name("Riot Shields")
			public boolean enableShields = true;
		}
	}

	@Config(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME+ "/Augments", category = "Augments")
	public static class Augments
	{
		@Name("Rail Rider")
		@Comment({"Augment Slot: Foot","When enabled, the augmented entity is able to Ride Minecart Tracks","Use Shift or disable via the Cyberware Menu"})
		public static RailRider RailRider = new RailRider();

		@Name("Thermoptic Camouflage")
		@Comment({"Augment Slot: Skin","When enabled, the augmented entity is rendered invisible","Controlled via the Cyberware Menu"})
		public static TOC TOC = new TOC();

		@Name("Plasteel Fist")
		@Comment({"Augment Slot: Hand","When equipped the augmented entity is able to mine at iron level when using their fist"})
		public static PlasteelFist plasteelFist = new PlasteelFist();

		@Name("DERPS")
		@Comment({"Augment Slot: Lower Organs","Using Ender Fields, the DERPS negates partial fall damage at the cost of energy","Controlled via the Cyberware Menu","Relies on Ender Teleportation Fields."})
		public static DERPS derps = new DERPS();

		@Name("Projected Kinetic Barrier")
		@Comment({"Augment Slot: Hand","When enabled, the augmented entity is able to block incoming projectiles by expending energy.","Requires Line of Sight. Controlled via the Cyberware Menus"})
		public static PKB pkb = new PKB();

		@Name("Ender Convergence Device")
		@Comment({"Augment Slot: Cranium","When enabled, teleports items towards the augmented entity.","Controlled via the Cyberware Menu","Relies on Ender Teleportation Fields"})
		public static ECD ecd = new ECD();
		
		@Name("Cybernetic Stomach")
		@Comment({"Augment Slot: Lower Organs Essential", "Increases efficiency of digestion of foods"})
		public static CyberStomach cyberStomach = new CyberStomach();
		
		public static class CyberStomach
		{
			@Comment("Tolerance Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Tolerance")
			public int TOLERANCE_COST = 8;
			
			@Comment("Rarity of the Augment")
			@RangeInt(min = 0, max = 100)
			@Name("Rarity")
			public int RARITY = 10;
			
			@Comment("Power Bar Food Value")
			@Name("Power Bar - Food Value")
			@RangeInt(min = 0)
			public int FOOD_POWERBAR_AMT = 2;
			
			@Comment("Power Bar Saturation Value")
			@Name("Power Bar - Saturation Value")
			@RangeDouble(min = 0)
			public double FOOD_POWERBAR_SAT = 1.0d;
			
			@Comment("Power Bar Energy Value")
			@Name("Power Bar - Energy Value")
			@RangeInt(min = 0)
			public int FOOD_POWERBAR_NRG = 300;
			
			@Comment("Energy Drink Food Value")
			@Name("Energy Drink - Food Value")
			@RangeInt(min = 0)
			public int FOOD_ENERGYDRINK_AMT = 2;
			
			@Comment("Energy Drink Saturation Value")
			@Name("Energy Drink - Saturation Value")
			@RangeDouble(min = 0)
			public double FOOD_ENERGYDRINK_SAT = 0.5d;
			
			@Comment("Energy Drink Energy Value")
			@Name("Energy Drink - Energy Value")
			@RangeInt(min = 0)
			public int FOOD_ENERGYDRINK_NRG = 500;
		}

		public static class TOC
		{
			@Comment("Energy Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Energy Cost")
			public int ENERGY_COST = 75;

			@Comment("Tolerance Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Tolerance")
			public int TOLERANCE_COST = 6;

			@Comment("Rarity of the Augment")
			@RangeInt(min = 0, max = 100)
			@Name("Rarity")
			public int RARITY = 2;
		}

		public static class DERPS
		{
			@Comment("Energy Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Energy Cost")
			public int ENERGY_COST = 30;

			@Comment("Tolerance Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Tolerance")
			public int TOLERANCE_COST = 6;

			@Comment("Rarity of the Augment")
			@RangeInt(min = 0, max = 100)
			@Name("Rarity")
			public int RARITY = 10;
		}

		public static class RailRider
		{
			@Comment("Energy Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Energy Cost")
			public int ENERGY_COST = 30;

			@Comment("Tolerance Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Tolerance")
			public int TOLERANCE_COST = 6;

			@Comment("Rarity of the Augment")
			@RangeInt(min = 0, max = 100)
			@Name("Rarity")
			public int RARITY = 40;
		}

		public static class PlasteelFist
		{
			@Comment("Tolerance Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Tolerance")
			public int TOLERANCE_COST = 6;

			@Comment("Rarity of the Augment")
			@RangeInt(min = 0, max = 100)
			@Name("Rarity")
			public int RARITY = 15;
		}

		public static class PKB
		{
			@Comment("Energy Cost of the Augment per Projectile Reflected")
			@RangeInt(min = 0)
			@Name("Energy Cost")
			public int ENERGY_COST = 55;

			@Comment("Tolerance Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Tolerance")
			public int TOLERANCE_COST = 6;

			@Comment("Rarity of the Augment")
			@RangeInt(min = 0, max = 100)
			@Name("Rarity")
			public int RARITY = 1;
		}

		public static class ECD
		{
			@Comment("Energy Cost of the Augment per Item Collected")
			@RangeInt(min = 0)
			@Name("Energy Cost")
			public int ENERGY_COST = 20;

			@Comment("Tolerance Cost of the Augment")
			@RangeInt(min = 0)
			@Name("Tolerance")
			public int TOLERANCE_COST = 6;

			@Comment("Rarity of the Augment")
			@RangeInt(min = 0, max = 100)
			@Name("Rarity")
			public int RARITY = 20;

			@Comment("Range at which the ECD can pull items")
			@RangeInt(min = 0,max = 48)
			@Name("Operational Range")
			public int RANGE = 8;

			@Comment("Number of Items Collected per Operation")
			@RangeInt(min = 1, max = 128)
			@Name("Collection Amount")
			public int AMOUNT = 16;

			@Comment("Items Blacklisted from being pulled by the ECD")
			public String[] BLACKLIST = {"appliedenergistics2:item.itemcrystalseed", "botania:livingrock",
					"botania:manatablet"};

		}

	}

	@Config(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME+ "/Entities", category = "Entities")
	public static class Entities
	{
		
		@Comment({"Enables Grey Goo. Occasionally spawns from dieing Cybermobs","Not Yet Implemented"})
		@Name("Enable Grey Goo")
		public static boolean enableGreyGoo = true;
		
		@Comment("Enables ReWIRED Villager, the Runner. Trades for Cyberware and various Equipment")
		@Name("Enable Runner Villager")
		public static boolean enableVillager = true;
		
		@Comment("Configuration Options relating to the Cyberskeleton")
		@Name("Cyberskeleton Config")
		public static Cyberskelton cyberskelton = new Cyberskelton();
		
		@Comment("Expand the lists of Cyberized Enemies with non-standard entries")
		@Name("Additional Augmentation")
		public static Augmentation augmentation = new Augmentation();
		
		public static class Augmentation
		{
			@Comment({"Add the registry name of the Entity to Augment", "One entry per line in the format of modid:registry name","Example:  minecraft:zombie"})
			@Name("Augment External Entities")
			public String[] additionCyberEntities = {""};
		}
		
		public static class Cyberskelton
		{
			@Comment("Enabled Cyberskeleton Spawning. Replaces Regular Skeletons Occasionally")
			@Name("Enable Cyberskeleton")
			public boolean enableCyberskeleton = true;
			
			@Name("Cyberskeleton Weight")
			@RangeInt(min = 0)
			public int WEIOHT = 60;
			
			@Name("Cyberskeleton Min")
			@RangeInt(min = 0)
			public int MIN_PACK = 1;
			
			@Name("Cyberskeleton Max")
			@RangeInt(min = 0)
			public int MAX_PACK = 3;
			
		}
	}
}
