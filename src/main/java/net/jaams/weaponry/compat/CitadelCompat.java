package net.jaams.weaponry.compat;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicEntityModel;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;

import net.minecraft.client.model.geom.ModelPart;

/**
 * Optional compatibility with the Citadel mod (AlexModGuy).
 *
 * <p>Citadel ships {@link BasicModelPart}, a non-final duplicate of Minecraft's {@link ModelPart}
 * that many entity models (e.g. Alex's Mobs) use instead of the vanilla class. Because it is a
 * separate class hierarchy, the animation API's bone-transform code cannot treat it as a
 * {@code ModelPart} and would otherwise crash with a {@link ClassCastException}.</p>
 *
 * <p>This bridge maps the vanilla {@code ModelPart} fields ({@code xRot/yRot/zRot}, {@code x/y/z},
 * {@code xScale/yScale/zScale}) onto Citadel's equivalents ({@code rotateAngleX/Y/Z},
 * {@code rotationPointX/Y/Z}) and accounts for the fact that Citadel divides the rotation point by
 * 16 when rendering. Citadel is a {@code compileOnly} dependency, so this class only ever touches
 * its types while {@link #isLoaded()} is true; every branch that references a Citadel type is
 * guarded by {@code loaded && ...} so the missing classes are never resolved when the mod is
 * absent.</p>
 */
public final class CitadelCompat {
    private static final float POS_FACTOR = 16.0F;
    private static final boolean loaded;
    private static final java.util.WeakHashMap<Object, float[]> initialPoses = new java.util.WeakHashMap<>();

    static {
        boolean l = false;
        try {
            Class.forName("com.github.alexthe666.citadel.client.model.basic.BasicModelPart");
            l = true;
        } catch (Throwable t) {
            l = false;
        }
        loaded = l;
    }

    private CitadelCompat() {
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean isBasicModelPart(Object o) {
        return loaded && o instanceof BasicModelPart;
    }

    public static boolean isBasicEntityModel(Object o) {
        return loaded && o instanceof BasicEntityModel;
    }

    // ==================== Rotation (radians) ====================

    public static float getXRot(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.xRot;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            return bmp.rotateAngleX;
        }
        return 0f;
    }

    public static void setXRot(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.xRot = v;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            bmp.rotateAngleX = v;
        }
    }

    public static float getYRot(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.yRot;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            return bmp.rotateAngleY;
        }
        return 0f;
    }

    public static void setYRot(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.yRot = v;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            bmp.rotateAngleY = v;
        }
    }

    public static float getZRot(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.zRot;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            return bmp.rotateAngleZ;
        }
        return 0f;
    }

    public static void setZRot(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.zRot = v;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            bmp.rotateAngleZ = v;
        }
    }

    // ==================== Position (block units, same as ModelPart) ====================

    public static float getX(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.x;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            return bmp.rotationPointX / POS_FACTOR;
        }
        return 0f;
    }

    public static void setX(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.x = v;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            bmp.rotationPointX = v * POS_FACTOR;
        }
    }

    public static float getY(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.y;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            return bmp.rotationPointY / POS_FACTOR;
        }
        return 0f;
    }

    public static void setY(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.y = v;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            bmp.rotationPointY = v * POS_FACTOR;
        }
    }

    public static float getZ(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.z;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            return bmp.rotationPointZ / POS_FACTOR;
        }
        return 0f;
    }

    public static void setZ(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.z = v;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            bmp.rotationPointZ = v * POS_FACTOR;
        }
    }

    // ==================== Scale (BasicModelPart has no scale support) ====================

    public static float getXScale(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.xScale;
        }
        if (loaded && bone instanceof AdvancedModelBox amb) {
            return amb.scaleX;
        }
        return 1.0F;
    }

    public static void setXScale(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.xScale = v;
            return;
        }
        if (loaded && bone instanceof AdvancedModelBox amb) {
            amb.scaleX = v;
        }
    }

    public static float getYScale(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.yScale;
        }
        if (loaded && bone instanceof AdvancedModelBox amb) {
            return amb.scaleY;
        }
        return 1.0F;
    }

    public static void setYScale(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.yScale = v;
            return;
        }
        if (loaded && bone instanceof AdvancedModelBox amb) {
            amb.scaleY = v;
        }
    }

    public static float getZScale(Object bone) {
        if (bone instanceof ModelPart mp) {
            return mp.zScale;
        }
        if (loaded && bone instanceof AdvancedModelBox amb) {
            return amb.scaleZ;
        }
        return 1.0F;
    }

    public static void setZScale(Object bone, float v) {
        if (bone instanceof ModelPart mp) {
            mp.zScale = v;
            return;
        }
        if (loaded && bone instanceof AdvancedModelBox amb) {
            amb.scaleZ = v;
        }
    }

    // ==================== Pose capture / reset ====================

    public static void captureInitialPose(Object bone) {
        if (!loaded || !(bone instanceof BasicModelPart bmp) || initialPoses.containsKey(bone)) {
            return;
        }
        initialPoses.put(bone, new float[] {
                bmp.rotateAngleX, bmp.rotateAngleY, bmp.rotateAngleZ,
                bmp.rotationPointX, bmp.rotationPointY, bmp.rotationPointZ
        });
    }

    /** Resets only the rotation point (position) to the captured initial value. */
    public static void resetPosition(Object bone) {
        if (bone instanceof ModelPart mp) {
            var p = mp.getInitialPose();
            mp.x = p.x;
            mp.y = p.y;
            mp.z = p.z;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            float[] p = initialPoses.get(bone);
            if (p != null) {
                bmp.rotationPointX = p[3];
                bmp.rotationPointY = p[4];
                bmp.rotationPointZ = p[5];
            }
        }
    }

    /** Resets rotation, position and scale to the captured initial value. */
    public static void resetToInitial(Object bone) {
        if (bone instanceof ModelPart mp) {
            var p = mp.getInitialPose();
            mp.x = p.x;
            mp.y = p.y;
            mp.z = p.z;
            mp.xRot = p.xRot;
            mp.yRot = p.yRot;
            mp.zRot = p.zRot;
            mp.xScale = 1.0F;
            mp.yScale = 1.0F;
            mp.zScale = 1.0F;
            return;
        }
        if (loaded && bone instanceof AdvancedModelBox amb) {
            float[] p = initialPoses.get(bone);
            if (p != null) {
                amb.rotateAngleX = p[0];
                amb.rotateAngleY = p[1];
                amb.rotateAngleZ = p[2];
                amb.rotationPointX = p[3];
                amb.rotationPointY = p[4];
                amb.rotationPointZ = p[5];
            }
            amb.scaleX = 1.0F;
            amb.scaleY = 1.0F;
            amb.scaleZ = 1.0F;
            return;
        }
        if (loaded && bone instanceof BasicModelPart bmp) {
            float[] p = initialPoses.get(bone);
            if (p != null) {
                bmp.rotateAngleX = p[0];
                bmp.rotateAngleY = p[1];
                bmp.rotateAngleZ = p[2];
                bmp.rotationPointX = p[3];
                bmp.rotationPointY = p[4];
                bmp.rotationPointZ = p[5];
            }
        }
    }

    // ==================== Bone lookup ====================

    /**
     * Best-effort bone lookup by name in a Citadel {@link BasicEntityModel}.
     *
     * <p>For {@link AdvancedEntityModel} instances every box (including nested ones) is exposed
     * through {@code getAllParts()}, so we match directly against each {@code boxName}. This makes
     * models whose named bones hang off a single root part (e.g. {@code ModelUnderminerDwarf}, where
     * everything is parented to {@code body}) fully animatable, since a naive {@code parts()} search
     * would only ever find the root.</p>
     */
    public static Object getBone(Object model, String boneName) {
        if (boneName == null) {
            return null;
        }
        String target = normalizeBoneName(boneName);
        if (model instanceof AdvancedEntityModel<?> aem) {
            for (AdvancedModelBox part : aem.getAllParts()) {
                if (target.equals(normalizeBoneName(part.boxName))) {
                    captureInitialPose(part);
                    return part;
                }
            }
            return null;
        }
        if (isBasicEntityModel(model)) {
            for (BasicModelPart part : ((BasicEntityModel<?>) model).parts()) {
                if (target.equals(normalizeBoneName(getName(part)))) {
                    captureInitialPose(part);
                    return part;
                }
            }
        }
        return null;
    }

    private static String getName(BasicModelPart part) {
        if (part instanceof AdvancedModelBox amb) {
            return amb.boxName;
        }
        return null;
    }

    /**
     * Normalises a bone name so the many naming conventions used across different entity models
     * ({@code left_arm}, {@code arm_left}, {@code leftArm}, {@code LeftArm}, ...) all map to the same
     * logical bone. A name is reduced to {@code <side>_<limb>} when it contains a side and a known
     * limb keyword, so animations can target a bone regardless of how a given model labels it.
     */
    private static String normalizeBoneName(String name) {
        if (name == null) {
            return null;
        }
        String clean = name.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z]", "");
        if (clean.isEmpty()) {
            return "";
        }
        boolean left = clean.contains("left");
        boolean right = clean.contains("right");
        String side = right ? "right" : (left ? "left" : null);
        String limb = null;
        String[] limbs = { "arm", "leg", "wing", "ear", "foot", "hand", "horn", "antler", "eye", "brow", "paw",
                "claw", "finger", "thumb", "toe", "shoulder", "flipper", "tentacle", "tail", "snout", "jaw",
                "muzzle", "beak", "head", "neck", "chest", "body", "torso", "root" };
        for (String l : limbs) {
            if (clean.contains(l)) {
                limb = l;
                break;
            }
        }
        if (side != null && limb != null) {
            return side + "_" + limb;
        }
        if (limb != null) {
            return limb;
        }
        if (side != null) {
            return side;
        }
        return clean;
    }
}
