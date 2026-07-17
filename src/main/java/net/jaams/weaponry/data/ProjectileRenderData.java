package net.jaams.weaponry.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;

public class ProjectileRenderData {
	private final CompoundTag tag;

	public ProjectileRenderData(ItemStack stack) {
		this.tag = stack.hasTag() ? stack.getTag() : null;
	}

	public float getFloat(String key, float def) {
		return tag != null && tag.contains(key, Tag.TAG_FLOAT) ? tag.getFloat(key) : def;
	}

	public String getString(String key, String def) {
		return tag != null && tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : def;
	}

	public ItemDisplayContext getDisplay(String key, ItemDisplayContext def) {
		if (tag != null && tag.contains(key, Tag.TAG_STRING)) {
			try {
				return ItemDisplayContext.valueOf(tag.getString(key));
			} catch (Exception ignored) {
			}
		}
		return def;
	}
}
