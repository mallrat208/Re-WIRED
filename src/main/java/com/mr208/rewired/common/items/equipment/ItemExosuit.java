package com.mr208.rewired.common.items.equipment;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.Content;
import com.mr208.rewired.common.handlers.ConfigHandler.Equipment;
import net.minecraft.block.BlockDispenser;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemExosuit extends ItemArmorInterlink implements IColorableEquipment
{
	public ItemExosuit(EntityEquipmentSlot equipmentSlotIn)
	{
		super(ArmorMaterial.IRON, 0, equipmentSlotIn);
		String name = "exosuit." + equipmentSlotIn.getName().toLowerCase();
		this.setUnlocalizedName(ReWIRED.MOD_ID+"."+name);
		this.setRegistryName(name);
		this.setHasSubtypes(false);
		this.setCreativeTab(ReWIRED.creativeTab);
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
		
		Content.registeredItems.add(this);
		ForgeRegistries.ITEMS.register(this);
		
		//MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean isNeuralInterfaceRequired(ItemStack stack)
	{
		return Equipment.exosuit.requiresAugment;
	}
	
	@Override
	public boolean isDyable()
	{
		return false;
	}
}
