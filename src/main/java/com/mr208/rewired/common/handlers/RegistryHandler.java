package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ReWIRED.MOD_ID)
public class RegistryHandler
{
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		for(Item item :ReWIREDContent.registeredItems)
		{
			event.getRegistry().register(item);
		}
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		for(Block block : ReWIREDContent.registeredBlocks)
		{
			event.getRegistry().register(block);
		}
	}


}
