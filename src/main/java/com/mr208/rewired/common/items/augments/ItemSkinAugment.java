package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.common.effects.ReWIREDEffects;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSkinAugment extends ItemAugment
{
	public ItemSkinAugment(String name, EnumSlot slots, String[] subnames)
	{
		super(name, slots, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public int getPowerConsumption(ItemStack itemStack)
	{
		return super.getPowerConsumption(itemStack);
	}

	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		return itemStack.getItemDamage() == 0;
	}

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
		}

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(CyberwareHelper.isAugmentAvailable(event.getEntityPlayer(), new ItemStack(this,1,0)))
		{
			if(event.getEntityPlayer().isPotionActive(MobEffects.INVISIBILITY))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onCyberwareUpdate(CyberwareUpdateEvent event)
	{
		EntityLivingBase entityLivingBase = event.getEntityLiving();

		if(entityLivingBase==null)
			return;

		ItemStack test = new ItemStack(this,1,0);

		if(entityLivingBase.ticksExisted % 20 == 0 && CyberwareHelper.isAugmentAvailable(entityLivingBase, test))
		{
			ICyberwareUserData data = CyberwareAPI.getCapability(entityLivingBase);
			if(data.usePower(test, 40))
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
	}
}
