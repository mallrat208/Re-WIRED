package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import com.mr208.rewired.common.entities.EntityCyberSkeleton;
import com.mr208.rewired.common.handlers.ConfigHandler.Entities;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.block.tile.TileEntityBeacon;
import flaxbeard.cyberware.common.lib.LibConstants;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid =ReWIRED.MOD_ID)
public class EventHandler
{

	public static final EventHandler INSTANCE = new EventHandler();
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void handleCyberSkeletonSpawn(LivingSpawnEvent.SpecialSpawn event)
	{
		if(event.getEntityLiving() instanceof EntitySkeleton&& !(event.getEntityLiving() instanceof EntityCyberSkeleton)) {
			EntitySkeleton skeleton;
			skeleton = (EntitySkeleton) event.getEntityLiving();
			
			int tier = TileEntityBeacon.isInRange(skeleton.world, skeleton.posX, skeleton.posY, skeleton.posZ);
			if (tier > 0) {
				float chance = (tier == 2 ? LibConstants.BEACON_CHANCE : (tier == 1 ? LibConstants.BEACON_CHANCE_INTERNAL : LibConstants.LARGE_BEACON_CHANCE));
				if (!Entities.enableCyberskeleton || !(event.getWorld().rand.nextFloat() < (chance / 100)))
					return;
				
				EntityCyberSkeleton cyberSkeleton = new EntityCyberSkeleton(event.getWorld());
				
				cyberSkeleton.setLocationAndAngles(skeleton.posX, skeleton.posY, skeleton.posZ, skeleton.rotationYaw, skeleton.rotationPitch);
				
				for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
					cyberSkeleton.setItemStackToSlot(slot, skeleton.getItemStackFromSlot(slot));
				}
				
				event.getWorld().spawnEntity(cyberSkeleton);
				skeleton.deathTime = 19;
				skeleton.setHealth(0F);
				
				
				float chestRand = cyberSkeleton.world.rand.nextFloat();
				
				if (!cyberSkeleton.world.isRemote && skeleton.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty() && chestRand < LibConstants.ZOMBIE_TRENCH_CHANCE / 100F) {
					ItemStack stack = new ItemStack(CyberwareContent.trenchcoat);
					int rand = skeleton.world.rand.nextInt(3);
					if (rand == 0) {
						CyberwareContent.trenchcoat.setColor(stack, 0x664028);
					} else if (rand == 1) {
						CyberwareContent.trenchcoat.setColor(stack, 0xEAEAEA);
					}
					skeleton.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
					skeleton.setDropChance(EntityEquipmentSlot.CHEST, .3F);
				}
			}
		}
		
	}

}
