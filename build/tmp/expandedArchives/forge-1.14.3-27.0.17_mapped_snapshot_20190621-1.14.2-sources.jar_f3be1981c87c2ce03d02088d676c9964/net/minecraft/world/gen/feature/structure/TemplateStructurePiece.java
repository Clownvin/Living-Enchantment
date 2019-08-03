package net.minecraft.world.gen.feature.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger field_214825_d = LogManager.getLogger();
   protected Template template;
   protected PlacementSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(IStructurePieceType p_i51338_1_, int p_i51338_2_) {
      super(p_i51338_1_, p_i51338_2_);
   }

   public TemplateStructurePiece(IStructurePieceType p_i51339_1_, CompoundNBT p_i51339_2_) {
      super(p_i51339_1_, p_i51339_2_);
      this.templatePosition = new BlockPos(p_i51339_2_.getInt("TPX"), p_i51339_2_.getInt("TPY"), p_i51339_2_.getInt("TPZ"));
   }

   protected void setup(Template templateIn, BlockPos pos, PlacementSettings settings) {
      this.template = templateIn;
      this.setCoordBaseMode(Direction.NORTH);
      this.templatePosition = pos;
      this.placeSettings = settings;
      this.boundingBox = templateIn.func_215388_b(settings, pos);
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readAdditional(CompoundNBT tagCompound) {
      tagCompound.putInt("TPX", this.templatePosition.getX());
      tagCompound.putInt("TPY", this.templatePosition.getY());
      tagCompound.putInt("TPZ", this.templatePosition.getZ());
   }

   /**
    * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
    * end, it adds Fences...
    */
   public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
      this.placeSettings.setBoundingBox(structureBoundingBoxIn);
      this.boundingBox = this.template.func_215388_b(this.placeSettings, this.templatePosition);
      if (this.template.addBlocksToWorld(worldIn, this.templatePosition, this.placeSettings, 2)) {
         for(Template.BlockInfo template$blockinfo : this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK)) {
            if (template$blockinfo.nbt != null) {
               StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
               if (structuremode == StructureMode.DATA) {
                  this.handleDataMarker(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, worldIn, randomIn, structureBoundingBoxIn);
               }
            }
         }

         for(Template.BlockInfo template$blockinfo1 : this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.JIGSAW)) {
            if (template$blockinfo1.nbt != null) {
               String s = template$blockinfo1.nbt.getString("final_state");
               BlockStateParser blockstateparser = new BlockStateParser(new StringReader(s), false);
               BlockState blockstate = Blocks.AIR.getDefaultState();

               try {
                  blockstateparser.parse(true);
                  BlockState blockstate1 = blockstateparser.getState();
                  if (blockstate1 != null) {
                     blockstate = blockstate1;
                  } else {
                     field_214825_d.error("Error while parsing blockstate {} in jigsaw block @ {}", s, template$blockinfo1.pos);
                  }
               } catch (CommandSyntaxException var13) {
                  field_214825_d.error("Error while parsing blockstate {} in jigsaw block @ {}", s, template$blockinfo1.pos);
               }

               worldIn.setBlockState(template$blockinfo1.pos, blockstate, 3);
            }
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb);

   public void offset(int x, int y, int z) {
      super.offset(x, y, z);
      this.templatePosition = this.templatePosition.add(x, y, z);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}