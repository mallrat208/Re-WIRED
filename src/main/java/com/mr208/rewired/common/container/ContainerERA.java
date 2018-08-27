package com.mr208.rewired.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;

public class ContainerERA extends ContainerChest
{
	public ContainerERA(EntityPlayer player)
	{
		super(player.inventory, player.getInventoryEnderChest(), player);
	}
}
