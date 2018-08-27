package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.handlers.ConfigHandler.Augments;
import com.mr208.rewired.common.items.equipment.INeuralInterlinkItem;
import com.mr208.rewired.common.items.equipment.ItemArmorInterlink;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
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
import net.minecraftforge.items.ItemHandlerHelper;

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
		if(itemStack.getItemDamage() == 0)
			return Augments.ecd.ENERGY_COST;

		return super.getPowerConsumption(itemStack);
	}

	@Override
	public boolean hasCustomPowerMessage(ItemStack itemStack)
	{
		if(itemStack.getItemDamage() == 0)
			return true;
		
		return super.hasCustomPowerMessage(itemStack);
	}

	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		if(itemStack.getItemDamage() == 0)
			return true;
		
		return super.hasMenu(itemStack);
	}
	
	@Override
	public void onRemoved(EntityLivingBase entityLivingBase, ItemStack itemStack)
	{
		super.onRemoved(entityLivingBase, itemStack);
		
		//When Removing the Neural Interace, any equipped MMI-Items need to be removed
		if(itemStack.getItemDamage() == 1 && entityLivingBase instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entityLivingBase;
			
			for(ItemStack stack : player.inventory.armorInventory)
			{
				if(stack.getItem() instanceof INeuralInterlinkItem && ((INeuralInterlinkItem)stack.getItem()).isNeuralInterfaceRequired(stack))
				{
					EntityEquipmentSlot entityEquipmentSlot = EntityLiving.getSlotForItemStack(stack);
					player.setItemStackToSlot(entityEquipmentSlot, ItemStack.EMPTY);
					ItemHandlerHelper.giveItemToPlayer(player,stack.copy());
				}
			}
		}
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

							if(!data.usePower(test, getPowerConsumption(test)))
								break;

							item.setPositionAndUpdate(x,y+0.75,z);
							hasCollected = true;

							maximum++;
						}
					}

					if(hasCollected && entityLivingBase.ticksExisted % 80 == 0)
					{
						entityLivingBase.world.playSound(null, entityLivingBase.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS,0.6f,1.0f);
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

		if(BLACKLIST.contains(item.getItem().getItem()))
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
			String[] blacklistedItem = item.split(":");
			if(blacklistedItem.length==2)
			{
				Item blItem = Item.REGISTRY.getObject(new ResourceLocation(blacklistedItem[0],blacklistedItem[1]));

				if(blItem != null)
					BLACKLIST.add(blItem);
			}
		}
	}
}
