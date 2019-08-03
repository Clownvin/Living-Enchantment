package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ActiveRenderInfo {
   private boolean field_216789_a;
   private IBlockReader field_216790_b;
   private Entity field_216791_c;
   private Vec3d field_216792_d = Vec3d.ZERO;
   private final BlockPos.MutableBlockPos field_216793_e = new BlockPos.MutableBlockPos();
   private Vec3d field_216794_f;
   private Vec3d field_216795_g;
   private Vec3d field_216796_h;
   private float pitch;
   private float yaw;
   private boolean field_216799_k;
   private boolean field_216800_l;
   private float field_216801_m;
   private float field_216802_n;

   public void func_216772_a(IBlockReader p_216772_1_, Entity p_216772_2_, boolean p_216772_3_, boolean p_216772_4_, float p_216772_5_) {
      this.field_216789_a = true;
      this.field_216790_b = p_216772_1_;
      this.field_216791_c = p_216772_2_;
      this.field_216799_k = p_216772_3_;
      this.field_216800_l = p_216772_4_;
      this.func_216776_a(p_216772_2_.getYaw(p_216772_5_), p_216772_2_.getPitch(p_216772_5_));
      this.func_216775_b(MathHelper.lerp((double)p_216772_5_, p_216772_2_.prevPosX, p_216772_2_.posX), MathHelper.lerp((double)p_216772_5_, p_216772_2_.prevPosY, p_216772_2_.posY) + (double)MathHelper.lerp(p_216772_5_, this.field_216802_n, this.field_216801_m), MathHelper.lerp((double)p_216772_5_, p_216772_2_.prevPosZ, p_216772_2_.posZ));
      if (p_216772_3_) {
         if (p_216772_4_) {
            this.yaw += 180.0F;
            this.pitch += -this.pitch * 2.0F;
            this.func_216784_b();
         }

         this.func_216782_a(-this.func_216779_a(4.0D), 0.0D, 0.0D);
      } else if (p_216772_2_ instanceof LivingEntity && ((LivingEntity)p_216772_2_).isSleeping()) {
         Direction direction = ((LivingEntity)p_216772_2_).func_213376_dz();
         this.func_216776_a(direction != null ? direction.getHorizontalAngle() - 180.0F : 0.0F, 0.0F);
         this.func_216782_a(0.0D, 0.3D, 0.0D);
      } else {
         this.func_216782_a((double)-0.05F, 0.0D, 0.0D);
      }

      GlStateManager.rotatef(this.pitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(this.yaw + 180.0F, 0.0F, 1.0F, 0.0F);
   }

   public void func_216783_a() {
      if (this.field_216791_c != null) {
         this.field_216802_n = this.field_216801_m;
         this.field_216801_m += (this.field_216791_c.getEyeHeight() - this.field_216801_m) * 0.5F;
      }

   }

   private double func_216779_a(double p_216779_1_) {
      for(int i = 0; i < 8; ++i) {
         float f = (float)((i & 1) * 2 - 1);
         float f1 = (float)((i >> 1 & 1) * 2 - 1);
         float f2 = (float)((i >> 2 & 1) * 2 - 1);
         f = f * 0.1F;
         f1 = f1 * 0.1F;
         f2 = f2 * 0.1F;
         Vec3d vec3d = this.field_216792_d.add((double)f, (double)f1, (double)f2);
         Vec3d vec3d1 = new Vec3d(this.field_216792_d.x - this.field_216794_f.x * p_216779_1_ + (double)f + (double)f2, this.field_216792_d.y - this.field_216794_f.y * p_216779_1_ + (double)f1, this.field_216792_d.z - this.field_216794_f.z * p_216779_1_ + (double)f2);
         RayTraceResult raytraceresult = this.field_216790_b.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.field_216791_c));
         if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            double d0 = raytraceresult.getHitVec().distanceTo(this.field_216792_d);
            if (d0 < p_216779_1_) {
               p_216779_1_ = d0;
            }
         }
      }

      return p_216779_1_;
   }

   protected void func_216782_a(double p_216782_1_, double p_216782_3_, double p_216782_5_) {
      double d0 = this.field_216794_f.x * p_216782_1_ + this.field_216795_g.x * p_216782_3_ + this.field_216796_h.x * p_216782_5_;
      double d1 = this.field_216794_f.y * p_216782_1_ + this.field_216795_g.y * p_216782_3_ + this.field_216796_h.y * p_216782_5_;
      double d2 = this.field_216794_f.z * p_216782_1_ + this.field_216795_g.z * p_216782_3_ + this.field_216796_h.z * p_216782_5_;
      this.func_216774_a(new Vec3d(this.field_216792_d.x + d0, this.field_216792_d.y + d1, this.field_216792_d.z + d2));
   }

   protected void func_216784_b() {
      float f = MathHelper.cos((this.yaw + 90.0F) * ((float)Math.PI / 180F));
      float f1 = MathHelper.sin((this.yaw + 90.0F) * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(-this.pitch * ((float)Math.PI / 180F));
      float f3 = MathHelper.sin(-this.pitch * ((float)Math.PI / 180F));
      float f4 = MathHelper.cos((-this.pitch + 90.0F) * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin((-this.pitch + 90.0F) * ((float)Math.PI / 180F));
      this.field_216794_f = new Vec3d((double)(f * f2), (double)f3, (double)(f1 * f2));
      this.field_216795_g = new Vec3d((double)(f * f4), (double)f5, (double)(f1 * f4));
      this.field_216796_h = this.field_216794_f.crossProduct(this.field_216795_g).scale(-1.0D);
   }

   protected void func_216776_a(float p_216776_1_, float p_216776_2_) {
      this.pitch = p_216776_2_;
      this.yaw = p_216776_1_;
      this.func_216784_b();
   }

   protected void func_216775_b(double p_216775_1_, double p_216775_3_, double p_216775_5_) {
      this.func_216774_a(new Vec3d(p_216775_1_, p_216775_3_, p_216775_5_));
   }

   protected void func_216774_a(Vec3d p_216774_1_) {
      this.field_216792_d = p_216774_1_;
      this.field_216793_e.setPos(p_216774_1_.x, p_216774_1_.y, p_216774_1_.z);
   }

   public Vec3d getProjectedView() {
      return this.field_216792_d;
   }

   public BlockPos func_216780_d() {
      return this.field_216793_e;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public Entity func_216773_g() {
      return this.field_216791_c;
   }

   public boolean func_216786_h() {
      return this.field_216789_a;
   }

   public boolean func_216770_i() {
      return this.field_216799_k;
   }

   public IFluidState func_216771_k() {
      if (!this.field_216789_a) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         IFluidState ifluidstate = this.field_216790_b.getFluidState(this.field_216793_e);
         return !ifluidstate.isEmpty() && this.field_216792_d.y >= (double)((float)this.field_216793_e.getY() + ifluidstate.func_215679_a(this.field_216790_b, this.field_216793_e)) ? Fluids.EMPTY.getDefaultState() : ifluidstate;
      }
   }

   public final Vec3d func_216787_l() {
      return this.field_216794_f;
   }

   public final Vec3d func_216788_m() {
      return this.field_216795_g;
   }

   public void func_216781_o() {
      this.field_216790_b = null;
      this.field_216791_c = null;
      this.field_216789_a = false;
   }

   public net.minecraft.block.BlockState getBlockAtCamera() {
      if (!this.field_216789_a)
         return net.minecraft.block.Blocks.AIR.getDefaultState();
      else
         return this.field_216790_b.getBlockState(this.field_216793_e).getStateAtViewpoint(this.field_216790_b, this.field_216793_e, this.field_216792_d);
   }
}