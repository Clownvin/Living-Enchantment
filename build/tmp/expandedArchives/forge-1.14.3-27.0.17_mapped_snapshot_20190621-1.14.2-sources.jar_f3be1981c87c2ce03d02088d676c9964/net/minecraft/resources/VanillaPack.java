package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements IResourcePack {
   public static Path basePath;
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class<?> baseClass;
   private static final Map<ResourcePackType, FileSystem> field_217810_e = Util.make(Maps.newHashMap(), (p_217809_0_) -> {
      synchronized(VanillaPack.class) {
         for(ResourcePackType resourcepacktype : ResourcePackType.values()) {
            URL url = VanillaPack.class.getResource("/" + resourcepacktype.getDirectoryName() + "/.mcassetsroot");

            try {
               URI uri = url.toURI();
               if ("jar".equals(uri.getScheme())) {
                  FileSystem filesystem;
                  try {
                     filesystem = FileSystems.getFileSystem(uri);
                  } catch (FileSystemNotFoundException var11) {
                     filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                  }

                  p_217809_0_.put(resourcepacktype, filesystem);
               }
            } catch (IOException | URISyntaxException urisyntaxexception) {
               LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)urisyntaxexception);
            }
         }

      }
   });
   public final Set<String> resourceNamespaces;

   public VanillaPack(String... resourceNamespacesIn) {
      this.resourceNamespaces = ImmutableSet.copyOf(resourceNamespacesIn);
   }

   public InputStream getRootResourceStream(String fileName) throws IOException {
      if (!fileName.contains("/") && !fileName.contains("\\")) {
         if (basePath != null) {
            Path path = basePath.resolve(fileName);
            if (Files.exists(path)) {
               return Files.newInputStream(path);
            }
         }

         return this.getInputStreamVanilla(fileName);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
      InputStream inputstream = this.getInputStreamVanilla(type, location);
      if (inputstream != null) {
         return inputstream;
      } else {
         throw new FileNotFoundException(location.getPath());
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter) {
      Set<ResourceLocation> set = Sets.newHashSet();
      if (basePath != null) {
         try {
            set.addAll(this.getResourceLocations(maxDepth, "minecraft", basePath.resolve(type.getDirectoryName()).resolve("minecraft"), pathIn, filter));
         } catch (IOException var14) {
            ;
         }

         if (type == ResourcePackType.CLIENT_RESOURCES) {
            Enumeration<URL> enumeration = null;

            try {
               enumeration = baseClass.getClassLoader().getResources(type.getDirectoryName() + "/minecraft");
            } catch (IOException var13) {
               ;
            }

            while(enumeration != null && enumeration.hasMoreElements()) {
               try {
                  URI uri = enumeration.nextElement().toURI();
                  if ("file".equals(uri.getScheme())) {
                     set.addAll(this.getResourceLocations(maxDepth, "minecraft", Paths.get(uri), pathIn, filter));
                  }
               } catch (IOException | URISyntaxException var12) {
                  ;
               }
            }
         }
      }

      try {
         URL url1 = VanillaPack.class.getResource("/" + type.getDirectoryName() + "/.mcassetsroot");
         if (url1 == null) {
            LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
            return set;
         }

         URI uri1 = url1.toURI();
         if ("file".equals(uri1.getScheme())) {
            URL url = new URL(url1.toString().substring(0, url1.toString().length() - ".mcassetsroot".length()) + "minecraft");
            if (url == null) {
               return set;
            }

            Path path = Paths.get(url.toURI());
            set.addAll(this.getResourceLocations(maxDepth, "minecraft", path, pathIn, filter));
         } else if ("jar".equals(uri1.getScheme())) {
            Path path1 = field_217810_e.get(type).getPath("/" + type.getDirectoryName() + "/minecraft");
            set.addAll(this.getResourceLocations(maxDepth, "minecraft", path1, pathIn, filter));
         } else {
            LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", (Object)uri1);
         }
      } catch (NoSuchFileException | FileNotFoundException var10) {
         ;
      } catch (IOException | URISyntaxException urisyntaxexception) {
         LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)urisyntaxexception);
      }

      return set;
   }

   private Collection<ResourceLocation> getResourceLocations(int p_195781_1_, String p_195781_2_, Path p_195781_3_, String p_195781_4_, Predicate<String> p_195781_5_) throws IOException {
      List<ResourceLocation> list = Lists.newArrayList();
      Iterator<Path> iterator = Files.walk(p_195781_3_.resolve(p_195781_4_), p_195781_1_).iterator();

      while(iterator.hasNext()) {
         Path path = iterator.next();
         if (!path.endsWith(".mcmeta") && Files.isRegularFile(path) && p_195781_5_.test(path.getFileName().toString())) {
            list.add(new ResourceLocation(p_195781_2_, p_195781_3_.relativize(path).toString().replaceAll("\\\\", "/")));
         }
      }

      return list;
   }

   @Nullable
   protected InputStream getInputStreamVanilla(ResourcePackType type, ResourceLocation location) {
      String s = func_223458_d(type, location);
      if (basePath != null) {
         Path path = basePath.resolve(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());
         if (Files.exists(path)) {
            try {
               return Files.newInputStream(path);
            } catch (IOException var7) {
               ;
            }
         }
      }

      try {
         URL url = VanillaPack.class.getResource(s);
         return func_223459_a(s, url) ? getExtraInputStream(type, s) : null;
      } catch (IOException var6) {
         return VanillaPack.class.getResourceAsStream(s);
      }
   }

   private static String func_223458_d(ResourcePackType p_223458_0_, ResourceLocation p_223458_1_) {
      return "/" + p_223458_0_.getDirectoryName() + "/" + p_223458_1_.getNamespace() + "/" + p_223458_1_.getPath();
   }

   private static boolean func_223459_a(String p_223459_0_, @Nullable URL p_223459_1_) throws IOException {
      return p_223459_1_ != null && (p_223459_1_.getProtocol().equals("jar") || FolderPack.validatePath(new File(p_223459_1_.getFile()), p_223459_0_));
   }

   @Nullable
   protected InputStream getInputStreamVanilla(String pathIn) {
      return getExtraInputStream(ResourcePackType.SERVER_DATA, "/" + pathIn);
   }

   public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
      String s = func_223458_d(type, location);
      if (basePath != null) {
         Path path = basePath.resolve(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());
         if (Files.exists(path)) {
            return true;
         }
      }

      try {
         URL url = VanillaPack.class.getResource(s);
         return func_223459_a(s, url);
      } catch (IOException var5) {
         return false;
      }
   }

   public Set<String> getResourceNamespaces(ResourcePackType type) {
      return this.resourceNamespaces;
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
      try (InputStream inputstream = this.getRootResourceStream("pack.mcmeta")) {
         Object object = ResourcePack.<T>getResourceMetadata(deserializer, inputstream);
         return (T)object;
      } catch (FileNotFoundException | RuntimeException var16) {
         return (T)null;
      }
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }

   //Vanilla used to just grab from the classpath, this breaks dev environments, and Forge runtime
   //as forge ships vanilla assets in an 'extra' jar with no classes.
   //So find that extra jar using the .mcassetsroot marker.
   private InputStream getExtraInputStream(ResourcePackType type, String resource) {
      try {
         FileSystem fs = field_217810_e.get(type);
         if (fs != null)
            return Files.newInputStream(fs.getPath(resource));
         return VanillaPack.class.getResourceAsStream(resource);
      } catch (IOException e) {
         return VanillaPack.class.getResourceAsStream(resource);
      }
   }
}