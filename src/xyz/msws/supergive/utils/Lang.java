package xyz.msws.supergive.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An enum to keep track of messages in messages.
 *
 * @author imodm
 */
public enum Lang {
    PREFIX("Prefix", "&9SuperGive>&7"),
    MUST_BE_PLAYER("Command.Result.MustBePlayer", "%prefix% You must be a player to run this command."),
    MUST_HAVE_ITEM("Command.Result.MustHaveItem", "%prefix% You must have an item in your hand to run this command."),
    GENERATE_PREFIX("Command.Generate.Prefix", "%prefix% The item in your hand can be generated with the command:"),
    SPECIFY_ITEM("Command.Argument.Missing.Item", "%prefix% You must specify an item to give."),
    SPECIFY_TARGET("Command.Argument.Missing.Target", "%prefix% You must specify a target."),
    INVALID_TARGET("Command.Argument.Invalid.Target", "%prefix% Unable to parse the target &e%s&7."),
    TARGET_MISSING("Command.Result.NoTargets", "%prefix% No entities were selected by &e%s&7."),
    NO_PERMISSION("Command.Result.NoPermission", "%prefix% This requires the permission &e%s&7."),
    INVALID_BLOCK("Command.Argument.Invalid.Block",
            "%prefix% &e%s &7cannot hold items, try looking at a chest, dropper, etc."),
    INVALID_ITEM("Command.Argument.Invalid.Item", "%prefix% Unable to parse the item &e%s&7."),
    NO_RESULT("Command.Result.None", "%prefix% Successfully did nothing."),
    GIVE_SENDER("Command.Result.Given", "%prefix% Successfully gave &a%s &e%s&7."),
    GIVE_RECEIVER("Command.Result.Received", "%prefix% &6%s&7 has given you &e%s&7."),
    MISSING_ARGUMENT("Command.Argument.Missing.Generic", "%prefix% You are missing an argument for that command."),
    NO_LOADOUTS("Command.Loadouts.None",
            "%prefix% There are no loadouts set yet, create one with &e/loadout create&7."),
    LOADOUTS_PREFIX("Command.Loadouts.Prefix", "%prefix% Listing all loadouts available."),
    SPECIFY_NAME("Command.Argument.Missing.Name", "%prefix% You must specify a name."),
    LOADOUT_EXISTS("Command.Loadouts.Exists", "%prefix% A loadout already exists under that name."),
    LOADOUT_CREATED("Command.Loadouts.Created", "%prefix% Successfully created the &a%s&7 loadout."),
    LOADOUT_ALREADY_EDITING("Command.Loadouts.AlreadyEditing", "%prefix% You are already editing the &a%s&7 loadout."),
    UNKNOWN_LOADOUT("Command.Argument.Unknown.Loadout", "%prefix% Could not find a loadout under tha name &e%s&7."),
    LOADOUT_EDITING("Command.Loadouts.NowEditing",
            "%prefix% You are now editing the &e%s&7 loadout.\nTo edit the loadout, simply &lmodify your own inventory&7.\nType &e/loadout edit&7 to save the changes, or &c/loadout cancel&7 to cancel them."),
    LOADOUT_DELETED("Command.Loadouts.Deleted", "%prefix% Successfully deleted the &c%s&7 loadout."),
    LOADOUT_NOT_DELETED("command.Loadouts.NotDeleted", "%prefix% Unable to delete the &c%s&7 loadout."),
    LOADOUT_NOT_EDITING("Command.Loadouts.NotEditing", "%prefix% You are not editing a loadout."),
    LOADOUT_CANCELED("Command.Loadouts.Canceled", "%prefix% You canceled and reverted all changes to &e%s&7."),
    INVALID_ARGUMENT("Command.Argument.Invalid.Generic", "A specified argument was invalid."),
    NOTALL_SELECTOR("Command.Result.Warn.SelectorPerms",
            "%prefix% You do not have access to all selectors, this may or may not be intended."),
    HELP_MESSAGE("Command.Help",
            "%prefix% &7SuperGive &7Format: &6/give &e[target] &a[item] &3<modifiers>\n&eTab Completion &7is a very useful function, it will show the possibilities for each item.\nA few possible valid commands would be:\n&6/give &a@self &ediamondsword\n&6/give &a@players &estone 32\n&6/give &a@zombie &estick &3knockback:3\n&6/give &a@world,perm:rank.staff &efireworkrocket &3firework:red,blue,flicker"),
    NOTALL_BUILDER("Command.Result.Warn.AttributePerms",
            "%prefix% &cYou lacked permissions for these modifications: &b%s&7.");

    private final String key;
    private final Object def;
    private Object value;

    Lang(String key, Object value) {
        this.key = key;
        this.value = value;
        this.def = value;
    }

    public void setValue(Object v) {
        this.value = v;
    }

    public Object getValue() {
        return value;
    }

    public Object getDefault() {
        return def;
    }

    public String getKey() {
        return key;
    }

    /**
     * Overrides all current values from the specified config
     *
     * @param lang
     */
    public static void load(YamlConfiguration lang) {
        for (Lang l : Lang.values()) {
            l.setValue(lang.get(l.getKey(), l.getValue()));
        }
    }

    public void send(CommandSender sender) {
        MSG.tell(sender, value.toString().replace("%prefix%", PREFIX.getValue().toString()));
    }

    /**
     * Sends the message to the {@link CommandSender}, if there are placeholders
     * they will be formatted via {@link String#format(String, Object...)}.
     *
     * @param sender
     * @param params
     */
    public void send(CommandSender sender, Object... params) {
        MSG.tell(sender, String.format(value.toString().replace("%prefix%", PREFIX.getValue().toString()), params));
    }
}
