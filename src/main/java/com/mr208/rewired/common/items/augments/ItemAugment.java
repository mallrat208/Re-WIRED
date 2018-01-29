package com.mr208.rewired.common.items.augments;

import com.mojang.realmsclient.gui.ChatFormatting;

import flaxbeard.cyberware.api.item.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.CyberwareContent;

import com.mr208.rewired.common.items.ItemReWIRED;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemAugment extends ItemReWIRED implements ICyberware, ICyberwareTabItem, IDeconstructable, IMenuItem
{
	private EnumSlot[] slots;
	private int[] essence;
	private NonNullList<NonNullList<ItemStack>> components;
	private int[] weight;

	public ItemAugment(String name, EnumSlot[] slots, String[] subnames)
	{
		super(name, subnames);

		this.slots = slots;
		this.essence = new int[subnames.length+1];
		this.weight = new int[subnames.length+1];
		this.components = NonNullList.create();

		this.setCreativeTab(Cyberware.creativeTab);
	}

	public ItemAugment(String name, EnumSlot slot, String[] subnames)
	{
		this(name, new EnumSlot[] {slot}, subnames);
	}

	public ItemAugment(String name, EnumSlot slot)
	{
		this(name, slot, new String[0]);
	}

	public int getWeight(ItemStack stack)
	{
		return weight[stack.getItemDamage()];
	}

	public ItemAugment setWeights(int... weights)
	{
		this.weight = new int[weights.length];
		for (int meta = 0; meta < weights.length; meta++)
		{
			ItemStack stack = new ItemStack(this, 1, meta);
			int installedStackSize = installedStackSize(stack);
			stack.setCount(installedStackSize);
			this.setQuality(stack, CyberwareAPI.QUALITY_SCAVENGED);
			//TODO: Seperate Lists for the Various Cybermobs
			CyberwareContent.zombieItems.add(new CyberwareContent.ZombieItem(weights[meta], stack));
			this.weight[meta] = weights[meta];
		}
		return this;
	}

	public ItemAugment setEssenceCost(int... essence)
	{
		this.essence = essence;
		return this;
	}

	public ItemAugment setComponents(NonNullList<ItemStack>... components)
	{
		NonNullList<NonNullList<ItemStack>> list = NonNullList.create();
		for (NonNullList<ItemStack> l : components)
		{
			list.add(l);
		}
		this.components = list;
		return this;
	}

	@Override
	public int getEssenceCost(ItemStack itemStack)
	{
		int cost = getUnmodifiedEssenceCost(itemStack);
		if (getQuality(itemStack) == CyberwareAPI.QUALITY_SCAVENGED)
		{
			float half = cost / 2F;
			if(cost > 0)
			{
				cost = cost + (int) Math.ceil(half);
			}
			else
			{
				cost = cost - (int) Math.ceil(half);
			}
		}

		return cost;
	}

	protected int getUnmodifiedEssenceCost(ItemStack stack)
	{
		return essence[Math.min(this.subNames.length, stack.getItemDamage())];
	}

	@Override
	public EnumSlot getSlot(ItemStack itemStack)
	{
		return slots[Math.min(slots.length-1, itemStack.getItemDamage())];
	}

	@Override
	public int installedStackSize(ItemStack itemStack)
	{
		return 1;
	}

	@Override
	public boolean isIncompatible(ItemStack itemStack, ItemStack itemStack1)
	{
		return false;
	}

	@Override
	public boolean isEssential(ItemStack itemStack)
	{
		return false;
	}

	@Override
	public List<String> getInfo(ItemStack itemStack)
	{
		List<String> ret = new ArrayList<>();
		List<String> desc = this.getDescription(itemStack);
		if (desc != null && desc.size() > 0)
		{
			ret.addAll(desc);
		}

		return ret;
	}

	public List<String> getStackDesc(ItemStack itemStack)
	{
		String[] toReturnArray = I18n.format("cyberware.tooltip." + this.getRegistryName().toString().substring(8)
				+ (this.subNames.length > 0 ? "." + itemStack.getItemDamage() : "")).split("\\\\n");
		List<String> toReturn = new ArrayList<String>(Arrays.asList(toReturnArray));

		if(toReturn.size() > 0 && toReturn.get(0).length() == 0)
		{
			toReturn.remove(0);
		}

		return toReturn;
	}

	public List<String> getDescription(ItemStack stack)
	{
		List<String> toReturn = getStackDesc(stack);

		if (installedStackSize(stack) > 1)
		{
			toReturn.add(ChatFormatting.BLUE + I18n.format("cyberware.tooltip.max_install", installedStackSize(stack)));
		}

		boolean hasPowerConsumption = false;
		String toAddPowerConsumption = "";
		for (int i = 0; i < installedStackSize(stack); i++)
		{
			ItemStack temp = stack.copy();
			temp.setCount(i+1);
			int cost = this.getPowerConsumption(temp);
			if (cost > 0)
			{
				hasPowerConsumption = true;
			}

			if (i != 0)
			{
				toAddPowerConsumption += I18n.format("cyberware.tooltip.joiner");
			}

			toAddPowerConsumption += " " + cost;
		}

		if (hasPowerConsumption)
		{
			String toTranslate = hasCustomPowerMessage(stack) ?
					"cyberware.tooltip." + this.getRegistryName().toString().substring(8)
							+ (this.subNames.length > 0 ? "." + stack.getItemDamage() : "") + ".power_consumption"
					:
					"cyberware.tooltip.power_consumption";
			toReturn.add(ChatFormatting.GREEN + I18n.format(toTranslate, toAddPowerConsumption));
		}

		boolean hasPowerProduction = false;
		String toAddPowerProduction = "";
		for (int i = 0; i < installedStackSize(stack); i++)
		{
			ItemStack temp = stack.copy();
			temp.setCount(i+1);
			int cost = this.getPowerProduction(temp);
			if (cost > 0)
			{
				hasPowerProduction = true;
			}

			if (i != 0)
			{
				toAddPowerProduction += I18n.format("cyberware.tooltip.joiner");
			}

			toAddPowerProduction += " " + cost;
		}

		if (hasPowerProduction)
		{
			String toTranslate = hasCustomPowerMessage(stack) ?
					"cyberware.tooltip." + this.getRegistryName().toString().substring(8)
							+ (this.subNames.length > 0 ? "." + stack.getItemDamage() : "") + ".power_production"
					:
					"cyberware.tooltip.power_production";
			toReturn.add(ChatFormatting.GREEN + I18n.format(toTranslate, toAddPowerProduction));
		}

		if (getCapacity(stack) > 0)
		{
			String toTranslate = hasCustomCapacityMessage(stack) ?
					"cyberware.tooltip." + this.getRegistryName().toString().substring(8)
							+ (this.subNames.length > 0 ? "." + stack.getItemDamage() : "") + ".capacity"
					:
					"cyberware.tooltip.capacity";
			toReturn.add(ChatFormatting.GREEN + I18n.format(toTranslate, getCapacity(stack)));
		}


		boolean hasEssenceCost = false;
		boolean essenceCostNegative = true;
		String toAddEssence = "";
		for (int i = 0; i < installedStackSize(stack); i++)
		{
			ItemStack temp = stack.copy();
			temp.setCount(i+1);
			int cost = this.getEssenceCost(temp);
			if (cost != 0)
			{
				hasEssenceCost = true;
			}
			if (cost < 0)
			{
				essenceCostNegative = false;
			}

			if (i != 0)
			{
				toAddEssence += I18n.format("cyberware.tooltip.joiner");
			}

			toAddEssence += " " + Math.abs(cost);
		}

		if (hasEssenceCost)
		{
			toReturn.add(ChatFormatting.DARK_PURPLE + I18n.format(essenceCostNegative ? "cyberware.tooltip.essence" : "cyberware.tooltip.essence_add", toAddEssence));
		}

		return toReturn;
	}

	public int getPowerConsumption(ItemStack itemStack)
	{
		return 0;
	}

	public int getPowerProduction(ItemStack itemStack)
	{
		return 0;
	}

	public boolean hasCustomPowerMessage(ItemStack itemStack)
	{
		return false;
	}

	public boolean hasCustomCapacityMessage(ItemStack itemStack)
	{
		return false;
	}

	@Override
	public NonNullList<NonNullList<ItemStack>> required(ItemStack itemStack)
	{
		return NonNullList.create();
	}

	@Override
	public EnumCategory getCategory(ItemStack itemStack)
	{
		return EnumCategory.values()[this.getSlot(itemStack).ordinal()];
	}

	@Override
	public int getCapacity(ItemStack itemStack)
	{
		return 0;
	}

	@Override
	public void onAdded(EntityLivingBase entityLivingBase, ItemStack itemStack)
	{

	}

	@Override
	public void onRemoved(EntityLivingBase entityLivingBase, ItemStack itemStack)
	{

	}

	@Override
	public boolean canDestroy(ItemStack itemStack)
	{
		return itemStack.getItemDamage() < this.components.size();
	}

	@Override
	public NonNullList<ItemStack> getComponents(ItemStack itemStack)
	{
		return components.get(Math.min(this.components.size()-1, itemStack.getItemDamage()));
	}

	@Override
	public Quality getQuality(ItemStack itemStack)
	{
		Quality q = CyberwareAPI.getQualityTag(itemStack);
		if(q == null) return  CyberwareAPI.QUALITY_MANUFACTURED;
		return q;
	}

	@Override
	public ItemStack setQuality(ItemStack itemStack, Quality quality)
	{
		if(quality == CyberwareAPI.QUALITY_MANUFACTURED)
		{
			if(!itemStack.isEmpty() && itemStack.hasTagCompound())
			{
				itemStack.getTagCompound().removeTag(CyberwareAPI.QUALITY_TAG);
				if(itemStack.getTagCompound().hasNoTags())
				{
					itemStack.setTagCompound(null);
				}
			}
			return itemStack;
		}
		return this.canHoldQuality(itemStack, quality) ? CyberwareAPI.writeQualityTag(itemStack, quality) : itemStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack itemStack)
	{
		Quality q = getQuality(itemStack);
		if(q != null && q.getNameModifier() != null)
		{
			return I18n.format(q.getNameModifier(), ("" + I18n.format(this.getUnlocalizedNameInefficiently(itemStack)+".name")).trim()).trim();
		}
		return ("" + I18n.format(this.getUnlocalizedNameInefficiently(itemStack)+ ".name").trim());
	}

	@Override
	public boolean canHoldQuality(ItemStack itemStack, Quality quality)
	{
		return true;
	}

	protected boolean isAugmentEnabled(EntityPlayer player, ItemStack augment)
	{
		return EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(player,augment));
	}

	protected boolean isAugmentEnabled(EntityLivingBase entityLivingBase, ItemStack augment)
	{
		return EnableDisableHelper.isEnabled(CyberwareAPI.getCyberware(entityLivingBase,augment));
	}

	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		return false;
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

	private final float[] enabledColor = new float[] {1F,0F,0F};

	@Override
	public float[] getColor(ItemStack itemStack)
	{

		return EnableDisableHelper.isEnabled(itemStack) ? enabledColor : null;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab)) {
			if (subNames.length == 0)
			{
				items.add(new ItemStack(this));
			}
			for (int i = 0; i < subNames.length; i++)
			{
				items.add(new ItemStack(this, 1, i));
			}
		}
	}
}


