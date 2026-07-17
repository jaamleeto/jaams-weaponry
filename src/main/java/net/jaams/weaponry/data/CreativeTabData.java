package net.jaams.weaponry.data;

import java.util.List;
import java.util.ArrayList;

public class CreativeTabData {
    public String tab_id = "weaponry";
    public Boolean enabled = true;
    public int priority = 0;
    public List<Entry> entries = new ArrayList<>();

    public static class Entry {
        public String item;
        public String nbt;
        public boolean remove = false;
        public boolean clear_defaults = false;
        public int weight = 0;
        public String after;
        public String before;
        public String section;
        public String condition_mode = "and";
        public List<Condition> conditions = new ArrayList<>();
    }

    public static class Condition {
        public String mod_id;
    }
}
