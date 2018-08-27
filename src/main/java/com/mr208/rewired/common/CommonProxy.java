package com.mr208.rewired.common;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.effects.ReWIREDEffects;
import com.mr208.rewired.common.entities.AugmentEntities;
import com.mr208.rewired.common.handlers.GuiHandler;
import com.mr208.rewired.common.handlers.NetworkHandler;
import com.mr208.rewired.common.handlers.VillagerHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy
{
	public void onPreInit()
	{
		Content.onPreInit();
		ReWIREDEffects.onPreInit();
		VillagerHandler.onPreInit();
	}
	
	public void onInit()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ReWIRED.INSTANCE, new GuiHandler());
		Content.onInit();
		NetworkHandler.init();
	}
	
	public void onPostInit()
	{
		Content.onPostInit();
		AugmentEntities.onPostInit();
	}
}
