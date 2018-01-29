package com.mr208.rewired.common.items.augments;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class ItemLegAugment extends ItemAugment
{
	public ItemLegAugment(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void onKnockback(LivingKnockBackEvent event)
	{
	
	}
}
