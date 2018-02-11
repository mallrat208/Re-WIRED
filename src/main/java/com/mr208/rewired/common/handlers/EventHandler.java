package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import com.mr208.rewired.common.entities.EntityCyberSkeleton;
import com.mr208.rewired.common.handlers.ConfigHandler.Entities;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntityBeacon;
import flaxbeard.cyberware.common.lib.LibConstants;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = ReWIRED.MOD_ID)
public class EventHandler
{

	public static final EventHandler INSTANCE = new EventHandler();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void handleCyberSkeletonSpawn(LivingSpawnEvent.SpecialSpawn event)
	{
		if(event.getEntityLiving() instanceof AbstractSkeleton && !(event.getEntityLiving() instanceof EntityCyberSkeleton) && !(event.getEntityLiving() instanceof EntityWitherSkeleton))
		{
			AbstractSkeleton skeleton;
			skeleton = (AbstractSkeleton) event.getEntityLiving();
			
			int tier = TileEntityBeacon.isInRange(skeleton.world, skeleton.posX, skeleton.posY, skeleton.posZ);
			if (tier > 0) {
				float chance = (tier == 2 ? LibConstants.BEACON_CHANCE : (tier == 1 ? LibConstants.BEACON_CHANCE_INTERNAL : LibConstants.LARGE_BEACON_CHANCE));
				
				if (!Entities.cyberskelton.enableCyberskeleton || !(event.getWorld().rand.nextFloat() < (chance / 100f))) return;
				
				EntityCyberSkeleton cyberSkeleton = new EntityCyberSkeleton(event.getWorld());
				
				cyberSkeleton.setLocationAndAngles(skeleton.posX, skeleton.posY, skeleton.posZ, skeleton.rotationYaw, skeleton.rotationPitch);
				
				for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
					cyberSkeleton.setItemStackToSlot(slot, skeleton.getItemStackFromSlot(slot));
				}
				
				event.getWorld().spawnEntity(cyberSkeleton);
				skeleton.deathTime = 19;
				skeleton.setHealth(0F);
			}
		}
		
		if (event.getEntityLiving() instanceof EntitySkeleton && CyberwareConfig.CLOTHES && !CyberwareConfig.NO_CLOTHES)
		{
			EntitySkeleton skeleton = (EntitySkeleton) event.getEntityLiving();
			
			if (!skeleton.world.isRemote && skeleton.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() && skeleton.world.rand.nextFloat() < LibConstants.ZOMBIE_SHADES_CHANCE / 100F)
			{
				if (skeleton.world.rand.nextBoolean())
				{
					if(skeleton.world.rand.nextBoolean())
						skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(CyberwareContent.shades));
					else
						skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(CyberwareContent.shades2));
				}
				else
				{
					skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ReWIREDContent.armorARVisor));
				}
				
				skeleton.setDropChance(EntityEquipmentSlot.HEAD, .2F);
			}
		}
	}
}
