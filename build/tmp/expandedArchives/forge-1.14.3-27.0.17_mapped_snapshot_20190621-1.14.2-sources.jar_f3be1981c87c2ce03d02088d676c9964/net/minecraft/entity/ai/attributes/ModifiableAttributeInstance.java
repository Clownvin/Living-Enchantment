package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final AbstractAttributeMap attributeMap;
   private final IAttribute genericAttribute;
   private final Map<AttributeModifier.Operation, Set<AttributeModifier>> mapByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
   private double baseValue;
   private boolean needsUpdate = true;
   private double cachedValue;

   public ModifiableAttributeInstance(AbstractAttributeMap attributeMapIn, IAttribute genericAttributeIn) {
      this.attributeMap = attributeMapIn;
      this.genericAttribute = genericAttributeIn;
      this.baseValue = genericAttributeIn.getDefaultValue();

      for(AttributeModifier.Operation attributemodifier$operation : AttributeModifier.Operation.values()) {
         this.mapByOperation.put(attributemodifier$operation, Sets.newHashSet());
      }

   }

   /**
    * Get the Attribute this is an instance of
    */
   public IAttribute getAttribute() {
      return this.genericAttribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double baseValue) {
      if (baseValue != this.getBaseValue()) {
         this.baseValue = baseValue;
         this.flagForUpdate();
      }
   }

   public Collection<AttributeModifier> func_220368_a(AttributeModifier.Operation p_220368_1_) {
      return this.mapByOperation.get(p_220368_1_);
   }

   public Collection<AttributeModifier> getModifiers() {
      Set<AttributeModifier> set = Sets.newHashSet();

      for(AttributeModifier.Operation attributemodifier$operation : AttributeModifier.Operation.values()) {
         set.addAll(this.func_220368_a(attributemodifier$operation));
      }

      return set;
   }

   /**
    * Returns attribute modifier, if any, by the given UUID
    */
   @Nullable
   public AttributeModifier getModifier(UUID uuid) {
      return this.mapByUUID.get(uuid);
   }

   public boolean hasModifier(AttributeModifier modifier) {
      return this.mapByUUID.get(modifier.getID()) != null;
   }

   public void applyModifier(AttributeModifier modifier) {
      if (this.getModifier(modifier.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Set<AttributeModifier> set = this.mapByName.computeIfAbsent(modifier.getName(), (p_220369_0_) -> {
            return Sets.newHashSet();
         });
         this.mapByOperation.get(modifier.getOperation()).add(modifier);
         set.add(modifier);
         this.mapByUUID.put(modifier.getID(), modifier);
         this.flagForUpdate();
      }
   }

   protected void flagForUpdate() {
      this.needsUpdate = true;
      this.attributeMap.onAttributeModified(this);
   }

   public void removeModifier(AttributeModifier modifier) {
      for(AttributeModifier.Operation attributemodifier$operation : AttributeModifier.Operation.values()) {
         this.mapByOperation.get(attributemodifier$operation).remove(modifier);
      }

      Set<AttributeModifier> set = this.mapByName.get(modifier.getName());
      if (set != null) {
         set.remove(modifier);
         if (set.isEmpty()) {
            this.mapByName.remove(modifier.getName());
         }
      }

      this.mapByUUID.remove(modifier.getID());
      this.flagForUpdate();
   }

   public void removeModifier(UUID p_188479_1_) {
      AttributeModifier attributemodifier = this.getModifier(p_188479_1_);
      if (attributemodifier != null) {
         this.removeModifier(attributemodifier);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void removeAllModifiers() {
      Collection<AttributeModifier> collection = this.getModifiers();
      if (collection != null) {
         for(AttributeModifier attributemodifier : Lists.newArrayList(collection)) {
            this.removeModifier(attributemodifier);
         }

      }
   }

   public double getValue() {
      if (this.needsUpdate) {
         this.cachedValue = this.computeValue();
         this.needsUpdate = false;
      }

      return this.cachedValue;
   }

   private double computeValue() {
      double d0 = this.getBaseValue();

      for(AttributeModifier attributemodifier : this.func_220370_b(AttributeModifier.Operation.ADDITION)) {
         d0 += attributemodifier.getAmount();
      }

      double d1 = d0;

      for(AttributeModifier attributemodifier1 : this.func_220370_b(AttributeModifier.Operation.MULTIPLY_BASE)) {
         d1 += d0 * attributemodifier1.getAmount();
      }

      for(AttributeModifier attributemodifier2 : this.func_220370_b(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
         d1 *= 1.0D + attributemodifier2.getAmount();
      }

      return this.genericAttribute.clampValue(d1);
   }

   private Collection<AttributeModifier> func_220370_b(AttributeModifier.Operation p_220370_1_) {
      Set<AttributeModifier> set = Sets.newHashSet(this.func_220368_a(p_220370_1_));

      for(IAttribute iattribute = this.genericAttribute.getParent(); iattribute != null; iattribute = iattribute.getParent()) {
         IAttributeInstance iattributeinstance = this.attributeMap.getAttributeInstance(iattribute);
         if (iattributeinstance != null) {
            set.addAll(iattributeinstance.func_220368_a(p_220370_1_));
         }
      }

      return set;
   }
}