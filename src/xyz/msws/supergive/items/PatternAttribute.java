package xyz.msws.supergive.items;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds support for customizing banners with patterns. Format:
 * [patterntype]:[color]
 *
 * @author imodm
 */
public class PatternAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BannerMeta))
            return item;
        BannerMeta banner = (BannerMeta) meta;
        PatternType type = null;
        DyeColor color = null;

        if (!line.contains(":"))
            return item;
        try {
            type = PatternType.valueOf(line.split(":")[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            return item;
        }

		try {
            color = DyeColor.valueOf(line.split(":")[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            org.bukkit.Color c = Utils.getColor(line.split(":")[1]);
            if (c == null)
                return item;
            color = DyeColor.getByColor(c);
            if (color == null)
                return item;
        }

        Pattern pattern = new Pattern(color, type);
        banner.addPattern(pattern);
        item.setItemMeta(banner);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BannerMeta))
            return null;
        BannerMeta banner = (BannerMeta) meta;
        StringBuilder result = new StringBuilder();
        for (Pattern patt : banner.getPatterns()) {
            result.append(patt.getPattern().toString().toLowerCase()).append(":").append(patt.getColor().toString().toLowerCase())
                    .append(" ");
        }
        return result.toString().trim();
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;

        if (!args[1].toLowerCase().contains("banner"))
            return null;

        List<String> result = new ArrayList<>();

        for (PatternType type : PatternType.values()) {
            if (type.toString().toLowerCase().startsWith(current.toLowerCase())) {
                result.add(type.toString().toLowerCase() + ":");
            }
        }
        if (current.contains(":")) {
            String c = current.split(":").length > 1 ? current.split(":")[1] : "";
            String prev = current.split(":")[0] + ":";

            for (DyeColor color : DyeColor.values()) {
                if (color.toString().toLowerCase().startsWith(c.toLowerCase()))
                    result.add(prev + color.toString().toLowerCase());
            }
        }
        return result;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.pattern";
    }

    @Override
    public String humanReadable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BannerMeta))
            return null;
        BannerMeta banner = (BannerMeta) meta;
        StringBuilder result = new StringBuilder();
        try {
            for (Pattern patt : banner.getPatterns()) {
                result.append(net.md_5.bungee.api.ChatColor.of(new Color(patt.getColor().getColor().asRGB()))).append(patt.getPattern().toString().toLowerCase()).append(" ");
            }
        } catch (NoSuchMethodError e) {
            for (Pattern patt : banner.getPatterns()) {
                result.append(MSG.theme()).append(patt.getPattern().toString().toLowerCase()).append(":")
                        .append(patt.getColor().toString().toLowerCase()).append(" ");
            }
        }

        return result.toString().trim();
    }

}
