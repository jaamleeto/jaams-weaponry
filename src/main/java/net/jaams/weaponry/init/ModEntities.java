package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.entity.AxeProjectileEntity;
import net.jaams.weaponry.entity.BroomProjectileEntity;
import net.jaams.weaponry.entity.BulletProjectileEntity;
import net.jaams.weaponry.entity.CleaverProjectileEntity;
import net.jaams.weaponry.entity.DynamiteProjectileEntity;
import net.jaams.weaponry.entity.EchoBulletProjectileEntity;
import net.jaams.weaponry.entity.FireBulletProjectileEntity;
import net.jaams.weaponry.entity.GiantShurikenProjectileEntity;
import net.jaams.weaponry.entity.GlowingBulletProjectileEntity;
import net.jaams.weaponry.entity.HeavyBulletProjectileEntity;
import net.jaams.weaponry.entity.HuntersBoomerangProjectileEntity;
import net.jaams.weaponry.entity.ItemProjectileEntity;
import net.jaams.weaponry.entity.KunaiProjectileEntity;
import net.jaams.weaponry.entity.ProngedKunaiProjectileEntity;
import net.jaams.weaponry.entity.RingProjectileEntity;
import net.jaams.weaponry.entity.RoyalAxeProjectileEntity;
import net.jaams.weaponry.entity.RoyalSpearProjectileEntity;
import net.jaams.weaponry.entity.SharpBulletProjectileEntity;
import net.jaams.weaponry.entity.SharpStoneProjectileEntity;
import net.jaams.weaponry.entity.ShurikenProjectileEntity;
import net.jaams.weaponry.entity.SpearProjectileEntity;
import net.jaams.weaponry.entity.StakeProjectileEntity;
import net.jaams.weaponry.entity.TridentProjectileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, JaamsWeaponryMod.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<FireBulletProjectileEntity>> FIRE_BULLET_PROJECTILE = register(
        "fire_bullet_projectile",
        EntityType.Builder.<FireBulletProjectileEntity>of(FireBulletProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<HeavyBulletProjectileEntity>> HEAVY_BULLET_PROJECTILE = register(
        "heavy_bullet_projectile",
        EntityType.Builder.<HeavyBulletProjectileEntity>of(HeavyBulletProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<GlowingBulletProjectileEntity>> GLOWING_BULLET_PROJECTILE = register(
        "glowing_bullet_projectile",
        EntityType.Builder.<GlowingBulletProjectileEntity>of(GlowingBulletProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<EchoBulletProjectileEntity>> ECHO_BULLET_PROJECTILE = register(
        "echo_bullet_projectile",
        EntityType.Builder.<EchoBulletProjectileEntity>of(EchoBulletProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SharpBulletProjectileEntity>> SHARP_BULLET_PROJECTILE = register(
        "sharp_bullet_projectile",
        EntityType.Builder.<SharpBulletProjectileEntity>of(SharpBulletProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<BulletProjectileEntity>> BULLET_PROJECTILE = register(
        "bullet_projectile",
        EntityType.Builder.<BulletProjectileEntity>of(BulletProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DynamiteProjectileEntity>> DYNAMITE_PROJECTILE = register(
        "dynamite_projectile",
        EntityType.Builder.<DynamiteProjectileEntity>of(DynamiteProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<HuntersBoomerangProjectileEntity>> HUNTERS_BOOMERANG_PROJECTILE = register(
        "hunters_boomerang_projectile",
        EntityType.Builder.<HuntersBoomerangProjectileEntity>of(HuntersBoomerangProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SharpStoneProjectileEntity>> SHARP_STONE_PROJECTILE = register(
        "sharp_stone_projectile",
        EntityType.Builder.<SharpStoneProjectileEntity>of(SharpStoneProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<StakeProjectileEntity>> STAKE_PROJECTILE = register(
        "stake_projectile",
        EntityType.Builder.<StakeProjectileEntity>of(StakeProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ShurikenProjectileEntity>> SHURIKEN_PROJECTILE = register(
        "shuriken_projectile",
        EntityType.Builder.<ShurikenProjectileEntity>of(ShurikenProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<KunaiProjectileEntity>> KUNAI_PROJECTILE = register(
        "kunai_projectile",
        EntityType.Builder.<KunaiProjectileEntity>of(KunaiProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<GiantShurikenProjectileEntity>> GIANT_SHURIKEN_PROJECTILE = register(
        "giant_shuriken_projectile",
        EntityType.Builder.<GiantShurikenProjectileEntity>of(GiantShurikenProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ProngedKunaiProjectileEntity>> PRONGED_KUNAI_PROJECTILE = register(
        "pronged_kunai_projectile",
        EntityType.Builder.<ProngedKunaiProjectileEntity>of(ProngedKunaiProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SpearProjectileEntity>> SPEAR_PROJECTILE = register(
        "spear_projectile",
        EntityType.Builder.<SpearProjectileEntity>of(SpearProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RoyalSpearProjectileEntity>> ROYAL_SPEAR_PROJECTILE = register(
        "royal_spear_projectile",
        EntityType.Builder.<RoyalSpearProjectileEntity>of(RoyalSpearProjectileEntity::new, MobCategory.MISC)
            
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<CleaverProjectileEntity>> CLEAVER_PROJECTILE = register(
        "cleaver_projectile",
        EntityType.Builder.<CleaverProjectileEntity>of(CleaverProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<BroomProjectileEntity>> BROOM_PROJECTILE = register(
        "broom_projectile",
        EntityType.Builder.<BroomProjectileEntity>of(BroomProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RingProjectileEntity>> RING_PROJECTILE = register(
        "ring_projectile",
        EntityType.Builder.<RingProjectileEntity>of(RingProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<TridentProjectileEntity>> TRIDENT_PROJECTILE = register(
        "trident_projectile",
        EntityType.Builder.<TridentProjectileEntity>of(TridentProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<AxeProjectileEntity>> AXE_PROJECTILE = register(
        "axe_projectile",
        EntityType.Builder.<AxeProjectileEntity>of(AxeProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RoyalAxeProjectileEntity>> ROYAL_AXE_PROJECTILE = register(
        "royal_axe_projectile",
        EntityType.Builder.<RoyalAxeProjectileEntity>of(RoyalAxeProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ItemProjectileEntity>> ITEM_PROJECTILE = register(
        "item_projectile",
        EntityType.Builder.<ItemProjectileEntity>of(ItemProjectileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
    }
}
