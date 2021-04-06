package xyz.msws.supergive.loadout;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.utils.MSG;

import java.util.*;

/**
 * Represents a loadout that contains numerous items
 *
 * @author imodm
 */
@SuppressWarnings("deprecation")
@SerializableAs("Loadout")
public class Loadout implements ConfigurationSerializable {
    private ItemStack[] items = null;
    private boolean clear = false, smartEquip = true;
    private String name = null;

    /**
     * Initialize a loadout from a configuration section
     *
     * @param section
     */
    public Loadout(ConfigurationSection section) {
        name = section.getString("Name");
        List<ItemStack> its = new ArrayList<>();
        for (String item : section.getStringList("Items")) {
            ItemStack i = SuperGive.getPlugin().getBuilder().build(item);
            if (i == null || i.getType() == Material.AIR) {
                MSG.warn("Invalid item format for " + section.getName() + ": " + item);
                continue;
            }
            its.add(SuperGive.getPlugin().getBuilder().build(item));
        }
        clear = section.getBoolean("Clear", false);
        smartEquip = section.getBoolean("Smart", true);
        this.items = its.toArray(new ItemStack[0]);
    }

    /**
     * Initialize a loadout from a map of data, used for
     * {@link ConfigurationSerializable}
     *
     * @param data
     */
    @SuppressWarnings("unchecked")
    public Loadout(Map<String, Object> data) {
        name = (String) data.getOrDefault("Name", null);
        clear = (boolean) data.getOrDefault("Clear", false);
        smartEquip = (boolean) data.getOrDefault("Smart", true);
        List<String> its = (List<String>) data.getOrDefault("Items", new ArrayList<>());
        List<ItemStack> res = new ArrayList<>();
        for (String i : its)
            res.add(SuperGive.getPlugin().getBuilder().build(i));

        this.items = res.toArray(new ItemStack[0]);
    }

    /**
     * Convert the loadout to a map
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        if (name != null)
            data.put("Name", name);
        if (clear)
            data.put("Clear", clear);
        if (!smartEquip)
            data.put("Smart", smartEquip);
        List<String> items = new ArrayList<>();
        for (ItemStack item : this.items) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            items.add(SuperGive.getPlugin().getBuilder().toString(item));
        }
        data.put("Items", items);
        return data;
    }

    public Loadout(List<ItemStack> items) {
        this.items = items.toArray(new ItemStack[0]);
    }

    public Loadout(ItemStack... itemStacks) {
        this.items = itemStacks;
    }

    /**
     * Sets the loadout's name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the loadout's name, can be null
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set whether or not to clear an inventory when giving this loadout
     *
     * @param clear
     */
    public void setClear(boolean clear) {
        this.clear = clear;
    }

    /**
     * Get whether or not the loadout clears the inventory
     *
     * @return
     */
    public boolean doesClear() {
        return clear;
    }

    /**
     * Set whether or not the loadout equips using the SmartEquip method (config)
     *
     * @param value
     */
    public void setSmartEquip(boolean value) {
        this.smartEquip = value;
    }

    /**
     * Get whether or not the loadout uses SmartEquip
     *
     * @return
     */
    public boolean smartEquips() {
        return smartEquip;
    }

    /**
     * Gets all items
     *
     * @return
     */
    public ItemStack[] getItems() {
        return items;
    }

    /**
     * Handles clearing and smart equipping, wraps holder into a
     * {@link DynamicHolder}
     *
     * @param holder
     */
    public void give(DynamicHolder dyn) {
        if (clear)
            dyn.clearInventory();

        if (!smartEquip) {
            dyn.addItem(items);
            return;
        }
        List<ItemStack> items = new ArrayList<>(), toAdd = new ArrayList<>();
        Collections.addAll(items, this.items);

        Iterator<ItemStack> it = items.iterator();
        while (it.hasNext()) {
            ItemStack item = it.next();
            if (item == null || item.getType() == Material.AIR)
                continue;
            ItemStack result = giveSmartEquipSlot(dyn, item);
            if (result == null)
                continue;
            it.remove();
            toAdd.add(result);
        }

        toAdd.addAll(items);

        for (ItemStack leftOver : toAdd)
            dyn.addItem(leftOver);
    }

    public void give(InventoryHolder holder) {
        give(new DynamicHolder(holder));
    }

    public void give(LivingEntity living) {
        give(new DynamicHolder(living));
    }

    /**
     * Returns the list of items in a human readable format
     *
     * @return
     */
    public String humanReadable() {
        if (name != null)
            return name;
        StringBuilder builder = new StringBuilder();
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            builder.append(SuperGive.getPlugin().getBuilder().humanReadable(item)).append(", ");
        }
        if (builder.toString().isEmpty()) {
            return "No items";
        }
        return builder.toString().substring(0, builder.length() - 2);
    }

    /**
     * Returns the list of items formatted conveninently for a lore/list
     *
     * @return
     */
    public String loreReadable() {
        StringBuilder builder = new StringBuilder();
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            builder.append("&7" + SuperGive.getPlugin().getBuilder().humanReadable(item)).append("\n");
        }
        if (builder.toString().isEmpty())
            return "&7No items";
        return builder.toString().substring(0, builder.length() - 1);
    }

    /**
     * Should return the item that was previously in the slot
     *
     * @param dyn
     * @param item
     * @return
     */
    public ItemStack giveSmartEquipSlot(DynamicHolder dyn, ItemStack item) {
        ConfigurationSection section = SuperGive.getPlugin().getConfig().getConfigurationSection("SmartEquip");
        if (section == null)
            return null;
        if (item == null || item.getType() == Material.AIR)
            return null;
        List<String> options = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            if (item.getType().toString().toLowerCase().contains(key.toLowerCase())) {
                options = section.getStringList(key);
                break;
            }
        }
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            if (dyn.isLivingEntity()) {
                EquipmentSlot slot = null;
                try {
                    slot = EquipmentSlot.valueOf(option.toUpperCase());
                } catch (IllegalArgumentException e) {
                }
                if (slot != null) {
                    ItemStack old = dyn.getLiving().getEquipment().getItem(slot);
                    dyn.getLiving().getEquipment().setItem(slot, item);
                    return (old == null || old.getType() == Material.AIR) ? new ItemStack(Material.AIR) : old;
                }
            }

            if (!dyn.hasInventory())
                continue;

            try {
                int slot = Integer.parseInt(option);
                if (slot >= dyn.getInventory().getSize())
                    return null;
                ItemStack old = dyn.getInventory().getItem(slot);
                if (old != null && old.getType() != Material.AIR) {
                    if (i < options.size() - 1)
                        continue;
                }
                dyn.getInventory().setItem(slot, item);
                return (old == null || old.getType() == Material.AIR) ? new ItemStack(Material.AIR) : old;
            } catch (NumberFormatException e) {
            }
        }
        return null;

    }

}
