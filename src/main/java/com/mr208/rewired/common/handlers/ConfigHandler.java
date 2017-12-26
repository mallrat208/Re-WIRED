package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import sun.security.ec.ECDHKeyAgreement;

public class ConfigHandler
{
	@Config(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME+ "/General", category = "General")
	public static class General
	{

	}

	@Config(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME+ "/Equipment", category = "Equipment")
	public static class Equipment
	{
		@Comment("Enables Riot Shields")
		public static boolean enableShields = true;
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

		public static class TOC
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
			public int RARITY = 60;
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
			public int RARITY = 60;
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
			public int RARITY = 60;
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
			public int RARITY = 60;
		}

		public static class PKB
		{
			@Comment("Energy Cost of the Augment per Projectile Reflected")
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
			public int RARITY = 60;
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
			public int RARITY = 60;

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

	}
}
