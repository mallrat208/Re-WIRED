package com.mr208.rewired.client.gui;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.Content;
import com.mr208.rewired.common.handlers.NetworkHandler;
import com.mr208.rewired.common.handlers.packets.PacketGUI;
import flaxbeard.cyberware.api.CyberwareAPI;
import micdoodle8.mods.galacticraft.api.client.tabs.AbstractTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

public class InventoryTabERA extends AbstractTab
{
	public InventoryTabERA()
	{
		super(0,0,0, new ItemStack(Content.skinAugments,1,2));
	}
	
	@Override
	public void onTabClicked()
	{
		Minecraft.getMinecraft().player.openGui(ReWIRED.INSTANCE, 1, Minecraft.getMinecraft().player.world, 0, 0, 0);
		NetworkHandler.INSTANCE.sendToServer(new PacketGUI(1,0,0,0));
	}
	
	@Override
	public boolean shouldAddToList()
	{
		EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
		return CyberwareAPI.hasCapability(playerSP) && CyberwareAPI.isCyberwareInstalled(playerSP, new ItemStack(Content.skinAugments,1,2));
	}
}
