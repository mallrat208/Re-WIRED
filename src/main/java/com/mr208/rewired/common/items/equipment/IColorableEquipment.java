package com.mr208.rewired.common.items.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import java.awt.*;

public interface IColorableEquipment
{
	public static final String COLORTAG = "COLOR_OVERLAY";
	
	default void setColor(ItemStack stack, int color)
	{
		NBTTagCompound nbttagcompound = stack.getTagCompound();
		
		if (nbttagcompound == null)
			nbttagcompound = new NBTTagCompound();
		
		nbttagcompound.setInteger(COLORTAG, color);
		stack.setTagCompound(nbttagcompound);
	}
	
	default boolean hasColor(ItemStack stack)
	{
		return (stack.hasTagCompound() && stack.getTagCompound().hasKey(COLORTAG));
	}
	
	default int getColorInt(ItemStack stack)
	{
		if(!stack.isEmpty())
		{
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey(COLORTAG))
			{
				return stack.getTagCompound().getInteger(COLORTAG);
			}
		}
		
		return 65518;
	}
	
	default float[] getColorFloat(ItemStack stack)
	{
		int color = getColorInt(stack);
		
		return new Color(color).getRGBColorComponents(null);
	}
	
	default boolean isDyable()
	{
		return true;
	}
}
