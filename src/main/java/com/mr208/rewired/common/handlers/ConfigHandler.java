package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import net.minecraftforge.common.config.Config;

@Config(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME, category = "ReWIRED.General")
public class ConfigHandler
{
	@Config.Comment("Enables Riot Shields")
	public static boolean enableShields = true;
}
