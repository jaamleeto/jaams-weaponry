package net.jaams.weaponry.init;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.effect.ArchersGraceMobEffect;
import net.jaams.weaponry.effect.DepletionMobEffect;
import net.jaams.weaponry.effect.IncapableMobEffect;
import net.jaams.weaponry.effect.KnockedOutMobEffect;
import net.jaams.weaponry.effect.VigorousRageMobEffect;
import net.jaams.weaponry.effect.WarriorsGraceMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMobEffects {

    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, JaamsWeaponryMod.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> DEPLETION = REGISTRY.register("depletion", () -> new DepletionMobEffect());
    public static final DeferredHolder<MobEffect, MobEffect> INCAPABLE = REGISTRY.register("incapable", () -> new IncapableMobEffect());
    public static final DeferredHolder<MobEffect, MobEffect> VIGOROUS_RAGE = REGISTRY.register("vigorous_rage", () -> new VigorousRageMobEffect());
    public static final DeferredHolder<MobEffect, MobEffect> WARRIORS_GRACE = REGISTRY.register("warriors_grace", () -> new WarriorsGraceMobEffect());
    public static final DeferredHolder<MobEffect, MobEffect> ARCHERS_GRACE = REGISTRY.register("archers_grace", () -> new ArchersGraceMobEffect());
    public static final DeferredHolder<MobEffect, MobEffect> KNOCKED_OUT = REGISTRY.register("knocked_out", () -> new KnockedOutMobEffect());
}
