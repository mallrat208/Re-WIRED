package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
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
	public boolean hasMenu(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 0;
	}

	@Override
	public boolean hasCustomCapacityMessage(ItemStack itemStack)
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

				if(eventFallDistance*30 <= entityPower)
				{
					data.usePower(new ItemStack(this,1,0),((int)eventFallDistance) * 30);
				}
				else
				{
					int maxBlocks = Math.floorDiv(entityPower, 30);
					data.usePower(new ItemStack(this,1,0), maxBlocks * 30);
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
}
