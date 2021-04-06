package xyz.msws.supergive.items;

import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Adds ability to specify a command for commandblocks.
 *
 * @author imodm
 */
public class CommandAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.toLowerCase().startsWith("command:"))
            return item;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta))
            return item;
        BlockStateMeta bsm = (BlockStateMeta) meta;
        if (!(bsm.getBlockState() instanceof CommandBlock))
            return item;
        CommandBlock command = (CommandBlock) bsm.getBlockState();
        command.setCommand(line.substring("command:".length()));
        bsm.setBlockState(command);
        item.setItemMeta(bsm);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta))
            return null;
        BlockStateMeta bsm = (BlockStateMeta) meta;
        if (!(bsm.getBlockState() instanceof CommandBlock))
            return null;
        return "command:" + ((CommandBlock) bsm.getBlockState()).getCommand();
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.command";
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;
        if (!args[1].toLowerCase().contains("command"))
            return null;
        if (!current.toLowerCase().startsWith("command:")) {
            if ("command:".startsWith(current.toLowerCase()))
                return Arrays.asList("command:");
            return null;
        }
        return null;
    }

    @Override
    public String humanReadable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof BlockStateMeta))
            return null;
        BlockStateMeta bsm = (BlockStateMeta) meta;
        if (!(bsm.getBlockState() instanceof CommandBlock))
            return null;
        return "that runs " + ((CommandBlock) bsm.getBlockState()).getCommand();
    }

}
