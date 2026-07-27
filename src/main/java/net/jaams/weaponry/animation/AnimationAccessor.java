package net.jaams.weaponry.animation;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import net.jaams.weaponry.mixins.access.ModelPartAccessorMixin;

public interface AnimationAccessor {
    ModelPart get(String boneName);

    void resetPosition(ModelPart part, String boneName);

    final class Humanoid implements AnimationAccessor {
        private final HumanoidModel<LivingEntity> model;

        public Humanoid(HumanoidModel<LivingEntity> model) {
            this.model = model;
        }

        @Override
        public ModelPart get(String boneName) {
            return switch (boneName) {
                case "torso", "body" -> model.body;
                case "head" -> model.head;
                case "right_arm" -> model.rightArm;
                case "left_arm" -> model.leftArm;
                case "right_leg" -> model.rightLeg;
                case "left_leg" -> model.leftLeg;
                default -> null;
            };
        }

        @Override
        public void resetPosition(ModelPart part, String boneName) {
            switch (boneName) {
                case "torso", "body":
                    part.x = 0f;
                    part.y = 0f;
                    part.z = 0f;
                    break;
                case "head":
                    part.setPos(0.0F, 0.0F, 0.0F);
                    break;
                case "right_arm":
                    part.setPos(-5.0F, 2.0F, 0.0F);
                    break;
                case "left_arm":
                    part.setPos(5.0F, 2.0F, 0.0F);
                    break;
                case "right_leg":
                    part.setPos(-1.9F, 12.0F, 0.1F);
                    break;
                case "left_leg":
                    part.setPos(1.9F, 12.0F, 0.1F);
                    break;
            }
        }
    }

    final class Hierarchical implements AnimationAccessor {
        private final HierarchicalModel<?> model;
        private final Map<String, Vec3> defaultPositions = new HashMap<>();

        public Hierarchical(HierarchicalModel<?> model) {
            this.model = model;
        }

        @Override
        public ModelPart get(String boneName) {
            ModelPart part = findBone(model.root(), boneName);
            if (part == null) {
                String camelName = snakeToCamel(boneName);
                if (!camelName.equals(boneName)) {
                    part = findBone(model.root(), camelName);
                }
            }
            if (part != null) {
                var initPose = part.getInitialPose();
                defaultPositions.putIfAbsent(boneName, new Vec3(initPose.x, initPose.y, initPose.z));
            }
            return part;
        }

        private ModelPart findBone(ModelPart parent, String name) {
            try {
                return parent.getChild(name);
            } catch (NoSuchElementException e) {
                for (ModelPart child : ((ModelPartAccessorMixin) (Object) parent).getChildren().values()) {
                    ModelPart found = findBone(child, name);
                    if (found != null)
                        return found;
                }
                return null;
            }
        }

        private static String snakeToCamel(String s) {
            StringBuilder sb = new StringBuilder();
            boolean upper = false;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '_') {
                    upper = true;
                } else if (upper) {
                    sb.append(Character.toUpperCase(c));
                    upper = false;
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        @Override
        public void resetPosition(ModelPart part, String boneName) {
            Vec3 defaultPos = defaultPositions.get(boneName);
            if (defaultPos != null) {
                part.x = (float) defaultPos.x;
                part.y = (float) defaultPos.y;
                part.z = (float) defaultPos.z;
            } else {
                part.x = 0f;
                part.y = 0f;
                part.z = 0f;
            }
        }
    }
}
