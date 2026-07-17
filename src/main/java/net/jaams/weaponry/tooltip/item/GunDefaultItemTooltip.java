package net.jaams.weaponry.tooltip.item;

import java.util.ArrayList;
import java.util.List;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.init.ModEnchantments;
import net.jaams.weaponry.loader.GunModifierLoader;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModTooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class GunDefaultItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null) {
            return;
        }
        if (GunModifierLoader.INSTANCE.isGunExplicitlyDisabled(stack)) return;
        if (hasJsonGun(stack)) {
            return;
        }
        if (ModGuns.getGunType(stack) != ModGuns.GunType.GUN) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
        addDamageModifier(stack, tooltip);
        addKnockbackModifier(stack, tooltip);
        addPiercingModifier(stack, tooltip);
        addProjectileCount(stack, tooltip);
        addSpreadAngle(stack, tooltip);
        addInaccuracy(stack, tooltip);
        addProjectileSpeed(stack, tooltip);
        addCooldown(stack, tooltip);
        addRecoil(stack, tooltip);
        addBaseAmmo(stack, tooltip);
        addGunInventoryDescription(stack, tooltip);
    }

    private static void addBaseAmmo(ItemStack stack, List<Component> tooltip) {
        if (!TooltipsConfig.TOOLTIP_GUN_BASE_AMMO.get()) return;
        List<String> ammoRules = getAmmoSlotRules(stack);
        if (ammoRules.isEmpty()) {
            ModGuns.GunType gunType = ModGuns.getGunType(stack);
            if (gunType != null) {
                ammoRules = getDefaultAmmoRules(gunType);
            }
        }
        if (!ammoRules.isEmpty()) {
            resolveAndAddAmmoTooltip(stack, tooltip, ammoRules);
        }
    }

    private static List<String> getAmmoSlotRules(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("GunSlotRules")) {
            CompoundTag rules = stack.getTag().getCompound("GunSlotRules");
            if (rules.contains("Slot1")) {
                Tag value = rules.get("Slot1");
                if (value instanceof ListTag listTag) {
                    List<String> ruleList = new ArrayList<>();
                    for (Tag t : listTag) {
                        if (t instanceof StringTag st) ruleList.add(st.getAsString());
                    }
                    if (!ruleList.isEmpty()) return ruleList;
                } else if (value instanceof StringTag st) {
                    String rule = st.getAsString().trim();
                    if (!rule.isEmpty()) return List.of(rule);
                }
            }
        }
        return List.of();
    }

    private static List<String> getDefaultAmmoRules(ModGuns.GunType gunType) {
        List<String> result;
        switch (gunType) {
            case PISTOL:
            case SCATTERGUN:
            case GUN:
            case REVOLVER:
            case PEPPERBOX:
                result = List.of("#jaams_weaponry:misc/bullets");
                break;
            case SHOTGUN:
                result = List.of("#jaams_weaponry:misc/shotshells");
                break;
            default:
                result = List.of();
                break;
        }
        return result;
    }

    private static void resolveAndAddAmmoTooltip(ItemStack stack, List<Component> tooltip, List<String> rules) {
        List<Component> names = new ArrayList<>();
        int maxShown = 5;
        int total = 0;
        boolean hasAny = false;

        for (String rule : rules) {
            String trimmed = rule.startsWith("!") ? rule.substring(1).trim() : rule.trim();
            if (trimmed.isEmpty()) continue;
            if ("any".equalsIgnoreCase(trimmed)) {
                hasAny = true;
                continue;
            }
            if (trimmed.startsWith("#")) {
                String tagId = trimmed.substring(1).trim();
                ResourceLocation loc = ResourceLocation.tryParse(tagId);
                if (loc == null) continue;
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, loc);
                for (Item item : ForgeRegistries.ITEMS) {
                    if (item != null && item != Items.AIR && new ItemStack(item).is(tagKey)) {
                        total++;
                        if (names.size() < maxShown) {
                            names.add(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.ITALIC));
                        }
                    }
                }
            } else {
                ResourceLocation loc = ResourceLocation.tryParse(trimmed);
                if (loc == null) continue;
                Item item = ForgeRegistries.ITEMS.getValue(loc);
                if (item != null && item != Items.AIR) {
                    total++;
                    if (names.size() < maxShown) {
                        names.add(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.ITALIC));
                    }
                }
            }
        }

        if (hasAny && names.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.base_ammo",
                    Component.translatable("tooltip.jaams_weaponry.ammo.any")
                            .withStyle(ChatFormatting.ITALIC))
                    .withStyle(ChatFormatting.GRAY));
        } else if (!names.isEmpty()) {
            Component ammoComp = names.stream()
                    .reduce((a, b) -> a.copy().append(
                            Component.literal(", ").withStyle(ChatFormatting.GRAY)).append(b))
                    .orElse(Component.empty());
            int remaining = total - maxShown;
            if (remaining > 0) {
                ammoComp = ammoComp.copy().append(
                        Component.literal(" +" + remaining)
                                .withStyle(ChatFormatting.DARK_GRAY));
            }
            tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.base_ammo", ammoComp)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static boolean hasJsonGun(ItemStack stack) {
        return GunItemData.getData(stack).isPresent();
    }

    private static void addProjectileCount(ItemStack stack, List<Component> tooltip) {
        int value = getFinalInt(stack, "GunProjectileCount", 1)
                + ModGuns.getAttachmentProjectileCountAddend(stack)
                - ModGuns.getAttachmentProjectileCountSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "projectile_count", Math.max(1, value));
    }

    private static void addSpreadAngle(ItemStack stack, List<Component> tooltip) {
        double value = getFinalDouble(stack, "GunSpreadAngle", 0.0) * ModGuns.getAttachmentSpreadMultiplier(stack)
                + ModGuns.getAttachmentSpreadAddend(stack)
                - ModGuns.getAttachmentSpreadSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "spread_angle", value);
    }

    private static void addInaccuracy(ItemStack stack, List<Component> tooltip) {
        double value = getFinalDouble(stack, "GunProjectileInaccuracy", 0.0)
                * ModGuns.getAttachmentInaccuracyMultiplier(stack)
                + ModGuns.getAttachmentInaccuracyAddend(stack)
                - ModGuns.getAttachmentInaccuracySubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "inaccuracy", value);
    }

    private static void addProjectileSpeed(ItemStack stack, List<Component> tooltip) {
        double value = getFinalDouble(stack, "GunProjectileSpeed", 4.5) * ModGuns.getAttachmentSpeedMultiplier(stack)
                + ModGuns.getAttachmentSpeedAddend(stack)
                - ModGuns.getAttachmentSpeedSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "projectile_speed", value);
    }

    private static void addCooldown(ItemStack stack, List<Component> tooltip) {
        double cooldown = getFinalDouble(stack, "GunCooldown", 20.0) * ModGuns.getAttachmentCooldownMultiplier(stack)
                + ModGuns.getAttachmentCooldownAddend(stack)
                - ModGuns.getAttachmentCooldownSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "cooldown",
                ModTooltips.roundToTwoDecimals(cooldown / 20.0));
    }

    private static void addRecoil(ItemStack stack, List<Component> tooltip) {
        double recoil = getFinalDouble(stack, "GunRecoilDistance", 0.0) * ModGuns.getAttachmentRecoilMultiplier(stack)
                + ModGuns.getAttachmentRecoilAddend(stack)
                - ModGuns.getAttachmentRecoilSubtrahend(stack);
        int backblastLevel = stack.getEnchantmentLevel(ModEnchantments.BACKBLAST.get());
        if (backblastLevel > 0) {
            recoil += EnchantmentsConfig.BACKBLAST_RECOIL_BONUS_PER_LEVEL.get() * backblastLevel;
        }
        ModTooltips.addStat(stack, tooltip, "recoil", ModTooltips.roundToTwoDecimals(recoil));
    }

    private static void addDamageModifier(ItemStack stack, List<Component> tooltip) {
        double value = getFinalDouble(stack, "GunProjectileDamageModifier", 0.0)
                * ModGuns.getAttachmentDamageMultiplier(stack)
                + ModGuns.getAttachmentDamageAddend(stack)
                - ModGuns.getAttachmentDamageSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "damage_modifier", value);
    }

    private static void addKnockbackModifier(ItemStack stack, List<Component> tooltip) {
        double value = getFinalDouble(stack, "GunProjectileKnockbackModifier", 0.0)
                * ModGuns.getAttachmentKnockbackMultiplier(stack)
                + ModGuns.getAttachmentKnockbackAddend(stack)
                - ModGuns.getAttachmentKnockbackSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "knockback_modifier", value);
    }

    private static void addPiercingModifier(ItemStack stack, List<Component> tooltip) {
        int value = getFinalInt(stack, "GunProjectilePiercingModifier", 0)
                + ModGuns.getAttachmentPiercingAddend(stack)
                - ModGuns.getAttachmentPiercingSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "piercing_modifier", Math.max(0, value));
    }

    private static void addGunInventoryDescription(ItemStack stack, List<Component> tooltip) {
        if (!GunSystemCommonConfig.GUN_INVENTORY.get()) return;
        if (!TooltipsConfig.TOOLTIP_GUN_INVENTORY_HINT.get()) return;
        ModEnums.KeyOption keyOption = GunSystemClientConfig.GUN_INV_KEY.get();
        if (keyOption != null) {
            String keyName = ModTooltips.getKeyDisplayName(keyOption);
            Component message = Component.translatable("tooltip.jaams_weaponry.gun_inventory.long_desc", Component.literal(keyName).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);
            ModTooltips.addLongDescriptionComponent(stack, tooltip, message);
        }
    }

    private static double getFinalDouble(ItemStack stack, String key, double defaultValue) {
        if (stack.hasTag() && stack.getTag().contains(key)) {
            return stack.getTag().getDouble(key);
        }
        return defaultValue;
    }

    private static int getFinalInt(ItemStack stack, String key, int defaultValue) {
        if (stack.hasTag() && stack.getTag().contains(key)) {
            return stack.getTag().getInt(key);
        }
        return defaultValue;
    }
}
