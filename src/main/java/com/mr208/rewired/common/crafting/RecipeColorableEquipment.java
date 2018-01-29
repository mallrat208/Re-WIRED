package com.mr208.rewired.common.crafting;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.items.equipment.IColorableEquipment;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeColorableEquipment extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	public RecipeColorableEquipment()
	{
		this.setRegistryName(ReWIRED.MOD_ID, "colorableequipment");
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		boolean dyeableArmor = false;
		boolean dye = false;
		
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof IColorableEquipment && ((IColorableEquipment)stack.getItem()).isDyable() && !dyeableArmor)
					dyeableArmor = true;
				else if(DyeUtils.isDye(stack) && !dye)
					dye = true;
				else
					return false;
			}
		}
		return dyeableArmor && dye;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack dyeableArmor = ItemStack.EMPTY;
		ItemStack dye = ItemStack.EMPTY;
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty())
				if(stack.getItem() instanceof IColorableEquipment && ((IColorableEquipment)stack.getItem()).isDyable())
					dyeableArmor = stack.copy();
				else if(DyeUtils.isDye(stack))
					dye = stack.copy();
		}
		
		if(!dyeableArmor.isEmpty() && !dye.isEmpty())
		{
			NBTTagCompound armorTag = dyeableArmor.hasTagCompound()? dyeableArmor.getTagCompound() : new NBTTagCompound();
			int color = DyeUtils.colorFromStack(dye).isPresent() ? DyeUtils.colorFromStack(dye).get().getColorValue() : 0;
			
			armorTag.setInteger(IColorableEquipment.COLORTAG, color);
			
			dyeableArmor.setTagCompound(armorTag);
			return dyeableArmor;
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canFit(int width, int height)
	{
		return width * height >= 2;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isDynamic()
	{
		return true;
	}
}
