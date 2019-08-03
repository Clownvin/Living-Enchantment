package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileUtil;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormat {
   private static final Logger field_215785_a = LogManager.getLogger();
   private static final DateTimeFormatter BACKUP_DATE_FORMAT = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   private final Path field_215786_c;
   private final Path field_215787_d;
   private final DataFixer field_215788_e;

   public SaveFormat(Path p_i51277_1_, Path p_i51277_2_, DataFixer p_i51277_3_) {
      this.field_215788_e = p_i51277_3_;

      try {
         Files.createDirectories(Files.exists(p_i51277_1_) ? p_i51277_1_.toRealPath() : p_i51277_1_);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      this.field_215786_c = p_i51277_1_;
      this.field_215787_d = p_i51277_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return "Anvil";
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getSaveList() throws AnvilConverterException {
      if (!Files.isDirectory(this.field_215786_c)) {
         throw new AnvilConverterException((new TranslationTextComponent("selectWorld.load_folder_access")).getString());
      } else {
         List<WorldSummary> list = Lists.newArrayList();
         File[] afile = this.field_215786_c.toFile().listFiles();

         for(File file1 : afile) {
            if (file1.isDirectory()) {
               String s = file1.getName();
               WorldInfo worldinfo = this.getWorldInfo(s);
               if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133)) {
                  boolean flag = worldinfo.getSaveVersion() != this.func_215782_e();
                  String s1 = worldinfo.getWorldName();
                  if (StringUtils.isEmpty(s1)) {
                     s1 = s;
                  }

                  long i = 0L;
                  list.add(new WorldSummary(worldinfo, s, s1, 0L, flag));
               }
            }
         }

         return list;
      }
   }

   private int func_215782_e() {
      return 19133;
   }

   public SaveHandler getSaveLoader(String saveName, @Nullable MinecraftServer server) {
      return func_215783_a(this.field_215786_c, this.field_215788_e, saveName, server);
   }

   protected static SaveHandler func_215783_a(Path p_215783_0_, DataFixer p_215783_1_, String p_215783_2_, @Nullable MinecraftServer p_215783_3_) {
      return new SaveHandler(p_215783_0_.toFile(), p_215783_2_, p_215783_3_, p_215783_1_);
   }

   /**
    * gets if the map is old chunk saving (true) or McRegion (false)
    */
   public boolean isOldMapFormat(String saveName) {
      WorldInfo worldinfo = this.getWorldInfo(saveName);
      return worldinfo != null && worldinfo.getSaveVersion() != this.func_215782_e();
   }

   /**
    * converts the map to mcRegion
    */
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      return AnvilSaveConverter.func_215792_a(this.field_215786_c, this.field_215788_e, filename, progressCallback);
   }

   /**
    * Returns the world's WorldInfo object
    */
   @Nullable
   public WorldInfo getWorldInfo(String saveName) {
      return func_215779_a(this.field_215786_c, this.field_215788_e, saveName);
   }

   @Nullable
   protected static WorldInfo func_215779_a(Path p_215779_0_, DataFixer p_215779_1_, String p_215779_2_) {
      File file1 = new File(p_215779_0_.toFile(), p_215779_2_);
      if (!file1.exists()) {
         return null;
      } else {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            WorldInfo worldinfo = func_215780_a(file2, p_215779_1_);
            if (worldinfo != null) {
               return worldinfo;
            }
         }

         file2 = new File(file1, "level.dat_old");
         return file2.exists() ? func_215780_a(file2, p_215779_1_) : null;
      }
   }

   @Nullable
   public static WorldInfo func_215780_a(File p_215780_0_, DataFixer p_215780_1_) {
       return getWorldData(p_215780_0_, p_215780_1_, null);
   }

   @Nullable
   public static WorldInfo getWorldData(File p_215780_0_, DataFixer p_215780_1_, @Nullable SaveHandler saveHandler) {
      try {
         CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(new FileInputStream(p_215780_0_));
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
         CompoundNBT compoundnbt2 = compoundnbt1.contains("Player", 10) ? compoundnbt1.getCompound("Player") : null;
         compoundnbt1.remove("Player");
         int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
         WorldInfo ret = new WorldInfo(NBTUtil.update(p_215780_1_, DefaultTypeReferences.LEVEL, compoundnbt1, i), p_215780_1_, i, compoundnbt2);
         if (saveHandler != null)
            net.minecraftforge.fml.WorldPersistenceHooks.handleWorldDataLoad(saveHandler, ret, compoundnbt);
         return ret;
      } catch (net.minecraftforge.fml.StartupQuery.AbortedException e) {
          throw e;
      } catch (Exception exception) {
         field_215785_a.error("Exception reading {}", p_215780_0_, exception);
         return null;
      }
   }

   /**
    * Renames the world by storing the new name in level.dat. It does *not* rename the directory containing the world
    * data.
    */
   @OnlyIn(Dist.CLIENT)
   public void renameWorld(String dirName, String newName) {
      File file1 = new File(this.field_215786_c.toFile(), dirName);
      if (file1.exists()) {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            try {
               CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(new FileInputStream(file2));
               CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
               compoundnbt1.putString("LevelName", newName);
               CompressedStreamTools.writeCompressed(compoundnbt, new FileOutputStream(file2));
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNewLevelIdAcceptable(String saveName) {
      try {
         Path path = this.field_215786_c.resolve(saveName);
         Files.createDirectory(path);
         Files.deleteIfExists(path);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   /**
    * Deletes a world directory.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean deleteWorldDirectory(String saveName) {
      File file1 = new File(this.field_215786_c.toFile(), saveName);
      if (!file1.exists()) {
         return true;
      } else {
         field_215785_a.info("Deleting level {}", (Object)saveName);

         for(int i = 1; i <= 5; ++i) {
            field_215785_a.info("Attempt {}...", (int)i);
            if (func_215784_a(file1.listFiles())) {
               break;
            }

            field_215785_a.warn("Unsuccessful in deleting contents.");
            if (i < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
                  ;
               }
            }
         }

         return file1.delete();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static boolean func_215784_a(File[] p_215784_0_) {
      for(File file1 : p_215784_0_) {
         field_215785_a.debug("Deleting {}", (Object)file1);
         if (file1.isDirectory() && !func_215784_a(file1.listFiles())) {
            field_215785_a.warn("Couldn't delete directory {}", (Object)file1);
            return false;
         }

         if (!file1.delete()) {
            field_215785_a.warn("Couldn't delete file {}", (Object)file1);
            return false;
         }
      }

      return true;
   }

   /**
    * Return whether the given world can be loaded.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean canLoadWorld(String saveName) {
      return Files.isDirectory(this.field_215786_c.resolve(saveName));
   }

   @OnlyIn(Dist.CLIENT)
   public Path func_215781_c() {
      return this.field_215786_c;
   }

   /**
    * Gets a file within the given world.
    */
   public File getFile(String saveName, String filePath) {
      return this.field_215786_c.resolve(saveName).resolve(filePath).toFile();
   }

   /**
    * Gets the folder for the given world.
    */
   @OnlyIn(Dist.CLIENT)
   private Path getWorldFolder(String saveName) {
      return this.field_215786_c.resolve(saveName);
   }

   /**
    * Gets the folder where backups are stored
    */
   @OnlyIn(Dist.CLIENT)
   public Path getBackupsFolder() {
      return this.field_215787_d;
   }

   /**
    * Creates a backup of the given world.
    *  
    * @return The size of the created backup in bytes
    */
   @OnlyIn(Dist.CLIENT)
   public long createBackup(String worldName) throws IOException {
      final Path path = this.getWorldFolder(worldName);
      String s = LocalDateTime.now().format(BACKUP_DATE_FORMAT) + "_" + worldName;
      Path path1 = this.getBackupsFolder();

      try {
         Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      Path path2 = path1.resolve(FileUtil.func_214992_a(path1, s, ".zip"));

      try (final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2)))) {
         final Path path3 = Paths.get(worldName);
         Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException {
               String s1 = path3.resolve(path.relativize(p_visitFile_1_)).toString().replace('\\', '/');
               ZipEntry zipentry = new ZipEntry(s1);
               zipoutputstream.putNextEntry(zipentry);
               com.google.common.io.Files.asByteSource(p_visitFile_1_.toFile()).copyTo(zipoutputstream);
               zipoutputstream.closeEntry();
               return FileVisitResult.CONTINUE;
            }
         });
      }

      return Files.size(path2);
   }
}