package org.betamc.diamondlogger;

import org.bukkit.Material;

public enum OreType {

    COAL("Coal Ore"),
    IRON("Iron Ore"),
    GOLD("Gold Ore"),
    LAPIS("Lapis Ore"),
    DIAMOND("Diamond Ore"),
    REDSTONE("Redstone Ore");

    private final String oreName;

    OreType(String oreName) {
        this.oreName = oreName;
    }

    public String getOreName() {
        return this.oreName;
    }

    public static OreType getByMaterial(Material material) {
        switch (material) {
            case COAL_ORE:
                return COAL;
            case IRON_ORE:
                return IRON;
            case GOLD_ORE:
                return GOLD;
            case LAPIS_ORE:
                return LAPIS;
            case DIAMOND_ORE:
                return DIAMOND;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                return REDSTONE;
            default:
                return null;
        }
    }
}
