package xyz.msws.supergive.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.logging.Level;

/**
 * Useful utility class for messages
 *
 * @author imodm
 */
public class MSG {
    public static ChatColor ALL = ChatColor.WHITE;
    public static ChatColor PLAYER = ChatColor.YELLOW;
    public static ChatColor STAFF = ChatColor.GOLD;

    public static ChatColor ADMIN = ChatColor.RED;

    public static final ChatColor DEFAULT = ChatColor.GRAY;

    public static ChatColor FORMATTER = ChatColor.GRAY;
    public static final ChatColor FORMAT_INFO = ChatColor.GREEN;
    public static ChatColor FORMAT_SEPARATOR = ChatColor.YELLOW;

    public static final ChatColor NUMBER = ChatColor.YELLOW;
    public static ChatColor TIME = ChatColor.GOLD;
    public static ChatColor DATE = ChatColor.DARK_GREEN;
    public static ChatColor MONEY = ChatColor.GREEN;

    public static final ChatColor SUBJECT = ChatColor.AQUA;

    public static final ChatColor PREFIX = ChatColor.BLUE;

    public static ChatColor ERROR = ChatColor.RED;
    public static ChatColor FAIL = ChatColor.RED;
    public static final ChatColor SUCCESS = ChatColor.GREEN;

    public static final ChatColor BOLD = ChatColor.BOLD;
    public static ChatColor ITALIC = ChatColor.ITALIC;
    public static ChatColor MAGIC = ChatColor.MAGIC;
    public static ChatColor UNDER = ChatColor.UNDERLINE;
    public static ChatColor STRIKE = ChatColor.STRIKETHROUGH;
    public static final ChatColor RESET = ChatColor.RESET;

    public static void log(String message) {
        Bukkit.getLogger().log(Level.INFO, message);
    }

    public static void warn(String message) {
        Bukkit.getLogger().log(Level.WARNING, message);
    }

    public static void error(String message) {
        Bukkit.getLogger().log(Level.SEVERE, message);
    }

    public static void tell(CommandSender sender, Object msg) {
        if (msg == null)
            return;
        if (msg instanceof Collection<?>) {
            ((Collection<?>) msg).forEach((obj) -> tell(sender, obj));
        } else if (msg instanceof Object[]) {
            for (Object obj : (Object[]) msg)
                tell(sender, obj);
        } else {
            sender.sendMessage(color(msg.toString()));
        }
    }

    public static void log(Object message) {
        tell(Bukkit.getConsoleSender(), message);
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void tell(CommandSender sender, String module, String message) {
        tell(sender, PREFIX + module + "> " + DEFAULT + message);
    }

    public static void announce(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> tell(p, message));
    }

    public static void announce(String perm, String message) {
        Bukkit.getOnlinePlayers().parallelStream().filter(p -> p.hasPermission(perm)).forEach(p -> tell(p, message));
    }

    /**
     * Simplify a string for easy key usage
     *
     * @param value
     * @return a-zA-Z regex compatible
     */
    public static String normalize(String value) {
        return value.toLowerCase().replaceAll("[^a-z]", "");
    }

    /**
     * Returns string with camel case, and with _'s replaced with spaces
     *
     * @param string hello_how is everyone
     * @return Hello How Is Everyone
     */
    public static String camelCase(String string) {
        String prevChar = " ";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (i > 0)
                prevChar = string.charAt(i - 1) + "";
            if (prevChar.matches("[a-zA-Z]")) {
                res.append((string.charAt(i) + "").toLowerCase());
            } else {
                res.append((string.charAt(i) + "").toUpperCase());
            }
        }
        return res.toString().replace("_", " ");
    }

    public static String plural(String name) {
        return name + (name.toLowerCase().endsWith("s") ? "'" : "'s");
    }

    public static String plural(String name, int amo) {
        if (amo != 1)
            return plural(name);
        return name;
    }

    public static String getTime(long mils) {
        if (mils == 0) {
            return "Just Now";
        }

        boolean isNegative = mils < 0;
        double mil = Math.abs(mils);
        String[] names = {"milliseconds", "seconds", "minutes", "hours", "days", "weeks", "months", "years", "decades",
                "centuries"};
        String[] sNames = {"millisecond", "second", "minute", "hour", "day", "week", "month", "year", "decade",
                "century"};
        Double[] length = {1.0, 1000.0, 60000.0, 3.6e+6, 8.64e+7, 6.048e+8, 2.628e+9, 3.154e+10, 3.154e+11,
                3.154e+12};
        String suff = "";
        for (int i = length.length - 1; i >= 0; i--) {
            if (mil >= length[i]) {
                mil = mil / length[i];
                if (mil == 1)
                    suff = sNames[i];
                break;
            }
        }
        String name = mil + "";
        if (Math.round(mil) == mil) {
            name = (int) Math.round(mil) + "";
        }
        if (name.contains(".")) {
            if (name.split("\\.")[1].length() > 2)
                name = parseDecimal(name, 2);
        }
        if (isNegative)
            name = "-" + name;
        return name + " " + suff;
    }

    public static String parseDecimal(double decimal, int length) {
        return String.format("%." + length + "f", decimal);
    }

    public static String parseDecimal(String decimal, int length) {
        return parseDecimal(Double.parseDouble(decimal), length);
    }
}
