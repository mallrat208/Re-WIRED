package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class ItemCraniumAugment extends ItemAugment
{

	protected static List<Item> BLACKLIST;

	public ItemCraniumAugment(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public int getPowerConsumption(ItemStack itemStack)
	{
		if(itemStack.getItem() instanceof ItemCraniumAugment)
		{
			switch (itemStack.getItemDamage())
			{
				case 0:
					return ConfigHandler.Augments.ecd.ENERGY_COST;
				default:
			}
		}

		return 0;
	}

	@Override
	public boolean hasCustomPowerMessage(ItemStack itemStack)
	{
		if(itemStack.getItem() instanceof ItemCraniumAugment)
		{
			switch (itemStack.getItemDamage())
			{
				case 0:
					return true;
				default:
			}
		}
		return super.hasCustomPowerMessage(itemStack);
	}

	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		if(itemStack.getItem() instanceof ItemCraniumAugment)
		{
			switch (itemStack.getItemDamage())
			{
				case 0:
					return true;
				default:
			}
		}
		return super.hasMenu(itemStack);
	}

	@SubscribeEvent
	public void onCyberUpdate(CyberwareUpdateEvent event)
	{
		EntityLivingBase entityLivingBase = event.getEntityLiving();

		ItemStack test = new ItemStack(this,1,0);

		//Only trigger once per second on the server if the augment is installed and active
		if(!entityLivingBase.world.isRemote && entityLivingBase.ticksExisted % 20 == 0 && CyberwareHelper.isAugmentAvailable(entityLivingBase,test))
		{
			if(BLACKLIST == null)
				populateBlacklist();

			double x,y,z;
			x = entityLivingBase.posX;
			y = entityLivingBase.posY;
			z = entityLivingBase.posZ;

			List<EntityItem> itemsInRange = entityLivingBase.world.getEntitiesWithinAABB(EntityItem.class,
					new AxisAlignedBB(x - ConfigHandler.Augments.ecd.RANGE,
							y  - ConfigHandler.Augments.ecd.RANGE,
							z - ConfigHandler.Augments.ecd.RANGE,
							x + ConfigHandler.Augments.ecd.RANGE,
							y + ConfigHandler.Augments.ecd.RANGE,
							z + ConfigHandler.Augments.ecd.RANGE));

			ICyberwareUserData data = CyberwareAPI.getCapability(entityLivingBase);
			boolean hasCollected = false;
			int maximum = 0;

			if(itemsInRange.size()>0)
			{
				EnderTeleportEvent teleportEvent = new EnderTeleportEvent(entityLivingBase, x, y, z, 0.0f);
				if(!MinecraftForge.EVENT_BUS.post(teleportEvent))
				{
					for(EntityItem item: itemsInRange)
					{
						if (canItemBeTeleported(item) && entityLivingBase.getDistance(item) > 2.0f)
						{
							if(maximum > ConfigHandler.Augments.ecd.AMOUNT)
								break;

							if(!data.usePower(test, ConfigHandler.Augments.ecd.ENERGY_COST))
								break;

							item.setPositionAndUpdate(x,y+0.75,z);
							hasCollected = true;

							maximum++;
						}
					}

					if(hasCollected)
					{
						entityLivingBase.world.playSound(null, entityLivingBase.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS,1.0f,1.0f);
					}

					CyberwareAPI.updateData(entityLivingBase);
				}
			}
		}
	}

	private boolean canItemBeTeleported(EntityItem item)
	{
		if(item.isDead)
			return false;

		if(BLACKLIST.contains(item.getItem()))
			return false;

		//Prevent Movement from IE Conveyors
		NBTTagCompound nbtTagCompound = item.getEntityData();
		if(nbtTagCompound.hasKey("PreserveRemoteMovement") && nbtTagCompound.getBoolean("PreserveRemoteMovement"))
			return false;

		return true;
	}

	private void populateBlacklist()
	{
		BLACKLIST = new ArrayList<>();
		for(String item: ConfigHandler.Augments.ecd.BLACKLIST)
		{
			String[] blacklistedItem = item.split("@");
			if(blacklistedItem.length==2)
			{
				Item blItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(blacklistedItem[0],blacklistedItem[1]));
				if(blItem != null)
					BLACKLIST.add(blItem);
			}
		}
	}

}
