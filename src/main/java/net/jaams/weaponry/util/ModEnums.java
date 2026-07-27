package net.jaams.weaponry.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class ModEnums {

    public enum ThrowMode {
        CHARGE_AND_RELEASE,
        INSTANT_ON_RIGHT_CLICK,
        CHARGE_AND_FINISH_USING,
        CHARGE_RELEASE_AND_FINISH
    }

    public enum GunFirePattern {
        DEFAULT,
        HORIZONTAL,
        VERTICAL,
        CIRCLE,
        HEART;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static GunFirePattern fromString(String str) {
            if (str == null)
                return DEFAULT;
            try {
                return valueOf(str.toUpperCase());
            } catch (IllegalArgumentException e) {
                return DEFAULT;
            }
        }
    }

    public enum AberrationType {
        NONE,
        SHAKE,
        BLUR,
        DISTORT
    }

    public enum KeyOption {
        ALT(InputConstants.KEY_LALT, InputConstants.KEY_RALT),
        SHIFT(InputConstants.KEY_LSHIFT, InputConstants.KEY_RSHIFT),
        CONTROL(InputConstants.KEY_LCONTROL, InputConstants.KEY_RCONTROL);

        public final int leftKey;
        public final int rightKey;

        KeyOption(int leftKey, int rightKey) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
        }

        public boolean isPressed() {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), leftKey)
                    || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), rightKey);
        }
    }

    public enum BackgroundColorOption {
        BLACK(0xA0000000), 
        WHITE(0xA0E0E0E0), 
        LIGHT_GRAY(0xA0D3D3D3), 
        DARK_GRAY(0xA0909090), 
        RED(0xA0CC6666), 
        GREEN(0xA06DBB6D), 
        BLUE(0xA06699CC), 
        PINK(0xA0FFB6C1), 
        ORANGE(0xA0FFA07A), 
        LAVENDER(0xA0E6E6FA), 
        MINT(0xA0BDFCC9), 
        SANDY_BEIGE(0xA0F5DEB3), 
        GOLDEN(0xA0FFD700), 
        OLIVE(0xA08FBC8F), 
        PALE_YELLOW(0xA0FFFACD), 
        LIGHT_BROWN(0xA0D2B48C), 
        BROWN(0xFF704840), 
        CYAN(0xA000FFFF), 
        PURPLE(0xA0800080), 
        TEAL(0xA0008080), 
        BEIGE(0xA0F5F5DC), 
        TURQUOISE(0xA040E0D0), 
        FUCHSIA(0xA0FF00FF); 

        private final int color;

        BackgroundColorOption(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    public enum TransparencyOption {
        TRANSPARENT(0x00000000),
        SEMI_TRANSPARENT(0x7F000000),
        OPAQUE(0xFF000000);

        private final int alpha;

        TransparencyOption(int alpha) {
            this.alpha = alpha;
        }

        public int getAlpha() {
            return this.alpha;
        }
    }

    public enum QuickSwapMode {
        INSTANT_ON_RIGHT_CLICK,
        CHARGE_AND_RELEASE,
        CHARGE_AND_FINISH_USING,
        CHARGE_RELEASE_AND_FINISH
    }

    public enum SlashAssaultMode {
        SPRINT_CLICK,
        INSTANT_ON_RIGHT_CLICK,
        CHARGE_AND_RELEASE,
        CHARGE_AND_FINISH_USING,
        CHARGE_RELEASE_AND_FINISH
    }

    public enum PiercingAssaultMode {
        SPRINT_CLICK,
        INSTANT_ON_RIGHT_CLICK,
        CHARGE_AND_RELEASE,
        CHARGE_AND_FINISH_USING,
        CHARGE_RELEASE_AND_FINISH
    }

    public enum ShockImpactMode {
        INSTANT_ON_RIGHT_CLICK,
        CHARGE_AND_RELEASE,
        CHARGE_AND_FINISH_USING,
        CHARGE_RELEASE_AND_FINISH
    }

    public enum OverlayPosition {
        TOP,
        BOTTOM
    }
}
