package satellite.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class OriginalFontRenderer extends MCFontRenderer {
	
	public UnicodeFont font;
	
	public OriginalFontRenderer(String fontName, int size) {
		super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().isUnicode());
		(font = new UnicodeFont(new Font(fontName, Font.PLAIN, size))).addAsciiGlyphs();
		font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		try {
			font.loadGlyphs();
		}catch(SlickException e) {
			e.printStackTrace();
		}
		
		FONT_HEIGHT = font.getHeight("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789") / 2;
	}

	public OriginalFontRenderer(String filePath, int size, boolean bold, boolean italic) {
		super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().isUnicode());
		try {
			(font = new UnicodeFont(filePath, size, bold, italic)).addAsciiGlyphs();
			font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
			font.loadGlyphs();	
		}catch(SlickException e) {
			e.printStackTrace();
		}
		
		FONT_HEIGHT = font.getHeight("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789") / 2;
	}

	private int renderString(String text, float x, float y, int color) {
		if(text == null) return 0;
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		y *= 2;
		y = y * 2 - 2.0F;
		boolean blend = GL11.glIsEnabled(GL11.GL_BLEND), lighting = GL11.glIsEnabled(GL11.GL_LIGHTING), texture2d = GL11.glIsEnabled(GL11.GL_TEXTURE_2D), alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		float red = RGBA(color)[0];
		float green = RGBA(color)[1];
		float blue = RGBA(color)[2];
		

        for (int i = 0; i < text.length(); ++i)
        {
            char c0 = text.charAt(i);

            if (c0 == 167 && i + 1 < text.length())
            {
                int l = "0123456789abcdefklmnor".indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase(Locale.ROOT).charAt(0));

                if (l < 16)
                {
                    if (l < 0 || l > 15)
                    {
                        l = 15;
                    }
                    
                    int fColor = this.colorCode[1];
                    
                    red = RGBA(fColor)[0];
                    green = RGBA(fColor)[1];
                    blue = RGBA(fColor)[2];
                }
                 ++i;
            }else {
            	font.drawString(x, y, Character.toString(c0), new org.newdawn.slick.Color(red, green, blue, RGBA(color)[3]));
            	x += this.getCharWidth(c0);	
            }
        }
        
        if(!blend)
        	GL11.glDisable(GL11.GL_BLEND);
		if(lighting)
			GL11.glEnable(GL11.GL_LIGHTING);
		if(texture2d)
        	GL11.glEnable(GL11.GL_TEXTURE_2D);
        if(alpha)
        	GL11.glEnable(GL11.GL_ALPHA_TEST);
        
        GlStateManager.bindTexture(0);
        GlStateManager.color(0, 0, 0);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();
        
        return (int) x;
	}
	
	public int drawStringWithShadow(String text, float x, float y, int color) {
		renderString((text), x + 1f, y + 1f, java.awt.Color.BLACK.getRGB());
		return renderString(text, x, y, color);
	}
	
	public void drawCenteredStringWithoutShadow(String text, int x, int y, int color) {
		renderString((text), (x - this.getStringWidth(text) / 2)+1F, y+1F, java.awt.Color.BLACK.getRGB());
		renderString(text, (x - this.getStringWidth(text) / 2), y, color);
	}
	
	public void drawCenteredString(String text, int x, int y, int color) {
		renderString(text, (x - this.getStringWidth(text) / 2), y, color);
	}
	
	public int drawString(String text, double x, double y, int color) {
		return renderString(text, (int)x, (int)y, color);
	}
	
	@Override
	public int getCharWidth(char character) {
		return font.getWidth((Character.toString(character)));
	}
	
	@Override
	public int getStringWidth(String text) {
		return font.getWidth((text)) / 2;
		
	}
}

