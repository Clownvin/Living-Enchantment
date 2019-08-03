package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface IDataProvider {
   HashFunction HASH_FUNCTION = Hashing.sha1();

   /**
    * Performs this provider's action.
    */
   void act(DirectoryCache cache) throws IOException;

   /**
    * Gets a name for this provider, to use in logging.
    */
   String getName();

   static void func_218426_a(Gson p_218426_0_, DirectoryCache p_218426_1_, JsonElement p_218426_2_, Path p_218426_3_) throws IOException {
      String s = p_218426_0_.toJson(p_218426_2_);
      String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
      if (!Objects.equals(p_218426_1_.getPreviousHash(p_218426_3_), s1) || !Files.exists(p_218426_3_)) {
         Files.createDirectories(p_218426_3_.getParent());

         try (BufferedWriter bufferedwriter = Files.newBufferedWriter(p_218426_3_)) {
            bufferedwriter.write(s);
         }
      }

      p_218426_1_.func_208316_a(p_218426_3_, s1);
   }
}