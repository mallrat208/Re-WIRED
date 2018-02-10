package com.mr208.rewired.common.entities;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.handlers.ConfigHandler.Entities;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.CyberwareUserDataImpl.Provider;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.entity.EntityCyberZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = ReWIRED.MOD_ID)
public class AugmentEntities
{
	private static ArrayList<Class> augmentedEntityClasses = new ArrayList<>();
	
	public static void onPreInit()
	{
		for(String name: Entities.augmentation.additionCyberEntities)
		{
			try
			{
				Class clazz = Class.forName(name);
				if(EntityLivingBase.class.isAssignableFrom(clazz))
				{
					augmentedEntityClasses.add(clazz);
					ReWIRED.LOGGER.info("Added {} as a valid Augmented Entity", name);
					
				}
				
			} catch(ClassNotFoundException e)
			{
				ReWIRED.LOGGER.warn("Failed to find class: {}, skipping.", name);
			}
		}
		
		ReWIRED.LOGGER.info("Added {} Entities to be Augmented", augmentedEntityClasses.size());
	}
	
	public static void onPostInit()
	{
		for(String entry: Entities.augmentation.additionCyberEntities)
		{
			
			EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entry));
			
			if(entityEntry!=null)
			{
				ReWIRED.LOGGER.info("Found Entry for {}, Let's do some upgrades", entry);
				augmentedEntityClasses.add(entityEntry.getEntityClass());
			}
			
			
		}
		
		ReWIRED.LOGGER.info("{} Entities have been registered for augmentation", augmentedEntityClasses.size());
	}
	
	@SubscribeEvent
	public static void attachCyberwareData(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof EntityLivingBase)
		{
			if(augmentedEntityClasses.contains(event.getObject().getClass()))
			{
				event.addCapability(Provider.NAME, new CyberwareUserDataImpl.Provider());
			}
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingUpdate(LivingUpdateEvent event)
	{
		if(!event.getEntityLiving().world.isRemote && event.getEntityLiving().ticksExisted==1)
		{
			if(augmentedEntityClasses.contains(event.getEntityLiving().getClass()))
			{
				EntityLivingBase entityLivingBase = event.getEntityLiving();
				CyberwareHelper.addRandomCyberware(entityLivingBase, true);
			}
		}
	}
	
	@SubscribeEvent
	public static void entityDeathEvent(LivingDropsEvent event)
	{
		if (augmentedEntityClasses.contains(event.getEntityLiving().getClass()) && CyberwareAPI.hasCapability(event.getEntityLiving()))
		{
			EntityLivingBase entityLivingBase = event.getEntityLiving();
			World world = entityLivingBase.getEntityWorld();
			
			ICyberwareUserData cyberware = CyberwareAPI.getCapability(entityLivingBase);
			
			float rarity = Math.min(100, CyberwareConfig.DROP_RARITY + event.getLootingLevel() * 5F);
			if (world.rand.nextFloat() < (rarity / 100F))
			{
				List<ItemStack> allWares = new ArrayList<>();
				for (ICyberware.EnumSlot slot : ICyberware.EnumSlot.values())
				{
					NonNullList<ItemStack> stuff = cyberware.getInstalledCyberware(slot);
					for (ItemStack stack : stuff)
					{
						if (!stack.isEmpty())
							allWares.add(stack);
					}
				}
				
				allWares.removeAll(Collections.singleton(null));
				
				ItemStack drop = null;
				int count = 0;
				while (count < 50 && (drop == null || drop.getItem() == CyberwareContent.creativeBattery || drop.getItem() == CyberwareContent.bodyPart))
				{
					int random = world.rand.nextInt(allWares.size());
					drop = allWares.get(random).copy();
					drop = CyberwareAPI.sanitize(drop);
					drop = CyberwareAPI.getCyberware(drop).setQuality(drop, CyberwareAPI.QUALITY_SCAVENGED);
					drop.setCount(1);
					count++;
				}
				
				if (count < 50)
				{
					event.getDrops().add(new EntityItem(world, entityLivingBase.posX, entityLivingBase.posY,entityLivingBase.posZ, drop));
				}
			}
		}
	}
}
