package com.mr208.rewired;

import com.mr208.rewired.common.CommonProxy;
import com.mr208.rewired.common.ReWIREDContent;
import com.mr208.rewired.common.effects.ReWIREDEffects;
import com.mr208.rewired.common.handlers.NetworkHandler;
import com.mr208.rewired.common.handlers.VillagerHandler;
import flaxbeard.cyberware.api.item.ICyberware;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ReWIRED.MOD_ID, name = ReWIRED.MOD_NAME, version = ReWIRED.MOD_VERSION, dependencies = ReWIRED.MOD_DEPS)
public class ReWIRED
{
	public static final String MOD_ID = "rewired";
	public static final String MOD_NAME = "ReWIRED";
	public static final String MOD_VERSION = "@MOD_VERSION@";
	public static final String MOD_DEPS = "after:cyberware;";

	@Instance(MOD_ID)
	public static ReWIRED INSTANCE;

	public static final String CLIENT_PROXY = "com.mr208.rewired.client.ClientProxy";
	public static final String COMMON_PROXY = "com.mr208.rewired.common.CommonProxy";

	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static CommonProxy PROXY;

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final ICyberware.Quality QUALITY_PROTOTYPE = new ICyberware.Quality("rewired.quality.prototype","rewired.quality.prototype.name_modifier", "prototype");

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ReWIREDContent.onPreInit();
		ReWIREDEffects.onPreInit();
		VillagerHandler.onPreInit();
	}

	@EventHandler
	public void onInit(FMLInitializationEvent event)
	{
		NetworkHandler.init();
	}

	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{

	}

	public static CreativeTabs creativeTab = new CreativeTabs(MOD_ID)
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(Items.APPLE);
		}
	};
}
