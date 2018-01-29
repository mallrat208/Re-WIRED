package com.mr208.rewired.client.render.item;

import com.google.common.collect.ImmutableMap;
import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.client.model.ModelTechVisor;
import com.mr208.rewired.client.render.ReWIREDRenderer;
import com.mr208.rewired.common.items.equipment.ItemTechVisor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GLContext;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BakedCustomItemModel implements IBakedModel
{
	private IBakedModel baseModel;
	private ItemStack stack;
	
	private TransformType prevTransform;
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	public static ModelTechVisor techVisor = new ModelTechVisor();
	
	public BakedCustomItemModel(IBakedModel model, ItemStack s)
	{
		baseModel = model;
		stack = s;
	}
	
	private void doRender(TransformType type)
	{

		if(stack.getItem() instanceof ItemTechVisor)
		{
			GlStateManager.pushMatrix();
			GlStateManager.rotate(180, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(90, 0.0F, -1.0F, 0.0F);
			GlStateManager.translate(0.1F, 0.2F, 0.0F);
			mc.renderEngine.bindTexture(new ResourceLocation(ReWIRED.MOD_ID, "textures/models/equipment/techvisor.png"));
			techVisor.render(0.125F, stack);
			GlStateManager.popMatrix();
		}
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
	{
		if(side != null)
		{
			List<BakedQuad> faceQuads = new LinkedList<>();
			
			if(Block.getBlockFromItem(stack.getItem()) != null)
			{
			
			}
			
			return faceQuads;
		}
		
		Tessellator tessellator = Tessellator.getInstance();
		List<BakedQuad> generalQuads = new LinkedList<>();
		
		//Test if the current thread has a Context loaded (we don't use it so no need to import its type)
		Object contextCapabilities;
		try {
			contextCapabilities = GLContext.getCapabilities();
		} catch (RuntimeException e){
			contextCapabilities = null;
		}
		
		if (contextCapabilities != null && ReWIREDRenderer.isDrawing(tessellator)) {
			try {
				VertexFormat prevFormat = null;
				int prevMode = -1;
				
				ReWIREDRenderer.pauseRenderer(tessellator);
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.5F, 0.5F, 0.5F);
				GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
				doRender(prevTransform);
				GlStateManager.enableLighting();
				GlStateManager.enableLight(0);
				GlStateManager.enableLight(1);
				GlStateManager.enableColorMaterial();
				GlStateManager.colorMaterial(1032, 5634);
				GlStateManager.enableCull();
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				GlStateManager.popMatrix();
				
				ReWIREDRenderer.resumeRenderer(tessellator);
			} catch (Throwable t) {
				ReWIRED.LOGGER.error("Error caught in CustomItemModel", t);
			}
		}
		
		return generalQuads;
	}
	
	@Override
	public boolean isAmbientOcclusion()
	{
		return baseModel.isAmbientOcclusion();
	}
	
	@Override
	public boolean isGui3d()
	{
		return baseModel.isGui3d();
	}
	
	@Override
	public boolean isBuiltInRenderer()
	{
		return baseModel.isBuiltInRenderer();
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return baseModel.getParticleTexture();
	}
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return baseModel.getItemCameraTransforms();
	}
	
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
	{
		prevTransform = cameraTransformType;
		
		return Pair.of(this, transforms.get(cameraTransformType).getMatrix());
	}
	
	// Copy from old CTM
	public static Map<TransformType, TRSRTransformation> transforms = ImmutableMap.<TransformType, TRSRTransformation>builder()
			.put(TransformType.GUI,                         get(0, 0, 0, 30, 225, 0, 0.625f))
			.put(TransformType.THIRD_PERSON_RIGHT_HAND,     get(0, 2.5f, 0, 75, 45, 0, 0.375f))
			.put(TransformType.THIRD_PERSON_LEFT_HAND,      get(0, 2.5f, 0, 75, 45, 0, 0.375f))
			.put(TransformType.FIRST_PERSON_RIGHT_HAND,     get(0, 0, 0, 0, 45, 0, 0.4f))
			.put(TransformType.FIRST_PERSON_LEFT_HAND,      get(0, 0, 0, 0, 225, 0, 0.4f))
			.put(TransformType.GROUND,                      get(0, 2, 0, 0, 0, 0, 0.25f))
			.put(TransformType.HEAD,                        get(0, 0, 0, 0, 0, 0, 1))
			.put(TransformType.FIXED,                       get(0, 0, 0, 0, 0, 0, 1))
			.put(TransformType.NONE,                        get(0, 0, 0, 0, 0, 0, 0))
			.build();
	
	private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s)
	{
		return new TRSRTransformation(
				new Vector3f(tx / 16, ty / 16, tz / 16),
				TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
				new Vector3f(s, s, s),
				null);
	}
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.NONE;
	}
}
