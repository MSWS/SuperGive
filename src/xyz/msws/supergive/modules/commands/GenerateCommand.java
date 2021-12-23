package xyz.msws.supergive.modules.commands;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.utils.Lang;
import xyz.msws.supergive.utils.MSG;

/**
 * Tells the player the appropriate command to generate an item in their hand.
 *
 * @author imodm
 */
public class GenerateCommand extends BukkitCommand {

    private final SuperGive plugin;

    protected GenerateCommand(SuperGive plugin) {
        super("generate");
        this.plugin = plugin;
        this.setPermission("supergive.command.generate");
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender))
            return true;
        if (!(sender instanceof Player)) {
            Lang.MUST_BE_PLAYER.send(sender);
            return true;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInHand();
        if (item.getType() == Material.AIR) {
            Lang.MUST_HAVE_ITEM.send(sender);
            return true;
        }
        String str = "&7/give @self &e" + plugin.getBuilder().toString(item);

        Lang.GENERATE_PREFIX.send(sender);
        BaseComponent[] result = null;
        if (ChatColor.stripColor(MSG.color(str)).length() > 256) {
            result = new ComponentBuilder(MSG.color("&7/give @self &e[Click to copy...]"))
                    .event(new ClickEvent(Action.COPY_TO_CLIPBOARD, ChatColor.stripColor(MSG.color(str))))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            TextComponent.fromLegacyText(MSG.color("&7Click to copy command"))))
                    .create();
        } else {
            result = new ComponentBuilder(MSG.color(str))
                    .event(new ClickEvent(Action.SUGGEST_COMMAND, ChatColor.stripColor(MSG.color(str))))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            TextComponent.fromLegacyText(MSG.color("&7Click to prepare command"))))
                    .create();
        }

        player.spigot().sendMessage(result);
        return true;
    }

}
