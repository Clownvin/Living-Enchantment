package com.clownvin.livingenchantment.entity.item;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.LivingConfig;
import com.clownvin.livingenchantment.enchantment.EnchantmentLiving;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLivingXPOrb extends Entity {


    /**
     * A constantly increasing value that RenderXPOrb uses to control the colour shifting (Green / yellow)
     */
    public int xpColor;
    /**
     * The age of the XP orb in ticks.
     */
    public int xpOrbAge;
    public int delayBeforeCanPickup;
    /**
     * This is how much XP this orb has.
     */
    public double xpValue;
    /**
     * The health of this XP orb.
     */
    private int xpOrbHealth = 5;
    /**
     * The closest EntityPlayer to this orb.
     */
    private EntityPlayer closestPlayer;
    /**
     * Threshold color for tracking players
     */
    private int xpTargetColor;


    public EntityLivingXPOrb(World worldIn, double x, double y, double z, double expValue) {
        super(worldIn);
        this.setSize(0.5F, 0.5F);
        this.setPosition(x, y, z);
        this.rotationYaw = (float) (Math.random() * 360.0D);
        this.motionX = (0.5 - Math.random()) * 0.66;
        this.motionY = Math.random() * 0.33;
        this.motionZ = (0.5 - Math.random()) * 0.66;
        this.xpValue = expValue;
        this.xpColor = (int) (Math.random() * 255);
        float xpSplit = getXPSplit((float) expValue);
        if (this.xpValue > xpSplit) {
            world.spawnEntity(new EntityLivingXPOrb(world, x, y, z, xpSplit));
            this.xpValue -= xpSplit;
            //player.world.spawnEntity(new EntityLivingXPOrb(player.world, (double) event.getEntityLiving().getPosition().getX() + 0.5D, (double) event.getEntityLiving().getPosition().getY() + 0.5D, (double) event.getEntityLiving().getPosition().getZ() + 0.5D, (float) LivingConfig.getXPForLiving((EntityLiving) event.getEntityLiving())));
        }
    }

    public EntityLivingXPOrb(World worldIn) {
        super(worldIn);
    }

    /**
     * Get a fragment of the maximum experience points value for the supplied value of experience points value.
     */
    public static float getXPSplit(float expValue) {
        if (expValue >= 2477) {
            return 2477;
        } else if (expValue >= 1237) {
            return 1237;
        } else if (expValue >= 617) {
            return 617;
        } else if (expValue >= 307) {
            return 307;
        } else if (expValue >= 149) {
            return 149;
        } else if (expValue >= 73) {
            return 73;
        } else if (expValue >= 37) {
            return 37;
        } else if (expValue >= 17) {
            return 17;
        } else if (expValue >= 7) {
            return 7;
        } else {
            return expValue >= 3 ? 3 : 1;
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (player.world.isRemote || this.delayBeforeCanPickup != 0 || player.xpCooldown != 0) {
            return;
        }
        if ((player.getHeldItemMainhand().getItem() instanceof ItemBow || player.getHeldItemOffhand().getItem() instanceof ItemBow) && player.getItemInUseCount() > 0) {
            return;
        }
        ItemStack stack = EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, player);
        if (stack.isEmpty()) {
            return;
        }
        NBTTagCompound tag = LivingEnchantment.getEnchantmentNBTTag(stack);
        player.xpCooldown = 1;
        player.onItemPickup(this, 1);
        if (LivingConfig.general.xpShare)
            LivingEnchantment.addExp(player, this.xpValue);
        else
            LivingEnchantment.addExp(player, stack, tag, this.xpValue);
        this.setDead();
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), (float) LivingConfig.general.xpVolume, 0.85F + (float) (Math.random() * 0.3F));
    }

    @Override
    protected void entityInit() {
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        float f = 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightnessForRender();
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f * 15.0F * 16.0F);

        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.delayBeforeCanPickup > 0)
            --this.delayBeforeCanPickup;

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!this.hasNoGravity())
            this.motionY -= 0.029999999329447746D;

        if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
            this.motionY = 0.20000000298023224D;
            this.motionX = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.motionZ = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
        double d0 = 8.0D;

        if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
            if (this.closestPlayer == null || this.closestPlayer.getDistanceSq(this) > 64.0D) {
                this.closestPlayer = this.world.getClosestPlayerToEntity(this, 8.0D);
            }

            this.xpTargetColor = this.xpColor;
        }

        if (this.closestPlayer != null && (this.closestPlayer.isSpectator() || EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, this.closestPlayer).isEmpty())) {
            this.closestPlayer = null;
        }

        if (this.closestPlayer != null) {
            double d1 = (this.closestPlayer.posX - this.posX) / 8.0D;
            double d2 = (this.closestPlayer.posY + (double) this.closestPlayer.getEyeHeight() / 2.0D - this.posY) / 8.0D;
            double d3 = (this.closestPlayer.posZ - this.posZ) / 8.0D;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
            double d5 = 1.0D - d4;

            if (d5 > 0.0D) {
                d5 = d5 * d5;
                this.motionX += d1 / d4 * d5 * 0.1D;
                this.motionY += d2 / d4 * d5 * 0.1D;
                this.motionZ += d3 / d4 * d5 * 0.1D;
            }
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        float f = 0.98F;

        if (this.onGround) {
            BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
            net.minecraft.block.state.IBlockState underState = this.world.getBlockState(underPos);
            f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.98F;
        }

        this.motionX *= (double) f;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double) f;

        if (this.onGround) {
            this.motionY *= -0.8999999761581421D;
        }

        ++this.xpColor;
        ++this.xpOrbAge;

        if (this.xpOrbAge >= 6000) {
            this.setDead();
        }
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    @Override
    public boolean handleWaterMovement() {
        return this.world.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this);
    }

    /**
     * Will deal the specified amount of fire damage to the entity if the entity isn't immune to fire damage.
     */
    @Override
    protected void dealFireDamage(int amount) {
        this.attackEntityFrom(DamageSource.IN_FIRE, (float) amount);
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.world.isRemote || this.isDead) return false; //Forge: Fixes MC-53850
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else {
            this.markVelocityChanged();
            this.xpOrbHealth = (int) ((float) this.xpOrbHealth - amount);

            if (this.xpOrbHealth <= 0) {
                this.setDead();
            }

            return false;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (short) this.xpOrbHealth);
        compound.setShort("Age", (short) this.xpOrbAge);
        compound.setShort("Value", (short) this.xpValue);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.xpOrbHealth = compound.getShort("Health");
        this.xpOrbAge = compound.getShort("Age");
        this.xpValue = compound.getShort("Value");
    }

    /**
     * Returns a number from 1 to 10 based on how much XP this orb is worth. This is used by RenderXPOrb to determine
     * what texture to use.
     */
    @SideOnly(Side.CLIENT)
    public int getTextureByXP() {
        if (this.xpValue >= 2477) {
            return 10;
        } else if (this.xpValue >= 1237) {
            return 9;
        } else if (this.xpValue >= 617) {
            return 8;
        } else if (this.xpValue >= 307) {
            return 7;
        } else if (this.xpValue >= 149) {
            return 6;
        } else if (this.xpValue >= 73) {
            return 5;
        } else if (this.xpValue >= 37) {
            return 4;
        } else if (this.xpValue >= 17) {
            return 3;
        } else if (this.xpValue >= 7) {
            return 2;
        } else {
            return this.xpValue >= 3 ? 1 : 0;
        }
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }
}
