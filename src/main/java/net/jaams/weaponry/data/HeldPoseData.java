package net.jaams.weaponry.data;

import java.util.List;
import java.util.ArrayList;


public class HeldPoseData {
    public List<String> target = new ArrayList<>();
    public Boolean enabled = true;
    public int priority = 0;
    public transient String id;
    public String pose = "";
    public String hand = "mainhand";
    public String entity = "";
    public boolean first_person = false;
    public String condition_mode = "and";
    public List<Condition> conditions = new ArrayList<>();

    
    public String animation = "";
    public float animation_speed = 1.0f;

    public static class Condition {
        public String type;
        public String enchantment;
        public int level = 0;
        public String tag;
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
        boolean negate = entity.startsWith("!");
        String pattern = negate ? entity.substring(1) : entity;
        boolean matches;
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            matches = entityType.matches(regex);
        } else {
            matches = entityType.equals(pattern);
        }
        return negate != matches;
    }
}
