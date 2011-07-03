package moCreatures.renders;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import moCreatures.entities.EntityWerewolf;
import moCreatures.helpers.EntityLivingHelper;
import moCreatures.models.ModelWereHuman;
import moCreatures.models.ModelWerewolf;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.ModelBase;
import net.minecraft.src.RenderLiving;

import org.lwjgl.opengl.GL11;

public class RenderWerewolf extends RenderLiving
{

    public RenderWerewolf(ModelWereHuman modelwerehuman, ModelBase modelbase, float f)
    {
        super(modelbase, f);
        setRenderPassModel(modelwerehuman);
        tempWerewolf = (ModelWerewolf)modelbase;
    }

    protected boolean func_176_a(EntityWerewolf entitywerewolf, int i)
    {
        if(!entitywerewolf.humanform)
        {
        	EntityLivingHelper.setTexture(entitywerewolf, "/moCreatures/textures/werewolf.png");
            loadTexture("/moCreatures/textures/wereblank.png");
        } else
        {
        	EntityLivingHelper.setTexture(entitywerewolf, "/moCreatures/textures/wereblank.png");
            loadTexture("/moCreatures/textures/werehuman.png");
        }
        return i == 0 && !entitywerewolf.wereboolean;
    }

    protected boolean shouldRenderPass(EntityLiving entityliving, int i, float f)
    {
        return func_176_a((EntityWerewolf)entityliving, i);
    }

    public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2, 
            float f, float f1)
    {
        EntityWerewolf entitywerewolf = (EntityWerewolf)entityliving;
        if(entitywerewolf.humanform)
        {
            super.doRenderLiving(entityliving, d, d1, d2, f, f1);
            return;
        }
        boolean flag = entitywerewolf.hunched;
        GL11.glPushMatrix();
        GL11.glDisable(2884 /*GL_CULL_FACE*/);
        mainModel.onGround = func_167_c(entityliving, f1);
        mainModel.isRiding = entityliving.isRiding();
        if(renderPassModel != null)
        {
            renderPassModel.isRiding = mainModel.isRiding;
        }
        try
        {
            float f2 = entityliving.prevRenderYawOffset + (entityliving.renderYawOffset - entityliving.prevRenderYawOffset) * f1;
            float f3 = entityliving.prevRotationYaw + (entityliving.rotationYaw - entityliving.prevRotationYaw) * f1;
            float f4 = entityliving.prevRotationPitch + (entityliving.rotationPitch - entityliving.prevRotationPitch) * f1;
            func_22012_b(entityliving, d, d1, d2);
            float f5 = func_170_d(entityliving, f1);
            rotateCorpse(entityliving, f5, f2, f1);
            float f6 = 0.0625F;
            GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
            GL11.glScalef(-1F, -1F, 1.0F);
            preRenderCallback(entityliving, f1);
            GL11.glTranslatef(0.0F, -24F * f6 - 0.007813F, 0.0F);
            float f7 = entityliving.field_705_Q + (entityliving.field_704_R - entityliving.field_705_Q) * f1;
            float f8 = entityliving.field_703_S - entityliving.field_704_R * (1.0F - f1);
            if(f7 > 1.0F)
            {
                f7 = 1.0F;
            }
            loadDownloadableImageTexture(entityliving.skinUrl, entityliving.getEntityTexture());
            GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            tempWerewolf.render(f8, f7, f5, f3 - f2, f4, f6, flag);
            for(int i = 0; i < 4; i++)
            {
                if(shouldRenderPass(entityliving, i, f1))
                {
                    renderPassModel.render(f8, f7, f5, f3 - f2, f4, f6);
                    GL11.glDisable(3042 /*GL_BLEND*/);
                    GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                }
            }

            renderEquippedItems(entityliving, f1);
            float f9 = entityliving.getEntityBrightness(f1);
            int j = getColorMultiplier(entityliving, f9, f1);
            if((j >> 24 & 0xff) > 0 || entityliving.hurtTime > 0 || entityliving.deathTime > 0)
            {
                GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(770, 771);
                GL11.glDepthFunc(514);
                if(entityliving.hurtTime > 0 || entityliving.deathTime > 0)
                {
                    GL11.glColor4f(f9, 0.0F, 0.0F, 0.4F);
                    tempWerewolf.render(f8, f7, f5, f3 - f2, f4, f6, flag);
                    for(int k = 0; k < 4; k++)
                    {
                        if(shouldRenderPass(entityliving, k, f1))
                        {
                            GL11.glColor4f(f9, 0.0F, 0.0F, 0.4F);
                            renderPassModel.render(f8, f7, f5, f3 - f2, f4, f6);
                        }
                    }

                }
                if((j >> 24 & 0xff) > 0)
                {
                    float f10 = (float)(j >> 16 & 0xff) / 255F;
                    float f11 = (float)(j >> 8 & 0xff) / 255F;
                    float f12 = (float)(j & 0xff) / 255F;
                    float f13 = (float)(j >> 24 & 0xff) / 255F;
                    GL11.glColor4f(f10, f11, f12, f13);
                    tempWerewolf.render(f8, f7, f5, f3 - f2, f4, f6, flag);
                    for(int l = 0; l < 4; l++)
                    {
                        if(shouldRenderPass(entityliving, l, f1))
                        {
                            GL11.glColor4f(f10, f11, f12, f13);
                            renderPassModel.render(f8, f7, f5, f3 - f2, f4, f6);
                        }
                    }

                }
                GL11.glDepthFunc(515);
                GL11.glDisable(3042 /*GL_BLEND*/);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
            }
            GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        GL11.glEnable(2884 /*GL_CULL_FACE*/);
        GL11.glPopMatrix();
        passSpecialRender(entityliving, d, d1, d2);
    }

    private ModelWerewolf tempWerewolf;
}
