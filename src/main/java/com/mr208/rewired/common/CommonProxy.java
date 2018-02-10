package com.mr208.rewired.common;

import com.mr208.rewired.common.effects.ReWIREDEffects;
import com.mr208.rewired.common.entities.AugmentEntities;
import com.mr208.rewired.common.handlers.NetworkHandler;
import com.mr208.rewired.common.handlers.VillagerHandler;

public class CommonProxy
{
	public void onPreInit()
	{
		ReWIREDContent.onPreInit();
		ReWIREDEffects.onPreInit();
		VillagerHandler.onPreInit();
	}
	
	public void onInit()
	{
		ReWIREDContent.onInit();
		NetworkHandler.init();
	}
	
	public void onPostInit()
	{
		ReWIREDContent.onPostInit();
		AugmentEntities.onPostInit();
	}
}
