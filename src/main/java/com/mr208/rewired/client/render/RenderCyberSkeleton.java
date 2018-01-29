package com.mr208.rewired.client.render;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.entities.EntityCyberSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class RenderCyberSkeleton extends RenderSkeleton
{
	
	private static final ResourceLocation SKELETON = new ResourceLocation(ReWIRED.MOD_ID + ":textures/models/cyberskeleton.png");
	private static final ResourceLocation SKELETON_HIGHLIGHT = new ResourceLocation(ReWIRED.MOD_ID + ":textures/models/cyberskeleton_highlight.png");
	
	@SideOnly(Side.CLIENT)
	public static class LayerSkeletonHighlight<T extends EntityCyberSkeleton> implements LayerRenderer<T>
	{
		private final RenderCyberSkeleton csRenderer;
		
		public LayerSkeletonHighlight(RenderCyberSkeleton skeleonRenderer)
		{
			this.csRenderer= skeleonRenderer;
		}
		
		@Override
		public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
		{
			this.csRenderer.bindTexture(SKELETON_HIGHLIGHT);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			
			if(entitylivingbaseIn.isInvisible())
			{
				GlStateManager.depthMask(false);
			}
			else
			{
				GlStateManager.depthMask(true);
			}
			
			int i = 61680;
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.csRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			//i = entitylivingbaseIn.getBrightnessForRender(partialTicks);
			i = 61680;
			j = i % 65536;
			k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
			this.csRenderer.setLightmap(entitylivingbaseIn);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
		}
		
		@Override
		public boolean shouldCombineTextures()
		{
			return false;
		}
	}
	
	public RenderCyberSkeleton(RenderManager renderManagerIn) {
		super(renderManagerIn);
		
		List<LayerRenderer<AbstractSkeleton>> defaultLayers = this.layerRenderers;
		defaultLayers.add(new LayerSkeletonHighlight(this));
		this.layerRenderers = defaultLayers;
	}
	
	@Override
	protected ResourceLocation getEntityTexture(AbstractSkeleton entity)
	{
		return SKELETON;
	}
}

