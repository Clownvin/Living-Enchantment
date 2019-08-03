package net.minecraft.data;

import com.google.common.base.Charsets;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.gen.feature.template.Template;
import org.apache.commons.io.IOUtils;

public class StructureValidator implements IDataProvider {
   private final DataGenerator field_218453_b;

   public StructureValidator(DataGenerator p_i50785_1_) {
      this.field_218453_b = p_i50785_1_;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) throws IOException {
      for(Path path : this.field_218453_b.getInputFolders()) {
         Path path1 = path.resolve("data/minecraft/structures/");
         if (Files.isDirectory(path1)) {
            func_218446_a(DataFixesManager.getDataFixer(), path1);
         }
      }

   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Structure validator";
   }

   private static void func_218446_a(DataFixer p_218446_0_, Path p_218446_1_) throws IOException {
      try (Stream<Path> stream = Files.walk(p_218446_1_)) {
         stream.forEach((p_218449_1_) -> {
            if (Files.isRegularFile(p_218449_1_)) {
               func_218447_b(p_218446_0_, p_218449_1_);
            }

         });
      }

   }

   private static void func_218447_b(DataFixer p_218447_0_, Path p_218447_1_) {
      try {
         String s = p_218447_1_.getFileName().toString();
         if (s.endsWith(".snbt")) {
            func_218450_c(p_218447_0_, p_218447_1_);
         } else {
            if (!s.endsWith(".nbt")) {
               throw new IllegalArgumentException("Unrecognized format of file");
            }

            func_218451_d(p_218447_0_, p_218447_1_);
         }

      } catch (Exception exception) {
         throw new StructureValidator.ValidationException(p_218447_1_, exception);
      }
   }

   private static void func_218450_c(DataFixer p_218450_0_, Path p_218450_1_) throws Exception {
      CompoundNBT compoundnbt;
      try (InputStream inputstream = Files.newInputStream(p_218450_1_)) {
         String s = IOUtils.toString(inputstream, Charsets.UTF_8);
         compoundnbt = JsonToNBT.getTagFromJson(s);
      }

      func_218452_a(p_218450_0_, func_218448_a(compoundnbt));
   }

   private static void func_218451_d(DataFixer p_218451_0_, Path p_218451_1_) throws Exception {
      CompoundNBT compoundnbt;
      try (InputStream inputstream = Files.newInputStream(p_218451_1_)) {
         compoundnbt = CompressedStreamTools.readCompressed(inputstream);
      }

      func_218452_a(p_218451_0_, func_218448_a(compoundnbt));
   }

   private static CompoundNBT func_218448_a(CompoundNBT p_218448_0_) {
      if (!p_218448_0_.contains("DataVersion", 99)) {
         p_218448_0_.putInt("DataVersion", 500);
      }

      return p_218448_0_;
   }

   private static CompoundNBT func_218452_a(DataFixer p_218452_0_, CompoundNBT p_218452_1_) {
      Template template = new Template();
      template.read(NBTUtil.update(p_218452_0_, DefaultTypeReferences.STRUCTURE, p_218452_1_, p_218452_1_.getInt("DataVersion")));
      return template.writeToNBT(new CompoundNBT());
   }

   static class ValidationException extends RuntimeException {
      public ValidationException(Path p_i50003_1_, Throwable p_i50003_2_) {
         super("Failed to process file: " + p_i50003_1_.toAbsolutePath().toString(), p_i50003_2_);
      }
   }
}