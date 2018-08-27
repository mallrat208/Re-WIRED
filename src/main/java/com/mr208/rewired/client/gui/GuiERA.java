package com.mr208.rewired.client.gui;

import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class GuiERA extends GuiChest
{
	private final IInventory upperChestInventory;
	private final IInventory lowerChestInventory;
	
	public GuiERA(EntityPlayer entityPlayer)
	{
		super(entityPlayer.inventory, entityPlayer.getInventoryEnderChest());
		
		this.upperChestInventory = entityPlayer.getInventoryEnderChest();
		this.lowerChestInventory = entityPlayer.inventory;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(I18n.format("rewired.gui.era"), 8, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiLeft += TabRegistry.getPotionOffset();
		//this.potionOffsetLast = TabRegistry.getPotionOffsetNEI();
		
		int cornerX = this.guiLeft;
		int cornerY = this.guiTop;
		
		TabRegistry.updateTabValues(cornerX,cornerY,InventoryTabERA.class);
		TabRegistry.addTabsToList(this.buttonList);
		
	}
}
