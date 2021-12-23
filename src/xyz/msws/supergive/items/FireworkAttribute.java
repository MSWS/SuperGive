package xyz.msws.supergive.items;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.CColor;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adds support for adding custom firework explosions. Proper format is
 * firework:[color],[color],[flicker],[trail]
 *
 * @author imodm
 */
public class FireworkAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("firework:"))
            return item;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof FireworkMeta fire))
            return item;

        String[] split = line.substring("firework:".length()).split(",");
        String cString = split[0],
                fString = split.length > 1 ? line.split(",")[1] : null;

        List<Color> colors = new ArrayList<>();
        for (String c : cString.split(" ")) {
            Color col = Utils.getColor(c);
            if (col != null)
                colors.add(Utils.getColor(c));
        }

        FireworkEffect.Builder effect = FireworkEffect.builder().withColor(colors);
        if (fString != null) {
            colors = new ArrayList<>();
            for (String c : fString.split(" ")) {
                Color col = Utils.getColor(c);
                if (col != null)
                    colors.add(Utils.getColor(c));
            }
            effect.withFade(colors);
        }

        for (String s : line.split(",")) {
            switch (s.toLowerCase()) {
                case "flicker" -> effect.withFlicker();
                case "trail" -> effect.withTrail();
                default -> {
                    if (s.startsWith("power")) {
                        try {
                            fire.setPower(Integer.parseInt(s.substring("power".length()).trim()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    try {
                        Type type = Type.valueOf(s.toUpperCase());
                        effect.with(type);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        fire.addEffect(effect.build());
        item.setItemMeta(fire);
        return item;
    }

    private String colorToString(Color c) {
        CColor cc = CColor.fromBukkit(c);
        if (cc != null)
            return cc.toString();
        return c.getRed() + "|" + c.getGreen() + "|" + c.getBlue();
    }

    @Override
    public String getModification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof FireworkMeta fire))
            return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fire.getEffectsSize(); i++) {
            FireworkEffect effect = fire.getEffects().get(i);

            result.append("firework:");
            result.append(effect.getColors().stream().map(this::colorToString).collect(Collectors.joining(" "))).append(",");
            if (!effect.getFadeColors().isEmpty()) {
                result.append(effect.getFadeColors().stream().map(this::colorToString).collect(Collectors.joining(" ")))
                        .append(",");
            }
            result.append(effect.getType());
            if (effect.hasFlicker())
                result.append(",flicker");
            if (effect.hasTrail())
                result.append(",trail");
            result.append((i == fire.getEffectsSize() - 1) ? ",power" + fire.getPower() : " ");
        }
        return result.toString();
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;
        if (!MSG.normalize(args[1]).contains("fireworkrocket"))
            return null;

        List<String> result = new ArrayList<>();

        String c = current.split(",")[current.split(",").length - 1].trim();
        String prev = current.substring(0, current.lastIndexOf(",") + 1);

        boolean specified = false;
        for (String arg : args) {
            if (arg.toLowerCase().startsWith("firework:")) {
                specified = true;
                break;
            }
        }

        if (!specified) {
            if ("firework:".startsWith(c.toLowerCase()))
                result.add("firework:");
            return result;
        }

        for (String s : new String[]{"power", "flicker", "trail"}) {
            if (s.startsWith(c.toLowerCase()))
                result.add(prev + s);
        }
        for (CColor color : CColor.values()) {
            if (("firework:" + color).toLowerCase().startsWith(c))
                result.add("firework:" + MSG.normalize(color.toString()));
            if (color.toString().toLowerCase().startsWith(c.toLowerCase()) || c.isEmpty())
                result.add(prev + MSG.normalize(color.toString()));
        }
        for (Type type : Type.values()) {
            if (type.toString().toLowerCase().startsWith(c.toLowerCase())) {
                result.add(prev + type);
            }
        }

        return result;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.firework";
    }

    @Override
    public String humanReadable(ItemStack item) {
        String result = getModification(item);
        if (result == null || result.length() < "firework:".length())
            return result;
        return result.substring("firework:".length());
    }

}
