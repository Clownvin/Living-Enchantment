package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class StringNBT implements INBT {
   private String data;

   public StringNBT() {
      this("");
   }

   public StringNBT(String data) {
      Objects.requireNonNull(data, "Null string not allowed");
      this.data = data;
   }

   /**
    * Write the actual data contents of the tag, implemented in NBT extension classes
    */
   public void write(DataOutput output) throws IOException {
      output.writeUTF(this.data);
   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(288L);
      this.data = input.readUTF();
      NBTSizeTracker.readUTF(sizeTracker, this.data);  // Forge: Correctly read String length including header.
   }

   /**
    * Gets the type byte for the tag.
    */
   public byte getId() {
      return 8;
   }

   public String toString() {
      return quoteAndEscape(this.data);
   }

   /**
    * Creates a clone of the tag.
    */
   public StringNBT copy() {
      return new StringNBT(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof StringNBT && Objects.equals(this.data, ((StringNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public String getString() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      String s = quoteAndEscape(this.data);
      String s1 = s.substring(0, 1);
      ITextComponent itextcomponent = (new StringTextComponent(s.substring(1, s.length() - 1))).applyTextStyle(SYNTAX_HIGHLIGHTING_STRING);
      return (new StringTextComponent(s1)).appendSibling(itextcomponent).appendText(s1);
   }

   public static String quoteAndEscape(String p_197654_0_) {
      StringBuilder stringbuilder = new StringBuilder(" ");
      char c0 = 0;

      for(int i = 0; i < p_197654_0_.length(); ++i) {
         char c1 = p_197654_0_.charAt(i);
         if (c1 == '\\') {
            stringbuilder.append('\\');
         } else if (c1 == '"' || c1 == '\'') {
            if (c0 == 0) {
               c0 = (char)(c1 == '"' ? 39 : 34);
            }

            if (c0 == c1) {
               stringbuilder.append('\\');
            }
         }

         stringbuilder.append(c1);
      }

      if (c0 == 0) {
         c0 = '"';
      }

      stringbuilder.setCharAt(0, c0);
      stringbuilder.append(c0);
      return stringbuilder.toString();
   }
}