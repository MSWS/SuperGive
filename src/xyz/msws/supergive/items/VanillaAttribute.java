package xyz.msws.supergive.items;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class VanillaAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.toLowerCase().startsWith("attribute:"))
            return item;
        ItemMeta meta = item.getItemMeta();
        Attribute att = null;
        try {
            String res = Utils.getOption(line.split(":")[1], Attribute.values());
            if (res == null) {
                MSG.warn("Unknown attribute: " + line.split(":")[1]);
                return item;
            }
            att = Attribute.valueOf(res);
            try {
                double number = Double.parseDouble(line.split(":")[2]);
                meta.addAttributeModifier(att, new AttributeModifier("supergive", number, Operation.ADD_NUMBER));
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                MSG.warn("Unable to parse number for attribute: " + line);
                return item;
            }
        } catch (IllegalArgumentException e) {
            MSG.warn("Unknown item attribute: " + line.substring("attribute:".length()));
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasAttributeModifiers() || meta.getAttributeModifiers() == null || meta.getAttributeModifiers().isEmpty())
            return null;
        StringBuilder result = new StringBuilder();
        for (Entry<Attribute, AttributeModifier> entry : meta.getAttributeModifiers().entries()) {
            result.append("attribute:").append(MSG.normalize(entry.getKey().toString())).append(":").append(entry.getValue().getAmount())
                    .append(" ");
        }

        return result.toString().trim();
    }

    @Override
    public String humanReadable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasAttributeModifiers() || meta.getAttributeModifiers() == null || meta.getAttributeModifiers().isEmpty())
            return null;
        List<String> attributes = new ArrayList<>();
        for (Entry<Attribute, AttributeModifier> entry : meta.getAttributeModifiers().entries()) {
            attributes.add(MSG.camelCase(entry.getKey().toString()) + " &7of " + ChatColor.YELLOW
                    + entry.getValue().getAmount());
        }
        return "attributed with " + String.join(" &7and a ", attributes);
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.vanilla";
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;
        if (!current.toLowerCase().startsWith("attribute:")) {
            if ("attribute:".startsWith(current.toLowerCase()))
                return Collections.singletonList("attribute:");
            return null;
        }

        List<String> result = new ArrayList<>();
        for (Attribute att : Attribute.values()) {
            if (("attribute:" + MSG.normalize(att.toString())).startsWith(current.toLowerCase())) {
                result.add("attribute:" + MSG.normalize(att.toString()).replace("generic", ""));
            }
        }

        return result;
    }

}
