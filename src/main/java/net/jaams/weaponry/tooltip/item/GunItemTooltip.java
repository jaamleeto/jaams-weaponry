package net.jaams.weaponry.tooltip.item;
import net.jaams.weaponry.util.ModComponents;

import java.util.ArrayList;
import java.util.List;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.configuration.common.EnchantmentsConfig;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.init.ModEnchantments;
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
import net.minecraft.core.registries.BuiltInRegistries;

public class GunItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null) {
            return;
        }
        GunItemData data = GunItemData.getData(stack).orElse(null);
        if (data == null || data.gun == null || !data.gun.gun_enabled) {
            return;
        }
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
        addDamageModifier(stack, tooltip, data);
        addKnockbackModifier(stack, tooltip, data);
        addPiercingModifier(stack, tooltip, data);
        addProjectileCount(stack, tooltip, data);
        addSpreadAngle(stack, tooltip, data);
        addInaccuracy(stack, tooltip, data);
        addProjectileSpeed(stack, tooltip, data);
        addCooldown(stack, tooltip, data);
        addRecoil(stack, tooltip, data);
        addBaseAmmo(stack, tooltip, data);
        addAttachments(stack, tooltip, data);
        addGunInventoryDescription(stack, tooltip);
    }

    private static void addAttachments(ItemStack stack, List<Component> tooltip, GunItemData data) {
        if (!TooltipsConfig.TOOLTIP_GUN_ATTACHMENTS.get()) return;
        List<String> attachmentRules = getAttachmentSlotRules(stack, data);
        if (attachmentRules.isEmpty()) {
            attachmentRules = getDefaultAttachmentRules(data.gun.gun_type);
        }
        if (!attachmentRules.isEmpty()) {
            resolveAndAddAttachmentTooltip(stack, tooltip, attachmentRules);
        }
    }

    private static List<String> getAttachmentSlotRules(ItemStack stack, GunItemData data) {
        List<String> rules = new ArrayList<>();
        if (ModComponents.has(stack) && ModComponents.get(stack).contains("GunSlotRules")) {
            CompoundTag slotRules = ModComponents.get(stack).getCompound("GunSlotRules");
            for (int slot : new int[]{0, 2}) {
                String key = "Slot" + slot;
                if (slotRules.contains(key)) {
                    Tag value = slotRules.get(key);
                    if (value instanceof ListTag listTag) {
                        for (Tag t : listTag) {
                            if (t instanceof StringTag st) rules.add(st.getAsString());
                        }
                    } else if (value instanceof StringTag st) {
                        String rule = st.getAsString().trim();
                        if (!rule.isEmpty()) rules.add(rule);
                    }
                }
            }
        }
        if (rules.isEmpty() && data != null && data.gun != null) {
            for (int slot : new int[]{0, 2}) {
                if (data.gun.slot_rules.containsKey(slot)) {
                    List<String> slotRules = data.gun.slot_rules.get(slot);
                    if (slotRules != null) rules.addAll(slotRules);
                }
            }
        }
        return rules;
    }

    private static List<String> getDefaultAttachmentRules(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> List.of("jaams_weaponry:copper_muzzle", "jaams_weaponry:copper_quick_draw_magazine");
            case "SCATTERGUN" -> List.of("jaams_weaponry:copper_choke", "jaams_weaponry:copper_quick_draw_magazine");
            case "SHOTGUN" -> List.of("jaams_weaponry:copper_choke", "jaams_weaponry:copper_extended_magazine");
            default -> List.of();
        };
    }

    private static void resolveAndAddAttachmentTooltip(ItemStack stack, List<Component> tooltip, List<String> rules) {
        List<Component> names = new ArrayList<>();
        int maxShown = 5;
        int total = 0;
        boolean hasAny = false;
        for (String rule : rules) {
            String trimmed = rule.startsWith("!") ? rule.substring(1).trim() : rule.trim();
            if (trimmed.isEmpty()) continue;
            if ("any".equalsIgnoreCase(trimmed)) { hasAny = true; continue; }
            if (trimmed.startsWith("#")) {
                String tagId = trimmed.substring(1).trim();
                ResourceLocation loc = ResourceLocation.tryParse(tagId);
                if (loc == null) continue;
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, loc);
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item != null && item != Items.AIR && new ItemStack(item).is(tagKey)) {
                        total++;
                        if (names.size() < maxShown) names.add(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.ITALIC));
                    }
                }
            } else {
                ResourceLocation loc = ResourceLocation.tryParse(trimmed);
                if (loc == null) continue;
                Item item = BuiltInRegistries.ITEM.get(loc);
                if (item != null && item != Items.AIR) {
                    total++;
                    if (names.size() < maxShown) names.add(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.ITALIC));
                }
            }
        }
        if (hasAny && names.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.attachments",
                    Component.translatable("tooltip.jaams_weaponry.ammo.any")
                            .withStyle(ChatFormatting.ITALIC))
                    .withStyle(ChatFormatting.GRAY));
        } else if (!names.isEmpty()) {
            Component attachComp = names.stream()
                    .reduce((a, b) -> a.copy().append(
                            Component.literal(", ").withStyle(ChatFormatting.GRAY)).append(b))
                    .orElse(Component.empty());
            int remaining = total - maxShown;
            if (remaining > 0) {
                attachComp = attachComp.copy().append(
                        Component.literal(" +" + remaining)
                                .withStyle(ChatFormatting.DARK_GRAY));
            }
            tooltip.add(Component.translatable("tooltip.jaams_weaponry.properties.attachments", attachComp)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static void addBaseAmmo(ItemStack stack, List<Component> tooltip, GunItemData data) {
        if (!TooltipsConfig.TOOLTIP_GUN_BASE_AMMO.get()) return;
        List<String> ammoRules = getAmmoSlotRules(stack, data);
        if (ammoRules.isEmpty()) {
            ammoRules = getDefaultAmmoRules(data.gun.gun_type);
        }
        resolveAndAddAmmoTooltip(stack, tooltip, ammoRules);
    }

    private static List<String> getAmmoSlotRules(ItemStack stack, GunItemData data) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains("GunSlotRules")) {
            CompoundTag rules = ModComponents.get(stack).getCompound("GunSlotRules");
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
        if (data != null && data.gun != null && data.gun.slot_rules.containsKey(1)) {
            List<String> rules = data.gun.slot_rules.get(1);
            if (rules != null && !rules.isEmpty()) return new ArrayList<>(rules);
        }
        return List.of();
    }

    private static List<String> getDefaultAmmoRules(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL", "SCATTERGUN", "GUN" -> List.of("#jaams_weaponry:misc/bullets");
            case "SHOTGUN" -> List.of("#jaams_weaponry:misc/shotshells");
            default -> List.of();
        };
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
                for (Item item : BuiltInRegistries.ITEM) {
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
                Item item = BuiltInRegistries.ITEM.get(loc);
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

    private static void addProjectileCount(ItemStack stack, List<Component> tooltip, GunItemData data) {
        int count = getFinalInt(stack, "GunProjectileCount", data.shoot != null ? data.shoot.projectile_count : null, getDefaultProjectileCount(data.gun.gun_type));
        if (count > 0) {
            ModTooltips.addStat(stack, tooltip, "projectile_count", count);
        }
    }

    private static void addSpreadAngle(ItemStack stack, List<Component> tooltip, GunItemData data) {
        double spread = getFinalDouble(stack, "GunSpreadAngle", data.shoot != null ? data.shoot.spread_angle : null, getDefaultSpreadAngle(data.gun.gun_type));
        if (spread > 0) {
            ModTooltips.addStat(stack, tooltip, "spread_angle", spread);
        }
    }

    private static void addInaccuracy(ItemStack stack, List<Component> tooltip, GunItemData data) {
        double inaccuracy = getFinalDouble(stack, "GunProjectileInaccuracy", data.shoot != null ? data.shoot.inaccuracy : null, getDefaultInaccuracy(data.gun.gun_type));
        if (inaccuracy > 0) {
            ModTooltips.addStat(stack, tooltip, "inaccuracy", inaccuracy);
        }
    }

    private static void addProjectileSpeed(ItemStack stack, List<Component> tooltip, GunItemData data) {
        double speed = getFinalDouble(stack, "GunProjectileSpeed", data.shoot != null ? data.shoot.projectile_speed : null, getDefaultProjectileSpeed(data.gun.gun_type));
        if (speed > 0) {
            speed *= ModGuns.getMuzzleSpeedMultiplier(stack);
            ModTooltips.addStat(stack, tooltip, "projectile_speed", speed);
        }
    }

    private static void addCooldown(ItemStack stack, List<Component> tooltip, GunItemData data) {
        double cooldown = getFinalDouble(stack, "GunCooldown", data.shoot != null ? data.shoot.cooldown : null, getDefaultCooldown(data.gun.gun_type));
        if (cooldown > 0) {
            double seconds = cooldown / 20.0;
            ModTooltips.addStat(stack, tooltip, "cooldown", ModTooltips.roundToTwoDecimals(seconds));
        }
    }

    private static void addRecoil(ItemStack stack, List<Component> tooltip, GunItemData data) {
        double recoil = getFinalDouble(stack, "GunRecoilDistance", data.shoot != null ? data.shoot.recoil_distance : null, getDefaultRecoil(data.gun.gun_type));
        if (recoil > 0) {
            int backblastLevel = ModEnchantments.level(stack, ModEnchantments.BACKBLAST);
            if (backblastLevel > 0) {
                recoil += EnchantmentsConfig.BACKBLAST_RECOIL_BONUS_PER_LEVEL.get() * backblastLevel;
            }
            ModTooltips.addStat(stack, tooltip, "recoil", ModTooltips.roundToTwoDecimals(recoil));
        }
    }

    private static void addDamageModifier(ItemStack stack, List<Component> tooltip, GunItemData data) {
        double damage = getFinalDouble(stack, "GunProjectileDamageModifier", data.shoot != null ? data.shoot.damage_modifier : null, getDefaultDamageModifier(data.gun.gun_type));
        if (damage > 0) {
            damage *= ModGuns.getMuzzleDamageMultiplier(stack);
            ModTooltips.addStat(stack, tooltip, "damage_modifier", damage);
        }
    }

    private static void addKnockbackModifier(ItemStack stack, List<Component> tooltip, GunItemData data) {
        double knockback = getFinalDouble(stack, "GunProjectileKnockbackModifier", data.shoot != null ? data.shoot.knockback_modifier : null, getDefaultKnockbackModifier(data.gun.gun_type));
        if (knockback > 0) {
            ModTooltips.addStat(stack, tooltip, "knockback_modifier", knockback);
        }
    }

    private static void addPiercingModifier(ItemStack stack, List<Component> tooltip, GunItemData data) {
        int piercing = getFinalInt(stack, "GunProjectilePiercingModifier", data.shoot != null ? data.shoot.piercing_modifier : null, getDefaultPiercingModifier(data.gun.gun_type));
        if (piercing > 0) {
            ModTooltips.addStat(stack, tooltip, "piercing_modifier", piercing);
        }
    }

    private static void addGunInventoryDescription(ItemStack stack, List<Component> tooltip) {
        if (!GunSystemCommonConfig.GUN_INVENTORY.get()) {
            return;
        }
        if (!TooltipsConfig.TOOLTIP_GUN_INVENTORY_HINT.get()) {
            return;
        }
        ModEnums.KeyOption keyOption = GunSystemClientConfig.GUN_INV_KEY.get();
        if (keyOption != null) {
            String keyName = ModTooltips.getKeyDisplayName(keyOption);
            Component message = Component.translatable("tooltip.jaams_weaponry.gun_inventory.long_desc", Component.literal(keyName).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);
            ModTooltips.addLongDescriptionComponent(stack, tooltip, message);
        }
    }

    private static int getDefaultProjectileCount(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_COUNT.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_COUNT.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_COUNT.get();
            default -> 1;
        };
    }

    private static double getDefaultSpreadAngle(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_SPREAD_ANGLE.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_SPREAD_ANGLE.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_SPREAD_ANGLE.get();
            default -> 0.0;
        };
    }

    private static double getDefaultInaccuracy(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_INACCURACY.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_INACCURACY.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_INACCURACY.get();
            default -> 0.0;
        };
    }

    private static double getDefaultProjectileSpeed(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_SPEED.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_SPEED.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_SPEED.get();
            default -> 4.5;
        };
    }

    private static double getDefaultCooldown(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_COOLDOWN.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_COOLDOWN.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_COOLDOWN.get();
            default -> 20;
        };
    }

    private static double getDefaultRecoil(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_RECOIL_DISTANCE.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_RECOIL_DISTANCE.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_RECOIL_DISTANCE.get();
            default -> 0.0;
        };
    }

    private static double getDefaultDamageModifier(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
            default -> 0.0;
        };
    }

    private static double getDefaultKnockbackModifier(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
            default -> 0.0;
        };
    }

    private static int getDefaultPiercingModifier(String gunType) {
        return switch (gunType.toUpperCase()) {
            case "PISTOL" -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
            case "SCATTERGUN" -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
            case "SHOTGUN" -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
            default -> 0;
        };
    }

    private static double getFinalDouble(ItemStack stack, String key, Double jsonValue, double defaultValue) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains(key)) {
            return ModComponents.get(stack).getDouble(key);
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        return defaultValue;
    }

    private static int getFinalInt(ItemStack stack, String key, Integer jsonValue, int defaultValue) {
        if (ModComponents.has(stack) && ModComponents.get(stack).contains(key)) {
            return ModComponents.get(stack).getInt(key);
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        return defaultValue;
    }
}
