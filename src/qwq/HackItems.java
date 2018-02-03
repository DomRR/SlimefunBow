package qwq;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;

import java.util.Random;

public final class HackItems implements Listener{

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        boolean debug = Main.debug;
        int i = new Random().nextInt(3);
        if (debug) i = 0;
        if (i != 0) return;
        if (e.getCurrentItem() == null || e.getWhoClicked() == null) return;
        if (getItemName(e.getWhoClicked(), e.getRawSlot()) == null) {
            return;
        }
        Player p = (Player)(e.getWhoClicked());
        if (p.getWorld().getName().toString().equals("instance")) return;
        if (debug) Bukkit.getLogger().info(getItemName(e.getWhoClicked(), e.getRawSlot()));
        if ((ChatColor.stripColor(getItemName(e.getWhoClicked(), e.getRawSlot())).contains("UMP45")) || ChatColor.stripColor(getItemName(e.getWhoClicked(), e.getRawSlot())).contains("MAC-10") || ChatColor.stripColor(getItemName(e.getWhoClicked(), e.getRawSlot())).contains("PP")) {
            Bukkit.getLogger().info("非法物品: " + p.getName());
        }
    }

    public String getItemName(Player p, int i){
        PlayerInventory in = p.getInventory();
        if (Main.debug) Bukkit.getLogger().info(in.toString());
        if (in.getItem(i) == null) {
            if (Main.debug) Bukkit.getLogger().info(i + ": return 2");
            return null;
        }
        if (Main.debug) Bukkit.getLogger().info(i + "::");
        return p.getInventory().getItem(i).getItemMeta().getDisplayName();
    }

    public String getItemName(HumanEntity p, int i){
        Player p$2 = (Player) p;
        return getItemName(p$2, i);
    }
}

//        if (p == null) {
//            if (debug) Bukkit.getLogger().info("return: null");
//            return;
//        }