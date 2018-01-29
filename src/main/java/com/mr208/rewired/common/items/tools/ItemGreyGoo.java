package com.mr208.rewired.common.items.tools;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mr208.rewired.common.items.ItemReWIRED;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGreyGoo extends ItemReWIRED
{
	public ItemGreyGoo(String name)
	{
		super(name);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(ChatFormatting.DARK_GRAY + I18n.format(this.getUnlocalizedName()+ ".tooltip"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@SubscribeEvent
	public void onAnvilUpdateEvent(AnvilUpdateEvent event)
	{
		ItemStack repairableItem = event.getLeft();
		ItemStack greyGoo = event.getRight();
		
		if(!repairableItem.isEmpty() && repairableItem.getItem().isRepairable() && greyGoo.getItem() instanceof ItemGreyGoo)
		{
			event.setMaterialCost(1);
			
			int repairAmount = MathHelper.intFloorDiv(repairableItem.getMaxDamage(),3);
			
			
		}
	}
	
	@SubscribeEvent
	public void onAnvilRepairEvent(AnvilRepairEvent event)
	{
	
	}
}
