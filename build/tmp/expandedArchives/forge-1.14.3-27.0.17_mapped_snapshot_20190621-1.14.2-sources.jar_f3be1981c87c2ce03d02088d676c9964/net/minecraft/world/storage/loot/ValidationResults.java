package net.minecraft.world.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;

public class ValidationResults {
   private final Multimap<String, String> problems;
   private final Supplier<String> pathSupplier;
   private String cachedPath;

   public ValidationResults() {
      this(HashMultimap.create(), () -> {
         return "";
      });
   }

   public ValidationResults(Multimap<String, String> p_i51264_1_, Supplier<String> p_i51264_2_) {
      this.problems = p_i51264_1_;
      this.pathSupplier = p_i51264_2_;
   }

   private String getPath() {
      if (this.cachedPath == null) {
         this.cachedPath = this.pathSupplier.get();
      }

      return this.cachedPath;
   }

   public void addProblem(String p_216105_1_) {
      this.problems.put(this.getPath(), p_216105_1_);
   }

   public ValidationResults descend(String p_216108_1_) {
      return new ValidationResults(this.problems, () -> {
         return this.getPath() + p_216108_1_;
      });
   }

   public Multimap<String, String> getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }
}