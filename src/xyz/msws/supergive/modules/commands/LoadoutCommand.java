package xyz.msws.supergive.modules.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.events.LoadoutCreateEvent;
import xyz.msws.supergive.events.LoadoutEditEvent;
import xyz.msws.supergive.loadout.DynamicHolder;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;
import xyz.msws.supergive.utils.Lang;
import xyz.msws.supergive.utils.MSG;

import java.util.*;
import java.util.Map.Entry;

/**
 * A command to allow for ingame modification of {@link Loadout}.
 *
 * @author imodm
 */
@SuppressWarnings("deprecation")
public class LoadoutCommand extends BukkitCommand {

    private SuperGive plugin;
    private LoadoutManager lm;

    public LoadoutCommand(SuperGive plugin, String name) {
        super(name);
        this.setAliases(Arrays.asList("ld"));
        this.setPermission("supergive.command.loadout");
        this.setDescription("Manage loadouts");
        this.setUsage("[create/delete/modify]");
        this.plugin = plugin;
        lm = plugin.getModule(LoadoutManager.class);
    }

    private Map<UUID, String> loadouts = new HashMap<>();
    private Map<UUID, ItemStack[]> items = new HashMap<>();

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender))
            return true;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                Lang.MISSING_ARGUMENT.send(sender);
                return true;
            }
            Player player = (Player) sender;
            if (lm.getLoadoutNames().isEmpty()) {
                Lang.NO_LOADOUTS.send(sender);
                return true;
            }
            MSG.tell(sender, " ");
            Lang.LOADOUTS_PREFIX.send(sender);
            try {
                for (Entry<String, Loadout> entry : lm.getLoadoutMap().entrySet()) {
                    ComponentBuilder give = new ComponentBuilder();
                    give = give.append("GIVE").color(ChatColor.GOLD).bold(true)
                            .event(new ClickEvent(Action.RUN_COMMAND, "/give @me #" + entry.getKey()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
                                    .fromLegacyText(MSG.color("&7Click to run &e/give &b@me &a#" + entry.getKey()))));
                    give = give.append(" EDIT", FormatRetention.NONE).color(ChatColor.GREEN).bold(true)
                            .event(new ClickEvent(Action.RUN_COMMAND, "/loadout edit " + entry.getKey()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                                    MSG.color("&7Click to run &e/loadout &6edit &a" + entry.getKey()))));
                    give = give.append(" ", FormatRetention.NONE).append("DELETE").color(ChatColor.RED).bold(true)
                            .event(new ClickEvent(Action.RUN_COMMAND, "/loadout delete " + entry.getKey()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                                    MSG.color("&7Click to run &e/loadout &cdelete &a" + entry.getKey()))));
                    give = give.append(" - ", FormatRetention.NONE).color(ChatColor.GRAY)
                            .append(entry.getKey(), FormatRetention.NONE).color(ChatColor.YELLOW)
                            .append(" (" + entry.getValue().getItems().length + " items)", FormatRetention.NONE)
                            .color(ChatColor.GRAY)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    TextComponent.fromLegacyText(MSG.color(MSG.FORMAT_INFO + entry.getKey()
                                            + " contains:\n" + entry.getValue().loreReadable()))));
                    player.spigot().sendMessage(give.create());
                }
            } catch (NoSuchMethodError e) {
                // 1.8 Compatability
                for (Entry<String, Loadout> entry : lm.getLoadoutMap().entrySet()) {
                    MSG.tell(sender,
                            "&a" + entry.getKey() + "&7 - &8(&9" + entry.getValue().getItems().length + "&b items&8)");
                }
            }

            return true;
        }
        Player player;
        String name;
        switch (args[0].toLowerCase()) {
            case "create":
                if (!sender.hasPermission("supergive.command.loadout.create")) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.loadout.create");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    Lang.MUST_BE_PLAYER.send(sender);
                    return true;
                }
                player = (Player) sender;
                if (args.length <= 1) {
                    Lang.SPECIFY_NAME.send(sender);
                    return true;
                }

                name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));
                if (lm.getLoadout(MSG.normalize(name)) != null) {
                    Lang.LOADOUT_EXISTS.send(sender, name);
                    return true;
                }

                Loadout loadout = new Loadout(player.getInventory().getContents());
                loadout.setName(name);

                LoadoutCreateEvent event = new LoadoutCreateEvent(player, loadout);
                Bukkit.getPluginManager().callEvent(event);
                loadout = event.getLoadout();

                lm.addLoadout(MSG.normalize(name), loadout);
                Lang.LOADOUT_CREATED.send(sender, name);
                for (ItemStack item : loadout.getItems()) {
                    if (item == null || item.getType() == Material.AIR)
                        continue;
                    MSG.tell(sender, "&7- " + plugin.getBuilder().humanReadable(item));
                }
                return true;
            case "edit":
                if (!sender.hasPermission("supergive.command.loadout.edit")) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.loadout.edit");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    Lang.MUST_BE_PLAYER.send(sender);
                    return true;
                }
                player = (Player) sender;
                if (args.length <= 1) {
                    if (loadouts.containsKey(player.getUniqueId())) {
                        if (!items.containsKey(player.getUniqueId())) {
                            MSG.tell(sender, "SuperGive",
                                    "A potential error occured when restoring your old items, please report this to MSWS if you see this message.");
                        }
                        // End editing of loadout
                        Loadout newLoad = new Loadout(player.getInventory().getContents());
                        Loadout old = lm.getLoadout(loadouts.get(player.getUniqueId()));
                        newLoad.setName(old.getName());

                        LoadoutEditEvent editEvent = new LoadoutEditEvent(player, old, newLoad);
                        Bukkit.getPluginManager().callEvent(editEvent);
                        newLoad = editEvent.getLoadout();

                        lm.addLoadout(loadouts.get(player.getUniqueId()), newLoad);
                        player.getInventory().clear();
                        player.getInventory().setContents(items.get(player.getUniqueId()));
                        items.remove(player.getUniqueId());
                        loadouts.remove(player.getUniqueId());
                        return true;
                    }
                    Lang.SPECIFY_NAME.send(sender);
                    return true;
                }

                if (loadouts.containsKey(player.getUniqueId())) {
                    Lang.LOADOUT_ALREADY_EDITING.send(sender, loadouts.get(player.getUniqueId()));
                    return true;
                }

                name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));

                Loadout load = lm.getLoadout(name);
                if (load == null) {
                    Lang.UNKNOWN_LOADOUT.send(sender, name);
                    return true;
                }
                if (!sender.hasPermission("supergive.command.loadout.edit." + name)) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.loadout.edit." + name);
                    return true;
                }

                items.put(player.getUniqueId(), player.getInventory().getContents());
                player.getInventory().clear();
                load.give(new DynamicHolder((InventoryHolder) player));
                Lang.LOADOUT_EDITING.send(sender, name);
                loadouts.put(player.getUniqueId(), name);
                break;
            case "delete":
                if (!sender.hasPermission("supergive.command.loadout.delete")) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.loadout.delete");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    Lang.MUST_BE_PLAYER.send(sender);
                    return true;
                }
                player = (Player) sender;
                if (args.length <= 1) {
                    Lang.SPECIFY_NAME.send(sender);
                    return true;
                }

                name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));

                if (lm.getLoadout(name) == null) {
                    Lang.UNKNOWN_LOADOUT.send(sender, name);
                    return true;
                }
                if (!sender.hasPermission("supergive.command.loadout.delete." + name)) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.loadout.delete." + name);
                    return true;
                }
                if (lm.deleteLoadout(name)) {
                    Lang.LOADOUT_DELETED.send(sender, name);
                } else {
                    Lang.LOADOUT_NOT_DELETED.send(sender, name);
                }
                break;
            case "cancel":
                if (!(sender instanceof Player)) {
                    Lang.MUST_BE_PLAYER.send(sender);
                    return true;
                }
                player = (Player) sender;
                if (!(loadouts.containsKey(player.getUniqueId()))) {
                    Lang.LOADOUT_NOT_EDITING.send(sender);
                    return true;
                }
                Lang.LOADOUT_CANCELED.send(sender, loadouts.get(player.getUniqueId()));
                loadouts.remove(player.getUniqueId());
                player.getInventory().setContents(items.get(player.getUniqueId()));
                break;
            default:
                Lang.INVALID_ARGUMENT.send(sender);
                break;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String s : new String[]{"create", "edit", "delete"}) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(s);
            }
            if (sender instanceof Player && loadouts.containsKey(((Player) sender).getUniqueId())) {
                if ("cancel".startsWith(args[0].toLowerCase()))
                    result.add("cancel");
            }
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete"))) {
            for (String s : lm.getLoadoutNames()) {
                if (s.toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add(s);
            }
        }
        return result;
    }

}
