package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.common.handlers.ConfigHandler;
import com.mr208.rewired.common.handlers.NetworkHandler;
import com.mr208.rewired.common.handlers.packets.PacketEntityMovement;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.misc.NNLUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemHandAugment extends ItemAugment
{
	public int REINFORCED_ALLOYED_FIST = 0;

	@GameRegistry.ObjectHolder("cyberware:hand_upgrades")
	public static final Item CYBERWARE_HAND_UPGRADE = null;

	public ItemHandAugment(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean hasCustomPowerMessage(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 1;
	}

	@Override
	public int getPowerConsumption(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 1?ConfigHandler.Augments.pkb.ENERGY_COST: super.getPowerConsumption(itemStack);
	}

	@Override
	public NonNullList<NonNullList<ItemStack>> required(ItemStack itemStack)
	{
		return NNLUtil.fromArray(
				new ItemStack[][] {
				new ItemStack[] {
						new ItemStack(CyberwareContent.cyberlimbs, 1, 0),
						new ItemStack(CyberwareContent.cyberlimbs, 1, 1) }});
	}

	@Override
	public boolean isIncompatible(ItemStack itemStack, ItemStack otherStack)
	{
		return otherStack.getItem() == this || (otherStack.getItem() == CYBERWARE_HAND_UPGRADE);
	}

	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 1;
	}

	@Override
	public void use(Entity entity, ItemStack itemStack)
	{
		EnableDisableHelper.toggle(itemStack);
	}

	@Override
	public String getUnlocalizedLabel(ItemStack itemStack)
	{
		return EnableDisableHelper.getUnlocalizedLabel(itemStack);
	}

	private static final float[] f = new float[]{1,0,0};

	@Override
	public float[] getColor(ItemStack itemStack)
	{
		return EnableDisableHelper.isEnabled(itemStack) ? f : null;
	}

	//Alloyed-Reinforced Fist Stuff
	@SubscribeEvent
	public void handleMineSpeed(PlayerEvent.BreakSpeed event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack test = new ItemStack(this, 1,0);

		if(isPrimaryLimbCybernetic(p) && CyberwareAPI.isCyberwareInstalled(p, test) && p.getHeldItemMainhand().isEmpty())
		{
			ItemStack pick = new ItemStack(Items.IRON_PICKAXE);
			event.setNewSpeed(event.getNewSpeed() * pick.getDestroySpeed(event.getState()));
		}
	}

	@SubscribeEvent
	public void handleMining(PlayerEvent.HarvestCheck event)
	{
		EntityPlayer p = event.getEntityPlayer();
		ItemStack test = new ItemStack(this,1,0);

		if(isPrimaryLimbCybernetic(p) && CyberwareAPI.isCyberwareInstalled(p, test) && p.getHeldItemMainhand().isEmpty())
		{
			ItemStack pick = new ItemStack(Items.IRON_PICKAXE);
			if(pick.canHarvestBlock(event.getTargetBlock()))
			{
				event.setCanHarvest(true);
			}
		}
	}

	private boolean isPrimaryLimbCybernetic(EntityPlayer player)
	{
		return player.getPrimaryHand() == EnumHandSide.RIGHT ?
				(CyberwareAPI.isCyberwareInstalled(player, new ItemStack(CyberwareContent.cyberlimbs,1,1))) :
				(CyberwareAPI.isCyberwareInstalled(player, new ItemStack(CyberwareContent.cyberlimbs, 1, 0)));
	}

	//Kinetic Dampener Stuff
	@SubscribeEvent
	public void onProjectileHit(LivingAttackEvent event)
	{
		if (!event.getSource().isProjectile() || event.getSource().isUnblockable())
			return;
		if (!(event.getEntityLiving() instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		World world = player.getEntityWorld();
		ItemStack test = new ItemStack(this,1,1);

		if (CyberwareAPI.isCyberwareInstalled(player, test) && isAugmentEnabled(player, test))
		{
			ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapability(player);

			if (cyberwareUserData.usePower(test, ConfigHandler.Augments.pkb.ENERGY_COST, false))
			{

				Entity projectile = event.getSource().getImmediateSource();
				Vec3d vectorMotion = new Vec3d(projectile.motionX, projectile.motionY, projectile.motionZ);
				Vec3d vectorLook = player.getLookVec();

				double difference = -vectorLook.dotProduct(vectorMotion.normalize());
				if (difference < 0.1)
					return;

				cyberwareUserData.updateCapacity();
				if (!player.world.isRemote) CyberwareAPI.updateData(player);
				event.setCanceled(true);


				if(getQuality(CyberwareAPI.getCyberware(player,new ItemStack(this))).equals(CyberwareAPI.QUALITY_MANUFACTURED))
				{
					player.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 2.5f);

					double returnSpeed = projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY + projectile.motionZ * projectile.motionZ;
					returnSpeed = Math.sqrt(returnSpeed);
					returnSpeed += 0.5F;

					projectile.motionX = vectorLook.x * returnSpeed;
					projectile.motionY = vectorLook.y * returnSpeed;
					projectile.motionZ = vectorLook.z * returnSpeed;

					projectile.rotationYaw = (float) (Math.atan2(projectile.motionX, projectile.motionZ) * 180.0D / Math.PI);
					projectile.rotationPitch = (float) (Math.atan2(projectile.motionY, returnSpeed) * 180.0D / Math.PI);

					NetworkHandler.INSTANCE.sendToAll(new PacketEntityMovement(projectile));

					if (projectile instanceof IProjectile)
					{
						projectile.motionX /= -0.10000000149011612D;
						projectile.motionY /= -0.10000000149011612D;
						projectile.motionZ /= -0.10000000149011612D;
					}
				}
			}
		}
	}
}
