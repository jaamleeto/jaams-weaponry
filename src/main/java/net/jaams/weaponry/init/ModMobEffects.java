package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.effect.ArchersGraceMobEffect;
import net.jaams.weaponry.effect.DepletionMobEffect;
import net.jaams.weaponry.effect.IncapableMobEffect;
import net.jaams.weaponry.effect.KnockedOutMobEffect;
import net.jaams.weaponry.effect.VigorousRageMobEffect;
import net.jaams.weaponry.effect.WarriorsGraceMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMobEffects {

    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JaamsWeaponryMod.MODID);
    public static final RegistryObject<MobEffect> DEPLETION = REGISTRY.register("depletion", () -> new DepletionMobEffect());
    public static final RegistryObject<MobEffect> INCAPABLE = REGISTRY.register("incapable", () -> new IncapableMobEffect());
    public static final RegistryObject<MobEffect> VIGOROUS_RAGE = REGISTRY.register("vigorous_rage", () -> new VigorousRageMobEffect());
    public static final RegistryObject<MobEffect> WARRIORS_GRACE = REGISTRY.register("warriors_grace", () -> new WarriorsGraceMobEffect());
    public static final RegistryObject<MobEffect> ARCHERS_GRACE = REGISTRY.register("archers_grace", () -> new ArchersGraceMobEffect());
    public static final RegistryObject<MobEffect> KNOCKED_OUT = REGISTRY.register("knocked_out", () -> new KnockedOutMobEffect());
}
