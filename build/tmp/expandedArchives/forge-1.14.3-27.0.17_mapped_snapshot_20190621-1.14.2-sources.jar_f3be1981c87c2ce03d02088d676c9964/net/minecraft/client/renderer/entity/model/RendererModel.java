package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererModel {
   public float textureWidth = 64.0F;
   public float textureHeight = 32.0F;
   private int textureOffsetX;
   private int textureOffsetY;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   public float rotateAngleX;
   public float rotateAngleY;
   public float rotateAngleZ;
   private boolean compiled;
   private int displayList;
   public boolean mirror;
   public boolean showModel = true;
   public boolean isHidden;
   public final List<ModelBox> cubeList = Lists.newArrayList();
   public List<RendererModel> childModels;
   public final String boxName;
   public float offsetX;
   public float offsetY;
   public float offsetZ;

   public RendererModel(Model model, String boxNameIn) {
      model.boxList.add(this);
      this.boxName = boxNameIn;
      this.setTextureSize(model.textureWidth, model.textureHeight);
   }

   public RendererModel(Model model) {
      this(model, (String)null);
   }

   public RendererModel(Model model, int texOffX, int texOffY) {
      this(model);
      this.setTextureOffset(texOffX, texOffY);
   }

   public void func_217177_a(RendererModel p_217177_1_) {
      this.rotateAngleX = p_217177_1_.rotateAngleX;
      this.rotateAngleY = p_217177_1_.rotateAngleY;
      this.rotateAngleZ = p_217177_1_.rotateAngleZ;
      this.rotationPointX = p_217177_1_.rotationPointX;
      this.rotationPointY = p_217177_1_.rotationPointY;
      this.rotationPointZ = p_217177_1_.rotationPointZ;
   }

   /**
    * Sets the current box's rotation points and rotation angles to another box.
    */
   public void addChild(RendererModel renderer) {
      if (this.childModels == null) {
         this.childModels = Lists.newArrayList();
      }

      this.childModels.add(renderer);
   }

   public void func_217179_c(RendererModel p_217179_1_) {
      if (this.childModels != null) {
         this.childModels.remove(p_217179_1_);
      }

   }

   public RendererModel setTextureOffset(int x, int y) {
      this.textureOffsetX = x;
      this.textureOffsetY = y;
      return this;
   }

   public RendererModel func_217178_a(String p_217178_1_, float p_217178_2_, float p_217178_3_, float p_217178_4_, int p_217178_5_, int p_217178_6_, int p_217178_7_, float p_217178_8_, int p_217178_9_, int p_217178_10_) {
      p_217178_1_ = this.boxName + "." + p_217178_1_;
      this.setTextureOffset(p_217178_9_, p_217178_10_);
      this.cubeList.add((new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_217178_2_, p_217178_3_, p_217178_4_, p_217178_5_, p_217178_6_, p_217178_7_, p_217178_8_)).setBoxName(p_217178_1_));
      return this;
   }

   public RendererModel addBox(float offX, float offY, float offZ, int width, int height, int depth) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
      return this;
   }

   public RendererModel addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F, mirrored));
      return this;
   }

   /**
    * Creates a textured box.
    */
   public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor));
   }

   public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrorIn) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor, mirrorIn));
   }

   public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
      this.rotationPointX = rotationPointXIn;
      this.rotationPointY = rotationPointYIn;
      this.rotationPointZ = rotationPointZIn;
   }

   public void render(float scale) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(scale);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
               if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                  GlStateManager.callList(this.displayList);
                  if (this.childModels != null) {
                     for(int k = 0; k < this.childModels.size(); ++k) {
                        this.childModels.get(k).render(scale);
                     }
                  }
               } else {
                  GlStateManager.pushMatrix();
                  GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                  GlStateManager.callList(this.displayList);
                  if (this.childModels != null) {
                     for(int j = 0; j < this.childModels.size(); ++j) {
                        this.childModels.get(j).render(scale);
                     }
                  }

                  GlStateManager.popMatrix();
               }
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
               if (this.rotateAngleZ != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotateAngleY != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotateAngleX != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               GlStateManager.callList(this.displayList);
               if (this.childModels != null) {
                  for(int i = 0; i < this.childModels.size(); ++i) {
                     this.childModels.get(i).render(scale);
                  }
               }

               GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public void renderWithRotation(float scale) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(scale);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.callList(this.displayList);
            GlStateManager.popMatrix();
         }
      }
   }

   /**
    * Allows the changing of Angles after a box has been rendered
    */
   public void postRender(float scale) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(scale);
            }

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
               if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                  GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
               }
            } else {
               GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
               if (this.rotateAngleZ != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotateAngleY != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotateAngleX != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }
            }

         }
      }
   }

   /**
    * Compiles a GL display list for this model
    */
   private void compileDisplayList(float scale) {
      this.displayList = GLAllocation.generateDisplayLists(1);
      GlStateManager.newList(this.displayList, 4864);
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

      for(int i = 0; i < this.cubeList.size(); ++i) {
         this.cubeList.get(i).render(bufferbuilder, scale);
      }

      GlStateManager.endList();
      this.compiled = true;
   }

   /**
    * Returns the model renderer with the new texture parameters.
    */
   public RendererModel setTextureSize(int textureWidthIn, int textureHeightIn) {
      this.textureWidth = (float)textureWidthIn;
      this.textureHeight = (float)textureHeightIn;
      return this;
   }
}