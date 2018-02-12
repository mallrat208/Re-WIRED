package com.mr208.rewired.client.render;


import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ReWIREDRenderer
{
	private static VertexFormat prevFormat = null;
	private static int prevMode = -1;
	
	public static void pauseRenderer(Tessellator tess)
	{
		if(ReWIREDRenderer.isDrawing(tess))
		{
			prevFormat = tess.getBuffer().getVertexFormat();
			prevMode = tess.getBuffer().getDrawMode();
			tess.draw();
		}
	}
	
	public static void saveRenderer(Tessellator tess)
	{
		if(ReWIREDRenderer.isDrawing(tess))
		{
			prevFormat = tess.getBuffer().getVertexFormat();
			prevMode = tess.getBuffer().getDrawMode();
		}
	}
	
	public static void resumeRenderer(Tessellator tess)
	{
		if(prevFormat != null)
		{
			tess.getBuffer().begin(prevMode, prevFormat);
		}
		
		prevFormat = null;
		prevMode = -1;
	}
	
	public static boolean isDrawing(Tessellator tess)
	{
		return isDrawing(tess.getBuffer());
	}
	
	public static boolean isDrawing(BufferBuilder buffer)
	{
		return (boolean)ReflectionHelper.getPrivateValue(BufferBuilder.class, buffer, "isDrawing", "field_179010_r");
	}
}
