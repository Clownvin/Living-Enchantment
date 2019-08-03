package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class GenerationSettings {
   protected int field_214971_a = 32;
   protected final int field_214972_b = 8;
   protected int field_214973_c = 32;
   protected int field_214974_d = 5;
   protected int field_214975_e = 32;
   protected int field_214976_f = 128;
   protected int field_214977_g = 3;
   protected int field_214978_h = 32;
   protected final int field_214979_i = 8;
   protected final int field_214980_j = 16;
   protected final int field_214981_k = 8;
   protected int field_214982_l = 20;
   protected final int field_214983_m = 11;
   protected final int field_214984_n = 16;
   protected final int field_214985_o = 8;
   protected int field_214986_p = 80;
   protected final int field_214987_q = 20;
   protected BlockState field_214988_r = Blocks.STONE.getDefaultState();
   protected BlockState field_214989_s = Blocks.WATER.getDefaultState();

   public int getVillageDistance() {
      return this.field_214971_a;
   }

   public int getVillageSeparation() {
      return 8;
   }

   public int getOceanMonumentSpacing() {
      return this.field_214973_c;
   }

   public int getOceanMonumentSeparation() {
      return this.field_214974_d;
   }

   public int getStrongholdDistance() {
      return this.field_214975_e;
   }

   public int getStrongholdCount() {
      return this.field_214976_f;
   }

   public int getStrongholdSpread() {
      return this.field_214977_g;
   }

   public int getBiomeFeatureDistance() {
      return this.field_214978_h;
   }

   public int getBiomeFeatureSeparation() {
      return 8;
   }

   public int getShipwreckDistance() {
      return 16;
   }

   public int getShipwreckSeparation() {
      return 8;
   }

   public int getOceanRuinDistance() {
      return 16;
   }

   public int getOceanRuinSeparation() {
      return 8;
   }

   public int getEndCityDistance() {
      return this.field_214982_l;
   }

   public int getEndCitySeparation() {
      return 11;
   }

   public int getMansionDistance() {
      return this.field_214986_p;
   }

   public int getMansionSeparation() {
      return 20;
   }

   public BlockState getDefaultBlock() {
      return this.field_214988_r;
   }

   public BlockState getDefaultFluid() {
      return this.field_214989_s;
   }

   public void setDefaultBlock(BlockState p_214969_1_) {
      this.field_214988_r = p_214969_1_;
   }

   public void setDefaultFluid(BlockState p_214970_1_) {
      this.field_214989_s = p_214970_1_;
   }

   public int func_214967_t() {
      return 0;
   }

   public int func_214968_u() {
      return 256;
   }
}