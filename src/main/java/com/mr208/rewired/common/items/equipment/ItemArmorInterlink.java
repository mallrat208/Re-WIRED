package com.mr208.rewired.common.items.equipment;

import com.mr208.rewired.common.Content;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemArmorInterlink extends ItemArmor implements INeuralInterlinkItem,IColorableEquipment
{
	
	public ItemArmorInterlink(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
	{
		super(materialIn, renderIndexIn, equipmentSlotIn);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(isNeuralInterfaceRequired(stack))
		{
			tooltip.add(TextFormatting.DARK_RED + I18n.format("tooptip.neuralinterlink.desc"));
		}
		
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
	{
		if(isNeuralInterfaceRequired(stack))
		{
			if(CyberwareAPI.hasCapability(entity))
			{
				ICyberwareUserData data = CyberwareAPI.getCapability(entity);
				return data.isCyberwareInstalled(new ItemStack(Content.craniumAugments, 1, 1));
			}
			else
				return false;
		}
		else
			return true;
	}
	
	@Override
	public boolean isNeuralInterfaceRequired(ItemStack stack)
	{
		return false;
	}
}
