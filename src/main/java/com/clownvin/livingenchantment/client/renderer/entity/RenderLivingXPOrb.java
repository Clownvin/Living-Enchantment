package com.clownvin.livingenchantment.client.renderer.entity;

import com.clownvin.livingenchantment.entity.item.EntityLivingXPOrb;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderLivingXPOrb extends Render<EntityLivingXPOrb> {

    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");

    protected ResourceLocation getEntityTexture(EntityXPOrb p_110775_1_) {
        return EXPERIENCE_ORB_TEXTURES;
    }
    public RenderLivingXPOrb(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    @SubscribeEvent
    public static void registerRender(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityLivingXPOrb.class, RenderLivingXPOrb::new);
    }

    /**
     * Renders the desired {@code T} LIVING_XP_ORB_ENTITY_TYPE Entity.
     */
    @Override
    public void doRender(EntityLivingXPOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (this.renderOutlines)
            return;
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y, (float) z);
        this.bindEntityTexture(entity);
        RenderHelper.enableStandardItemLighting();
        int i = entity.getTextureByXP();
        float f = (float) (i % 4 * 16 + 0) / 64.0F;
        float f1 = (float) (i % 4 * 16 + 16) / 64.0F;
        float f2 = (float) (i / 4 * 16 + 0) / 64.0F;
        float f3 = (float) (i / 4 * 16 + 16) / 64.0F;
        int j = entity.getBrightnessForRender();
        int k = j % 65536;
        int l = j / 65536;
        OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float) k, (float) l);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f9 = ((float) entity.xpColor + partialTicks) / 2.0F;
        l = (int) ((MathHelper.sin(f9 + 0.0F) + 1.0F) * 0.5F * 255.0F);
        GlStateManager.translatef(0.0F, 0.1F, 0.0F);
        GlStateManager.rotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(0.3F, 0.3F, 0.3F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex((double) f, (double) f3).color(l, 255 - l, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex((double) f1, (double) f3).color(l, 255 - l, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex((double) f1, (double) f2).color(l, 255 - l, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex((double) f, (double) f2).color(l, 255 - l, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityLivingXPOrb entity) {
        return EXPERIENCE_ORB_TEXTURES;
    }
}
