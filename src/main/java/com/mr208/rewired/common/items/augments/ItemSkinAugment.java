package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ItemSkinAugment extends ItemAugment
{
	public ItemSkinAugment(String name, EnumSlot slots, String[] subnames)
	{
		super(name, slots, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public void onAdded(EntityLivingBase entityLivingBase, ItemStack itemStack)
	{
		if(itemStack.getItemDamage() == 0)
		{
			ItemStack installed = CyberwareAPI.getCyberware(entityLivingBase, itemStack);
			EnableDisableHelper.toggle(installed);
		}
		
		if(itemStack.getItemDamage() == 1)
		{
			ItemStack installed = CyberwareAPI.getCyberware(entityLivingBase, itemStack);
			EnableDisableHelper.toggle(installed);
			if(!(entityLivingBase instanceof EntityPlayer))
			{
				addAEGISBonus(entityLivingBase);
			}
		}
	}
	
	@Override
	public void onRemoved(EntityLivingBase entityLivingBase, ItemStack itemStack)
	{
		super.onRemoved(entityLivingBase, itemStack);
		
		if(itemStack.getItemDamage() == 1)
		{
			removeAEGISBonus(entityLivingBase);
		}
	}
	
	private void removeAEGISBonus(EntityLivingBase entityLivingBase)
	{
		entityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(AEGIS_ARMOR);
		entityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).removeModifier(AEGIS_TOUGHNESS);
		
		World world = entityLivingBase.getEntityWorld();
		world.playSound(null,entityLivingBase.getPosition(), SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.PLAYERS, 1f,.6f);
	}
	
	private void addAEGISBonus(EntityLivingBase entityLivingBase)
	{
		entityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(AEGIS_ARMOR);
		entityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).applyModifier(AEGIS_TOUGHNESS);
		
		World world = entityLivingBase.getEntityWorld();
		world.playSound(null,entityLivingBase.getPosition(), SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.PLAYERS, 1f,.6f);
	}
	
	@SubscribeEvent
	public void onDeath(LivingDeathEvent event)
	{
		if(CyberwareHelper.isAugmentAvailable(event.getEntityLiving(), new ItemStack(this,1,1)))
		{
			EntityLivingBase entityLivingBase = event.getEntityLiving();
			removeAEGISBonus(entityLivingBase);
			
			EnableDisableHelper.toggle(CyberwareAPI.getCyberware(entityLivingBase, new ItemStack(this,1,1)));
		}
	}
	
	@Override
	public int getPowerConsumption(ItemStack itemStack)
	{
		if(itemStack.getItem() instanceof ItemSkinAugment)
		{
			switch (itemStack.getItemDamage())
			{
				case 0:
					return ConfigHandler.Augments.TOC.ENERGY_COST;
				case 1:
					return 50;
				default:
					return super.getPowerConsumption(itemStack);
			}
		}

		return super.getPowerConsumption(itemStack);
	}

	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		switch(itemStack.getItemDamage())
		{
			case 0:
				return true;
			case 1:
				return true;
			default:
					return false;
		}
	}
	
	private static final AttributeModifier AEGIS_ARMOR = new AttributeModifier(UUID.fromString("f21b6200-6181-4776-8705-4d480f5ab035"),"generic.armor",12,0);
	private static final AttributeModifier AEGIS_TOUGHNESS = new AttributeModifier(UUID.fromString("fad806d4-3f6b-4539-83a2-28dd1396bdeb"),"generic.armorToughness",8,0);

	@Override
	public void use(Entity entity, ItemStack itemStack)
	{
		super.use(entity, itemStack);

		if(itemStack.getItemDamage() == 0)
		{
			if (!EnableDisableHelper.isEnabled(itemStack))
			{
				((EntityLivingBase) entity).removePotionEffect(MobEffects.INVISIBILITY);
			}
			
			return;
		}
		
		if(itemStack.getItemDamage() == 1 && entity instanceof EntityLivingBase)
		{
			EntityLivingBase e = (EntityLivingBase) entity;
			
			if(!EnableDisableHelper.isEnabled(itemStack))
				removeAEGISBonus(e);
			else
				addAEGISBonus(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayerEvent(RenderLivingEvent.Pre event)
	{
		if(CyberwareHelper.isAugmentAvailable(event.getEntity(), new ItemStack(this,1,0)))
		{
			if(event.getEntity().isPotionActive(MobEffects.INVISIBILITY))
				event.setCanceled(true);
		}
	}
	
	private Set<UUID> lastAEGIS = new HashSet<>();

	@SubscribeEvent
	public void onCyberwareUpdate(CyberwareUpdateEvent event)
	{
		EntityLivingBase entityLivingBase = event.getEntityLiving();

		if(entityLivingBase==null)
			return;

		//Thermoptical Camo
		ItemStack test = new ItemStack(this,1,0);

		if(entityLivingBase.ticksExisted % 20 == 0 && CyberwareHelper.isAugmentAvailable(entityLivingBase, test))
		{
			ICyberwareUserData data = CyberwareAPI.getCapability(entityLivingBase);
			if(data.usePower(test, getPowerConsumption(test)))
			{
				entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
			}
			else
			{
				entityLivingBase.removePotionEffect(MobEffects.INVISIBILITY);
			}

			data.updateCapacity();
			if(!entityLivingBase.world.isRemote)
				CyberwareAPI.updateData(entityLivingBase);
		}
		
		// AEGIS Defense Matrix
		 test = new ItemStack(this, 1,1);
		boolean last = lastAEGIS.contains(entityLivingBase.getUniqueID());
		
		boolean powerUsed = CyberwareHelper.isAugmentAvailable(entityLivingBase, test) && entityLivingBase.ticksExisted % 20 == 0 ? CyberwareAPI.getCapability(entityLivingBase).usePower(test, getPowerConsumption(test)) : last;
		
		if(powerUsed)
		{
			if(!lastAEGIS.contains(entityLivingBase.getUniqueID()))
				lastAEGIS.add(entityLivingBase.getUniqueID());
		}
		else
		{
			if(lastAEGIS.contains(entityLivingBase.getUniqueID()))
				lastAEGIS.remove(entityLivingBase.getUniqueID());
		}
		
	}
}
