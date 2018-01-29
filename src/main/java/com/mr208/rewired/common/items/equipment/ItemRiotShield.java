package com.mr208.rewired.common.items.equipment;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.items.ItemReWIRED;
import flaxbeard.cyberware.api.item.IDeconstructable;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRiotShield extends ItemReWIRED implements IDeconstructable
{
	public ToolMaterial shieldMaterial;
	private NonNullList<ItemStack> components = NonNullList.create();

	public ItemRiotShield(ToolMaterial material)
	{
		super("shield." + material.name().toLowerCase());
		this.shieldMaterial = material;
		this.setCreativeTab(ReWIRED.creativeTab);
		this.maxStackSize=1;
		this.setMaxDamage(material.getMaxUses());
		this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter()
		{
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return entityIn!=null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return false;
	}

	public void damageShield(ItemStack stackIn, int damageIn, EntityPlayer blockingEntity, Entity attackingEntity)
	{
		stackIn.damageItem(damageIn, blockingEntity);

	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS,playerIn.getActiveItemStack());
	}
	
	@Override
	public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity)
	{
		return true;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		ItemStack mat = this.shieldMaterial.getRepairItemStack();
		return (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) || super.getIsRepairable(toRepair, repair);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(ChatFormatting.DARK_GRAY + I18n.format(this.getUnlocalizedName()+".tooltip"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	//ITEM DECONSTRUCTION
	@Override
	public boolean canDestroy(ItemStack itemStack)
	{
		return !components.isEmpty();
	}

	@Override
	public NonNullList<ItemStack> getComponents(ItemStack itemStack)
	{
		return components;
	}
	
	public void setComponents(NonNullList<ItemStack> components)
	{
		this.components = components;
	}

	//EVENT FOR SHIELD DAMAGE
	@SubscribeEvent (priority = EventPriority.HIGH)
	public void handleShieldDamage(LivingAttackEvent event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();

			if (!player.getActiveItemStack().isEmpty())
			{
				ItemStack itemStack = player.getActiveItemStack();
				float damageIn = event.getAmount();

				if (damageIn >= 2.5F && itemStack.getItem() instanceof ItemRiotShield)
				{
					((ItemRiotShield) itemStack.getItem()).damageShield(itemStack, 1 + MathHelper.floor(damageIn), player, event.getSource().getTrueSource());
					if (itemStack.getCount() <= 0)
					{
						EnumHand activeHand = player.getActiveHand();
						ForgeEventFactory.onPlayerDestroyItem(player, player.getActiveItemStack(), activeHand);

						player.setItemStackToSlot(activeHand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);

						player.stopActiveHand();
						player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8F + player.world.rand.nextFloat() * 0.4F);
					}
				}
			}

		}
	}
}

