package net.minecraft.network;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.raid.Raid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugPacketSender {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void func_218802_a(ServerWorld p_218802_0_, ChunkPos p_218802_1_) {
   }

   public static void func_218799_a(ServerWorld p_218799_0_, BlockPos p_218799_1_) {
   }

   public static void func_218805_b(ServerWorld p_218805_0_, BlockPos p_218805_1_) {
   }

   public static void func_218801_c(ServerWorld p_218801_0_, BlockPos p_218801_1_) {
   }

   public static void func_218803_a(World p_218803_0_, MobEntity p_218803_1_, @Nullable Path p_218803_2_, float p_218803_3_) {
   }

   public static void func_218806_a(World p_218806_0_, BlockPos p_218806_1_) {
   }

   public static void func_218804_a(IWorld p_218804_0_, StructureStart p_218804_1_) {
   }

   public static void func_218800_a(World p_218800_0_, MobEntity p_218800_1_, GoalSelector p_218800_2_) {
   }

   public static void sendRaids(ServerWorld p_222946_0_, Collection<Raid> p_222946_1_) {
   }

   public static void func_218798_a(LivingEntity p_218798_0_) {
   }
}