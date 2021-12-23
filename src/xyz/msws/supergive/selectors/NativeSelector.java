package xyz.msws.supergive.selectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.modules.AbstractModule;
import xyz.msws.supergive.modules.ModulePriority;
import xyz.msws.supergive.utils.MSG;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Combines all current selectors into one
 *
 * @author imodm
 */
public class NativeSelector extends AbstractModule implements Selector {

    private final List<Selector> selectors = new ArrayList<>(); // List to keep order

    public NativeSelector(SuperGive plugin) {
        super(plugin);
    }

    /**
     * First selector should be widest to smallest
     */
    @Override
    public List<Entity> getEntities(String arg, CommandSender sender) {
        List<Entity> result = new ArrayList<>();
        for (String string : arg.split(",")) {
            for (Selector sel : selectors) {
                List<Entity> res = sel.getEntities(string, sender);
                if (res == null || res.isEmpty())
                    continue;
                if (result.isEmpty()) {
                    result = res;
                } else {
                    // Filter entities that aren't included
                    result = result.stream().filter(re -> res.contains(re)).collect(Collectors.toList());
                }
            }
        }
        return result;
    }

    public void addSelector(Selector sel) {
        selectors.add(sel);
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    @Override
    public String getDescriptor(String arg, CommandSender sender) {
        StringBuilder msg = new StringBuilder();
        List<Entity> result = new ArrayList<>();
        for (String string : arg.split(",")) {
            for (Selector sel : selectors) {
                List<Entity> res = sel.getEntities(string, sender);
                if (res == null || res.isEmpty())
                    continue;
                if (result.isEmpty()) {
                    result = res;
                    msg.append(MSG.theme()).append(sel.getDescriptor(string, sender));
                } else {
                    // Filter entities that aren't included
                    result = result.stream().filter(re -> res.contains(re)).collect(Collectors.toList());
                    msg.append(" &7that are ").append(MSG.theme()).append(sel.getDescriptor(string, sender));
                }
            }
        }
        return msg.toString();
    }

    @Override
    public List<String> tabComplete(String current) {
        List<String> result = new ArrayList<>();
        for (Selector sel : selectors) {
            List<String> l = sel.tabComplete(current);
            if (l == null || l.isEmpty())
                continue;
            result.addAll(sel.tabComplete(current));
        }
        return result;
    }

    @Override
    public void initialize() {
        selectors.add(new VanillaSelector());
        selectors.add(new AnnotatedSelector(plugin));
        selectors.add(new PermissionSelector());
        selectors.add(new NameSelector());
        selectors.add(new RadiusSelector());
        selectors.add(new WorldSelector());
    }

    @Override
    public void disable() {
        selectors.clear();
    }

    @Override
    public ModulePriority getPriority() {
        return ModulePriority.MEDIUM;
    }

}
