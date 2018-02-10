package com.mr208.rewired.client.render;

import com.mr208.rewired.ReWIRED;
import flaxbeard.cyberware.client.render.RenderPlayerCyberware;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerReWIRED extends RenderPlayerCyberware
{
	public boolean doAEGIS = false;
	
	private static final ResourceLocation aegis = new ResourceLocation(ReWIRED.MOD_ID, "textures/models/player_aegis.png");
	
	public RenderPlayerReWIRED(RenderManager renderManager, boolean arms)
	{
		super(renderManager, arms);
	}
	
	@Override
	public ResourceLocation getEntityTexture(AbstractClientPlayer entity)
	{
		return doAEGIS ? aegis : super.getEntityTexture(entity);
	}
	
}
