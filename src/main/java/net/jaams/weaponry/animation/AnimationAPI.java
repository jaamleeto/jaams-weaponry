package net.jaams.weaponry.animation;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.HumanoidArm;

import java.util.WeakHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.client.Minecraft;

import java.util.stream.Stream;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.Gson;

import net.jaams.weaponry.JaamsWeaponryMod;

public class AnimationAPI {
    public static final Map<String, PlayerAnimation> animations = new Object2ObjectOpenHashMap<>();
    public static final Map<Player, PlayerAnimation> active_animations = new Object2ObjectOpenHashMap<>();

    
    public static final Map<Player, Float> playerSwingBlend = new WeakHashMap<>();

    
    public static final Map<Player, HumanoidArm> playerSwingArm = new WeakHashMap<>();

    
    public static final Map<Integer, MobAnimationState> mob_active_animations = new HashMap<>();

    
    public static final Map<Integer, Float> mobSwingBlend = new HashMap<>();

    
    public static final Map<Integer, Boolean> mobSwingArm = new HashMap<>();

    
    public static final int BLEND_OUT_DURATION = 8;

    
    public static final Map<String, RandomAnimationGroup> randomGroups = new HashMap<>();

    public static class MobAnimationState {
        public String animationName;
        public PlayerAnimation animation;
        public float progress;
        public float lastTick;
        public boolean active;
        public int elapsedTicks;
        public boolean override;
        public float speed;
        public int duration;
        public final Set<Float> playedSounds = new HashSet<>();
        
        public final Set<Float> playedEvents = new HashSet<>();
        
        public int lastAdvanceTick = -1;
        
        public int blendOutTicks = 0;
        
        public int blendOutDuration = AnimationAPI.BLEND_OUT_DURATION;

        public MobAnimationState() {
            this.animationName = "";
            this.animation = null;
            this.progress = 0f;
            this.lastTick = 0f;
            this.active = false;
            this.elapsedTicks = 0;
            this.override = false;
            this.speed = 1.0f;
            this.duration = 0;
            this.blendOutTicks = 0;
            this.blendOutDuration = AnimationAPI.BLEND_OUT_DURATION;
        }
    }

    public static void loadAnimationFile(JsonObject file) {
        JsonObject animationsObject = file.get("animations").getAsJsonObject();
        for (int i = 0; i < animationsObject.size(); i++) {
            String animationName = animationsObject.keySet().stream().toList().get(i);
            JsonObject animationObject = animationsObject.get(animationName).getAsJsonObject();
            PlayerAnimation animation = new PlayerAnimation(animationObject);
            animations.put(animationName, animation);
        }
    }

    public static class PlayerAnimation {
        public final float length;
        public boolean loop = false;
        public boolean hold_on_last_frame = false;
        public final Map<String, PlayerBone> bones;
        public final Map<Float, String> soundEffects;

        
        
        
        
        

        
        public float maxDuration = 0f;

        
        public float speed = 1.0f;

        
        public boolean headRot = false;

        
        public boolean ignoreCrouching = false;

        
        public boolean ignoreSwing = false;

        
        public boolean ignoreItemPoses = false;

        
        public boolean hideArms = false;

        
        public boolean cancelOnAttack = false;

        
        public boolean cancelOnPlayerAnimator = false;

        
        public boolean cancelOnHurt = false;

        
        public boolean cancelOnSwing = false;

        
        public boolean cancelOnItemUse = false;

        
        public boolean cancelOnMove = false;

        
        public boolean cancelOnRun = false;

        
        public boolean cancelOnSneaking = false;

        
        public boolean cancelOnCrawl = false;

        
        public boolean skipInFirstPerson = false;

        
        public boolean isPose = false;

        
        public boolean combinable = false;

        
        public float blendSpeed = 1.0f;

        
        public int blendIn = -1;

        
        public int blendOut = -1;

        
        public final Map<Float, List<AnimationEvent>> events = new HashMap<>();

        public PlayerAnimation(JsonObject animation) {
            if (animation.has("animation_length"))
                this.length = animation.get("animation_length").getAsFloat();
            else
                this.length = 0;
            if (animation.has("loop")) {
                JsonElement loopType = animation.get("loop");
                if (loopType.isJsonPrimitive() && loopType.getAsJsonPrimitive().isBoolean())
                    this.loop = loopType.getAsBoolean();
                else if (loopType.isJsonPrimitive())
                    this.hold_on_last_frame = true;
            }

            
            

            if (animation.has("max_duration"))
                this.maxDuration = animation.get("max_duration").getAsFloat();

            if (animation.has("speed"))
                this.speed = animation.get("speed").getAsFloat();

            if (animation.has("head_rot"))
                this.headRot = animation.get("head_rot").getAsBoolean();
            else if (animation.has("headRot"))
                this.headRot = animation.get("headRot").getAsBoolean();

            if (animation.has("ignore_crouching"))
                this.ignoreCrouching = animation.get("ignore_crouching").getAsBoolean();
            else if (animation.has("ignoreCrouching"))
                this.ignoreCrouching = animation.get("ignoreCrouching").getAsBoolean();

            if (animation.has("ignore_swing"))
                this.ignoreSwing = animation.get("ignore_swing").getAsBoolean();
            else if (animation.has("ignoreSwing"))
                this.ignoreSwing = animation.get("ignoreSwing").getAsBoolean();

            if (animation.has("ignore_item_poses"))
                this.ignoreItemPoses = animation.get("ignore_item_poses").getAsBoolean();
            else if (animation.has("ignoreItemPoses"))
                this.ignoreItemPoses = animation.get("ignoreItemPoses").getAsBoolean();

            if (animation.has("hide_arms"))
                this.hideArms = animation.get("hide_arms").getAsBoolean();
            else if (animation.has("hideArms"))
                this.hideArms = animation.get("hideArms").getAsBoolean();

            if (animation.has("cancel_on_attack"))
                this.cancelOnAttack = animation.get("cancel_on_attack").getAsBoolean();
            else if (animation.has("cancelOnAttack"))
                this.cancelOnAttack = animation.get("cancelOnAttack").getAsBoolean();

            if (animation.has("cancel_on_player_animator"))
                this.cancelOnPlayerAnimator = animation.get("cancel_on_player_animator").getAsBoolean();
            else if (animation.has("cancelOnPlayerAnimator"))
                this.cancelOnPlayerAnimator = animation.get("cancelOnPlayerAnimator").getAsBoolean();

            if (animation.has("cancel_on_hurt"))
                this.cancelOnHurt = animation.get("cancel_on_hurt").getAsBoolean();
            else if (animation.has("cancelOnHurt"))
                this.cancelOnHurt = animation.get("cancelOnHurt").getAsBoolean();

            if (animation.has("cancel_on_swing"))
                this.cancelOnSwing = animation.get("cancel_on_swing").getAsBoolean();
            else if (animation.has("cancelOnSwing"))
                this.cancelOnSwing = animation.get("cancelOnSwing").getAsBoolean();

            if (animation.has("cancel_on_item_use"))
                this.cancelOnItemUse = animation.get("cancel_on_item_use").getAsBoolean();
            else if (animation.has("cancelOnItemUse"))
                this.cancelOnItemUse = animation.get("cancelOnItemUse").getAsBoolean();

            if (animation.has("cancel_on_move"))
                this.cancelOnMove = animation.get("cancel_on_move").getAsBoolean();
            else if (animation.has("cancelOnMove"))
                this.cancelOnMove = animation.get("cancelOnMove").getAsBoolean();

            if (animation.has("cancel_on_run"))
                this.cancelOnRun = animation.get("cancel_on_run").getAsBoolean();
            else if (animation.has("cancelOnRun"))
                this.cancelOnRun = animation.get("cancelOnRun").getAsBoolean();

            if (animation.has("cancel_on_sneaking"))
                this.cancelOnSneaking = animation.get("cancel_on_sneaking").getAsBoolean();
            else if (animation.has("cancelOnSneaking"))
                this.cancelOnSneaking = animation.get("cancelOnSneaking").getAsBoolean();

            if (animation.has("cancel_on_crawl"))
                this.cancelOnCrawl = animation.get("cancel_on_crawl").getAsBoolean();
            else if (animation.has("cancelOnCrawl"))
                this.cancelOnCrawl = animation.get("cancelOnCrawl").getAsBoolean();

            if (animation.has("skip_in_first_person"))
                this.skipInFirstPerson = animation.get("skip_in_first_person").getAsBoolean();
            else if (animation.has("skipInFirstPerson"))
                this.skipInFirstPerson = animation.get("skipInFirstPerson").getAsBoolean();

            if (animation.has("is_pose"))
                this.isPose = animation.get("is_pose").getAsBoolean();
            else if (animation.has("isPose"))
                this.isPose = animation.get("isPose").getAsBoolean();

            if (animation.has("combinable"))
                this.combinable = animation.get("combinable").getAsBoolean();

            if (animation.has("blend_speed"))
                this.blendSpeed = animation.get("blend_speed").getAsFloat();
            else if (animation.has("blendSpeed"))
                this.blendSpeed = animation.get("blendSpeed").getAsFloat();
            if (this.blendSpeed <= 0)
                this.blendSpeed = 1.0f;

            if (animation.has("blend_in"))
                this.blendIn = animation.get("blend_in").getAsInt();
            else if (animation.has("blendIn"))
                this.blendIn = animation.get("blendIn").getAsInt();

            if (animation.has("blend_out"))
                this.blendOut = animation.get("blend_out").getAsInt();
            else if (animation.has("blendOut"))
                this.blendOut = animation.get("blendOut").getAsInt();

            
            if (animation.has("events")) {
                JsonObject eventsObj = animation.getAsJsonObject("events");
                for (Map.Entry<String, JsonElement> eventEntry : eventsObj.entrySet()) {
                    try {
                        float time = Float.parseFloat(eventEntry.getKey());
                        List<AnimationEvent> eventList = new ArrayList<>();
                        JsonElement value = eventEntry.getValue();
                        if (value.isJsonArray()) {
                            JsonArray arr = value.getAsJsonArray();
                            for (int i = 0; i < arr.size(); i++) {
                                eventList.add(new AnimationEvent(arr.get(i).getAsJsonObject()));
                            }
                        } else if (value.isJsonObject()) {
                            
                            eventList.add(new AnimationEvent(value.getAsJsonObject()));
                        }
                        if (!eventList.isEmpty()) {
                            this.events.put(time, eventList);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            this.bones = new HashMap<>();
            if (animation.has("bones")) {
                JsonObject bonesObj = animation.getAsJsonObject("bones");
                for (String boneName : bonesObj.keySet()) {
                    this.bones.put(boneName, new PlayerBone(bonesObj.getAsJsonObject(boneName)));
                }
            }
            this.soundEffects = new HashMap<>();
            if (animation.has("sound_effects")) {
                JsonObject soundEffectsObj = animation.getAsJsonObject("sound_effects");
                for (Map.Entry<String, JsonElement> entry : soundEffectsObj.entrySet()) {
                    try {
                        float time = Float.parseFloat(entry.getKey());
                        JsonObject soundData = entry.getValue().getAsJsonObject();
                        if (soundData.has("effect")) {
                            String soundId = soundData.get("effect").getAsString();
                            soundEffects.put(time, soundId);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class PlayerBone {
        public final List<Keyframe> rotations;
        public final List<Keyframe> positions;
        public final List<Keyframe> scales;

        public PlayerBone(JsonObject bone) {
            this.rotations = parseTransform(bone, "rotation");
            this.positions = parseTransform(bone, "position");
            this.scales = parseTransform(bone, "scale");
        }

        public static class Keyframe {
            public final float time;
            public final KeyframeValue value;
            public final KeyframeValue pre;
            public final KeyframeValue post;
            public final boolean catmullrom;

            public Keyframe(float time, KeyframeValue value, KeyframeValue pre, KeyframeValue post,
                    boolean catmullrom) {
                this.time = time;
                this.value = value;
                this.pre = pre != null ? pre : value;
                this.post = post != null ? post : value;
                this.catmullrom = catmullrom;
            }
        }

        public static class KeyframeValue {
            public final Vec3 vector;
            public final String molang;

            public KeyframeValue(Vec3 vector) {
                this.vector = vector;
                this.molang = null;
            }

            public KeyframeValue(String molang) {
                this.molang = molang;
                this.vector = null;
            }

            public boolean isMolang() {
                return molang != null;
            }
        }

        private List<Keyframe> parseTransform(JsonObject bone, String key) {
            List<Keyframe> result = new ArrayList<>();
            if (!bone.has(key)) {
                return result;
            }
            JsonElement element = bone.get(key);
            if (element.isJsonArray()) {
                result.add(new Keyframe(0f, parseValue(element), null, null, false));
            } else if (element.isJsonPrimitive()) {
                result.add(new Keyframe(0f, parseValue(element), null, null, false));
            } else if (element.isJsonObject()) {
                JsonObject keyframes = element.getAsJsonObject();
                for (String timeStr : keyframes.keySet()) {
                    float time = Float.parseFloat(timeStr);
                    JsonElement frameValue = keyframes.get(timeStr);
                    if (frameValue.isJsonArray() || frameValue.isJsonPrimitive()) {
                        result.add(new Keyframe(time, parseValue(frameValue), null, null, false));
                    } else if (frameValue.isJsonObject()) {
                        JsonObject frameObj = frameValue.getAsJsonObject();
                        KeyframeValue value = frameObj.has("post") ? parseValue(frameObj.get("post"))
                                : parseValue(frameValue);
                        KeyframeValue pre = frameObj.has("pre") ? parseValue(frameObj.get("pre")) : null;
                        KeyframeValue post = frameObj.has("post") ? parseValue(frameObj.get("post")) : null;
                        boolean catmullrom = frameObj.has("lerp_mode")
                                && frameObj.get("lerp_mode").getAsString().equalsIgnoreCase("catmullrom");
                        result.add(new Keyframe(time, value, pre, post, catmullrom));
                    }
                }
            }
            return result;
        }

        private KeyframeValue parseValue(JsonElement element) {
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                boolean hasMolang = false;
                StringBuilder molangArray = new StringBuilder("[");
                for (int i = 0; i < array.size(); i++) {
                    if (i > 0)
                        molangArray.append(",");
                    JsonElement elem = array.get(i);
                    if (elem.isJsonPrimitive()) {
                        JsonPrimitive prim = elem.getAsJsonPrimitive();
                        if (prim.isString()) {
                            hasMolang = true;
                            molangArray.append(prim.getAsString());
                        } else {
                            molangArray.append(prim.getAsFloat());
                        }
                    }
                }
                molangArray.append("]");
                if (hasMolang)
                    return new KeyframeValue(molangArray.toString());
                float x = array.size() > 0 && array.get(0).isJsonPrimitive() ? array.get(0).getAsFloat() : 0;
                float y = array.size() > 1 && array.get(1).isJsonPrimitive() ? array.get(1).getAsFloat() : 0;
                float z = array.size() > 2 && array.get(2).isJsonPrimitive() ? array.get(2).getAsFloat() : 0;
                return new KeyframeValue(new Vec3(x, y, z));
            }
            if (element.isJsonPrimitive()) {
                JsonPrimitive prim = element.getAsJsonPrimitive();
                if (prim.isString())
                    return new KeyframeValue(prim.getAsString());
                float value = prim.getAsFloat();
                return new KeyframeValue(new Vec3(value, value, value));
            }
            return new KeyframeValue(Vec3.ZERO);
        }

        public static Vec3 interpolate(List<Keyframe> keyframes, float time, LivingEntity entity) {
            if (keyframes.isEmpty())
                return null;
            if (keyframes.size() == 1) {
                Keyframe kf = keyframes.get(0);
                return kf.value.isMolang() ? evalMolang(kf.value.molang, time, entity) : kf.value.vector;
            }
            Keyframe lastKf = null;
            Keyframe nextKf = null;
            int lastIdx = -1;
            for (int i = 0; i < keyframes.size(); i++) {
                Keyframe kf = keyframes.get(i);
                if (time >= kf.time) {
                    lastKf = kf;
                    lastIdx = i;
                }
                if (time < kf.time) {
                    nextKf = kf;
                    break;
                }
            }
            if (lastKf == null)
                return null;
            Vec3 postVec = lastKf.post.isMolang() ? evalMolang(lastKf.post.molang, time, entity) : lastKf.post.vector;
            if (nextKf == null)
                return postVec;
            float t1 = lastKf.time;
            float t2_ = nextKf.time;
            if (t1 == t2_)
                return postVec;
            float alpha = (time - t1) / (t2_ - t1);
            Vec3 v1 = postVec;
            Vec3 v2 = nextKf.pre.isMolang() ? evalMolang(nextKf.pre.molang, time, entity) : nextKf.pre.vector;
            if (lastKf.catmullrom) {
                Vec3 p0 = v1, p1 = v1, p2 = v2, p3 = v2;
                if (lastIdx > 0) {
                    KeyframeValue kv = keyframes.get(lastIdx - 1).post;
                    p0 = kv.isMolang() ? evalMolang(kv.molang, time, entity) : kv.vector;
                }
                if (lastIdx + 1 < keyframes.size() - 1) {
                    KeyframeValue kv = keyframes.get(lastIdx + 2).pre;
                    p3 = kv.isMolang() ? evalMolang(kv.molang, time, entity) : kv.vector;
                }
                float t = alpha, t2 = t * t, t3 = t2 * t;
                return new Vec3(
                        0.5 * ((2 * p1.x) + (-p0.x + p2.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2
                                + (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3),
                        0.5 * ((2 * p1.y) + (-p0.y + p2.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2
                                + (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3),
                        0.5 * ((2 * p1.z) + (-p0.z + p2.z) * t + (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t2
                                + (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t3));
            }
            return new Vec3(v1.x + (v2.x - v1.x) * alpha, v1.y + (v2.y - v1.y) * alpha, v1.z + (v2.z - v1.z) * alpha);
        }

        private static Vec3 evalMolang(String expr, float time, LivingEntity entity) {
            Player player = entity instanceof Player ? (Player) entity : null;
            expr = preprocessMolangQueries(expr, time, entity);
            try {
                if (expr.trim().startsWith("[") && expr.trim().endsWith("]")) {
                    String inner = expr.trim().substring(1, expr.trim().length() - 1);
                    String[] parts = inner.split(",");
                    return new Vec3(parts.length > 0 ? evalFloat(parts[0].trim(), time, player) : 0,
                            parts.length > 1 ? evalFloat(parts[1].trim(), time, player) : 0,
                            parts.length > 2 ? evalFloat(parts[2].trim(), time, player) : 0);
                }
                float val = evalFloat(expr, time, player);
                return new Vec3(val, val, val);
            } catch (Exception e) {
                e.printStackTrace();
                return Vec3.ZERO;
            }
        }

        private static float evalFloat(String expr, float time, LivingEntity entity) {
            Player player = entity instanceof Player ? (Player) entity : null;
            if (expr == null || expr.isEmpty())
                return 0.0f;
            expr = expr.trim().replace(" ", "");
            String lower = expr.toLowerCase();
            if (lower.startsWith("math.sin(") && lower.endsWith(")")) {
                return (float) Math.sin(Math.toRadians(evalFloat(expr.substring(9, expr.length() - 1), time, player)));
            }
            if (lower.startsWith("math.cos(") && lower.endsWith(")")) {
                return (float) Math.cos(Math.toRadians(evalFloat(expr.substring(9, expr.length() - 1), time, player)));
            }
            if (lower.startsWith("math.tan(") && lower.endsWith(")")) {
                return (float) Math.tan(Math.toRadians(evalFloat(expr.substring(9, expr.length() - 1), time, player)));
            }
            if (lower.startsWith("math.abs(") && lower.endsWith(")")) {
                return Math.abs(evalFloat(expr.substring(9, expr.length() - 1), time, player));
            }
            if (lower.startsWith("math.sqrt(") && lower.endsWith(")")) {
                return (float) Math.sqrt(evalFloat(expr.substring(10, expr.length() - 1), time, player));
            }
            if (lower.startsWith("math.pow(") && lower.endsWith(")")) {
                String inner = expr.substring(9, expr.length() - 1);
                int commaPos = findTopLevelComma(inner);
                if (commaPos != -1) {
                    float base = evalFloat(inner.substring(0, commaPos), time, player);
                    float exp = evalFloat(inner.substring(commaPos + 1), time, player);
                    return (float) Math.pow(base, exp);
                }
            }
            if (lower.startsWith("math.min(") && lower.endsWith(")")) {
                String inner = expr.substring(9, expr.length() - 1);
                int commaPos = findTopLevelComma(inner);
                if (commaPos != -1) {
                    return Math.min(evalFloat(inner.substring(0, commaPos), time, player),
                            evalFloat(inner.substring(commaPos + 1), time, player));
                }
            }
            if (lower.startsWith("math.max(") && lower.endsWith(")")) {
                String inner = expr.substring(9, expr.length() - 1);
                int commaPos = findTopLevelComma(inner);
                if (commaPos != -1) {
                    return Math.max(evalFloat(inner.substring(0, commaPos), time, player),
                            evalFloat(inner.substring(commaPos + 1), time, player));
                }
            }
            if (lower.startsWith("math.clamp(") && lower.endsWith(")")) {
                String inner = expr.substring(11, expr.length() - 1);
                List<String> parts = new ArrayList<>();
                int depth = 0;
                int start = 0;
                for (int i = 0; i < inner.length(); i++) {
                    char c = inner.charAt(i);
                    if (c == '(')
                        depth++;
                    else if (c == ')')
                        depth--;
                    else if (c == ',' && depth == 0) {
                        parts.add(inner.substring(start, i));
                        start = i + 1;
                    }
                }
                parts.add(inner.substring(start));
                if (parts.size() == 3) {
                    float val = evalFloat(parts.get(0), time, player);
                    float min = evalFloat(parts.get(1), time, player);
                    float max = evalFloat(parts.get(2), time, player);
                    return Math.max(min, Math.min(max, val));
                }
            }
            int depth = 0;
            for (int i = expr.length() - 1; i >= 0; i--) {
                char c = expr.charAt(i);
                if (c == ')')
                    depth++;
                else if (c == '(')
                    depth--;
                else if (depth == 0) {
                    if (c == '+') {
                        return evalFloat(expr.substring(0, i), time, player)
                                + evalFloat(expr.substring(i + 1), time, player);
                    } else if (c == '-' && i > 0) {
                        char prev = expr.charAt(i - 1);
                        boolean isOperator = prev != '+' && prev != '-' && prev != '*' && prev != '/' && prev != '('
                                && prev != 'E' && prev != 'e';
                        if (isOperator) {
                            return evalFloat(expr.substring(0, i), time, player)
                                    - evalFloat(expr.substring(i + 1), time, player);
                        }
                    }
                }
            }
            depth = 0;
            for (int i = expr.length() - 1; i >= 0; i--) {
                char c = expr.charAt(i);
                if (c == ')')
                    depth++;
                else if (c == '(')
                    depth--;
                else if (depth == 0) {
                    if (c == '*') {
                        return evalFloat(expr.substring(0, i), time, player)
                                * evalFloat(expr.substring(i + 1), time, player);
                    }
                    if (c == '/') {
                        float denominator = evalFloat(expr.substring(i + 1), time, player);
                        return denominator == 0 ? 0 : evalFloat(expr.substring(0, i), time, player) / denominator;
                    }
                }
            }
            if (expr.startsWith("-")) {
                return -evalFloat(expr.substring(1), time, player);
            }
            try {
                return Float.parseFloat(expr);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }

        private static String preprocessMolangQueries(String expr, float time, LivingEntity entity) {
            if (entity == null)
                return expr;
            
            if (entity.level() != null && !entity.level().isClientSide())
                return expr;
            Player player = entity instanceof Player ? (Player) entity : null;
            java.util.function.Function<Float, String> fmt = (val) -> String.format(java.util.Locale.ROOT, "%.6f", val);
            Minecraft mc = Minecraft.getInstance();
            float xRot = player != null ? Mth.wrapDegrees(player.getXRot()) : 0f;
            float yRot = player != null ? Mth.wrapDegrees(player.getYRot()) : 0f;
            float health = entity.getHealth();
            float maxHealth = entity.getMaxHealth();
            float limbSwing = entity.walkAnimation.position();
            float limbSwingAmount = entity.walkAnimation.speed();
            float hurtTime = (float) entity.hurtTime;
            float deathTime = (float) entity.deathTime;
            String expr2 = expr
                    .replace("query.anim_time", fmt.apply(time))
                    .replace("query.head_x_rotation", fmt.apply(xRot))
                    .replace("query.head_y_rotation", fmt.apply(yRot))
                    .replace("query.body_x_rotation",
                            fmt.apply(player != null
                                    ? Mth.wrapDegrees(Mth.lerp(mc.getTimer().getGameTimeDeltaPartialTick(true), player.xRotO, player.getXRot()))
                                    : 0f))
                    .replace("query.body_y_rotation",
                            fmt.apply(player != null
                                    ? Mth.wrapDegrees(Mth.rotLerp(mc.getTimer().getGameTimeDeltaPartialTick(true), player.yBodyRotO, player.yBodyRot))
                                    : 0f))
                    .replace("query.life_time", fmt.apply(entity.tickCount / 20.0f))
                    .replace("query.health", fmt.apply(health))
                    .replace("query.max_health", fmt.apply(maxHealth))
                    .replace("query.is_on_ground", entity.onGround() ? "1.0" : "0.0")
                    .replace("query.is_in_water", entity.isInWater() ? "1.0" : "0.0")
                    .replace("query.is_sneaking", entity.isCrouching() ? "1.0" : "0.0")
                    .replace("query.is_sprinting", entity.isSprinting() ? "1.0" : "0.0")
                    .replace("query.is_swimming", entity.isSwimming() ? "1.0" : "0.0")
                    .replace("query.is_riding", entity.isPassenger() ? "1.0" : "0.0")
                    .replace("query.is_sleeping", entity instanceof Player p && p.isSleeping() ? "1.0" : "0.0")
                    .replace("query.is_alive", entity.isAlive() ? "1.0" : "0.0")
                    .replace("query.is_gliding", entity.isFallFlying() ? "1.0" : "0.0")
                    .replace("query.ground_speed",
                            fmt.apply((float) Math.sqrt(entity.getDeltaMovement().x * entity.getDeltaMovement().x
                                    + entity.getDeltaMovement().z * entity.getDeltaMovement().z)))
                    .replace("query.vertical_speed", fmt.apply((float) entity.getDeltaMovement().y))
                    .replace("query.speed", fmt.apply((float) entity.getDeltaMovement().length()))
                    .replace("query.limb_swing", fmt.apply(limbSwing))
                    .replace("query.limb_swing_amount", fmt.apply(limbSwingAmount))
                    .replace("query.modified_move_speed", fmt.apply(limbSwingAmount))
                    .replace("query.walk_anim_speed", fmt.apply(limbSwingAmount))
                    .replace("query.modified_distance_moved", fmt.apply(limbSwing))
                    .replace("query.hurt_time", fmt.apply(hurtTime))
                    .replace("query.death_time", fmt.apply(deathTime));

            if (player != null) {
                expr2 = expr2
                        .replace("query.swing_progress", fmt.apply(player.getAttackAnim(1.0f)))
                        .replace("query.is_attacking", player.getAttackAnim(1.0f) > 0.01f ? "1.0" : "0.0")
                        .replace("query.is_using_item", player.isUsingItem() ? "1.0" : "0.0")
                        .replace("query.use_item_interval", fmt.apply((float) player.getUseItemRemainingTicks()))
                        .replace("query.is_first_person", mc.options.getCameraType().isFirstPerson() ? "1.0" : "0.0")
                        .replace("query.main_hand_item_use_duration",
                                player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND
                                        ? fmt.apply((float) player.getUseItemRemainingTicks())
                                        : "0.0")
                        .replace("query.yaw_speed",
                                fmt.apply(Math.abs(Mth.wrapDegrees(player.getYRot() - player.yRotO))))
                        .replace("query.position_delta_x", fmt.apply((float) player.getDeltaMovement().x))
                        .replace("query.position_delta_y", fmt.apply((float) player.getDeltaMovement().y))
                        .replace("query.position_delta_z", fmt.apply((float) player.getDeltaMovement().z));
            } else {
                expr2 = expr2
                        .replace("query.swing_progress", "0.0")
                        .replace("query.is_attacking", "0.0")
                        .replace("query.is_using_item", "0.0")
                        .replace("query.use_item_interval", "0.0")
                        .replace("query.is_first_person", "0.0")
                        .replace("query.main_hand_item_use_duration", "0.0")
                        .replace("query.yaw_speed", fmt.apply(0f))
                        .replace("query.position_delta_x", fmt.apply((float) entity.getDeltaMovement().x))
                        .replace("query.position_delta_y", fmt.apply((float) entity.getDeltaMovement().y))
                        .replace("query.position_delta_z", fmt.apply((float) entity.getDeltaMovement().z));
            }
            return expr2;
        }

        private static int findTopLevelComma(String expr) {
            int depth = 0;
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                if (c == '(')
                    depth++;
                else if (c == ')')
                    depth--;
                else if (c == ',' && depth == 0)
                    return i;
            }
            return -1;
        }
    }

    
    public static final class ModAnimationsCache {
        private ModAnimationsCache() {
        }

        public static float getProgress(Player player) {
            return player.getPersistentData().getFloat("AnimationProgress");
        }

        public static float getLastTick(Player player) {
            return player.getPersistentData().getFloat("AnimationLastTick");
        }

        public static float getSpeed(Player player) {
            float speed = player.getPersistentData().getFloat("animation_speed");
            return speed > 0 ? speed : 1.0f;
        }
    }

    
    public static class AnimationEvent {
        public final String type;
        public final JsonObject data;

        public AnimationEvent(JsonObject json) {
            this.type = json.has("type") ? json.get("type").getAsString() : "custom";
            this.data = json;
        }

        public String getString(String key, String defaultValue) {
            return data.has(key) ? data.get(key).getAsString() : defaultValue;
        }

        public float getFloat(String key, float defaultValue) {
            return data.has(key) ? data.get(key).getAsFloat() : defaultValue;
        }

        public int getInt(String key, int defaultValue) {
            return data.has(key) ? data.get(key).getAsInt() : defaultValue;
        }
    }

    
    public static class RandomAnimationGroup {
        public final List<GroupEntry> animations;
        public final boolean noRepeat;
        public String lastPlayed = "";
        private static final Random RNG = new Random();

        public RandomAnimationGroup(List<GroupEntry> animations, boolean noRepeat) {
            this.animations = animations;
            this.noRepeat = noRepeat;
        }

        public String pick() {
            if (animations.isEmpty())
                return "";
            int totalWeight = 0;
            for (GroupEntry entry : animations) {
                if (!noRepeat || !entry.name.equals(lastPlayed)) {
                    totalWeight += entry.weight;
                }
            }
            if (totalWeight <= 0) {
                totalWeight = 0;
                for (GroupEntry entry : animations) {
                    totalWeight += entry.weight;
                }
                if (totalWeight <= 0) {
                    return animations.get(RNG.nextInt(animations.size())).name;
                }
                lastPlayed = "";
            }
            int roll = RNG.nextInt(totalWeight);
            int cumulative = 0;
            for (GroupEntry entry : animations) {
                if (!noRepeat || !entry.name.equals(lastPlayed)) {
                    cumulative += entry.weight;
                    if (roll < cumulative) {
                        lastPlayed = entry.name;
                        return entry.name;
                    }
                }
            }
            return animations.get(RNG.nextInt(animations.size())).name;
        }

        public static RandomAnimationGroup fromJson(JsonObject json) {
            List<GroupEntry> entries = new ArrayList<>();
            if (json.has("animations")) {
                JsonArray arr = json.getAsJsonArray("animations");
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject entryObj = arr.get(i).getAsJsonObject();
                    String name = entryObj.get("name").getAsString();
                    int weight = entryObj.has("weight") ? entryObj.get("weight").getAsInt() : 1;
                    entries.add(new GroupEntry(name, Math.max(1, weight)));
                }
            }
            boolean noRepeat = json.has("no_repeat") && json.get("no_repeat").getAsBoolean();
            return new RandomAnimationGroup(entries, noRepeat);
        }

        public static class GroupEntry {
            public final String name;
            public final int weight;

            public GroupEntry(String name, int weight) {
                this.name = name;
                this.weight = weight;
            }
        }
    }

    @EventBusSubscriber(modid = JaamsWeaponryMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class AnimationLoader {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                loadClientSideAnimations();
            });
        }

        private static void loadClientSideAnimations() {
            List<JsonObject> jsons = new ArrayList<>();
            List<String> namespaces = new ArrayList<>();
            ModList.get().getModFiles().forEach(modFileInfo -> {
                String modId = modFileInfo.getMods().get(0).getModId();
                Path rootPath = modFileInfo.getFile().findResource("data");
                if (rootPath == null || !Files.exists(rootPath)) {
                    return;
                }
                try {
                    Path animationsPath = rootPath.resolve(modId).resolve("bedrock_animations");
                    if (Files.exists(animationsPath) && Files.isDirectory(animationsPath)) {
                        try (Stream<Path> paths = Files.walk(animationsPath)) {
                            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json"))
                                    .forEach(animationFile -> {
                                        try {
                                            String content = Files.readString(animationFile, StandardCharsets.UTF_8);
                                            JsonObject jsonObject = new Gson().fromJson(content, JsonObject.class);
                                            jsons.add(jsonObject);
                                            namespaces.add(modId);
                                        } catch (Exception e) {
                                            System.err.println(
                                                    "Failed to load animation file: " + animationFile + " - "
                                                            + e.getMessage());
                                        }
                                    });
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to process animations for mod: " + modId + " - " + e.getMessage());
                }
            });
            if (!jsons.isEmpty()) {
                loadAnimations(jsons, namespaces);
            }
        }

        private static void loadAnimations(List<JsonObject> jsons, List<String> namespaces) {
            for (JsonObject animationJson : jsons) {
                JsonObject sourceAnimations = animationJson.getAsJsonObject("animations");
                if (sourceAnimations != null) {
                    for (Map.Entry<String, JsonElement> entry : sourceAnimations.entrySet()) {
                        String animationName = entry.getKey();
                        JsonObject animObj = entry.getValue().getAsJsonObject();
                        
                        if (animObj.has("type") && "random_group".equals(animObj.get("type").getAsString())) {
                            RandomAnimationGroup group = RandomAnimationGroup.fromJson(animObj);
                            AnimationAPI.randomGroups.put(animationName, group);
                        } else {
                            AnimationAPI.animations.put(animationName, new PlayerAnimation(animObj));
                        }
                    }
                }
            }
        }
    }
}
