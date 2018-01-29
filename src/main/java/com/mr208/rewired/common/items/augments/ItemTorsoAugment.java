package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.handlers.ConfigHandler.Augments;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;

public class ItemTorsoAugment extends ItemAugment
{
	public ItemTorsoAugment(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);

		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public int getPowerConsumption(ItemStack itemStack)
	{
		if(itemStack.getItemDamage() == 0)
		{
			return Augments.derps.ENERGY_COST;
		}
		
		return super.getPowerConsumption(itemStack);
	}
	
	@Override
	public boolean isIncompatible(ItemStack augment, ItemStack otherAugment)
	{
		return augment.getItemDamage() == 1 && CyberwareAPI.getCyberware(otherAugment).isEssential(otherAugment);
	}
	
	@Override
	public boolean isEssential(ItemStack augment)
	{
		return augment.getItemDamage() == 1;
	}
	
	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 0;
	}
	
	@Override
	public boolean hasCustomPowerMessage(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 0;
	}
	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		if(event.getDistance()>5.0F && CyberwareAPI.hasCapability(event.getEntityLiving()) && CyberwareHelper.isAugmentAvailable(event.getEntityLiving(), new ItemStack(this,1,0)))
		{
			EntityLivingBase entityLivingBase = event.getEntityLiving();

			EnderTeleportEvent enderTeleportEvent = new EnderTeleportEvent(entityLivingBase, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ,0.0F);
			if(!MinecraftForge.EVENT_BUS.post(enderTeleportEvent))
			{
				World entityWorld = entityLivingBase.getEntityWorld();
				ICyberwareUserData data = CyberwareAPI.getCapability(entityLivingBase);
				int entityPower = data.getStoredPower();
				float eventFallDistance = event.getDistance()-3.0F;

				event.setDistance(3F);

				if(eventFallDistance * getPowerConsumption(new ItemStack(this,1,0)) <= entityPower)
				{
					data.usePower(new ItemStack(this,1,0),((int)eventFallDistance) * getPowerConsumption(new ItemStack(this,1,0)));
				}
				else
				{
					int maxBlocks = Math.floorDiv(entityPower, getPowerConsumption(new ItemStack(this,1,0)));
					data.usePower(new ItemStack(this,1,0), maxBlocks * getPowerConsumption(new ItemStack(this,1,0)));
					event.setDistance(eventFallDistance-maxBlocks);
				}

				Random random = entityWorld.rand;
				for(int i = 0; i<32;++i)
				{
					entityWorld.spawnParticle(EnumParticleTypes.PORTAL,
							entityLivingBase.posX,
							entityLivingBase.posY + random.nextDouble() * 2.0D,
							entityLivingBase.posZ,
							random.nextGaussian(),
							0.0D,
							random.nextGaussian());
				}
				BlockPos pos = entityLivingBase.getPosition();
				entityWorld.playSound(null,entityLivingBase.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f,1.0f);

				data.updateCapacity();
				if(!entityLivingBase.world.isRemote)
					CyberwareAPI.updateData(entityLivingBase);
			}
		}
	}
	
	@SubscribeEvent
	public void onFinishUsing(LivingEntityUseItemEvent.Finish event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		ItemStack stack = event.getItem();
		
		if (entity instanceof EntityPlayer && CyberwareAPI.hasCapability(entity) && !stack.isEmpty() && isFood(stack))
		{
			if (CyberwareAPI.isCyberwareInstalled(entity, new ItemStack(this,1,1))) {
				((EntityPlayer) event.getEntity()).getFoodStats().addStats(2, 2.0F);
			}
		}
	}
	
	private boolean isFood(ItemStack itemStack)
	{
		if(itemStack.getItemUseAction() == EnumAction.EAT)
			return true;
		
		if(itemStack.getItemUseAction() == EnumAction.DRINK && !(itemStack.getItem() instanceof ItemPotion))
			return true;
			
		return false;
	}
}
