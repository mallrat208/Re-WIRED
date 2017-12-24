package com.mr208.rewired.common.util;

import com.mr208.rewired.common.entities.ICyberEntity;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.handler.CyberwareDataHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CyberwareHelper
{

	//Checks if an Augment is installed and activated
	public static boolean isAugmentAvailable(EntityLivingBase entityLivingBase, ItemStack augment)
	{
		return CyberwareAPI.isCyberwareInstalled(entityLivingBase, augment) && EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(entityLivingBase,augment));
	}

	//Based on Cyberwares default implementation of assigning Cyberware to Cyberzombies
	public static void addRandomCyberware(ICyberEntity iCyberEntity)
	{
		ICyberwareUserData data = CyberwareAPI.getCapability((EntityLivingBase) iCyberEntity);
		NonNullList<NonNullList<ItemStack>> wares = NonNullList.create();

		for (ICyberware.EnumSlot slot: ICyberware.EnumSlot.values())
		{
			NonNullList<ItemStack> toAdd = data.getInstalledCyberware(slot);
			toAdd.removeAll(Collections.singleton(ItemStack.EMPTY));
			wares.add(toAdd);
		}

		ItemStack battery = new ItemStack(CyberwareContent.creativeBattery);
		wares.get(CyberwareContent.creativeBattery.getSlot(battery).ordinal()).add(battery);

		int numberOfItemsToInstall = ((CyberwareContent.NumItems) WeightedRandom.getRandomItem(((EntityLivingBase)iCyberEntity).world.rand,CyberwareContent.numItems)).num;

		List<ItemStack> installed = new ArrayList<>();

		List<CyberwareContent.ZombieItem> items = new ArrayList(iCyberEntity.getCyberEntityItems());
		for(int i = 0; i < numberOfItemsToInstall; i++)
		{
			int tries = 0;
			ItemStack randomItem = ItemStack.EMPTY;
			ICyberware randomWare = null;

			do
			{
				randomItem = ((CyberwareContent.ZombieItem) WeightedRandom.getRandomItem(((EntityLivingBase)iCyberEntity).world.rand,items)).stack.copy();
				randomWare = CyberwareAPI.getCyberware(randomItem);
				randomItem.setCount(randomWare.installedStackSize(randomItem));
				tries++;
			}
			while(CyberwareDataHandler.contains(wares.get(randomWare.getSlot(randomItem).ordinal()), randomItem) && tries < 10);

			if(tries < 10)
			{
				NonNullList<NonNullList<ItemStack>> required = randomWare.required(randomItem);
				for(NonNullList<ItemStack> requiredCategory: required)
				{
					boolean found = false;
					for (ItemStack option: requiredCategory)
					{
						ICyberware optionWare = CyberwareAPI.getCyberware(option);						;
						option.setCount(optionWare.installedStackSize(option));
						if(CyberwareDataHandler.contains(wares.get(optionWare.getSlot(option).ordinal()),option));
						{
							found = true;
							break;
						}
					}

					if (!found)
					{
						ItemStack req = requiredCategory.get(((EntityLivingBase) iCyberEntity).world.rand.nextInt(requiredCategory.size())).copy();
						ICyberware reqWare = CyberwareAPI.getCyberware(req);
						req.setCount(reqWare.installedStackSize(req));
						wares.get(reqWare.getSlot(req).ordinal()).add(req);
						installed.add(req);
						i++;
					}
				}
				wares.get(randomWare.getSlot(randomItem).ordinal()).add(randomItem);
				installed.add(randomItem);
			}
		}

		for (ICyberware.EnumSlot slot : ICyberware.EnumSlot.values())
		{
			data.setInstalledCyberware((EntityLivingBase)iCyberEntity, slot, wares.get(slot.ordinal()));
		}
		data.updateCapacity();

		((EntityLivingBase)iCyberEntity).setHealth(((EntityLivingBase)iCyberEntity).getMaxHealth());
		iCyberEntity.setHasWare(true);

		CyberwareAPI.updateData((EntityLivingBase)iCyberEntity);

	}

}
