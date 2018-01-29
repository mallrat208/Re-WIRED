package com.mr208.rewired.client.render;


import com.mr208.rewired.client.model.ModelTechVisor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModelCustomArmor extends ModelBiped
{
	public static ModelCustomArmor INSTANCE = new ModelCustomArmor();
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public ArmorModel modelType;
	
	public ItemStack itemStack;
	
	public ModelCustomArmor()
	{
		resetPart(bipedHead, 0, 0, 0);
		resetPart(bipedBody, 0, 0, 0);
		resetPart(bipedRightArm, 5, 2, 0);
		resetPart(bipedLeftArm, -5, 2, 0);
		resetPart(bipedRightLeg, 0, 0, 0);
		resetPart(bipedLeftLeg, 0, 0, 0);
		
		bipedHeadwear.cubeList.clear();
	}
	
	public void init(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		reset();
		
		isSneak = entity.isSneaking();
		isRiding = entity.isRiding();
		
		if(entity instanceof EntityLivingBase)
		{
			isChild = ((EntityLivingBase)entity).isChild();
		}
		
		if(modelType.armorSlot == 0)
		{
			bipedHead.isHidden = entity.isInvisible();
			bipedHead.showModel = true;
		}
		else if(modelType.armorSlot == 1)
		{
			bipedBody.isHidden = entity.isInvisible();
			bipedBody.showModel = true;
		}
		else if(modelType.armorSlot == 3)
		{
			bipedLeftLeg.isHidden = entity.isInvisible();
			bipedLeftLeg.showModel = true;
			bipedRightLeg.isHidden = entity.isInvisible();
			bipedRightLeg.showModel = true;
		}
		
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		if (entityIn instanceof EntityArmorStand)
		{
			EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
			this.bipedHead.rotateAngleX = 0.017453292F * entityarmorstand.getHeadRotation().getX();
			this.bipedHead.rotateAngleY = 0.017453292F * entityarmorstand.getHeadRotation().getY();
			this.bipedHead.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadRotation().getZ();
			this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
			this.bipedBody.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
			this.bipedBody.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
			this.bipedBody.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
			this.bipedLeftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmRotation().getX();
			this.bipedLeftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmRotation().getY();
			this.bipedLeftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmRotation().getZ();
			this.bipedRightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmRotation().getX();
			this.bipedRightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmRotation().getY();
			this.bipedRightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmRotation().getZ();
			this.bipedLeftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegRotation().getX();
			this.bipedLeftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegRotation().getY();
			this.bipedLeftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegRotation().getZ();
			this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
			this.bipedRightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegRotation().getX();
			this.bipedRightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegRotation().getY();
			this.bipedRightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegRotation().getZ();
			this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
			copyModelAngles(this.bipedHead, this.bipedHeadwear);
		}
		else
			super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
	}
	
	public void reset()
	{
		bipedHead.isHidden = true;
		bipedBody.isHidden = true;
		bipedRightArm.isHidden = true;
		bipedLeftArm.isHidden = true;
		bipedRightLeg.isHidden = true;
		bipedLeftLeg.isHidden = true;
		
		bipedHead.showModel = false;
		bipedBody.showModel = false;
		bipedRightArm.showModel = false;
		bipedLeftArm.showModel = false;
		bipedRightLeg.showModel = false;
		bipedLeftLeg.showModel = false;
	}
	
	public void resetPart(ModelRenderer  renderer, float x, float y, float z)
	{
		renderer.cubeList.clear();
		ModelCustom model = new ModelCustom(this, renderer);
		renderer.addChild(model);
		setOffset(renderer, x, y, z);
		
	}
	
	public void setOffset(ModelRenderer renderer, float x, float y, float z)
	{
		renderer.offsetX = x;
		renderer.offsetY = y;
		renderer.offsetZ = z;
	}
	
	public class ModelCustom extends ModelRenderer
	{
		public ModelCustomArmor biped;
		public ModelRenderer partRenderer;
		
		public ModelCustom(ModelCustomArmor base, ModelRenderer renderer)
		{
			super(base);
			
			biped = base;
			partRenderer = renderer;
		}
		
		@Override
		public void render(float scale)
		{
			if(ModelCustomArmor.this.modelType != null)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(0,0,0);
				
				mc.renderEngine.bindTexture(modelType.texture);
				
				if(useModel(biped.modelType, partRenderer, biped))
				{
					if(biped.modelType == ArmorModel.TECHVISOR)
					{
						GlStateManager.translate(0,0, 0);
						ArmorModel.techVisorModel.render(0.0625F, itemStack);
					}
				}
				
				GlStateManager.popMatrix();
			}
		}
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		init(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}
	
	public static boolean useModel(ArmorModel type, ModelRenderer partRenderer, ModelCustomArmor biped)
	{
		if(type.armorSlot == 0)
			return partRenderer == biped.bipedHead;
		else if(type.armorSlot == 1)
			return partRenderer == biped.bipedBody;
		else if(type.armorSlot == 2)
			return partRenderer == biped.bipedLeftArm || partRenderer == biped.bipedRightArm || partRenderer == biped.bipedBody;
		else if(type.armorSlot == 3)
			return partRenderer == biped.bipedLeftLeg || partRenderer == biped.bipedRightLeg;
		
		return false;
	}
	
	public enum ArmorModel
	{
		TECHVISOR(0,new ResourceLocation("rewired","textures/models/equipment/techvisor.png"));
		
		public int armorSlot;
		public ResourceLocation texture;
		
		public static ModelTechVisor techVisorModel = new ModelTechVisor();
		
		ArmorModel(int i, ResourceLocation rl)
		{
			armorSlot = i;
			texture = rl;
		}
	}
}
