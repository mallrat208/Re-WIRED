package com.mr208.rewired.common.items;

import com.mr208.rewired.ReWIRED;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class ItemDebug extends ItemReWIRED
{
	public ItemDebug(String name)
	{
		super(name);
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if(player.world.isRemote)
			player.sendMessage(new TextComponentString(entity.getName()));
		
		return true;
	}
}
