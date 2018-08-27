package com.mr208.rewired.common.items;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.Content;
import flaxbeard.cyberware.api.CyberwareAPI;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class ItemReWIREDFood extends ItemFood
{
	private int energyGained;
	private EnumAction foodAction;
	private String name;
	private int healAmount;
	private float saturationModifier;
	
	public ItemReWIREDFood(String name, EnumAction action, int amount, float saturation, int energy,int maxStackSize)
	{
		super(amount, saturation, false);
		this.name = name;
		this.healAmount=amount;
		this.saturationModifier=saturation;
		this.foodAction = action;
		this.energyGained = energy;
		this.setUnlocalizedName(ReWIRED.MOD_ID + "." + name);
		this.setRegistryName(ReWIRED.MOD_ID, name);
		this.setMaxStackSize(maxStackSize);
		this.setCreativeTab(ReWIRED.creativeTab);
		this.setAlwaysEdible();
		
		ForgeRegistries.ITEMS.register(this);
		Content.registeredItems.add(this);
		
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		if(entityLiving instanceof EntityPlayer)
		{
			EntityPlayer entityplayer=(EntityPlayer)entityLiving;
			if(CyberwareAPI.isCyberwareInstalled(entityplayer, new ItemStack(Content.torsoAugments, 1, 1)))
			{
				if(!entityplayer.getFoodStats().needFood() && worldIn.rand.nextFloat() < .7)
					entityplayer.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 80, 1));
				
				entityplayer.getFoodStats().addStats(this.healAmount*2, this.saturationModifier);
				CyberwareAPI.getCapability(entityplayer).addPower(this.energyGained, ItemStack.EMPTY);
				
				CyberwareAPI.getCapability(entityplayer).updateCapacity();
			}
			else
			{
				float nauseaChance = 0.4f;
				if(!entityplayer.getFoodStats().needFood())
					nauseaChance =+ 0.4f;
				entityplayer.getFoodStats().addStats(this.healAmount, this.saturationModifier);
				if(worldIn.rand.nextFloat() < nauseaChance)
					entityplayer.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 80, 1));
			}
			
			worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat()*0.1F+0.9F);
			this.onFoodEaten(stack, worldIn, entityplayer);
			entityplayer.addStat(StatList.getObjectUseStats(this));
			
			if(entityplayer instanceof EntityPlayerMP)
			{
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
			}
		}
		
		stack.shrink(1);
		return stack;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(ChatFormatting.DARK_GRAY + I18n.format(this.getUnlocalizedName()+".tooltip"));
		tooltip.add(ChatFormatting.GREEN + I18n.format(this.getUnlocalizedName() + ".energy",this.energyGained));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return this.foodAction;
	}
}