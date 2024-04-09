package me.leminhbao.mitiergen.config;

public class ConfigConstants {
    public static final double DISABLED_MIN = 0.0;
    public static final double DISABLED_MAX = 0.0;

    public static final String DATA_TYPE_PERCENT = "PERCENT";
    public static final String DATA_TYPE_NUMBER = "NUMBER";

    public static class PLACEHOLDERS {
        public static final String NEXT_PAGE_ITEM = "nextPageItem";
        public static final String PREVIOUS_PAGE_ITEM = "previousPageItem";
        public static final String BACK_ITEM = "backItem";

        // Tier
        public static final String ADD_TIER_ITEM = "addTierItem";

        // Weapon type
        public static final String ADD_WEAPON_TYPE_ITEM = "addWeaponTypeItem";

        // Stat
        public static final String ADD_STAT_ITEM = "addStatItem";
        public static final String ADD_STAT_TYPE_ITEM = "addStatTypeItem";
        public static final String ADD_STAT_MIN_ITEM = "addStatMinItem";
        public static final String ADD_STAT_MAX_ITEM = "addStatMaxItem";

        public static final String TIER_DISPLAY = "tierDisplay";
        public static final String WEAPON_TYPE_DISPLAY = "weaponTypeDisplay";
        public static final String STAT_DISPLAY = "statDisplay";

        public static boolean notMatches(String s) {
            return !s.equals(NEXT_PAGE_ITEM) &&
                    !s.equals(PREVIOUS_PAGE_ITEM) &&
                    !s.equals(BACK_ITEM) &&
                    !s.equals(ADD_TIER_ITEM) &&
                    !s.equals(ADD_WEAPON_TYPE_ITEM) &&
                    !s.equals(ADD_STAT_ITEM) &&
                    !s.equals(ADD_STAT_TYPE_ITEM) &&
                    !s.equals(ADD_STAT_MIN_ITEM) &&
                    !s.equals(ADD_STAT_MAX_ITEM) &&
                    !s.equals(TIER_DISPLAY) &&
                    !s.equals(WEAPON_TYPE_DISPLAY) &&
                    !s.equals(STAT_DISPLAY);
        }
    }
}
