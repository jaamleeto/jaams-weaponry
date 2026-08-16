package net.jaams.weaponry.condition;

import com.google.gson.JsonElement;

public interface ConditionSource {

    String type();

    String modId();

    String enchantment();

    int level();

    String key();

    String nbtType();

    String item();

    String tag();

    String rarity();

    int nbtIntValue();

    boolean nbtBooleanValue();

    short nbtShortValue();

    long nbtLongValue();

    String nbtStringValue();

    String component();

    JsonElement componentValue();
}
