package net.jaams.weaponry.util;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.capability.aberration.AberrationProvider;
import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.dyeable.IDyeableItem;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.packet.AberrationPacket;
import net.jaams.weaponry.packet.AmountPacket;
import net.jaams.weaponry.particle.BigWaveParticleData;
import net.jaams.weaponry.particle.CustomBuffParticleData;
import net.jaams.weaponry.particle.CustomDebuffParticleData;
import net.jaams.weaponry.particle.CustomExplosionParticleData;
import net.jaams.weaponry.particle.CustomFlashParticleData;
import net.jaams.weaponry.particle.CustomHitParticleData;
import net.jaams.weaponry.particle.CustomSweepParticleData;
import net.jaams.weaponry.particle.CustomVerticalSweepParticleData;
import net.jaams.weaponry.particle.MiniSweepParticleData;
import net.jaams.weaponry.particle.SmallWaveParticleData;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModUtils {

    public static final Logger LOGGER = LogManager.getLogger();

    public static Integer getIntNBT(ItemStack stack, String key) {
        if (stack == null || !stack.hasTag())
            return null;
        CompoundTag tag = stack.getTag();
        return tag.contains(key, Tag.TAG_INT) ? tag.getInt(key) : null;
    }

    public static Double getDoubleNBT(ItemStack stack, String key) {
        if (stack == null || !stack.hasTag())
            return null;
        CompoundTag tag = stack.getTag();
        return tag.contains(key, Tag.TAG_DOUBLE) ? tag.getDouble(key) : null;
    }

    public static Float getFloatNBT(ItemStack stack, String key) {
        if (stack == null || !stack.hasTag())
            return null;
        CompoundTag tag = stack.getTag();
        return tag.contains(key, Tag.TAG_FLOAT) ? tag.getFloat(key) : null;
    }

    public static Boolean getBooleanNBT(ItemStack stack, String key) {
        if (stack == null || !stack.hasTag())
            return null;
        CompoundTag tag = stack.getTag();
        return tag.contains(key, Tag.TAG_BYTE) ? tag.getBoolean(key) : null;
    }

    public static String getStringNBT(ItemStack stack, String key) {
        if (stack == null || !stack.hasTag())
            return null;
        CompoundTag tag = stack.getTag();
        return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : null;
    }

    public static String getItemIdLowercase(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "";
        }
        return ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath().toLowerCase(Locale.ROOT);
    }

    public static int getDyeColorValue(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return -1;
        }
        if (stack.getItem() instanceof DyeItem dyeItem) {
            return dyeItem.getDyeColor().getTextColor();
        }
        return -1;
    }

    public static String getConfigOrNbtString(ItemStack stack, String nbtKey, Supplier<String> configValue) {
        if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_STRING)) {
            return stack.getTag().getString(nbtKey);
        }
        return configValue.get();
    }

    public static double getConfigOrNbtDouble(ItemStack stack, String nbtKey, DoubleSupplier configValue) {
        if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_DOUBLE)) {
            return stack.getTag().getDouble(nbtKey);
        }
        return configValue.getAsDouble();
    }

    public static float getConfigOrNbtFloat(ItemStack stack, String nbtKey, DoubleSupplier configValue) {
        if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_FLOAT)) {
            return stack.getTag().getFloat(nbtKey);
        }
        return (float) configValue.getAsDouble();
    }

    public static int getConfigOrNbtInt(ItemStack stack, String nbtKey, IntSupplier configValue) {
        if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_INT)) {
            return stack.getTag().getInt(nbtKey);
        }
        return configValue.getAsInt();
    }

    public static boolean getConfigOrNbtBoolean(ItemStack stack, String nbtKey, BooleanSupplier configValue) {
        if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_BYTE)) {
            return stack.getTag().getBoolean(nbtKey);
        }
        return configValue.getAsBoolean();
    }

    public static <T extends Enum<T>> T getConfigOrNbtEnum(ItemStack stack, String nbtKey, Supplier<T> configValue) {
        if (stack.hasTag() && stack.getTag().contains(nbtKey, Tag.TAG_STRING)) {
            String enumName = stack.getTag().getString(nbtKey);
            try {
                return Enum.valueOf(configValue.get().getDeclaringClass(), enumName);
            } catch (IllegalArgumentException e) {
            }
        }
        return configValue.get();
    }

    public static boolean isBlockableItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.hasTag() && stack.getTag().contains("Blockable")) {
            return stack.getTag().getBoolean("Blockable");
        }
        if (stack.getUseAnimation() == UseAnim.BLOCK) {
            return true;
        }
        Item item = stack.getItem();
        if (item instanceof ShieldItem || item.canPerformAction(stack, ToolActions.SHIELD_BLOCK)) {
            return true;
        }
        return false;
    }

    public static boolean hasDurability(ItemStack stack) {
        return stack.getMaxDamage() > 1;
    }

    public static boolean isHeldByPlayer(Player player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return false;
        }
        return player.getMainHandItem() == stack || player.getOffhandItem() == stack;
    }

    public static void applyTraitDurabilityCost(ItemStack stack, LivingEntity entity, int cost,
            Consumer<LivingEntity> onBreak) {
        if (cost <= 0 || stack == null || stack.isEmpty() || !stack.isDamageableItem())
            return;
        if (entity == null || entity.level().isClientSide)
            return;
        if (entity instanceof Player player && player.getAbilities().instabuild)
            return;

        if (entity instanceof Player player && isHeldByPlayer(player, stack)) {
            stack.hurtAndBreak(cost, entity, onBreak);
        } else {
            stack.setDamageValue(stack.getDamageValue() + cost);
        }
    }

    public static float getPowerForTime(int durationRemaining, int minChargeTicks, int maxChargeTicks) {
        if (durationRemaining <= minChargeTicks) {
            return 0.0f;
        }
        float effectiveDuration = durationRemaining - minChargeTicks;
        float maxEffective = maxChargeTicks - minChargeTicks;
        if (maxEffective <= 0) {
            return 1.0f;
        }
        float f = effectiveDuration / maxEffective;
        f = (f * f + f * 2.0f) / 3.0f;
        return Math.min(f, 1.0f);
    }

    public static float[] generateShotPitches(RandomSource random, int projectileCount) {
        float[] pitches = new float[projectileCount];
        for (int i = 0; i < projectileCount; i++) {
            boolean flag = random.nextBoolean();
            pitches[i] = 1.0F / (random.nextFloat() * 0.5F + 1.8F) + (flag ? 0.63F : 0.43F);
        }
        return pitches;
    }

    public static void applyDyeColor(ItemStack itemStack, int color) {
        Item item = itemStack.getItem();
        if (item instanceof IDyeableItem dyeableItem) {
            dyeableItem.setColor(itemStack, color);
        } else {
            CompoundTag tag = itemStack.getOrCreateTag();
            tag.putInt("color", color);
        }
    }

    public static int getCurrentColor(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IDyeableItem dyeableItem) {
            return dyeableItem.getColor(stack);
        } else {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("color", Tag.TAG_INT)) {
                return tag.getInt("color");
            }
            return -1;
        }
    }

    public static boolean removeColor(ItemStack itemStack) {
        boolean changed = false;
        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains("color")) {
            tag.remove("color");
            changed = true;
        }
        return changed;
    }

    public static boolean isImbued(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("ImbuedEffects", Tag.TAG_LIST);
    }

    public static int getImbuedColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("ImbuedColor", Tag.TAG_INT)) {
            return -1;
        }
        return tag.getInt("ImbuedColor");
    }

    public static void removeImbuement(ItemStack stack) {
        if (!stack.hasTag()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        tag.remove("ImbuedEffects");
        tag.remove("ImbuedUses");
        tag.remove("MaxImbuedUses");
        tag.remove("ImbuedColor");
    }

    public static boolean isWaxed(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Waxed");
    }

    public static double calculateDamage(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return 1.0;
        }
        double total = 0.0;
        if (itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE)) {
            total = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream()
                    .mapToDouble(AttributeModifier::getAmount).sum();
        }
        if (total <= 0.0
                && itemStack.getAttributeModifiers(EquipmentSlot.OFFHAND).containsKey(Attributes.ATTACK_DAMAGE)) {
            total = itemStack.getAttributeModifiers(EquipmentSlot.OFFHAND).get(Attributes.ATTACK_DAMAGE).stream()
                    .mapToDouble(AttributeModifier::getAmount).sum();
        }
        float baseDamage = total > 0.0 ? (float) total : 0.0F;
        float enchantmentDamage = EnchantmentHelper.getDamageBonus(itemStack, MobType.UNDEFINED);
        int powerLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
        float powerDamage = powerLevel > 0 ? (powerLevel * 1.5F) : 0.0F;
        double finalDamage = baseDamage + enchantmentDamage + powerDamage;
        return finalDamage > 0.0 ? finalDamage : 1.0;
    }

    public static void applyOrUpdateModifier(LivingEntity entity, Attribute attribute, UUID uuid, String name,
            double value, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(uuid);
            instance.addTransientModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }

    public static void removeModifier(LivingEntity entity, Attribute attribute, UUID uuid) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }

    public static boolean isEntityArmed(LivingEntity entity) {
        return isItemWeapon(entity.getMainHandItem()) || isItemWeapon(entity.getOffhandItem());
    }

    public static boolean isSneaking(LivingEntity sourceentity) {
        if (sourceentity instanceof Player) {
            return sourceentity.isShiftKeyDown();
        } else {
            return sourceentity.level().random.nextFloat() < 0.5f;
        }
    }

    public static boolean isMobAggressive(LivingEntity entity, Player player) {
        if (entity instanceof Mob mob) {
            return mob.getTarget() == player;
        }
        return false;
    }

    public static boolean isItemWeapon(ItemStack itemStack) {
        return itemStack.getItem().getDefaultAttributeModifiers(EquipmentSlot.MAINHAND)
                .containsKey(Attributes.ATTACK_DAMAGE);
    }

    public static boolean hasRestrictedEffect(LivingEntity entity) {
        return entity.hasEffect(ModMobEffects.KNOCKED_OUT.get()) || entity.hasEffect(ModMobEffects.INCAPABLE.get());
    }

    public static void handleItemBreak(ItemStack itemStack, LivingEntity entity, float breakChance, int particleCount,
            String soundEvent) {
        if (entity == null || itemStack == null || entity.level().isClientSide()
                || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (serverLevel.random.nextFloat() < breakChance && itemStack.getCount() > 0) {
            ItemStack particleStack = itemStack.copy();
            boolean isMainHand = ItemStack.isSameItem(itemStack, entity.getMainHandItem());
            entity.swing(isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
            Vec3 lookVec = entity.getLookAngle().normalize();
            Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
            float offset = isMainHand ? 0.15f : -0.15f;
            double startX = entity.getX() + rightVec.x * offset;
            double startY = entity.getEyeY();
            double startZ = entity.getZ() + rightVec.z * offset;
            Vec3 startPos = new Vec3(startX, startY, startZ);
            float particleDistance = 0.5f;
            Vec3 endPos = startPos.add(lookVec.scale(particleDistance));
            ClipContext clipContext = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, entity);
            BlockHitResult blockHit = entity.level().clip(clipContext);
            EntityHitResult entityHit = ModUtils.getEntityHitResult(serverLevel, entity, startPos, endPos);
            double adjustedDistance = particleDistance;
            if (blockHit.getType() != HitResult.Type.MISS) {
                adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(blockHit.getLocation()));
            }
            if (entityHit != null) {
                adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(entityHit.getLocation()));
            }
            adjustedDistance = Math.max(adjustedDistance - 0.1f, 0.1f);
            double particleX = startX + lookVec.x * adjustedDistance;
            double particleY = startY + lookVec.y * adjustedDistance;
            double particleZ = startZ + lookVec.z * adjustedDistance;
            ParticleOptions particle = new ItemParticleOption(ParticleTypes.ITEM, particleStack);
            serverLevel.sendParticles(particle, particleX, particleY, particleZ, particleCount, 0.1d, 0.1d, 0.1d,
                    0.05d);
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
            if (sound != null) {
                serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.HOSTILE,
                        1.0F, 1.0F); 
            }
            itemStack.shrink(1);
        }
    }

    public static void spawnCustomParticlesInFront(LivingEntity entity, ItemStack itemStack,
            ParticleOptions particleType, float r, float g, float b, float particleSize, float particleDistance,
            int particleCount, boolean useHandOffset) {
        if (entity == null || itemStack == null || entity.level().isClientSide()
                || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 lookVec = entity.getLookAngle().normalize();
        Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        double startX = entity.getX();
        double startY = entity.getEyeY();
        double startZ = entity.getZ();
        if (useHandOffset) {
            boolean isMainHand = ItemStack.isSameItem(itemStack, entity.getMainHandItem());
            float offset = isMainHand ? 0.15f : -0.15f;
            startX += rightVec.x * offset;
            startZ += rightVec.z * offset;
        }
        Vec3 startPos = new Vec3(startX, startY, startZ);
        Vec3 endPos = startPos.add(lookVec.scale(particleDistance));
        ClipContext clipContext = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                entity);
        BlockHitResult blockHit = entity.level().clip(clipContext);
        EntityHitResult entityHit = ModUtils.getEntityHitResult(serverLevel, entity, startPos, endPos);
        double adjustedDistance = particleDistance;
        if (blockHit.getType() != HitResult.Type.MISS) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(blockHit.getLocation()));
        }
        if (entityHit != null) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(entityHit.getLocation()));
        }
        adjustedDistance = Math.max(adjustedDistance - 0.1f, 0.1f);
        double particleX = startX + lookVec.x * adjustedDistance;
        double particleY = startY + lookVec.y * adjustedDistance;
        double particleZ = startZ + lookVec.z * adjustedDistance;
        ParticleOptions finalParticle = particleType;
        if (particleType instanceof CustomHitParticleData) {
            finalParticle = new CustomHitParticleData(r, g, b, particleSize);
        } else if (particleType instanceof CustomVerticalSweepParticleData) {
            finalParticle = new CustomVerticalSweepParticleData(r, g, b, particleSize);
        } else if (particleType instanceof CustomFlashParticleData) {
            finalParticle = new CustomFlashParticleData(r, g, b, particleSize);
        } else if (particleType instanceof CustomExplosionParticleData) {
            finalParticle = new CustomExplosionParticleData(r, g, b, particleSize);
        } else if (particleType instanceof CustomSweepParticleData) {
            finalParticle = new CustomSweepParticleData(r, g, b, particleSize);
        } else if (particleType instanceof MiniSweepParticleData) {
            finalParticle = new MiniSweepParticleData(r, g, b, particleSize);
        } else if (particleType instanceof BigWaveParticleData) {
            finalParticle = new BigWaveParticleData(r, g, b, particleSize);
        } else if (particleType instanceof SmallWaveParticleData) {
            finalParticle = new SmallWaveParticleData(r, g, b, particleSize);
        } else if (particleType instanceof CustomBuffParticleData) {
            finalParticle = new CustomBuffParticleData(r, g, b, particleSize);
        } else if (particleType instanceof CustomDebuffParticleData) {
            finalParticle = new CustomDebuffParticleData(r, g, b, particleSize);
        }
        serverLevel.sendParticles(finalParticle, particleX, particleY, particleZ, particleCount, 0.0, 0.0, 0.0, 0.0);
    }

    public static void spawnItemParticlesInFront(LivingEntity entity, ItemStack itemStack, int particleCount,
            float particleDistance, boolean useHandOffset) {
        if (entity == null || itemStack == null || entity.level().isClientSide()
                || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 lookVec = entity.getLookAngle().normalize();
        Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        double startX = entity.getX();
        double startY = entity.getEyeY();
        double startZ = entity.getZ();
        if (useHandOffset) {
            boolean isMainHand = ItemStack.isSameItem(itemStack, entity.getMainHandItem());
            float offset = isMainHand ? 0.15f : -0.15f;
            startX += rightVec.x * offset;
            startZ += rightVec.z * offset;
        }
        Vec3 startPos = new Vec3(startX, startY, startZ);
        Vec3 endPos = startPos.add(lookVec.scale(particleDistance));
        ClipContext clipContext = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                entity);
        BlockHitResult blockHit = entity.level().clip(clipContext);
        EntityHitResult entityHit = ModUtils.getEntityHitResult(serverLevel, entity, startPos, endPos);
        double adjustedDistance = particleDistance;
        if (blockHit.getType() != HitResult.Type.MISS) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(blockHit.getLocation()));
        }
        if (entityHit != null) {
            adjustedDistance = Math.min(adjustedDistance, startPos.distanceTo(entityHit.getLocation()));
        }
        adjustedDistance = Math.max(adjustedDistance - 0.1f, 0.1f);
        double particleX = startX + lookVec.x * adjustedDistance;
        double particleY = startY + lookVec.y * adjustedDistance;
        double particleZ = startZ + lookVec.z * adjustedDistance;
        ParticleOptions particle = new ItemParticleOption(ParticleTypes.ITEM, itemStack.copy());
        serverLevel.sendParticles(particle, particleX, particleY, particleZ, particleCount, 0.1d, 0.1d, 0.1d, 0.05d);
    }

    public static EntityHitResult getEntityHitResult(ServerLevel level, LivingEntity shooter, Vec3 startPos,
            Vec3 endPos) {
        AABB aabb = new AABB(startPos, endPos).inflate(1.0);
        for (Entity entity : level.getEntities(shooter, aabb, (e) -> e instanceof LivingEntity && e.isPickable())) {
            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            Optional<Vec3> hit = entityBox.clip(startPos, endPos);
            if (hit.isPresent()) {
                return new EntityHitResult(entity, hit.get());
            }
        }
        return null;
    }

    public static boolean isInFrontArea(LivingEntity entity, Entity source) {
        Direction entityFront = entity.getDirection();
        Direction attackerFacing = Direction.getNearest(source.getX() - entity.getX(), 0,
                source.getZ() - entity.getZ());
        Direction leftSide = entityFront.getCounterClockWise();
        Direction rightSide = entityFront.getClockWise();
        return attackerFacing == entityFront || attackerFacing == leftSide || attackerFacing == rightSide;
    }

    public static void playSound(Entity entity, String soundEvent) {
        if (!(entity.level() instanceof ServerLevel level))
            return;
        BlockPos pos = entity.blockPosition();
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
        if (sound != null) {
            level.playSound(null, pos, sound, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    public static void playSound(Entity entity, String soundEvent, SoundSource source) {
        if (!(entity.level() instanceof ServerLevel level))
            return;
        BlockPos pos = entity.blockPosition();
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
        if (sound != null) {
            level.playSound(null, pos, sound, source, 1.0f, 1.0f);
        }
    }

    public static void playSound(Entity entity, String soundEvent, SoundSource source, float volume, float pitch) {
        if (!(entity.level() instanceof ServerLevel level))
            return;
        BlockPos pos = entity.blockPosition();
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
        if (sound != null) {
            level.playSound(null, pos, sound, source, volume, pitch);
        }
    }

    public static void playClientSound(Entity entity, String soundEvent) {
        if (!(entity.level() instanceof ClientLevel level))
            return;
        BlockPos pos = entity.blockPosition();
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundEvent));
        if (sound != null) {
            level.playLocalSound(pos, sound, SoundSource.PLAYERS, 1.0f, 1.0f, false);
        }
    }

    public static ItemStack getItemInMainHand(LivingEntity entity, Item item) {
        ItemStack mainHand = entity.getMainHandItem();
        return mainHand.is(item) ? mainHand : ItemStack.EMPTY;
    }

    public static ItemStack getItemInEitherHand(LivingEntity entity, Item item) {
        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();
        return mainHand.is(item) ? mainHand : offHand.is(item) ? offHand : ItemStack.EMPTY;
    }

    public static ItemStack getItemInEitherHand(LivingEntity entity, Predicate<ItemStack> predicate) {
        ItemStack mainHand = entity.getMainHandItem();
        if (!mainHand.isEmpty() && predicate.test(mainHand)) {
            return mainHand;
        }
        ItemStack offHand = entity.getOffhandItem();
        if (!offHand.isEmpty() && predicate.test(offHand)) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    public static Vec3 getDirectionToTarget(LivingEntity entity, LivingEntity target) {
        return new Vec3(target.getX() - entity.getX(), target.getEyeY() - entity.getEyeY(),
                target.getZ() - entity.getZ()).normalize();
    }

    public static boolean canThrowAtTarget(LivingEntity entity, LivingEntity target, double minDistance,
            double maxDistance) {
        double distance = target.distanceTo(entity);
        Vec3 direction = getDirectionToTarget(entity, target);
        Vec3 entityLook = entity.getLookAngle().normalize();
        double angle = direction.dot(entityLook);
        return distance <= maxDistance && distance >= minDistance && angle > 0.5;
    }

    public static boolean hasEpicFightAttribute(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        if (entity.getType() == EntityType.PLAYER) {
            return false;
        }
        ResourceLocation staminaAttributeId = new ResourceLocation("epicfight", "weight");
        Attribute staminaAttribute = ForgeRegistries.ATTRIBUTES.getValue(staminaAttributeId);
        if (staminaAttribute == null) {
            return false;
        }
        return entity.getAttributes().hasAttribute(staminaAttribute);
    }

    public static boolean isItemInCuriosSlot(Entity entity, String itemId, String slotIdentifier) {
        if (entity == null || itemId == null || slotIdentifier == null) {
            return false;
        }
        CompoundTag entityData = new CompoundTag();
        entity.saveWithoutId(entityData);
        CompoundTag forgeCaps = entityData.getCompound("ForgeCaps");
        if (!forgeCaps.contains("curios:inventory")) {
            return false;
        }
        CompoundTag curiosInventory = forgeCaps.getCompound("curios:inventory");
        ListTag curiosList = curiosInventory.getList("Curios", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < curiosList.size(); i++) {
            CompoundTag curioSlot = curiosList.getCompound(i);
            String identifier = curioSlot.getString("Identifier");
            if (slotIdentifier.equals(identifier)) {
                CompoundTag stacksHandler = curioSlot.getCompound("StacksHandler");
                CompoundTag stacks = stacksHandler.getCompound("Stacks");
                ListTag items = stacks.getList("Items", CompoundTag.TAG_COMPOUND);
                for (int j = 0; j < items.size(); j++) {
                    CompoundTag item = items.getCompound(j);
                    String id = item.getString("id");
                    if (itemId.equals(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isEntityInBattleMode(Entity entity) {
        if (entity == null) {
            return false;
        }
        CompoundTag entityData = new CompoundTag();
        entity.saveWithoutId(entityData);
        CompoundTag forgeCaps = entityData.getCompound("ForgeCaps");
        if (forgeCaps.contains("epicfight:skill_cap")) {
            CompoundTag skillCap = forgeCaps.getCompound("epicfight:skill_cap");
            String playerMode = skillCap.getString("playerMode");
            return "EPICFIGHT".equals(playerMode) || "BATTLE".equals(playerMode);
        }
        return false;
    }

    public static boolean isAlliedEntity(Player player, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        if (entity.getType().is(ModTags.IS_ALLY)) {
            return true;
        }
        if (entity instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == player) {
            return true;
        }
        if (entity instanceof Player otherPlayer && !player.canHarmPlayer(otherPlayer)) {
            return true;
        }
        if (entity == player.getVehicle()) {
            return true;
        }
        return false;
    }

    public static void applyShakeEffect(Entity entity, double shakeAmount, int shakeResetDelay) {
        if (entity == null || !(entity instanceof ServerPlayer player) || player.level().isClientSide) {
            return;
        }
        player.getCapability(AberrationProvider.ABERRATION).ifPresent((aberration) -> {
            aberration.setEffectType(ModEnums.AberrationType.SHAKE);
            aberration.setIntensity(shakeAmount);
            aberration.setDuration(shakeResetDelay);
            JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new AberrationPacket(
                    player.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration()));
        });
    }

    public static void applyBonusDamage(LivingEntity attacker, LivingEntity target, ItemStack itemStack,
            float damageBonus) {
        if (attacker == null || target == null || itemStack == null || !target.isAlive()) {
            return;
        }
        try {
            DamageSource damageSource = attacker.damageSources().generic();
            attacker.getCapability(AmountProvider.AMOUNT).ifPresent((capability) -> {
                float baseDamage = (float) capability.getDamage();
                float totalDamage = baseDamage + damageBonus;
                if (totalDamage > 0) {
                    target.hurt(damageSource, totalDamage);
                }
            });
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("Error applying bonus damage to {}: {}", target, e.getMessage());
        }
    }

    public static void applyBackstabDamage(LivingEntity attacker, LivingEntity target, ItemStack itemStack,
            float damageBonus) {
        if (attacker == null || target == null || itemStack == null || !target.isAlive() || attacker.level() == null) {
            return;
        }
        try {
            RegistryAccess registryAccess = attacker.level().registryAccess();
            ResourceKey<DamageType> damageKey = ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation("jaams_weaponry:backstab"));
            DamageSource damageSource = new DamageSource(
                    registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey),
                    attacker);
            attacker.getCapability(AmountProvider.AMOUNT).ifPresent((capability) -> {
                float baseDamage = (float) capability.getDamage();
                float totalDamage = baseDamage + damageBonus;
                if (totalDamage > 0) {
                    target.hurt(damageSource, totalDamage);
                }
            });
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("Error applying backstab damage to {}: {}", target, e.getMessage());
        }
    }

    public static void applyBreachDamage(LivingEntity attacker, LivingEntity target, ItemStack itemStack,
            float damageBonus) {
        if (attacker == null || target == null || itemStack == null || !target.isAlive() || attacker.level() == null) {
            return;
        }
        try {
            RegistryAccess registryAccess = attacker.level().registryAccess();
            ResourceKey<DamageType> damageKey = ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation("jaams_weaponry:breach"));
            DamageSource damageSource = attacker instanceof Player
                    ? new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey),
                            (Player) attacker)
                    : new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey));
            attacker.getCapability(AmountProvider.AMOUNT).ifPresent((capability) -> {
                float baseDamage = (float) capability.getDamage();
                float totalDamage = baseDamage + damageBonus;
                if (totalDamage > 0) {
                    target.hurt(damageSource, totalDamage);
                }
            });
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("Error applying breach damage to {}: {}", target, e.getMessage());
        }
    }

    public static void applySmashDamage(LivingEntity attacker, LivingEntity target, ItemStack itemStack,
            float damageBonus) {
        if (attacker == null || target == null || itemStack == null || !target.isAlive() || attacker.level() == null) {
            return;
        }
        try {
            RegistryAccess registryAccess = attacker.level().registryAccess();
            ResourceKey<DamageType> damageKey = ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation("jaams_weaponry:smash"));
            DamageSource damageSource = attacker instanceof Player
                    ? new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey),
                            (Player) attacker)
                    : new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey));
            attacker.getCapability(AmountProvider.AMOUNT).ifPresent((capability) -> {
                float baseDamage = (float) capability.getDamage();
                float totalDamage = baseDamage + damageBonus;
                if (totalDamage > 0) {
                    target.hurt(damageSource, totalDamage);
                }
            });
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("Error applying smash damage to {}: {}", target, e.getMessage());
        }
    }

    public static void applyPiercingDamage(LivingEntity attacker, LivingEntity target, ItemStack itemStack,
            float damageBonus) {
        if (attacker == null || target == null || itemStack == null || !target.isAlive() || attacker.level() == null) {
            return;
        }
        try {
            RegistryAccess registryAccess = attacker.level().registryAccess();
            ResourceKey<DamageType> damageKey = ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation("jaams_weaponry:piercing"));
            DamageSource damageSource = attacker instanceof Player
                    ? new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey),
                            (Player) attacker)
                    : new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageKey));
            attacker.getCapability(AmountProvider.AMOUNT).ifPresent((capability) -> {
                float baseDamage = (float) capability.getDamage();
                float totalDamage = baseDamage + damageBonus;
                if (totalDamage > 0) {
                    target.hurt(damageSource, totalDamage);
                }
            });
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("Error applying piercing damage to {}: {}", target, e.getMessage());
        }
    }

    public static void applyMagicDamage(LivingEntity attacker, LivingEntity target, ItemStack itemStack,
            float damageBonus) {
        if (attacker == null || target == null || itemStack == null || !target.isAlive() || attacker.level() == null) {
            return;
        }
        try {
            RegistryAccess registryAccess = attacker.level().registryAccess();
            DamageSource damageSource = attacker instanceof Player
                    ? new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC),
                            (Player) attacker)
                    : new DamageSource(
                            registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC));
            attacker.getCapability(AmountProvider.AMOUNT).ifPresent((capability) -> {
                float baseDamage = (float) capability.getDamage();
                float totalDamage = baseDamage + damageBonus;
                if (totalDamage > 0) {
                    target.hurt(damageSource, totalDamage);
                }
            });
        } catch (Exception e) {
            JaamsWeaponryMod.LOGGER.error("Error applying magic damage to {}: {}", target, e.getMessage());
        }
    }

    public static float calculateDistanceBonus(LivingEntity sourceEntity, LivingEntity targetEntity, boolean isCritical,
            float maxBonusBlocks, float minDistanceBlocks, float distanceBonusPerBlock, float criticalBonusMultiplier) {
        double dx = sourceEntity.getX() - targetEntity.getX();
        double dz = sourceEntity.getZ() - targetEntity.getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double heightDifference = Math.abs(sourceEntity.getEyeY() - targetEntity.getEyeY());
        float totalBlocks = (float) Math.min(Math.max(horizontalDistance, heightDifference), maxBonusBlocks);
        if (totalBlocks < minDistanceBlocks) {
            return 0.0F;
        }
        return totalBlocks * distanceBonusPerBlock;
    }

    public static void cancelDamage(LivingEntity target, LivingEntity source) {
        if (!target.isDeadOrDying()) {
            source.getCapability(AmountProvider.AMOUNT).ifPresent((amount) -> {
                float healAmount = amount.getDamage();
                if (healAmount > 0 && Float.isFinite(healAmount)) {
                    float currentHealth = target.getHealth();
                    float maxHealth = target.getMaxHealth();
                    float newHealth = Math.min(currentHealth + healAmount, maxHealth);
                    target.setHealth(newHealth);
                    if (source instanceof ServerPlayer serverPlayer) {
                        JaamsWeaponryMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                                new AmountPacket(source.getId(), amount.getDamage()));
                    }
                }
            });
        }
    }

    public static boolean isBlocked(LivingEntity defender, Entity attacker) {
        if (defender.isBlocking() && attacker != null) {
            Vec3 attackerPos = attacker.position();
            Vec3 defenderLook = defender.getLookAngle();
            Vec3 directionToAttacker = attackerPos.subtract(defender.position()).normalize();
            Vec3 horizontalDirection = new Vec3(directionToAttacker.x, 0.0D, directionToAttacker.z).normalize();
            return horizontalDirection.dot(defenderLook) < 0.0D;
        }
        return false;
    }

    public static boolean isProjectileCritical(Player player, float power) {
        boolean attackPowerFull = power >= 1.0F;
        boolean inAir = !player.onGround();
        boolean hasFallen = player.fallDistance > 0.0F;
        boolean notClimbing = !player.onClimbable();
        boolean notWet = !player.isInWater();
        boolean notBlind = !player.hasEffect(MobEffects.BLINDNESS);
        boolean notRiding = !player.isPassenger();
        boolean notSprinting = !player.isSprinting();
        return attackPowerFull && inAir && hasFallen && notClimbing && notWet && notBlind && notRiding && notSprinting;
    }

    public static boolean isCritical(Player player, Entity target, float chargeStrength) {
        boolean attackPowerFull = chargeStrength > 0.9f;
        boolean hasFallen = player.fallDistance > 0.0F;
        boolean inAir = !player.onGround();
        boolean notClimbing = !player.onClimbable();
        boolean notWet = !player.isInWater();
        boolean notBlind = !player.hasEffect(MobEffects.BLINDNESS);
        boolean notRiding = !player.isPassenger();
        boolean notSprinting = !player.isSprinting();
        boolean targetValid = target instanceof LivingEntity;
        return attackPowerFull && hasFallen && inAir && notClimbing && notWet && notBlind && notRiding && notSprinting
                && targetValid;
    }

    public static boolean isFlyingEntity(LivingEntity entity, float flyingMinDistance) {
        if (entity.onGround()) {
            return false;
        }
        Level level = entity.level();
        Vec3 position = entity.position();
        double x = position.x;
        double y = position.y;
        double z = position.z;
        int blockY = (int) Math.floor(y);
        for (int i = blockY; i > level.getMinBuildHeight(); i--) {
            BlockState blockState = level.getBlockState(new BlockPos((int) x, i, (int) z));
            if (!blockState.isAir()) {
                double distanceToBlock = y - (i + 1);
                return distanceToBlock >= flyingMinDistance;
            }
        }
        return true;
    }

    public static boolean isLookingAtEntity(LivingEntity viewer, Entity target, double maxAngle) {
        Vec3 viewerLook = viewer.getLookAngle();
        Vec3 directionToTarget = target.position().subtract(viewer.position()).normalize();
        return viewerLook.dot(directionToTarget) > Math.cos(Math.toRadians(maxAngle));
    }

    public static boolean isHeadshot(Entity attacker, Entity target) {
        double headHeight = target.getBoundingBox().maxY - target.getBoundingBox().getYsize() * 0.25;
        return attacker.getEyePosition().y > headHeight;
    }

    public static boolean isBodyshot(Entity attacker, Entity target) {
        double minBodyHeight = target.getBoundingBox().minY + target.getBoundingBox().getYsize() * 0.25;
        double maxBodyHeight = target.getBoundingBox().maxY - target.getBoundingBox().getYsize() * 0.25;
        double attackerEyeHeight = attacker.getEyePosition().y;
        return attackerEyeHeight >= minBodyHeight && attackerEyeHeight <= maxBodyHeight;
    }

    public static void applyRecoil(LivingEntity entity, float recoilDistance, float crouchRecoilReduction,
            float verticalRecoilMultiplier) {
        if (entity == null || recoilDistance <= 0.0F) {
            return;
        }
        double recoilMultiplier = recoilDistance;
        if (entity instanceof Player player && player.isCrouching() && !player.isCreative()) {
            recoilMultiplier *= crouchRecoilReduction;
            if (!player.isCreative()) {
                player.getFoodData().addExhaustion(0.2F);
            }
        } else if (entity instanceof Player player && !player.isCreative()) {
            player.getFoodData().addExhaustion(0.5F);
        }
        double knockbackResistance = Math.max(0.0,
                Math.min(1.0, entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        double knockbackReduction = 1.0 - Math.pow(1.0 - knockbackResistance, 4.0);
        recoilMultiplier *= (1.0 - knockbackReduction);
        double movementSpeed = entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double baseMovementSpeed = 0.1;
        double movementSpeedMultiplier = Math.max(0.1, movementSpeed / baseMovementSpeed);
        recoilMultiplier *= movementSpeedMultiplier;
        double horizontalRecoil = 1.2 * recoilMultiplier;
        double verticalRecoil = 1.2 * verticalRecoilMultiplier * recoilMultiplier;
        horizontalRecoil = Math.max(0.0, horizontalRecoil);
        verticalRecoil = Math.max(0.0, verticalRecoil);
        if (horizontalRecoil < 0.01 && verticalRecoil < 0.01) {
            return;
        }
        float yaw = entity.getYHeadRot() * ((float) Math.PI / 180F);
        float pitch = (entity instanceof Player player) ? player.getXRot() * ((float) Math.PI / 180F) : 0.0F;
        double motionX = Math.sin(yaw) * horizontalRecoil * Math.cos(pitch);
        double motionZ = -Math.cos(yaw) * horizontalRecoil * Math.cos(pitch);
        double motionY = 0.0;
        final float MIN_PITCH_FOR_VERTICAL_DEGREES = 45.0F;
        float minPitchForVertical = (float) Math.toRadians(MIN_PITCH_FOR_VERTICAL_DEGREES);
        double horizontalScale = 1.0;
        if (pitch > minPitchForVertical) {
            float pitchFactor = (pitch - minPitchForVertical) / ((float) Math.PI / 2 - minPitchForVertical);
            pitchFactor = Math.min(1.0f, pitchFactor);
            double verticalScale = Math.cos((pitchFactor * Math.PI) / 2);
            motionY = verticalRecoil * (1.0 - verticalScale);
            horizontalScale = Math.max(0.5, 1.0 - pitchFactor * 0.5);
            motionX *= horizontalScale;
            motionZ *= horizontalScale;
        }
        motionX = Mth.clamp(motionX, -1.5, 1.5);
        motionY = Mth.clamp(motionY, -1.5 * verticalRecoilMultiplier, 1.5 * verticalRecoilMultiplier);
        motionZ = Mth.clamp(motionZ, -1.5, 1.5);

        Vec3 currentMotion = entity.getDeltaMovement();
        double finalMotionY = currentMotion.y + motionY;

        if (motionY > 0.0 && currentMotion.y < 0.0) {
            finalMotionY = (currentMotion.y * 0.5) + motionY;
        }

        entity.setDeltaMovement(currentMotion.x + motionX, finalMotionY, currentMotion.z + motionZ);
        entity.hurtMarked = true;
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
        } else if (!entity.level().isClientSide) {
            entity
                    .level()
                    .getEntities(null, entity.getBoundingBox().inflate(10.0))
                    .stream()
                    .filter((e) -> e instanceof ServerPlayer)
                    .forEach((e) -> ((ServerPlayer) e).connection.send(new ClientboundSetEntityMotionPacket(entity)));
        }
    }

    public static boolean matchesList(Set<String> set, ResourceLocation itemKey, Item item, boolean isWhitelist) {
        if (set == null || set.isEmpty()) {
            return false;
        }
        for (String entry : set) {
            entry = entry.trim();
            if (entry.isEmpty()) {
                continue;
            }
            if (entry.equals("all")) {
                if (ForgeRegistries.ITEMS.containsKey(itemKey)) {
                    return true;
                }
                continue;
            }
            if (entry.startsWith("#")) {
                try {
                    String tagName = entry.substring(1);
                    TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation(tagName));
                    if (item.builtInRegistryHolder().is(tag)) {
                        return true;
                    }
                } catch (ResourceLocationException e) {
                    LOGGER.warn("Invalid tag entry in list (ignoring): '{}'", entry);
                }
                continue;
            }
            if (entry.equals("bows:*")) {
                if (item instanceof BowItem) {
                    return true;
                }
            } else if (entry.equals("crossbows:*")) {
                if (item instanceof CrossbowItem) {
                    return true;
                }
            } else if (entry.equals("swords:*")) {
                if (item instanceof SwordItem) {
                    return true;
                }
            } else if (entry.equals("pickaxes:*")) {
                if (item instanceof PickaxeItem) {
                    return true;
                }
            } else if (entry.equals("axes:*")) {
                if (item instanceof AxeItem) {
                    return true;
                }
            } else if (entry.equals("blocks:*")) {
                if (item instanceof BlockItem) {
                    return true;
                }
            } else if (entry.equals("edibles:*")) {
                if (item.getFoodProperties() != null) {
                    return true;
                }
            } else if (entry.equals("shovels:*")) {
                if (item instanceof ShovelItem) {
                    return true;
                }
            } else if (entry.equals("hoes:*")) {
                if (item instanceof HoeItem) {
                    return true;
                }
            } else if (entry.equals("armor:*")) {
                if (item instanceof ArmorItem) {
                    return true;
                }
            } else if (entry.contains(":*")) {
                String[] parts = entry.split(":");
                if (parts.length == 2 && parts[1].equals("*")) {
                    String namespace = parts[0];
                    if (itemKey.getNamespace().equals(namespace)) {
                        return true;
                    }
                }
            } else if (entry.contains("*")) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    String namespace = parts[0];
                    String pattern = parts[1].replace("*", "");
                    if (itemKey.getNamespace().equals(namespace) && itemKey.getPath().contains(pattern)) {
                        return true;
                    }
                } else if (parts.length == 1) {
                    String pattern = parts[0].replace("*", "");
                    if (itemKey.getPath().contains(pattern)) {
                        return true;
                    }
                } else {
                    LOGGER.warn(
                            "Invalid wildcard pattern (ignoring): '{}'. Expected format: 'namespace:pattern' or 'pattern'",
                            entry);
                }
            } else {
                try {
                    ResourceLocation resourceLocation = new ResourceLocation(entry);
                    if (ForgeRegistries.ITEMS.containsKey(resourceLocation) && itemKey.equals(resourceLocation)) {
                        return true;
                    }
                } catch (ResourceLocationException e) {
                    LOGGER.warn("Invalid entry in list (ignoring): '{}'", entry);
                }
            }
        }
        return false;
    }
}
