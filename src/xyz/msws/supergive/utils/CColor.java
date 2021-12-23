package xyz.msws.supergive.utils;

import org.bukkit.Color;

/**
 * A wrapper to easily get {@link Color} from strings.
 *
 * @author imodm
 */
public enum CColor {
    AQUA(Color.AQUA), BLACK(Color.BLACK), BLUE(Color.BLUE), FUCHSIA(Color.FUCHSIA), GRAY(Color.GRAY),
    GREEN(Color.GREEN), LIME(Color.LIME), MAROON(Color.MAROON), NAVY(Color.NAVY), OLIVE(Color.OLIVE),
    ORANGE(Color.ORANGE), PURPLE(Color.PURPLE), RED(Color.RED), SILVER(Color.SILVER), TEAL(Color.TEAL),
    WHITE(Color.WHITE), YELLOW(Color.YELLOW);

    private final Color bColor;

    CColor(Color bukkit) {
        this.bColor = bukkit;
    }

    public Color bukkit() {
        return bColor;
    }

    public static CColor fromBukkit(Color col) {
        for (CColor c : values()) {
            if (c.bukkit().equals(col))
                return c;
        }
        return null;
    }
}
