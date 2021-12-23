package xyz.msws.supergive.modules.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.events.CommandGiveItemEvent;
import xyz.msws.supergive.items.ItemAttribute;
import xyz.msws.supergive.items.ItemBuilder;
import xyz.msws.supergive.loadout.DynamicHolder;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;
import xyz.msws.supergive.selectors.Selector;
import xyz.msws.supergive.utils.Lang;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Sounds;
import xyz.msws.supergive.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a much better integration of the /give command for Minecraft.
 * Possibles usages are:
 * <p>
 * give MSWS diamondsword <br>
 * give @all dirt give radius:15 stone 5 give <br>
 * give @e[type=skeleton] bow power:5 <br>
 * give perm:rank.admin diamondsword name:&cEnchanted Sword <br>
 * give MSWS @hand <br>
 * give @world stone 64 <br>
 * give MSWS bow unbreakable <br>
 * give MSWS skull owner:Notch protection:3 <br>
 *
 * @author imodm
 */
@SuppressWarnings("deprecation")
public class GiveCommand extends BukkitCommand {

    private final Selector selector;
    private final ItemBuilder builder;
    private final SuperGive plugin;

    protected GiveCommand(SuperGive plugin) {
        super("give");
        this.plugin = plugin;
        this.setPermission("supergive.command.give");
        this.setAliases(Arrays.asList("g", "sg", "supergive"));
        this.setUsage("<command> [target] [item] <args>");
        this.setDescription("An advanced and greatly improved give command");
        selector = plugin.getSelector();
        builder = plugin.getBuilder();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender))
            return true;

        if (args.length >= 1 && args[0].equalsIgnoreCase("reset")) {
            if (!sender.hasPermission("supergive.command.reset")) {
                Lang.NO_PERMISSION.send(sender, "supergive.command.reset");
                return true;
            }
            YamlConfiguration c = new YamlConfiguration();
            for (Lang l : Lang.values())
                c.set(l.getKey(), l.getDefault());
            plugin.saveResource("config.yml", true);
            try {
                c.save(new File(plugin.getDataFolder(), "lang.yml"));
            } catch (IOException e) {
                MSG.tell(sender, "SuperGive",
                        "An error occured when resetting the lang file, please check the console.");
                e.printStackTrace();
                return true;
            }
            Lang.load(c);
            MSG.tell(sender, "SuperGive", "Successfully reset the config and lang file.");
            return true;
        }

        if (args.length < 2) {
            if (args.length == 0) {
                Lang.HELP_MESSAGE.send(sender);
                Lang.SPECIFY_TARGET.send(sender);
            }
            Lang.SPECIFY_ITEM.send(sender);
            return true;
        }

        List<Entity> targets = selector.getEntities(args[0], sender);
        if (targets == null) {
            Lang.INVALID_TARGET.send(sender, args[0]);
            return true;
        }

        if (targets.isEmpty()) {
            Lang.TARGET_MISSING.send(sender, args[0]);
            if (!sender.hasPermission("supergive.selector.all"))
                Lang.NOTALL_SELECTOR.send(sender);
            return true;
        }

        ItemStack item = builder.build(String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length)), sender);

        Loadout loadout = null;
        switch (args[1].toLowerCase()) {
            case "@hand":
                if (!sender.hasPermission("supergive.command.give.hand")) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.give.hand");
                    return true;
                }
                if (sender instanceof Player) {
                    loadout = new Loadout(((Player) sender).getInventory().getItemInHand());
                    item = ((Player) sender).getInventory().getItemInHand();
                }
                break;
            case "@inventory":
                if (!sender.hasPermission("supergive.command.give.inventory")) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.give.inventory");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    Lang.MUST_BE_PLAYER.send(sender);
                    return true;
                }
                loadout = new Loadout(((Player) sender).getInventory().getContents());
                break;
            case "@block":
                if (!sender.hasPermission("supergive.command.give.block")) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.give.block");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    Lang.MUST_BE_PLAYER.send(sender);
                    return true;
                }
                RayTraceResult result = ((Player) sender).rayTraceBlocks(20);
                if (result == null || result.getHitBlock() == null) {
                    Lang.INVALID_BLOCK.send(sender, "Air");
                    return true;
                }
                Block target = result.getHitBlock();
                if (!(target.getState() instanceof Container)) {
                    Lang.INVALID_BLOCK.send(sender, MSG.camelCase(target.getType().toString()));
                    return true;
                }
                loadout = new Loadout(((Container) target.getState()).getInventory().getContents());
                break;
            case "@enderchest":
                if (!sender.hasPermission("supergive.command.give.enderchest")) {
                    Lang.NO_PERMISSION.send(sender, "supergive.command.give.enderchest");
                    return true;
                }
                if (!(sender instanceof Player)) {
                    Lang.MUST_BE_PLAYER.send(sender);
                    return true;
                }
                loadout = new Loadout(((Player) sender).getEnderChest().getContents());
                break;
            default:
                if (args[1].startsWith("#")) {
                    loadout = plugin.getModule(LoadoutManager.class).matchLoadout(args[1].substring(1));
                    if (loadout == null) {
                        MSG.tell(sender, "SuperGive", "Unknown loadout specified.");
                        return true;
                    }
                    if (!sender.hasPermission("supergive.command.give.loadout." + args[1].substring(1))) {
                        Lang.NO_PERMISSION.send(sender, "supergive.command.give.loadout" + args[1].substring(1));
                        return true;
                    }
                    break;
                }
                if (item == null) {
                    Lang.INVALID_ITEM.send(sender, args[1]);
                    return true;
                }
                loadout = new Loadout(item);
        }

        if (loadout == null) {
            Lang.INVALID_ITEM.send(sender, args[1]);
            return true;
        }

        if (item != null && item.getType() == Material.AIR) {
            Lang.NO_RESULT.send(sender);
            return true;
        }

        if (item != null) {
            if (!sender.hasPermission("supergive.command.give." + MSG.normalize(item.getType().toString()))) {
                Lang.NO_PERMISSION.send(sender, "supergive.command.give." + MSG.normalize(item.getType().toString()));
                return true;
            }
            if (!sender.hasPermission("supergive.command.give.unsafeenchants")
                    && Utils.containsUnsafeEnchantments(item)) {
                Lang.NO_PERMISSION.send(sender, "supergive.command.give.unsafeenchants");
                return true;
            }
        }

        if (sender instanceof Player) {
            CommandGiveItemEvent event = new CommandGiveItemEvent((Player) sender, targets, loadout);
            Bukkit.getPluginManager().callEvent(event);
            targets = event.getReceivers();
        }

        for (Entity ent : targets) {
            if (ent instanceof Player)
                ((Player) ent).playSound(ent.getLocation(), Sounds.ITEM_PICKUP.bukkitSound(), 2, 1);
            Lang.GIVE_RECEIVER.send(ent, sender.getName(), loadout.humanReadable());
            if (ent instanceof LivingEntity) {
                loadout.give(new DynamicHolder((LivingEntity) ent));
            } else if (ent instanceof InventoryHolder) {
                loadout.give(new DynamicHolder((InventoryHolder) ent));
            }
        }

        Lang.GIVE_SENDER.send(sender, selector.getDescriptor(args[0], sender), loadout.humanReadable());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = new ArrayList<>();
        if (this.getPermission() != null && !(sender.hasPermission(this.getPermission())))
            return result;

        if (args.length == 1 && sender.hasPermission("supergive.command.reset")
                && "reset".startsWith(args[0].toLowerCase()))
            result.add("reset");

        if (args.length == 1) {
            String current = args[0].split(",")[args[0].split(",").length - 1];
            String prev = "";
            if (args[0].split(",").length > 1) {
                prev = String.join(",", args).substring(0, String.join(",", args).lastIndexOf(",") + 1);
            }
            for (String s : plugin.getSelector().tabComplete(current)) {
                result.add(prev + s);
            }
        }

        if (args.length == 2) {
            try {
                for (Material mat : Material.values()) {
                    if (MSG.normalize(mat.getKey().getKey()).startsWith(args[1].toLowerCase())) {
                        result.add(MSG.normalize(mat.getKey().getKey()));
                    }
                }
            } catch (NoSuchMethodError e) {
                // 1.8 Compatibility
                for (Material mat : Material.values()) {
                    if (MSG.normalize(mat.toString()).startsWith(args[1].toLowerCase())) {
                        result.add(MSG.normalize(mat.toString()));
                    }
                }
            }

            for (String s : new String[]{"@hand", "@inventory", "@block", "@enderchest"}) {
                if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(s);
                }
            }
            if ("#".toLowerCase().startsWith(args[1].toLowerCase()))
                result.add("#");
            if (args[1].startsWith("#")) {
                for (String s : plugin.getModule(LoadoutManager.class).getLoadoutNames()) {
                    if (!sender.hasPermission("supergive.command.give.loadout." + s))
                        continue;
                    if (("#" + s).toLowerCase().startsWith(args[1].toLowerCase()))
                        result.add("#" + s);
                }
            }
        }
        if (args.length > 2) {
            for (ItemAttribute attr : plugin.getBuilder().getAttributes()) {
                if (attr.getPermission() != null && !sender.hasPermission(attr.getPermission()))
                    continue;
                List<String> add = attr.tabComplete(args[args.length - 1], args, sender);
                if (add == null || add.isEmpty())
                    continue;
                result.addAll(add);
            }
        }

        return result;
    }

}
