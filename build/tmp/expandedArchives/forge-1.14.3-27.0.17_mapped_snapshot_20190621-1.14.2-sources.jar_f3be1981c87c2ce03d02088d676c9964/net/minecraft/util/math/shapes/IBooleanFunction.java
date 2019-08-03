package net.minecraft.util.math.shapes;

public interface IBooleanFunction {
   IBooleanFunction field_223230_a_ = (p_223272_0_, p_223272_1_) -> {
      return false;
   };
   IBooleanFunction field_223231_b_ = (p_223271_0_, p_223271_1_) -> {
      return !p_223271_0_ && !p_223271_1_;
   };
   IBooleanFunction field_223232_c_ = (p_223270_0_, p_223270_1_) -> {
      return p_223270_1_ && !p_223270_0_;
   };
   IBooleanFunction field_223233_d_ = (p_223269_0_, p_223269_1_) -> {
      return !p_223269_0_;
   };
   IBooleanFunction field_223234_e_ = (p_223268_0_, p_223268_1_) -> {
      return p_223268_0_ && !p_223268_1_;
   };
   IBooleanFunction field_223235_f_ = (p_223267_0_, p_223267_1_) -> {
      return !p_223267_1_;
   };
   IBooleanFunction field_223236_g_ = (p_223266_0_, p_223266_1_) -> {
      return p_223266_0_ != p_223266_1_;
   };
   IBooleanFunction field_223237_h_ = (p_223265_0_, p_223265_1_) -> {
      return !p_223265_0_ || !p_223265_1_;
   };
   IBooleanFunction field_223238_i_ = (p_223264_0_, p_223264_1_) -> {
      return p_223264_0_ && p_223264_1_;
   };
   IBooleanFunction field_223239_j_ = (p_223263_0_, p_223263_1_) -> {
      return p_223263_0_ == p_223263_1_;
   };
   IBooleanFunction field_223240_k_ = (p_223262_0_, p_223262_1_) -> {
      return p_223262_1_;
   };
   IBooleanFunction field_223241_l_ = (p_223261_0_, p_223261_1_) -> {
      return !p_223261_0_ || p_223261_1_;
   };
   IBooleanFunction field_223242_m_ = (p_223260_0_, p_223260_1_) -> {
      return p_223260_0_;
   };
   IBooleanFunction field_223243_n_ = (p_223259_0_, p_223259_1_) -> {
      return p_223259_0_ || !p_223259_1_;
   };
   IBooleanFunction field_223244_o_ = (p_223258_0_, p_223258_1_) -> {
      return p_223258_0_ || p_223258_1_;
   };
   IBooleanFunction field_223245_p_ = (p_223257_0_, p_223257_1_) -> {
      return true;
   };

   boolean apply(boolean p_apply_1_, boolean p_apply_2_);
}