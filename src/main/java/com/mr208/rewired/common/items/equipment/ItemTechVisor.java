package com.mr208.rewired.common.items.equipment;

import com.google.common.collect.Multimap;
import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.client.render.ModelCustomArmor;
import com.mr208.rewired.client.render.ModelCustomArmor.ArmorModel;
import com.mr208.rewired.common.ReWIREDContent;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class ItemTechVisor extends ItemArmor implements IColorableEquipment
{
	public static final IAttribute RANGED_DAMAGE_BONUS = new RangedAttribute(null,"rewired.ranged_damage", 1.0D, 0.0D, 2048.0D);
	public static final UUID RANGED_DAMAGE_MODIFIER = UUID.fromString("c1216e4c-b4c6-44b1-beb5-28ec10a6638c");
	
	public ItemTechVisor(ArmorMaterial materialIn)
	{
		super(materialIn, 0, EntityEquipmentSlot.HEAD);
		String name = "visor";
		this.setUnlocalizedName(ReWIRED.MOD_ID+".armor."+name);
		this.setRegistryName(name);
		this.setHasSubtypes(false);
		this.setCreativeTab(ReWIRED.creativeTab);
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
		
		ReWIREDContent.registeredItems.add(this);
		ForgeRegistries.ITEMS.register(this);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public boolean isDamageable()
	{
		return false;
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
	{
		return armorType == EntityEquipmentSlot.HEAD;
	}
	
	@Override
	public boolean isDyable()
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		return "rewired:textures/models/equipment/techvisor.png";
	}
	
	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
		ModelCustomArmor model = ModelCustomArmor.INSTANCE;
		model.modelType = ArmorModel.TECHVISOR;
		model.itemStack = itemStack.copy();
		
		return model;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
	{
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
		
		if (equipmentSlot == this.armorType)
		{
			multimap.put(RANGED_DAMAGE_BONUS.getName(), new AttributeModifier(RANGED_DAMAGE_MODIFIER, "Ranged Weapon Modifier", 0.1, 1));
		}
		
		return multimap;
	}
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event)
	{
		if(!event.getEntity().world.isRemote)
		{
			if(event.getSource().isProjectile() && event.getSource().getTrueSource() != null && event.getSource().getImmediateSource() != event.getSource().getTrueSource() && event.getSource().getTrueSource() instanceof EntityLivingBase)
			{
				EntityLivingBase attackingEntity = (EntityLivingBase) event.getSource().getTrueSource();
				
				ItemStack headSlot = attackingEntity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				
				if(!headSlot.isEmpty() && headSlot.getItem() instanceof ItemTechVisor)
				{
					float damage = event.getAmount();
					float modifiedDamage;
					
					modifiedDamage = (float)( damage * 1.1);
					
					event.setAmount(modifiedDamage);
				}
			}
		}
	}
}

	

