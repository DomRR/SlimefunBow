package qwq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Date;

public class SlimefunCooldown implements Listener {
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        boolean debug = Main.debug;
        String prefix = Main.prefix;
        int sfCooldown = Main.sfCooldown;
        Player player = e.getPlayer();
        if (e.getItem() == null || !e.getItem().hasItemMeta()) return;
        if (debug) log(ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName().toString()));
        if (ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName().toString()).contains("粘液科技指南") && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (!Main.slimefunCooldown.containsKey(player.getUniqueId())) {
                Main.slimefunCooldown.put(player.getUniqueId(), (new Date().getTime() / 1000) + sfCooldown);
                if (debug) log(player.getName() + ": true");
            } else {
                Long cooldown = Main.slimefunCooldown.get(player.getUniqueId());
                if (debug) log(player.getName() + ": " + cooldown + "<" + (new Date().getTime() / 1000));
                if (cooldown < (new Date().getTime() / 1000)) {
                    if (debug) log("<");
                    Main.slimefunCooldown.remove(player.getUniqueId());
                    Main.slimefunCooldown.put(player.getUniqueId(), (new Date().getTime() / 1000) + sfCooldown);
                } else {
                    player.sendMessage(prefix + "请等 " + (cooldown - (new Date().getTime() / 1000)) + " 秒.");
                    player.getInventory().setItemInMainHand(null);
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    public void log(String s) {
        Bukkit.getLogger().info(s);
    }
}


//                    e.getItem().setItemMeta(Main.IM(Material.AIR, null, null));