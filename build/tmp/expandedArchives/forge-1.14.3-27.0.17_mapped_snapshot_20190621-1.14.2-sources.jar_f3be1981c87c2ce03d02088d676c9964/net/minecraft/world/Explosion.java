package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Explosion {
   private final boolean causesFire;
   private final Explosion.Mode mode;
   private final Random random = new Random();
   private final World world;
   private final double x;
   private final double y;
   private final double z;
   private final Entity exploder;
   private final float size;
   private DamageSource damageSource;
   private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
   private final Map<PlayerEntity, Vec3d> playerKnockbackMap = Maps.newHashMap();
   private final Vec3d position;

   @OnlyIn(Dist.CLIENT)
   public Explosion(World worldIn, @Nullable Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
      this(worldIn, entityIn, x, y, z, size, false, Explosion.Mode.DESTROY, affectedPositions);
   }

   @OnlyIn(Dist.CLIENT)
   public Explosion(World p_i50006_1_, @Nullable Entity p_i50006_2_, double p_i50006_3_, double p_i50006_5_, double p_i50006_7_, float p_i50006_9_, boolean p_i50006_10_, Explosion.Mode p_i50006_11_, List<BlockPos> p_i50006_12_) {
      this(p_i50006_1_, p_i50006_2_, p_i50006_3_, p_i50006_5_, p_i50006_7_, p_i50006_9_, p_i50006_10_, p_i50006_11_);
      this.affectedBlockPositions.addAll(p_i50006_12_);
   }

   public Explosion(World p_i50007_1_, @Nullable Entity p_i50007_2_, double p_i50007_3_, double p_i50007_5_, double p_i50007_7_, float p_i50007_9_, boolean p_i50007_10_, Explosion.Mode p_i50007_11_) {
      this.world = p_i50007_1_;
      this.exploder = p_i50007_2_;
      this.size = p_i50007_9_;
      this.x = p_i50007_3_;
      this.y = p_i50007_5_;
      this.z = p_i50007_7_;
      this.causesFire = p_i50007_10_;
      this.mode = p_i50007_11_;
      this.damageSource = DamageSource.causeExplosionDamage(this);
      this.position = new Vec3d(this.x, this.y, this.z);
   }

   public static float func_222259_a(Vec3d p_222259_0_, Entity p_222259_1_) {
      AxisAlignedBB axisalignedbb = p_222259_1_.getBoundingBox();
      double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
      double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
      double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
      double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
      double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
      if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
         int i = 0;
         int j = 0;

         for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
            for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
               for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
                  double d5 = MathHelper.lerp((double)f, axisalignedbb.minX, axisalignedbb.maxX);
                  double d6 = MathHelper.lerp((double)f1, axisalignedbb.minY, axisalignedbb.maxY);
                  double d7 = MathHelper.lerp((double)f2, axisalignedbb.minZ, axisalignedbb.maxZ);
                  Vec3d vec3d = new Vec3d(d5 + d3, d6, d7 + d4);
                  if (p_222259_1_.world.rayTraceBlocks(new RayTraceContext(vec3d, p_222259_0_, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, p_222259_1_)).getType() == RayTraceResult.Type.MISS) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (float)i / (float)j;
      } else {
         return 0.0F;
      }
   }

   /**
    * Does the first part of the explosion (destroy blocks)
    */
   public void doExplosionA() {
      Set<BlockPos> set = Sets.newHashSet();
      int i = 16;

      for(int j = 0; j < 16; ++j) {
         for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
               if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                  double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                  double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                  double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                  double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                  d0 = d0 / d3;
                  d1 = d1 / d3;
                  d2 = d2 / d3;
                  float f = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
                  double d4 = this.x;
                  double d6 = this.y;
                  double d8 = this.z;

                  for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                     BlockPos blockpos = new BlockPos(d4, d6, d8);
                     BlockState blockstate = this.world.getBlockState(blockpos);
                     IFluidState ifluidstate = this.world.getFluidState(blockpos);
                     if (!blockstate.isAir(this.world, blockpos) || !ifluidstate.isEmpty()) {
                        float f2 = Math.max(blockstate.getExplosionResistance(this.world, blockpos, exploder, this), ifluidstate.getExplosionResistance(this.world, blockpos, exploder, this));
                        if (this.exploder != null) {
                           f2 = this.exploder.getExplosionResistance(this, this.world, blockpos, blockstate, ifluidstate, f2);
                        }

                        f -= (f2 + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.world, blockpos, blockstate, f))) {
                        set.add(blockpos);
                     }

                     d4 += d0 * (double)0.3F;
                     d6 += d1 * (double)0.3F;
                     d8 += d2 * (double)0.3F;
                  }
               }
            }
         }
      }

      this.affectedBlockPositions.addAll(set);
      float f3 = this.size * 2.0F;
      int k1 = MathHelper.floor(this.x - (double)f3 - 1.0D);
      int l1 = MathHelper.floor(this.x + (double)f3 + 1.0D);
      int i2 = MathHelper.floor(this.y - (double)f3 - 1.0D);
      int i1 = MathHelper.floor(this.y + (double)f3 + 1.0D);
      int j2 = MathHelper.floor(this.z - (double)f3 - 1.0D);
      int j1 = MathHelper.floor(this.z + (double)f3 + 1.0D);
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
      net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, list, f3);
      Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

      for(int k2 = 0; k2 < list.size(); ++k2) {
         Entity entity = list.get(k2);
         if (!entity.isImmuneToExplosions()) {
            double d12 = (double)(MathHelper.sqrt(entity.getDistanceSq(new Vec3d(this.x, this.y, this.z))) / f3);
            if (d12 <= 1.0D) {
               double d5 = entity.posX - this.x;
               double d7 = entity.posY + (double)entity.getEyeHeight() - this.y;
               double d9 = entity.posZ - this.z;
               double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
               if (d13 != 0.0D) {
                  d5 = d5 / d13;
                  d7 = d7 / d13;
                  d9 = d9 / d13;
                  double d14 = (double)func_222259_a(vec3d, entity);
                  double d10 = (1.0D - d12) * d14;
                  entity.attackEntityFrom(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
                  double d11 = d10;
                  if (entity instanceof LivingEntity) {
                     d11 = ProtectionEnchantment.getBlastDamageReduction((LivingEntity)entity, d10);
                  }

                  entity.setMotion(entity.getMotion().add(d5 * d11, d7 * d11, d9 * d11));
                  if (entity instanceof PlayerEntity) {
                     PlayerEntity playerentity = (PlayerEntity)entity;
                     if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.isFlying)) {
                        this.playerKnockbackMap.put(playerentity, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                     }
                  }
               }
            }
         }
      }

   }

   /**
    * Does the second part of the explosion (sound, particles, drop spawn)
    */
   public void doExplosionB(boolean spawnParticles) {
      this.world.playSound((PlayerEntity)null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
      boolean flag = this.mode != Explosion.Mode.NONE;
      if (!(this.size < 2.0F) && flag) {
         this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      } else {
         this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      }

      if (flag) {
         for(BlockPos blockpos : this.affectedBlockPositions) {
            BlockState blockstate = this.world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (spawnParticles) {
               double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
               double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
               double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
               double d3 = d0 - this.x;
               double d4 = d1 - this.y;
               double d5 = d2 - this.z;
               double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
               d3 = d3 / d6;
               d4 = d4 / d6;
               d5 = d5 / d6;
               double d7 = 0.5D / (d6 / (double)this.size + 0.1D);
               d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
               d3 = d3 * d7;
               d4 = d4 * d7;
               d5 = d5 * d7;
               this.world.addParticle(ParticleTypes.POOF, (d0 + this.x) / 2.0D, (d1 + this.y) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5);
               this.world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, d3, d4, d5);
            }

            if (!blockstate.isAir(this.world, blockpos)) {
               if (this.world instanceof ServerWorld && blockstate.canDropFromExplosion(this.world, blockpos, this)) {
                  TileEntity tileentity = blockstate.hasTileEntity() ? this.world.getTileEntity(blockpos) : null;
                  LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withRandom(this.world.rand).withParameter(LootParameters.POSITION, blockpos).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity);
                  if (this.mode == Explosion.Mode.DESTROY) {
                     lootcontext$builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.size);
                  }

                  Block.spawnDrops(blockstate, lootcontext$builder);
               }

               this.world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 3);
               block.onExplosionDestroy(this.world, blockpos, this);
            }
         }
      }

      if (this.causesFire) {
         for(BlockPos blockpos1 : this.affectedBlockPositions) {
            if (this.world.getBlockState(blockpos1).isAir(world, blockpos1) && this.world.getBlockState(blockpos1.down()).isOpaqueCube(this.world, blockpos1.down()) && this.random.nextInt(3) == 0) {
               this.world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
            }
         }
      }

   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public void setDamageSource(DamageSource damageSourceIn) {
      this.damageSource = damageSourceIn;
   }

   public Map<PlayerEntity, Vec3d> getPlayerKnockbackMap() {
      return this.playerKnockbackMap;
   }

   /**
    * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
    */
   @Nullable
   public LivingEntity getExplosivePlacedBy() {
      if (this.exploder == null) {
         return null;
      } else if (this.exploder instanceof TNTEntity) {
         return ((TNTEntity)this.exploder).getTntPlacedBy();
      } else {
         return this.exploder instanceof LivingEntity ? (LivingEntity)this.exploder : null;
      }
   }

   public void clearAffectedBlockPositions() {
      this.affectedBlockPositions.clear();
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }

   public Vec3d getPosition() {
      return this.position;
   }

   public static enum Mode {
      NONE,
      BREAK,
      DESTROY;
   }
}