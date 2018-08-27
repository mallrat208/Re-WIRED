package com.mr208.rewired.common.handlers;

import com.mr208.rewired.client.gui.GuiERA;
import com.mr208.rewired.common.container.ContainerERA;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler
{
	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
			case 1:
				return new ContainerERA(player);
			default:
				return null;
		}
	}
	
	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
			case 1:
				return new GuiERA(player);
			default:
				return null;
		}
	}
}
