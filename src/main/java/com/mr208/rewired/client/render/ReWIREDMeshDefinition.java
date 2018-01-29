package com.mr208.rewired.client.render;

import com.mr208.rewired.common.items.augments.ItemAugment;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware.Quality;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class ReWIREDMeshDefinition implements ItemMeshDefinition
{
	
	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack)
	{
		ItemStack test = stack.copy();
		if (!test.isEmpty() && test.hasTagCompound())
		{
			test.getTagCompound().removeTag(CyberwareAPI.QUALITY_TAG);
		}
		
		ItemAugment ware = (ItemAugment) stack.getItem();
		String added = "";
		if (ware.subNames.length > 0)
		{
			int i = Math.min(ware.subNames.length - 1, stack.getItemDamage());
			added = "_" + ware.subNames[i];
		}
		
		Quality q = CyberwareAPI.getCyberware(stack).getQuality(stack);
		
		if (q != null && CyberwareAPI.getCyberware(test).getQuality(test) != q && q.getSpriteSuffix() != null)
		{
			return new ModelResourceLocation(ware.getRegistryName() + added + "_" + q.getSpriteSuffix(), "inventory");
		}
		else
		{
			return new ModelResourceLocation(ware.getRegistryName() + added, "inventory");
		}
	}
	
}
