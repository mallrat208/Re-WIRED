package com.mr208.rewired.client.model;

import com.mr208.rewired.common.items.equipment.IColorableEquipment;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ModelTechVisor extends ModelBase
{
	private ModelRenderer lens;
	private ModelRenderer back_frame;
	private ModelRenderer arm_1;
	private ModelRenderer arm_2;
	private ModelRenderer lens_mount;
	
	public ModelTechVisor()
	{
		this.textureWidth = 32;
		this.textureHeight = 32;
		
		this.back_frame = new ModelRenderer(this, 0, 0);
		this.back_frame.setRotationPoint(-4.5F, -5.0F, 3.5F);
		this.back_frame.addBox(0.0F, 0.0F, 0.0F, 9, 1, 1, 0.0F);
		this.setRotation(back_frame, 0,0,0);

		this.arm_1 = new ModelRenderer(this, 12, 4);
		this.arm_1.setRotationPoint(3.5F, -5.0F, -1.5F);
		this.arm_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 5, 0.0F);
		this.setRotation(arm_1, 0,0,0);
		
		this.arm_2 = new ModelRenderer(this, 1, 3);
		this.arm_2.setRotationPoint(-4.5F, -5.0F, -4.5F);
		this.arm_2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 8, 0.0F);
		this.setRotation(arm_2, 0,0,0);
		
		this.lens_mount = new ModelRenderer(this, 12, 4);
		this.lens_mount.setRotationPoint(-4.5F, -5.0F, -5.0F);
		this.lens_mount.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
		this.setRotation(lens_mount, 0,0,0);
		
		this.lens = new ModelRenderer(this, 0, 3);
		this.lens.setRotationPoint(-3.5F, -5.0F, -5.0F);
		this.lens.addBox(0.0F, 0.0F, 0.5F, 3, 3, 1, 0.0F);
		this.setRotation(lens, 0,0,0);
	}
	
	public void render(float scale, ItemStack itemStack)
	{
		float[] colors = new float[]{0,0,0};
		
		if(itemStack.getItem() instanceof IColorableEquipment)
		{
			colors = ((IColorableEquipment)itemStack.getItem()).getColorFloat(itemStack);
		}
		
		back_frame.render(scale);
		arm_1.render(scale);
		lens_mount.render(scale);
		arm_2.render(scale);
		
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color( colors[0], colors[1], colors[2], 0.40F);
		lens.render(scale);
		GlStateManager.disableBlend();

	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}

