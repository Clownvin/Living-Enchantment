package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.tileentity.model.DragonHeadModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkullTileEntityRenderer extends TileEntityRenderer<SkullTileEntity> {
   public static SkullTileEntityRenderer instance;
   private static final Map<SkullBlock.ISkullType, GenericHeadModel> MODELS = Util.make(Maps.newHashMap(), (p_209262_0_) -> {
      GenericHeadModel genericheadmodel = new GenericHeadModel(0, 0, 64, 32);
      GenericHeadModel genericheadmodel1 = new HumanoidHeadModel();
      DragonHeadModel dragonheadmodel = new DragonHeadModel(0.0F);
      p_209262_0_.put(SkullBlock.Types.SKELETON, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.WITHER_SKELETON, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.PLAYER, genericheadmodel1);
      p_209262_0_.put(SkullBlock.Types.ZOMBIE, genericheadmodel1);
      p_209262_0_.put(SkullBlock.Types.CREEPER, genericheadmodel);
      p_209262_0_.put(SkullBlock.Types.DRAGON, dragonheadmodel);
   });
   private static final Map<SkullBlock.ISkullType, ResourceLocation> SKINS = Util.make(Maps.newHashMap(), (p_209263_0_) -> {
      p_209263_0_.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      p_209263_0_.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      p_209263_0_.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      p_209263_0_.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      p_209263_0_.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      p_209263_0_.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkinLegacy());
   });

   public void render(SkullTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      float f = tileEntityIn.getAnimationProgress(partialTicks);
      BlockState blockstate = tileEntityIn.getBlockState();
      boolean flag = blockstate.getBlock() instanceof WallSkullBlock;
      Direction direction = flag ? blockstate.get(WallSkullBlock.FACING) : null;
      float f1 = 22.5F * (float)(flag ? (2 + direction.getHorizontalIndex()) * 4 : blockstate.get(SkullBlock.ROTATION));
      this.render((float)x, (float)y, (float)z, direction, f1, ((AbstractSkullBlock)blockstate.getBlock()).getSkullType(), tileEntityIn.getPlayerProfile(), destroyStage, f);
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      super.setRendererDispatcher(rendererDispatcherIn);
      instance = this;
   }

   public void render(float x, float y, float z, @Nullable Direction facing, float rotationIn, SkullBlock.ISkullType type, @Nullable GameProfile playerProfile, int destroyStage, float animationProgress) {
      GenericHeadModel genericheadmodel = MODELS.get(type);
      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(this.func_199356_a(type, playerProfile));
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (facing == null) {
         GlStateManager.translatef(x + 0.5F, y, z + 0.5F);
      } else {
         switch(facing) {
         case NORTH:
            GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.74F);
            break;
         case SOUTH:
            GlStateManager.translatef(x + 0.5F, y + 0.25F, z + 0.26F);
            break;
         case WEST:
            GlStateManager.translatef(x + 0.74F, y + 0.25F, z + 0.5F);
            break;
         case EAST:
         default:
            GlStateManager.translatef(x + 0.26F, y + 0.25F, z + 0.5F);
         }
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      if (type == SkullBlock.Types.PLAYER) {
         GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);
      }

      genericheadmodel.func_217104_a(animationProgress, 0.0F, 0.0F, rotationIn, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private ResourceLocation func_199356_a(SkullBlock.ISkullType p_199356_1_, @Nullable GameProfile p_199356_2_) {
      ResourceLocation resourcelocation = SKINS.get(p_199356_1_);
      if (p_199356_1_ == SkullBlock.Types.PLAYER && p_199356_2_ != null) {
         Minecraft minecraft = Minecraft.getInstance();
         Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(p_199356_2_);
         if (map.containsKey(Type.SKIN)) {
            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
         } else {
            resourcelocation = DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(p_199356_2_));
         }
      }

      return resourcelocation;
   }
}