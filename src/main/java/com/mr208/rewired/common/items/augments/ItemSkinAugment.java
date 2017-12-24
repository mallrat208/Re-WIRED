package com.mr208.rewired.common.items.augments;

import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemSkinAugment extends ItemAugment
{
	public ItemSkinAugment(String name, EnumSlot slots, String[] subnames)
	{
		super(name, slots, subnames);
	}

	@Override
	public int getPowerConsumption(ItemStack itemStack)
	{
		return super.getPowerConsumption(itemStack);
	}

	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 0;
	}

	public void cyberwareUpdate(CyberwareUpdateEvent event)
	{

	}
}
