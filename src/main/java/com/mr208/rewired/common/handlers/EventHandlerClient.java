package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.client.render.RenderHandReWIRED;
import com.mr208.rewired.client.render.RenderPlayerReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.EnumSide;
import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@EventBusSubscriber(modid =ReWIRED.MOD_ID,value = Side.CLIENT)
public class EventHandlerClient
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	@SideOnly(Side.CLIENT)
	private static final RenderPlayerReWIRED renderT=new RenderPlayerReWIRED(Minecraft.getMinecraft().getRenderManager(), true);
	
	@SideOnly(Side.CLIENT)
	public static final RenderPlayerReWIRED renderF=new RenderPlayerReWIRED(Minecraft.getMinecraft().getRenderManager(), false);
	
	//Based Heavily on Cyberware's handling of missing skin
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public static void handleMissingSkin(RenderPlayerEvent.Pre event)
	{
		EntityPlayer p=event.getEntityPlayer();
		boolean doAegis = false;
		
		if(CyberwareConfig.RENDER&&CyberwareAPI.hasCapability(p))
		{
			
			ICyberwareUserData cyberware=CyberwareAPI.getCapability(p);
			boolean hasLeftLeg = cyberware.hasEssential(EnumSlot.LEG, EnumSide.LEFT);
			boolean hasRightLeg = cyberware.hasEssential(EnumSlot.LEG, EnumSide.RIGHT);
			boolean hasLeftArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT);
			boolean hasRightArm = cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT);
			
			if(!(event.getRenderer() instanceof RenderPlayerReWIRED))
			{
				
				boolean bigArms=ReflectionHelper.getPrivateValue(RenderPlayer.class, event.getRenderer(), 0);
				
				
				if(CyberwareHelper.isAugmentAvailable(p, new ItemStack(ReWIREDContent.skinAugments, 1, 1)))
				{
					event.setCanceled(true);
					doAegis=true;
					
					if(bigArms)
					{
						renderT.doAEGIS= true;
					} else
					{
						renderF.doAEGIS= true;
					}
				}
				
				boolean lower=false;
				if(!hasRightLeg&&!hasLeftLeg)
				{
					// Hide pants + shoes
					pants.put(p.getEntityId(), p.inventory.armorInventory.get(1));
					p.inventory.armorInventory.set(1, ItemStack.EMPTY);
					shoes.put(p.getEntityId(), p.inventory.armorInventory.get(0));
					p.inventory.armorInventory.set(0, ItemStack.EMPTY);
					lower=true;
				}
				
				if(doAegis)
				{
					if(bigArms)
					{
						renderT.doRender((AbstractClientPlayer)p, event.getX(), event.getY()-(lower ? (11F/16F):0), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
						renderT.doAEGIS=false;
					}
					else
					{
						renderF.doRender((AbstractClientPlayer)p, event.getX(), event.getY()-(lower ? (11F/16F):0), event.getZ(), p.rotationYaw, event.getPartialRenderTick());
						renderF.doAEGIS=false;
					}
				}
			}
					
			RenderPlayer renderer=event.getRenderer();
			
			if (!hasLeftLeg)
			{
				renderer.getMainModel().bipedLeftLeg.isHidden = true;
			}
			
			if (!hasRightLeg)
			{
				renderer.getMainModel().bipedRightLeg.isHidden = true;
			}
			
			
			if (!hasLeftArm)
			{
				renderer.getMainModel().bipedLeftArm.isHidden = true;
				
				// Hide the main or offhand item if no arm there
				if (!mainHand.containsKey(p.getEntityId()))
				{
					mainHand.put(p.getEntityId(), p.getHeldItemMainhand());
					offHand.put(p.getEntityId(), p.getHeldItemOffhand());
				}
				if (mc.gameSettings.mainHand == EnumHandSide.LEFT)
				{
					p.inventory.mainInventory.set(p.inventory.currentItem,ItemStack.EMPTY);
				}
				else
				{
					p.inventory.offHandInventory.set(0, ItemStack.EMPTY);
				}
			}
			
			if (!hasRightArm)
			{
				renderer.getMainModel().bipedRightArm.isHidden = true;
				
				// Hide the main or offhand item if no arm there
				if (!mainHand.containsKey(p.getEntityId()))
				{
					mainHand.put(p.getEntityId(), p.getHeldItemMainhand());
					offHand.put(p.getEntityId(), p.getHeldItemOffhand());
				}
				if (mc.gameSettings.mainHand == EnumHandSide.RIGHT)
				{
					p.inventory.mainInventory.set(p.inventory.currentItem,ItemStack.EMPTY);
				}
				else
				{
					p.inventory.offHandInventory.set(0, ItemStack.EMPTY);
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void handleMissingSkin(RenderPlayerEvent.Post event)
	{
		if (CyberwareConfig.RENDER)
		{
			event.getRenderer().getMainModel().bipedLeftArm.isHidden = false;
			event.getRenderer().getMainModel().bipedRightArm.isHidden = false;
			event.getRenderer().getMainModel().bipedLeftLeg.isHidden = false;
			event.getRenderer().getMainModel().bipedRightLeg.isHidden = false;
			
			EntityPlayer p = event.getEntityPlayer();
			if (CyberwareAPI.hasCapability(p))
			{
				ICyberwareUserData cyberware = CyberwareAPI.getCapability(p);
				
				if (pants.containsKey(p.getEntityId()))
				{
					p.inventory.armorInventory.set(1,pants.get(p.getEntityId()));
					pants.remove(p.getEntityId());
				}
				
				if (shoes.containsKey(p.getEntityId()))
				{
					p.inventory.armorInventory.set(0,shoes.get(p.getEntityId()));
					shoes.remove(p.getEntityId());
				}
				
				if (!cyberware.hasEssential(EnumSlot.ARM, EnumSide.LEFT))
				{
					event.getRenderer().getMainModel().bipedLeftArm.isHidden = false;
					if (mainHand.containsKey(p.getEntityId()))
					{
						p.inventory.mainInventory.set(p.inventory.currentItem,mainHand.get(p.getEntityId()));
						p.inventory.offHandInventory.set(0,offHand.get(p.getEntityId()));
						mainHand.remove(p.getEntityId());
						offHand.remove(p.getEntityId());
					}
				}
				
				
				if (!cyberware.hasEssential(EnumSlot.ARM, EnumSide.RIGHT))
				{
					event.getRenderer().getMainModel().bipedRightArm.isHidden = false;
					if (mainHand.containsKey(p.getEntityId()))
					{
						p.inventory.mainInventory.set(p.inventory.currentItem,mainHand.get(p.getEntityId()));
						p.inventory.offHandInventory.set(0,offHand.get(p.getEntityId()));
						mainHand.remove(p.getEntityId());
						offHand.remove(p.getEntityId());
					}
				}
				
			}
		}
	}
	
	private static boolean missingArm = false;
	private static boolean missingSecondArm = false;
	private static boolean hasAegis = false;
	
	private static EnumHandSide oldHand;
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void handleRenderHand(RenderHandEvent event)
	{
		hasAegis = CyberwareHelper.isAugmentAvailable(mc.player, new ItemStack(ReWIREDContent.skinAugments,1,1));
		
		if(CyberwareConfig.RENDER && !(FMLClientHandler.instance().hasOptifine()) && hasAegis)
		{
			float partialTicks=event.getPartialTicks();
			EntityRenderer er=mc.entityRenderer;
			event.setCanceled(true);
			
			
			boolean flag=mc.getRenderViewEntity() instanceof EntityLivingBase&&((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping();
			
			if(mc.gameSettings.thirdPersonView==0&&!flag&&!mc.gameSettings.hideGUI&&!mc.playerController.isSpectator())
				{
				er.enableLightmap();
				renderItemInFirstPerson(partialTicks);
				er.disableLightmap();
			}
		}
	}
	
	public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
		return first != null ? first : checkNotNull(second);
	}
	
	private static void renderItemInFirstPerson(float partialTicks)
	{
		ItemRenderer ir = mc.getItemRenderer();
		AbstractClientPlayer abstractclientplayer = mc.player;
		float f = abstractclientplayer.getSwingProgress(partialTicks);
		
		//EnumHand enumhand = (EnumHand)Objects.firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
		EnumHand enumhand = firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
		
		float f1 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
		float f2 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
		boolean flag = true;
		boolean flag1 = true;
		
		if (abstractclientplayer.isHandActive())
		{
			ItemStack itemstack = abstractclientplayer.getActiveItemStack();
			
			if (!itemstack.isEmpty() && itemstack.getItem() == Items.BOW) //Forge: Data watcher can desync and cause this to NPE...
			{
				EnumHand enumhand1 = abstractclientplayer.getActiveHand();
				flag = enumhand1 == EnumHand.MAIN_HAND;
				flag1 = !flag;
			}
		}
		
		rotateArroundXAndY(f1, f2);
		setLightmap();
		rotateArm(partialTicks);
		GlStateManager.enableRescaleNormal();
		
		ItemStack itemStackMainHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 3);
		ItemStack itemStackOffHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 4);
		float equippedProgressMainHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 5);
		float prevEquippedProgressMainHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 6);
		float equippedProgressOffHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 7);
		float prevEquippedProgressOffHand = ReflectionHelper.getPrivateValue(ItemRenderer.class, ir, 8);
		
		RenderHandReWIRED.INSTANCE.itemStackMainHand = itemStackMainHand;
		RenderHandReWIRED.INSTANCE.itemStackOffHand = itemStackOffHand;
		
		if (flag && !missingSecondArm)
		{
			float f3 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
			float f5 = 1.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * partialTicks);
			RenderHandReWIRED.INSTANCE.leftRobot = hasAegis;
			RenderHandReWIRED.INSTANCE.rightRobot = hasAegis;
			RenderHandReWIRED.INSTANCE.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.MAIN_HAND, f3, itemStackMainHand, f5);
		}
		
		if (flag1 && !missingArm)
		{
			float f4 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
			float f6 = 1.0F - (prevEquippedProgressOffHand + (equippedProgressOffHand - prevEquippedProgressOffHand) * partialTicks);
			RenderHandReWIRED.INSTANCE.leftRobot = hasAegis;
			RenderHandReWIRED.INSTANCE.rightRobot = hasAegis;
			RenderHandReWIRED.INSTANCE.renderItemInFirstPerson(abstractclientplayer, partialTicks, f1, EnumHand.OFF_HAND, f4, itemStackOffHand, f6);
		}
		
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
	}
	
	
	private static void rotateArroundXAndY(float angle, float angleY)
	{
		GlStateManager.pushMatrix();
		GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}
	
	private static void setLightmap()
	{
		AbstractClientPlayer abstractclientplayer = mc.player;
		int i = mc.world.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double)abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
		float f = (float)(i & 65535);
		float f1 = (float)(i >> 16);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
	}
	
	private static void rotateArm(float p_187458_1_)
	{
		EntityPlayerSP entityplayersp = mc.player;
		float f = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * p_187458_1_;
		float f1 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * p_187458_1_;
		GlStateManager.rotate((entityplayersp.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((entityplayersp.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
	}
	
	@SubscribeEvent
	public static void handleWorldUnload(WorldEvent.Unload event)
	{
		if (missingArm)
		{
			GameSettings settings = Minecraft.getMinecraft().gameSettings;
			missingArm = false;
			settings.mainHand = oldHand;
		}
	}
	
	private static Map<Integer, ItemStack> mainHand = new HashMap<Integer, ItemStack>();
	private static Map<Integer, ItemStack> offHand = new HashMap<Integer, ItemStack>();
	
	private static Map<Integer, ItemStack> pants = new HashMap<Integer, ItemStack>();
	private static Map<Integer, ItemStack> shoes = new HashMap<Integer, ItemStack>();
	
}