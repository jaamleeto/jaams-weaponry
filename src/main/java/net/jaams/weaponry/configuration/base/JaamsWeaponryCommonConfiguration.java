package net.jaams.weaponry.configuration.base;

import net.minecraftforge.common.ForgeConfigSpec;

import net.jaams.weaponry.configuration.common.TraitsConfig;
import net.jaams.weaponry.configuration.common.RecipesConfig;
import net.jaams.weaponry.configuration.common.ProjectileCommonConfig;
import net.jaams.weaponry.configuration.common.MechanicsConfig;

import net.jaams.weaponry.configuration.common.ItemFeaturesConfig;
import net.jaams.weaponry.configuration.common.InteractionsConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.configuration.common.AssortedCommonConfig;
import net.jaams.weaponry.configuration.common.MobBehaviorConfig;

public class JaamsWeaponryCommonConfiguration {
    public static final ForgeConfigSpec SPEC;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        AssortedCommonConfig.register(builder);
        EffectsConfig.register(builder);
        GunSystemCommonConfig.register(builder);
        InteractionsConfig.register(builder);
        ItemFeaturesConfig.register(builder);

        MechanicsConfig.register(builder);
        EnchantmentsConfig.register(builder);
        ProjectileCommonConfig.register(builder);
        RecipesConfig.register(builder);
        TraitsConfig.register(builder);
        MobBehaviorConfig.register(builder);
        SPEC = builder.build();
    }
}
