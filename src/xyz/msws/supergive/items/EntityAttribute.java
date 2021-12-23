package xyz.msws.supergive.items;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adds support for specifying the entity that a spawner spawns.
 *
 * @author imodm
 */
public class EntityAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("entity:"))
            return item;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta bsm))
            return item;
        if (!(bsm.getBlockState() instanceof CreatureSpawner spawner))
            return item;

        EntityType type = Utils.getEntityType(line.split(":")[1]);
        if (type == null) {
            MSG.warn("Unknown entity type: " + line);
            return item;
        }

        try {
            spawner.setSpawnedType(type);
        } catch (IllegalArgumentException e) {
            MSG.warn("Invalid entity specified: " + line);
        }
        bsm.setBlockState(spawner);
        item.setItemMeta(bsm);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta bsm))
            return null;
        if (!(bsm.getBlockState() instanceof CreatureSpawner spawner))
            return null;
        return "entity:" + MSG.normalize(spawner.getSpawnedType().toString());
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;
        if (!args[1].equalsIgnoreCase("spawner"))
            return null;
        if ("entity:".startsWith(current.toLowerCase()) && !current.equalsIgnoreCase("entity:")) {
            return Collections.singletonList("entity:");
        } else {
            List<String> result = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (MSG.normalize(("entity:" + type.toString())).startsWith(MSG.normalize(current)))
                    result.add("entity:" + MSG.normalize(type.toString()));
            }
            return result;
        }
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.entity";
    }

    @Override
    public String humanReadable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta bsm))
            return null;
        if (!(bsm.getBlockState() instanceof CreatureSpawner spawner))
            return null;
        return "that spawns " + MSG.camelCase(spawner.getSpawnedType().toString());
    }

}
