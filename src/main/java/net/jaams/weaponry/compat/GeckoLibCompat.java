package net.jaams.weaponry.compat;

import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.WeakHashMap;

/**
 * Optional compatibility with GeckoLib (Bernie).
 *
 * <p>GeckoLib models are {@link GeoModel} whose bones are {@link GeoBone} rather than
 * Minecraft's {@code ModelPart} or Citadel's {@code BasicModelPart}.
 * The animation API therefore cannot treat them as a {@code ModelPart} and would otherwise
 * crash with a {@link ClassCastException}.</p>
 *
 * <p>This bridge maps the vanilla field set ({@code xRot/yRot/zRot}, {@code x/y/z},
 * {@code xScale/yScale/zScale}) onto GeckoLib's equivalents:</p>
 * <ul>
 *     <li>Rotation is in radians, same as {@code ModelPart} / {@code BasicModelPart}, so it is
 *         passed through directly.</li>
 *     <li>Position is stored in <b>pixels</b> (the geometry pivot space), exactly like Citadel's
 *         {@code rotationPoint}, so we divide by / multiply by 16 to match the block-unit values
 *         the animation API works in.</li>
 *     <li>Scale is a plain multiplier ({@code 1.0} default).</li>
 * </ul>
 */
public final class GeckoLibCompat {
    private static final float POS_FACTOR = 16.0F;
    private static final WeakHashMap<Object, float[]> initialPoses = new WeakHashMap<>();

    private GeckoLibCompat() {
    }

    public static boolean isGeoModel(Object o) {
        return o instanceof GeoModel;
    }

    public static boolean isGeoBone(Object o) {
        return o instanceof GeoBone;
    }

    public static Object getBone(Object model, String boneName) {
        if (!(model instanceof GeoModel<?> geo) || boneName == null) {
            return null;
        }
        // GeckoLib bone names are exact strings (e.g. "leftarm", "leftArm", "left_arm"), while the
        // animation API keys are normalised ("left_arm"). Try the common variants so a single
        // animation can drive any of the naming conventions a Geo model might use.
        for (String candidate : boneNameVariants(boneName)) {
            GeoBone bone = geo.getBone(candidate).orElse(null);
            if (bone != null) {
                captureInitialPose(bone);
                return bone;
            }
        }
        return null;
    }

    private static String[] boneNameVariants(String name) {
        String noUnderscore = name.replace("_", "");
        return new String[] { name, noUnderscore, toCamelCase(name), toCamelCase(noUnderscore) };
    }

    private static String toCamelCase(String name) {
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder(parts[0].toLowerCase(java.util.Locale.ROOT));
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(parts[i].charAt(0)))
                    .append(parts[i].substring(1).toLowerCase(java.util.Locale.ROOT));
        }
        return sb.toString();
    }

    // ==================== Rotation (radians) ====================

    /**
     * GeckoLib (Bedrock) bones use an inverted rotation frame compared to vanilla
     * {@code ModelPart} for the limb bones (same 180° Y-frame mismatch the Epic Fight
     * armature has), so rotations coming from the animation API must be sign-flipped on
     * the arms, legs and torso; otherwise e.g. {@code attack_overhead} swings the arms
     * backwards instead of forwards.
     */
    private static boolean isInvertedFrameBone(GeoBone b) {
        String name = b.getName().toLowerCase(java.util.Locale.ROOT).replace("_", "");
        return name.contains("arm") || name.contains("leg") || name.contains("torso")
                || name.contains("body") || name.contains("chest");
    }

    public static float getXRot(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) b.getRotX();
        }
        return 0f;
    }

    public static void setXRot(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setRotX(isInvertedFrameBone(b) ? -v : v);
        }
    }

    public static float getYRot(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) b.getRotY();
        }
        return 0f;
    }

    public static void setYRot(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setRotY(v);
        }
    }

    public static float getZRot(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) b.getRotZ();
        }
        return 0f;
    }

    public static void setZRot(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setRotZ(isInvertedFrameBone(b) ? -v : v);
        }
    }

    // ==================== Position (block units; stored as pixels) ====================

    public static float getX(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) (b.getPosX() / POS_FACTOR);
        }
        return 0f;
    }

    public static void setX(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setPosX(v * POS_FACTOR);
        }
    }

    public static float getY(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) (b.getPosY() / POS_FACTOR);
        }
        return 0f;
    }

    public static void setY(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setPosY(v * POS_FACTOR);
        }
    }

    public static float getZ(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) (b.getPosZ() / POS_FACTOR);
        }
        return 0f;
    }

    public static void setZ(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setPosZ(v * POS_FACTOR);
        }
    }

    // ==================== Scale ====================

    public static float getXScale(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) b.getScaleX();
        }
        return 1.0F;
    }

    public static void setXScale(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setScaleX(v);
        }
    }

    public static float getYScale(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) b.getScaleY();
        }
        return 1.0F;
    }

    public static void setYScale(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setScaleY(v);
        }
    }

    public static float getZScale(Object bone) {
        if (bone instanceof GeoBone b) {
            return (float) b.getScaleZ();
        }
        return 1.0F;
    }

    public static void setZScale(Object bone, float v) {
        if (bone instanceof GeoBone b) {
            b.setScaleZ(v);
        }
    }

    // ==================== Pose capture / reset ====================

    public static void captureInitialPose(Object bone) {
        if (!(bone instanceof GeoBone b) || initialPoses.containsKey(bone)) {
            return;
        }
        initialPoses.put(bone, new float[] {
                (float) b.getRotX(), (float) b.getRotY(), (float) b.getRotZ(),
                (float) b.getPosX(), (float) b.getPosY(), (float) b.getPosZ(),
                (float) b.getScaleX(), (float) b.getScaleY(), (float) b.getScaleZ()
        });
    }

    public static void resetPosition(Object bone) {
        if (bone instanceof GeoBone b) {
            float[] p = initialPoses.get(bone);
            if (p != null) {
                b.setPosX(p[3]);
                b.setPosY(p[4]);
                b.setPosZ(p[5]);
            }
        }
    }

    public static void resetToInitial(Object bone) {
        if (bone instanceof GeoBone b) {
            float[] p = initialPoses.get(bone);
            if (p != null) {
                b.setRotX(p[0]);
                b.setRotY(p[1]);
                b.setRotZ(p[2]);
                b.setPosX(p[3]);
                b.setPosY(p[4]);
                b.setPosZ(p[5]);
            }
            b.setScaleX(1.0F);
            b.setScaleY(1.0F);
            b.setScaleZ(1.0F);
        }
    }
}
