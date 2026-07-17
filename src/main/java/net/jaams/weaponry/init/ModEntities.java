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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JaamsWeaponryMod.MODID);
    public static final RegistryObject<EntityType<FireBulletProjectileEntity>> FIRE_BULLET_PROJECTILE = register(
        "fire_bullet_projectile",
        EntityType.Builder.<FireBulletProjectileEntity>of(FireBulletProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(FireBulletProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<HeavyBulletProjectileEntity>> HEAVY_BULLET_PROJECTILE = register(
        "heavy_bullet_projectile",
        EntityType.Builder.<HeavyBulletProjectileEntity>of(HeavyBulletProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(HeavyBulletProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<GlowingBulletProjectileEntity>> GLOWING_BULLET_PROJECTILE = register(
        "glowing_bullet_projectile",
        EntityType.Builder.<GlowingBulletProjectileEntity>of(GlowingBulletProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(GlowingBulletProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<EchoBulletProjectileEntity>> ECHO_BULLET_PROJECTILE = register(
        "echo_bullet_projectile",
        EntityType.Builder.<EchoBulletProjectileEntity>of(EchoBulletProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(EchoBulletProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<SharpBulletProjectileEntity>> SHARP_BULLET_PROJECTILE = register(
        "sharp_bullet_projectile",
        EntityType.Builder.<SharpBulletProjectileEntity>of(SharpBulletProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(SharpBulletProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<BulletProjectileEntity>> BULLET_PROJECTILE = register(
        "bullet_projectile",
        EntityType.Builder.<BulletProjectileEntity>of(BulletProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(BulletProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<DynamiteProjectileEntity>> DYNAMITE_PROJECTILE = register(
        "dynamite_projectile",
        EntityType.Builder.<DynamiteProjectileEntity>of(DynamiteProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(DynamiteProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<HuntersBoomerangProjectileEntity>> HUNTERS_BOOMERANG_PROJECTILE = register(
        "hunters_boomerang_projectile",
        EntityType.Builder.<HuntersBoomerangProjectileEntity>of(HuntersBoomerangProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(HuntersBoomerangProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<SharpStoneProjectileEntity>> SHARP_STONE_PROJECTILE = register(
        "sharp_stone_projectile",
        EntityType.Builder.<SharpStoneProjectileEntity>of(SharpStoneProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(SharpStoneProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<StakeProjectileEntity>> STAKE_PROJECTILE = register(
        "stake_projectile",
        EntityType.Builder.<StakeProjectileEntity>of(StakeProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(StakeProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<ShurikenProjectileEntity>> SHURIKEN_PROJECTILE = register(
        "shuriken_projectile",
        EntityType.Builder.<ShurikenProjectileEntity>of(ShurikenProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(ShurikenProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<KunaiProjectileEntity>> KUNAI_PROJECTILE = register(
        "kunai_projectile",
        EntityType.Builder.<KunaiProjectileEntity>of(KunaiProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(KunaiProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<GiantShurikenProjectileEntity>> GIANT_SHURIKEN_PROJECTILE = register(
        "giant_shuriken_projectile",
        EntityType.Builder.<GiantShurikenProjectileEntity>of(GiantShurikenProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(GiantShurikenProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<ProngedKunaiProjectileEntity>> PRONGED_KUNAI_PROJECTILE = register(
        "pronged_kunai_projectile",
        EntityType.Builder.<ProngedKunaiProjectileEntity>of(ProngedKunaiProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(ProngedKunaiProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<SpearProjectileEntity>> SPEAR_PROJECTILE = register(
        "spear_projectile",
        EntityType.Builder.<SpearProjectileEntity>of(SpearProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(SpearProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<RoyalSpearProjectileEntity>> ROYAL_SPEAR_PROJECTILE = register(
        "royal_spear_projectile",
        EntityType.Builder.<RoyalSpearProjectileEntity>of(RoyalSpearProjectileEntity::new, MobCategory.MISC)
            .setCustomClientFactory(RoyalSpearProjectileEntity::new)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<CleaverProjectileEntity>> CLEAVER_PROJECTILE = register(
        "cleaver_projectile",
        EntityType.Builder.<CleaverProjectileEntity>of(CleaverProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(CleaverProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<BroomProjectileEntity>> BROOM_PROJECTILE = register(
        "broom_projectile",
        EntityType.Builder.<BroomProjectileEntity>of(BroomProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(BroomProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<RingProjectileEntity>> RING_PROJECTILE = register(
        "ring_projectile",
        EntityType.Builder.<RingProjectileEntity>of(RingProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(RingProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<TridentProjectileEntity>> TRIDENT_PROJECTILE = register(
        "trident_projectile",
        EntityType.Builder.<TridentProjectileEntity>of(TridentProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(TridentProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<AxeProjectileEntity>> AXE_PROJECTILE = register(
        "axe_projectile",
        EntityType.Builder.<AxeProjectileEntity>of(AxeProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(AxeProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<RoyalAxeProjectileEntity>> ROYAL_AXE_PROJECTILE = register(
        "royal_axe_projectile",
        EntityType.Builder.<RoyalAxeProjectileEntity>of(RoyalAxeProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(RoyalAxeProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );
    public static final RegistryObject<EntityType<ItemProjectileEntity>> ITEM_PROJECTILE = register(
        "item_projectile",
        EntityType.Builder.<ItemProjectileEntity>of(ItemProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(ItemProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f)
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
    }
}
