package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoomScreen extends ContainerScreen<LoomContainer> {
   private static final ResourceLocation field_214113_k = new ResourceLocation("textures/gui/container/loom.png");
   private static final int field_214114_l = (BannerPattern.field_222480_O - 5 - 1 + 4 - 1) / 4;
   private static final DyeColor field_214115_m = DyeColor.GRAY;
   private static final DyeColor field_214116_n = DyeColor.WHITE;
   private static final List<DyeColor> field_214117_o = Lists.newArrayList(field_214115_m, field_214116_n);
   private ResourceLocation field_214118_p;
   private ItemStack field_214119_q = ItemStack.EMPTY;
   private ItemStack field_214120_r = ItemStack.EMPTY;
   private ItemStack field_214121_s = ItemStack.EMPTY;
   private final ResourceLocation[] field_214122_t = new ResourceLocation[BannerPattern.field_222480_O];
   private boolean field_214123_u;
   private boolean field_214124_v;
   private boolean field_214125_w;
   private float field_214126_x;
   private boolean field_214127_y;
   private int field_214128_z = 1;
   private int field_214112_A = 1;

   public LoomScreen(LoomContainer p_i51081_1_, PlayerInventory p_i51081_2_, ITextComponent p_i51081_3_) {
      super(p_i51081_1_, p_i51081_2_, p_i51081_3_);
      p_i51081_1_.func_217020_a(this::func_214111_b);
   }

   public void tick() {
      super.tick();
      if (this.field_214112_A < BannerPattern.field_222480_O) {
         BannerPattern bannerpattern = BannerPattern.values()[this.field_214112_A];
         String s = "b" + field_214115_m.getId();
         String s1 = bannerpattern.getHashname() + field_214116_n.getId();
         this.field_214122_t[this.field_214112_A] = BannerTextures.BANNER_DESIGNS.getResourceLocation(s + s1, Lists.newArrayList(BannerPattern.BASE, bannerpattern), field_214117_o);
         ++this.field_214112_A;
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 4.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.renderBackground();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(field_214113_k);
      int i = this.guiLeft;
      int j = this.guiTop;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
      Slot slot = this.container.func_217024_f();
      Slot slot1 = this.container.func_217022_g();
      Slot slot2 = this.container.func_217025_h();
      Slot slot3 = this.container.func_217026_i();
      if (!slot.getHasStack()) {
         this.blit(i + slot.xPos, j + slot.yPos, this.xSize, 0, 16, 16);
      }

      if (!slot1.getHasStack()) {
         this.blit(i + slot1.xPos, j + slot1.yPos, this.xSize + 16, 0, 16, 16);
      }

      if (!slot2.getHasStack()) {
         this.blit(i + slot2.xPos, j + slot2.yPos, this.xSize + 32, 0, 16, 16);
      }

      int k = (int)(41.0F * this.field_214126_x);
      this.blit(i + 119, j + 13 + k, 232 + (this.field_214123_u ? 0 : 12), 0, 12, 15);
      if (this.field_214118_p != null && !this.field_214125_w) {
         this.minecraft.getTextureManager().bindTexture(this.field_214118_p);
         blit(i + 141, j + 8, 20, 40, 1.0F, 1.0F, 20, 40, 64, 64);
      } else if (this.field_214125_w) {
         this.blit(i + slot3.xPos - 2, j + slot3.yPos - 2, this.xSize, 17, 17, 16);
      }

      if (this.field_214123_u) {
         int l = i + 60;
         int i1 = j + 13;
         int j1 = this.field_214128_z + 16;

         for(int k1 = this.field_214128_z; k1 < j1 && k1 < this.field_214122_t.length - 5; ++k1) {
            int l1 = k1 - this.field_214128_z;
            int i2 = l + l1 % 4 * 14;
            int j2 = i1 + l1 / 4 * 14;
            this.minecraft.getTextureManager().bindTexture(field_214113_k);
            int k2 = this.ySize;
            if (k1 == this.container.func_217023_e()) {
               k2 += 14;
            } else if (mouseX >= i2 && mouseY >= j2 && mouseX < i2 + 14 && mouseY < j2 + 14) {
               k2 += 28;
            }

            this.blit(i2, j2, 0, k2, 14, 14);
            if (this.field_214122_t[k1] != null) {
               this.minecraft.getTextureManager().bindTexture(this.field_214122_t[k1]);
               blit(i2 + 4, j2 + 2, 5, 10, 1.0F, 1.0F, 20, 40, 64, 64);
            }
         }
      } else if (this.field_214124_v) {
         int l2 = i + 60;
         int i3 = j + 13;
         this.minecraft.getTextureManager().bindTexture(field_214113_k);
         this.blit(l2, i3, 0, this.ySize, 14, 14);
         int j3 = this.container.func_217023_e();
         if (this.field_214122_t[j3] != null) {
            this.minecraft.getTextureManager().bindTexture(this.field_214122_t[j3]);
            blit(l2 + 4, i3 + 2, 5, 10, 1.0F, 1.0F, 20, 40, 64, 64);
         }
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.field_214127_y = false;
      if (this.field_214123_u) {
         int i = this.guiLeft + 60;
         int j = this.guiTop + 13;
         int k = this.field_214128_z + 16;

         for(int l = this.field_214128_z; l < k; ++l) {
            int i1 = l - this.field_214128_z;
            double d0 = p_mouseClicked_1_ - (double)(i + i1 % 4 * 14);
            double d1 = p_mouseClicked_3_ - (double)(j + i1 / 4 * 14);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 14.0D && d1 < 14.0D && this.container.enchantItem(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
               this.minecraft.playerController.sendEnchantPacket((this.container).windowId, l);
               return true;
            }
         }

         i = this.guiLeft + 119;
         j = this.guiTop + 9;
         if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ < (double)(i + 12) && p_mouseClicked_3_ >= (double)j && p_mouseClicked_3_ < (double)(j + 56)) {
            this.field_214127_y = true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.field_214127_y && this.field_214123_u) {
         int i = this.guiTop + 13;
         int j = i + 56;
         this.field_214126_x = ((float)p_mouseDragged_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
         int k = field_214114_l - 4;
         int l = (int)((double)(this.field_214126_x * (float)k) + 0.5D);
         if (l < 0) {
            l = 0;
         }

         this.field_214128_z = 1 + l * 4;
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (this.field_214123_u) {
         int i = field_214114_l - 4;
         this.field_214126_x = (float)((double)this.field_214126_x - p_mouseScrolled_5_ / (double)i);
         this.field_214126_x = MathHelper.clamp(this.field_214126_x, 0.0F, 1.0F);
         this.field_214128_z = 1 + (int)((double)(this.field_214126_x * (float)i) + 0.5D) * 4;
      }

      return true;
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      return p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
   }

   private void func_214111_b() {
      ItemStack itemstack = this.container.func_217026_i().getStack();
      if (itemstack.isEmpty()) {
         this.field_214118_p = null;
      } else {
         BannerTileEntity bannertileentity = new BannerTileEntity();
         bannertileentity.loadFromItemStack(itemstack, ((BannerItem)itemstack.getItem()).getColor());
         this.field_214118_p = BannerTextures.BANNER_DESIGNS.getResourceLocation(bannertileentity.getPatternResourceLocation(), bannertileentity.getPatternList(), bannertileentity.getColorList());
      }

      ItemStack itemstack3 = this.container.func_217024_f().getStack();
      ItemStack itemstack1 = this.container.func_217022_g().getStack();
      ItemStack itemstack2 = this.container.func_217025_h().getStack();
      CompoundNBT compoundnbt = itemstack3.getOrCreateChildTag("BlockEntityTag");
      this.field_214125_w = compoundnbt.contains("Patterns", 9) && !itemstack3.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;
      if (this.field_214125_w) {
         this.field_214118_p = null;
      }

      if (!ItemStack.areItemStacksEqual(itemstack3, this.field_214119_q) || !ItemStack.areItemStacksEqual(itemstack1, this.field_214120_r) || !ItemStack.areItemStacksEqual(itemstack2, this.field_214121_s)) {
         this.field_214123_u = !itemstack3.isEmpty() && !itemstack1.isEmpty() && itemstack2.isEmpty() && !this.field_214125_w;
         this.field_214124_v = !this.field_214125_w && !itemstack2.isEmpty() && !itemstack3.isEmpty() && !itemstack1.isEmpty();
      }

      this.field_214119_q = itemstack3.copy();
      this.field_214120_r = itemstack1.copy();
      this.field_214121_s = itemstack2.copy();
   }
}