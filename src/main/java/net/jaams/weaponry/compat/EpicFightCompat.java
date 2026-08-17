package net.jaams.weaponry.compat;

import java.util.Optional;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/**
 * Bridge class for optional Epic Fight (epicfight) integration.
 *
 * <p>Epic Fight is only a compile-time dependency. None of the methods here can
 * fail at runtime when the mod is not installed: direct references to Epic Fight
 * classes live in {@link EpicFightImpl}, which is only loaded after
 * {@link #isEpicFightLoaded()} has been checked and every access is additionally
 * guarded by a {@link Throwable} catch as a fallback for version mismatches.
 */
public final class EpicFightCompat {

    public static final String MOD_ID = "epicfight";

    public enum Mode {
        NONE, VANILLA, EPICFIGHT
    }

    private EpicFightCompat() {
    }

    public static boolean isEpicFightLoaded() {
        return ModList.get() != null && ModList.get().isLoaded(MOD_ID);
    }

    // ---------- entity patch ----------

    public static boolean hasPatch(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.getPatch(entity).isPresent();
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether Epic Fight currently overrides the entity's rendering with its own model. */
    public static boolean hasCustomRender(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.hasCustomRender(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether Epic Fight is currently playing a blocking action animation on the entity. */
    public static boolean isInaction(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.inaction(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether Epic Fight is currently playing an attack animation on the entity. */
    public static boolean isAttacking(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.attacking(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether Epic Fight is currently animating the entity (action or attack). */
    public static boolean isAnimating(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.inaction(entity) || EpicFightImpl.attacking(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean canBasicAttack(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canBasicAttack(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean canUseSkill(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canUseSkill(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean canUseItem(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canUseItem(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean canSwitchHoldingItem(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return true;
        }
        try {
            return EpicFightImpl.canSwitchHoldingItem(entity);
        } catch (Throwable t) {
            return true;
        }
    }

    public static boolean isMovementLocked(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.movementLocked(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isTurnLocked(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.turningLocked(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isKnockedDown(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.knockDown(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isHurt(LivingEntity entity) {
        if (!isEpicFightLoaded() || entity == null) {
            return false;
        }
        try {
            return EpicFightImpl.hurt(entity);
        } catch (Throwable t) {
            return false;
        }
    }

    // ---------- player mode ----------

    /** The player's Epic Fight mode, or {@link Mode#NONE} when not applicable. */
    public static Mode getMode(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return Mode.NONE;
        }
        try {
            return EpicFightImpl.getMode(player);
        } catch (Throwable t) {
            return Mode.NONE;
        }
    }

    public static boolean isEpicFightMode(Player player) {
        return getMode(player) == Mode.EPICFIGHT;
    }

    public static boolean isVanillaMode(Player player) {
        return getMode(player) == Mode.VANILLA;
    }

    /**
     * Whether Epic Fight's animated first-person model (config {@code ingame.first_person_model})
     * is enabled. When it is, Epic Fight takes over the first-person hand rendering and the mod
     * must let its {@code RenderHandEvent} run instead of cancelling {@code renderHandsWithItems}.
     */
    public static boolean isFirstPersonModelActive() {
        if (!isEpicFightLoaded()) {
            return false;
        }
        try {
            return EpicFightImpl.firstPersonModelActive();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Whether Epic Fight uses the vanilla player model when the player is not in combat mode
     * (config {@code ingame.vanilla_model}). When it is disabled, Epic Fight's own model renders
     * the player even out of combat, so the animation API's custom first-person body must not
     * activate (it has a visual bug against Epic Fight's model).
     */
    public static boolean isVanillaModelActive() {
        if (!isEpicFightLoaded()) {
            return false;
        }
        try {
            return EpicFightImpl.vanillaModelActive();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Whether the animation API's custom first-person body can render for the given player.
     *
     * <p>It can only render while the vanilla {@code PlayerRenderer} is in charge of the local
     * player's body in first-person view. Mirrors the behavior of
     * {@code LocalPlayerPatch.overrideRender()}:
     * <ul>
     *   <li>Epic Fight first-person model disabled &rarr; it never overrides first-person
     *       rendering, the vanilla renderer is in charge.</li>
     *   <li>Epic Fight first-person model enabled &rarr; it overrides while the player is in
     *       combat mode, or when the vanilla model config is disabled (its own model is used even
     *       out of combat).</li>
     * </ul>
     */
    public static boolean canRenderAnimatedFirstPerson(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return true;
        }
        try {
            if (!EpicFightImpl.firstPersonModelActive()) {
                return true;
            }
            return !isEpicFightMode(player) && EpicFightImpl.vanillaModelActive();
        } catch (Throwable t) {
            return true;
        }
    }

    // ---------- player stamina ----------

    public static float getStamina(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return 0.0F;
        }
        try {
            return EpicFightImpl.getStamina(player);
        } catch (Throwable t) {
            return 0.0F;
        }
    }

    public static float getMaxStamina(Player player) {
        if (!isEpicFightLoaded() || player == null) {
            return 0.0F;
        }
        try {
            return EpicFightImpl.getMaxStamina(player);
        } catch (Throwable t) {
            return 0.0F;
        }
    }

    public static boolean hasStamina(Player player, float amount) {
        if (!isEpicFightLoaded() || player == null) {
            return true;
        }
        try {
            return EpicFightImpl.hasStamina(player, amount);
        } catch (Throwable t) {
            return true;
        }
    }

    /**
     * All direct references to Epic Fight classes are kept here so the outer class
     * stays loadable when the mod is absent. This class is only loaded on first use,
     * which happens after {@link EpicFightCompat#isEpicFightLoaded()} is true.
     */
    private static final class EpicFightImpl {

        static Optional<LivingEntityPatch<?>> getPatch(LivingEntity entity) {
            if (entity == null) {
                return Optional.empty();
            }
            return EpicFightCapabilities.getUnparameterizedEntityPatch(entity, LivingEntityPatch.class)
                    .map(patch -> (LivingEntityPatch<?>) patch);
        }

        static Optional<PlayerPatch<?>> getPlayerPatch(Player player) {
            if (player == null) {
                return Optional.empty();
            }
            return EpicFightCapabilities.getUnparameterizedEntityPatch(player, PlayerPatch.class)
                    .map(patch -> (PlayerPatch<?>) patch);
        }

        static boolean hasCustomRender(LivingEntity entity) {
            return getPatch(entity).map(EntityPatch::overrideRender).orElse(false);
        }

        static boolean inaction(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().inaction()).orElse(false);
        }

        static boolean attacking(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().attacking()).orElse(false);
        }

        static boolean canBasicAttack(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canBasicAttack()).orElse(true);
        }

        static boolean canUseSkill(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canUseSkill()).orElse(true);
        }

        static boolean canUseItem(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canUseItem()).orElse(true);
        }

        static boolean canSwitchHoldingItem(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().canSwitchHoldingItem()).orElse(true);
        }

        static boolean movementLocked(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().movementLocked()).orElse(false);
        }

        static boolean turningLocked(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().turningLocked()).orElse(false);
        }

        static boolean knockDown(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().knockDown()).orElse(false);
        }

        static boolean hurt(LivingEntity entity) {
            return getPatch(entity).map(patch -> patch.getEntityState().hurt()).orElse(false);
        }

        static Mode getMode(Player player) {
            return getPlayerPatch(player)
                    .map(PlayerPatch::getPlayerMode)
                    .map(mode -> mode == PlayerPatch.PlayerMode.EPICFIGHT ? Mode.EPICFIGHT : Mode.VANILLA)
                    .orElse(Mode.NONE);
        }

        static boolean firstPersonModelActive() {
            return yesman.epicfight.config.ClientConfig.enableAnimatedFirstPersonModel;
        }

        static boolean vanillaModelActive() {
            return yesman.epicfight.config.ClientConfig.enableOriginalModel;
        }

        static float getStamina(Player player) {
            return getPlayerPatch(player).map(PlayerPatch::getStamina).orElse(0.0F);
        }

        static float getMaxStamina(Player player) {
            return getPlayerPatch(player).map(PlayerPatch::getMaxStamina).orElse(0.0F);
        }

        static boolean hasStamina(Player player, float amount) {
            return getPlayerPatch(player).map(patch -> patch.hasStamina(amount)).orElse(true);
        }
    }
}
