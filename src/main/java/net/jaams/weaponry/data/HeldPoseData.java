package net.jaams.weaponry.data;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;


public class HeldPoseData {
    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public String pose = "";
    public String hand = "mainhand";
    public List<String> entity = new ArrayList<>();
    public boolean first_person = false;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();

    
    public String animation = "";
    public float animation_speed = 1.0f;

    
    public String right_click_animation = "";
    public float right_click_animation_speed = 1.0f;

    public static class Condition {
        public String type;
        public String enchantment;
        public int level = 0;
        public String tag;

        
        public String effect;
        public int effect_level = 0;
        public String vanilla_pose;
        public Float health_below = null;
        public Float health_above = null;
    }

    public boolean appliesToHand(String slot) {
        if (hand == null || hand.isEmpty() || "either".equalsIgnoreCase(hand))
            return true;
        return hand.equalsIgnoreCase(slot);
    }


    public boolean appliesToEntity(String entityType) {
        if (entity == null || entity.isEmpty())
            return "minecraft:player".equals(entityType);
        if (entityType == null || entityType.isEmpty())
            return false;
        boolean anyPositive = false;
        for (String raw : entity) {
            if (raw == null)
                continue;
            String pattern = raw.trim();
            if (pattern.isEmpty())
                continue;
            boolean negate = pattern.startsWith("!");
            String p = negate ? pattern.substring(1) : pattern;
            boolean matches = matchesEntityPattern(p, entityType);
            if (negate) {
                if (matches)
                    return false;
            } else if (matches) {
                anyPositive = true;
            }
        }
        return anyPositive;
    }

    private boolean matchesEntityPattern(String pattern, String entityType) {
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            if (tagId == null)
                return false;
            TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tagId);
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.tryParse(entityType));
            return type != null && type.is(tagKey);
        }
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            return entityType.matches(regex);
        }
        return pattern.equals(entityType);
    }
}
