package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.raid.Raid;

public class CelebrateRaidVictoryTask extends Task<VillagerEntity> {
   @Nullable
   private Raid raid;

   public CelebrateRaidVictoryTask(int p_i50370_1_, int p_i50370_2_) {
      super(ImmutableMap.of(), p_i50370_1_, p_i50370_2_);
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      this.raid = worldIn.findRaid(new BlockPos(owner));
      return this.raid != null && this.raid.isVictory() && MoveToSkylightTask.func_223015_b(worldIn, owner);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.raid != null && !this.raid.isStopped();
   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      this.raid = null;
      p_212835_2_.getBrain().updateActivity(p_212835_1_.getDayTime(), p_212835_1_.getGameTime());
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      Random random = owner.getRNG();
      if (random.nextInt(100) == 0) {
         owner.func_213711_eb();
      }

      if (random.nextInt(200) == 0 && MoveToSkylightTask.func_223015_b(worldIn, owner)) {
         DyeColor dyecolor = DyeColor.values()[random.nextInt(DyeColor.values().length)];
         int i = random.nextInt(3);
         ItemStack itemstack = this.makeFirework(dyecolor, i);
         FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(owner.world, owner.posX, owner.posY + (double)owner.getEyeHeight(), owner.posZ, itemstack);
         owner.world.addEntity(fireworkrocketentity);
      }

   }

   private ItemStack makeFirework(DyeColor color, int flightTime) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 1);
      ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
      CompoundNBT compoundnbt = itemstack1.getOrCreateChildTag("Explosion");
      List<Integer> list = Lists.newArrayList();
      list.add(color.getFireworkColor());
      compoundnbt.putIntArray("Colors", list);
      compoundnbt.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.func_196071_a());
      CompoundNBT compoundnbt1 = itemstack.getOrCreateChildTag("Fireworks");
      ListNBT listnbt = new ListNBT();
      CompoundNBT compoundnbt2 = itemstack1.getChildTag("Explosion");
      if (compoundnbt2 != null) {
         listnbt.add(compoundnbt2);
      }

      compoundnbt1.putByte("Flight", (byte)flightTime);
      if (!listnbt.isEmpty()) {
         compoundnbt1.put("Explosions", listnbt);
      }

      return itemstack;
   }
}