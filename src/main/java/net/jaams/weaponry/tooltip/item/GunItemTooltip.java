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

public class GunItemTooltip {

    public static void add(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty() || tooltip == null) {
            return;
        }
        if (GunModifierLoader.INSTANCE.isGunExplicitlyDisabled(stack)) return;
        GunItemData data = GunItemData.getData(stack).orElse(null);
        if (data == null || data.gun == null || !data.gun.gun_enabled) {
            return;
        }
        String gunType = getEffectiveGunType(stack, data);
        ModTooltips.addExtraInfo(stack, tooltip, "tooltip.jaams_weaponry.properties.shooting", ChatFormatting.YELLOW);
        addDamageModifier(stack, tooltip, data, gunType);
        addKnockbackModifier(stack, tooltip, data, gunType);
        addPiercingModifier(stack, tooltip, data, gunType);
        addProjectileCount(stack, tooltip, data, gunType);
        addSpreadAngle(stack, tooltip, data, gunType);
        addInaccuracy(stack, tooltip, data, gunType);
        addProjectileSpeed(stack, tooltip, data, gunType);
        addCooldown(stack, tooltip, data, gunType);
        addRecoil(stack, tooltip, data, gunType);
        addBaseAmmo(stack, tooltip, data, gunType);
        addAttachments(stack, tooltip, data, gunType);
        addGunInventoryDescription(stack, tooltip);
    }

    private static String getEffectiveGunType(ItemStack stack, GunItemData data) {
        ModGuns.GunType nbtType = null;
        if (stack.hasTag() && stack.getTag().contains("GunType", CompoundTag.TAG_STRING)) {
            nbtType = ModGuns.getGunType(stack);
        }
        if (nbtType != null) {
            return nbtType.name();
        }
        if (data != null && data.gun != null && data.gun.gun_type != null && !data.gun.gun_type.isEmpty()) {
            return data.gun.gun_type;
        }
        return ModGuns.GunType.GUN.name();
    }

    private static void addAttachments(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        if (!TooltipsConfig.TOOLTIP_GUN_ATTACHMENTS.get()) return;
        List<String> attachmentRules = getAttachmentSlotRules(stack, data);
        if (attachmentRules.isEmpty()) {
            attachmentRules = getDefaultAttachmentRules(gunType);
        }
        if (!attachmentRules.isEmpty()) {
            resolveAndAddAttachmentTooltip(stack, tooltip, attachmentRules);
        }
    }

    private static List<String> getAttachmentSlotRules(ItemStack stack, GunItemData data) {
        List<String> rules = new ArrayList<>();
        if (stack.hasTag() && stack.getTag().contains("GunSlotRules")) {
            CompoundTag slotRules = stack.getTag().getCompound("GunSlotRules");
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
        List<String> result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = List.of("jaams_weaponry:copper_muzzle", "jaams_weaponry:copper_quick_draw_magazine");
                break;
            case "SCATTERGUN":
                result = List.of("jaams_weaponry:copper_choke", "jaams_weaponry:copper_quick_draw_magazine");
                break;
            case "SHOTGUN":
                result = List.of("jaams_weaponry:copper_choke", "jaams_weaponry:copper_extended_magazine");
                break;
            case "REVOLVER":
                result = List.of("jaams_weaponry:copper_quick_draw_magazine");
                break;
            case "PEPPERBOX":
                result = List.of("jaams_weaponry:copper_muzzle");
                break;
            default:
                result = List.of();
                break;
        }
        return result;
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
                for (Item item : ForgeRegistries.ITEMS) {
                    if (item != null && item != Items.AIR && new ItemStack(item).is(tagKey)) {
                        total++;
                        if (names.size() < maxShown) names.add(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.ITALIC));
                    }
                }
            } else {
                ResourceLocation loc = ResourceLocation.tryParse(trimmed);
                if (loc == null) continue;
                Item item = ForgeRegistries.ITEMS.getValue(loc);
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

    private static void addBaseAmmo(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        if (!TooltipsConfig.TOOLTIP_GUN_BASE_AMMO.get()) return;
        List<String> ammoRules = getAmmoSlotRules(stack, data);
        if (ammoRules.isEmpty()) {
            ammoRules = getDefaultAmmoRules(gunType);
        }
        resolveAndAddAmmoTooltip(stack, tooltip, ammoRules);
    }

    private static List<String> getAmmoSlotRules(ItemStack stack, GunItemData data) {
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
        if (data != null && data.gun != null && data.gun.slot_rules.containsKey(1)) {
            List<String> rules = data.gun.slot_rules.get(1);
            if (rules != null && !rules.isEmpty()) return new ArrayList<>(rules);
        }
        return List.of();
    }

    private static List<String> getDefaultAmmoRules(String gunType) {
        List<String> result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
            case "SCATTERGUN":
            case "GUN":
            case "REVOLVER":
            case "PEPPERBOX":
                result = List.of("#jaams_weaponry:misc/bullets");
                break;
            case "SHOTGUN":
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

    private static void addProjectileCount(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        int value = getFinalInt(stack, "GunProjectileCount", data.shoot != null ? data.shoot.projectile_count : -1, getDefaultProjectileCount(gunType))
                + ModGuns.getAttachmentProjectileCountAddend(stack)
                - ModGuns.getAttachmentProjectileCountSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "projectile_count", Math.max(1, value));
    }

    private static void addSpreadAngle(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        double value = getFinalDouble(stack, "GunSpreadAngle", data.shoot != null ? data.shoot.spread_angle : -1.0, getDefaultSpreadAngle(gunType))
                * ModGuns.getAttachmentSpreadMultiplier(stack)
                + ModGuns.getAttachmentSpreadAddend(stack)
                - ModGuns.getAttachmentSpreadSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "spread_angle", value);
    }

    private static void addInaccuracy(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        double value = getFinalDouble(stack, "GunProjectileInaccuracy", data.shoot != null ? data.shoot.inaccuracy : -1.0, getDefaultInaccuracy(gunType))
                * ModGuns.getAttachmentInaccuracyMultiplier(stack)
                + ModGuns.getAttachmentInaccuracyAddend(stack)
                - ModGuns.getAttachmentInaccuracySubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "inaccuracy", value);
    }

    private static void addProjectileSpeed(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        double speed = getFinalDouble(stack, "GunProjectileSpeed", data.shoot != null ? data.shoot.projectile_speed : -1.0, getDefaultProjectileSpeed(gunType))
                * ModGuns.getAttachmentSpeedMultiplier(stack)
                + ModGuns.getAttachmentSpeedAddend(stack)
                - ModGuns.getAttachmentSpeedSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "projectile_speed", speed);
    }

    private static void addCooldown(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        double cooldown = getFinalDouble(stack, "GunCooldown", data.shoot != null ? data.shoot.cooldown : -1.0, getDefaultCooldown(gunType))
                * ModGuns.getAttachmentCooldownMultiplier(stack)
                + ModGuns.getAttachmentCooldownAddend(stack)
                - ModGuns.getAttachmentCooldownSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "cooldown", ModTooltips.roundToTwoDecimals(cooldown / 20.0));
    }

    private static void addRecoil(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        double recoil = getFinalDouble(stack, "GunRecoilDistance", data.shoot != null ? data.shoot.recoil_distance : -1.0, getDefaultRecoil(gunType))
                * ModGuns.getAttachmentRecoilMultiplier(stack)
                + ModGuns.getAttachmentRecoilAddend(stack)
                - ModGuns.getAttachmentRecoilSubtrahend(stack);
        int backblastLevel = stack.getEnchantmentLevel(ModEnchantments.BACKBLAST.get());
        if (backblastLevel > 0) {
            recoil += EnchantmentsConfig.BACKBLAST_RECOIL_BONUS_PER_LEVEL.get() * backblastLevel;
        }
        ModTooltips.addStat(stack, tooltip, "recoil", ModTooltips.roundToTwoDecimals(recoil));
    }

    private static void addDamageModifier(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        double damage = getFinalDouble(stack, "GunProjectileDamageModifier", data.shoot != null ? data.shoot.damage_modifier : -1.0, getDefaultDamageModifier(gunType))
                * ModGuns.getAttachmentDamageMultiplier(stack)
                + ModGuns.getAttachmentDamageAddend(stack)
                - ModGuns.getAttachmentDamageSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "damage_modifier", damage);
    }

    private static void addKnockbackModifier(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        double value = getFinalDouble(stack, "GunProjectileKnockbackModifier", data.shoot != null ? data.shoot.knockback_modifier : -1.0, getDefaultKnockbackModifier(gunType))
                * ModGuns.getAttachmentKnockbackMultiplier(stack)
                + ModGuns.getAttachmentKnockbackAddend(stack)
                - ModGuns.getAttachmentKnockbackSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "knockback_modifier", value);
    }

    private static void addPiercingModifier(ItemStack stack, List<Component> tooltip, GunItemData data, String gunType) {
        int value = getFinalInt(stack, "GunProjectilePiercingModifier", data.shoot != null ? data.shoot.piercing_modifier : -1, getDefaultPiercingModifier(gunType))
                + ModGuns.getAttachmentPiercingAddend(stack)
                - ModGuns.getAttachmentPiercingSubtrahend(stack);
        ModTooltips.addStat(stack, tooltip, "piercing_modifier", Math.max(0, value));
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
        int result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_COUNT.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_COUNT.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_COUNT.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_PROJECTILE_COUNT.get();
                break;
            case "PEPPERBOX":
                result = ModGuns.REVOLVER_CHAMBER_COUNT;
                break;
            default:
                result = 1;
                break;
        }
        return result;
    }

    private static double getDefaultSpreadAngle(String gunType) {
        double result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_SPREAD_ANGLE.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_SPREAD_ANGLE.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_SPREAD_ANGLE.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_SPREAD_ANGLE.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_SPREAD_ANGLE.get();
                break;
            default:
                result = 0.0;
                break;
        }
        return result;
    }

    private static double getDefaultInaccuracy(String gunType) {
        double result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_INACCURACY.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_INACCURACY.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_INACCURACY.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_INACCURACY.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_INACCURACY.get();
                break;
            default:
                result = 0.0;
                break;
        }
        return result;
    }

    private static double getDefaultProjectileSpeed(String gunType) {
        double result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_SPEED.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_SPEED.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_SPEED.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_PROJECTILE_SPEED.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_SPEED.get();
                break;
            default:
                result = 4.5;
                break;
        }
        return result;
    }

    private static double getDefaultCooldown(String gunType) {
        double result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_COOLDOWN.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_COOLDOWN.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_COOLDOWN.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_COOLDOWN.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_COOLDOWN.get();
                break;
            default:
                result = 20;
                break;
        }
        return result;
    }

    private static double getDefaultRecoil(String gunType) {
        double result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_RECOIL_DISTANCE.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_RECOIL_DISTANCE.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_RECOIL_DISTANCE.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_RECOIL_DISTANCE.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_RECOIL_DISTANCE.get();
                break;
            default:
                result = 0.0;
                break;
        }
        return result;
    }

    private static double getDefaultDamageModifier(String gunType) {
        double result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_DAMAGE_MODIFIER.get();
                break;
            default:
                result = 0.0;
                break;
        }
        return result;
    }

    private static double getDefaultKnockbackModifier(String gunType) {
        double result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_KNOCKBACK_MODIFIER.get();
                break;
            default:
                result = 0.0;
                break;
        }
        return result;
    }

    private static int getDefaultPiercingModifier(String gunType) {
        int result;
        switch (gunType.toUpperCase()) {
            case "PISTOL":
                result = GunSystemCommonConfig.GUN_PISTOL_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
                break;
            case "SCATTERGUN":
                result = GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
                break;
            case "SHOTGUN":
                result = GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
                break;
            case "REVOLVER":
                result = GunSystemCommonConfig.GUN_REVOLVER_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
                break;
            case "PEPPERBOX":
                result = GunSystemCommonConfig.GUN_PEPPERBOX_SHOOT_PROJECTILE_PIERCING_MODIFIER.get();
                break;
            default:
                result = 0;
                break;
        }
        return result;
    }

    private static double getFinalDouble(ItemStack stack, String key, double jsonValue, double defaultValue) {
        if (stack.hasTag() && stack.getTag().contains(key)) {
            return stack.getTag().getDouble(key);
        }
        if (jsonValue != -1.0) {
            return jsonValue;
        }
        return defaultValue;
    }

    private static int getFinalInt(ItemStack stack, String key, int jsonValue, int defaultValue) {
        if (stack.hasTag() && stack.getTag().contains(key)) {
            return stack.getTag().getInt(key);
        }
        if (jsonValue != -1) {
            return jsonValue;
        }
        return defaultValue;
    }
}
