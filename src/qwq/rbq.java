package qwq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public final class rbq implements CommandExecutor {
    Statement statement = Main.statement;
    boolean debug = Main.debug;
    String prefix = Main.prefix;
    String database = Main.database;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rbq")) {
            if (!(sender instanceof Player)) {
                log("你必须是一个玩家!");
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(prefix + "没有足够的参数!");
                return false;
            }
            try {
                ResultSet result = statement.executeQuery("SELECT COUNT(1) FROM `minecraft`.`slimefunbow_rbq` WHERE rbq='" + sender.getName() + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (args[0].equalsIgnoreCase("tp")) {
            Player senderPlayer = (Player) sender;
            String target = "";
            try {
                ResultSet result = statement.executeQuery("SELECT COUNT(1) FROM `minecraft`.`slimefunbow_rbq` WHERE rbq='" + sender.getName() + "';");
                int count = 0;
                int count$2 = 0;
                while (result.next()) {
                    count = result.getInt(1);
                }
                result = statement.executeQuery("SELECT COUNT(1) FROM `minecraft`.`slimefunbow_rbq` WHERE admin='" + sender.getName() + "';");
                while (result.next()) {
                    count$2 = result.getInt(1);
                }
                if (count == 0 && count$2 == 0) {
                    sender.sendMessage(prefix + "你不是 rbq 或 主人.");
                    return true;
                }
                result = statement.executeQuery("SELECT * FROM `minecraft`.`slimefunbow_rbq` WHERE admin='" + sender.getName() + "';");
                Main.target = "";
                while (result.next()) {
                    Main.target = result.getString("admin");
                }
                if (debug) Bukkit.getLogger().info("admin" + Main.target);
                if (!Main.target.equals("")) {
                    result = statement.executeQuery("SELECT * FROM `minecraft`.`slimefunbow_rbq` WHERE admin='" + sender.getName() + "';");
                    while (result.next()) {
                        target = result.getString("rbq");
                    }
                    if (Bukkit.getServer().getPlayerExact(target) == null) {
                        sender.sendMessage(prefix + "玩家离线.");
                        return true;
                    }
                    if (debug) Bukkit.getLogger().info("rbq: " + Main.target);
                } else {
                    sender.sendMessage(prefix + "你是 rbq.");
                    return true;
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tp " + sender.getName() + " " + target);
                sender.sendMessage(prefix + "传送成功.");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (args[0].equalsIgnoreCase("accept")) {
            UUID requesterUUID = Main.rbqs.get(((Player) sender).getUniqueId());
            if (requesterUUID == null) {
                sender.sendMessage(prefix + "没有请求.");
                return true;
            }
            Player requester = Bukkit.getPlayer(requesterUUID);
            Main.rbqs.remove(((Player) sender).getUniqueId());
            Double money = Main.rbqMoney.get(((Player) sender).getUniqueId());
            if (debug) Bukkit.getLogger().info(money.toString());
            if (money != null) {
                if (getMoney(requester) < money) {
                    sender.sendMessage(ChatColor.RED + "对方的钱不够.");
                    Main.rbqMoney.remove(((Player) sender).getUniqueId());
                    return true;
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco take " + requester.getName() + " " + money);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give " + ChatColor.stripColor(sender.getName()) + " " + money);
                Main.rbqMoney.remove(((Player) sender).getUniqueId());
            }
            try {
                statement.execute("INSERT INTO `" + database + "`.`slimefunbow_rbq` (`rbq`, `admin`) VALUES ('" + sender.getName() + "', '" + requester.getName() + "');");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Player senderPlayer = (Player) sender;
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + senderPlayer.getName() + " meta addprefix 1 \"&a" + requester.getName() + "的rbq &a\"");
            Bukkit.getServer().broadcastMessage(prefix + senderPlayer.getDisplayName() + " 成为 " + requester.getDisplayName() + " 的 rbq.");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        try {
            ResultSet result = statement.executeQuery("SELECT COUNT(1) FROM `minecraft`.`slimefunbow_rbq` WHERE rbq='" + sender.getName() + "';");
            if (debug) Bukkit.getLogger().info(result.toString());
            int count = 0;
            while (result.next()) {
                count = result.getInt(1);
                if (debug) Bukkit.getLogger().info(Integer.toString(count));
            }
            if (count == 1) {
                sender.sendMessage(prefix + "你已经是 rbq 了.");
                return true;
            }
            result = statement.executeQuery("SELECT COUNT(1) FROM `minecraft`.`slimefunbow_rbq` WHERE admin='" + sender.getName() + "';");
            while (result.next()) {
                count = result.getInt(1);
            }
            if (count == 1) {
                sender.sendMessage(prefix + "你已经是 主人 了.");
                return true;
            }
            result = statement.executeQuery("SELECT COUNT(1) FROM `minecraft`.`slimefunbow_rbq` WHERE rbq='" + target.getName() + "';");
            while (result.next()) {
                count = result.getInt(1);
            }
            if (count == 1) {
                sender.sendMessage(prefix + "对方已经是 rbq 了.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (Bukkit.getServer().getPlayerExact(args[0]) == null) {
            sender.sendMessage(prefix + "玩家离线.");
            return true;
        }
        if (args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(prefix + ChatColor.RED + "rbq 是自己.");
            return true;
        }

        if (args.length == 2) {
            if (Main.getMoney((Player) sender) < Double.parseDouble(args[1])) {
                sender.sendMessage(prefix + ChatColor.RED + "你的钱不够.");
                return true;
            } else {
                Main.rbqMoney.put(target.getUniqueId(), Double.parseDouble(args[1]));
                if (debug) {
                    double temp = Main.rbqMoney.get(target.getUniqueId());
                    Bukkit.getLogger().info(Double.toString(temp));
                }
            }
        }
        Main.rbqs.put(target.getUniqueId(), ((Player) sender).getUniqueId());
        sender.sendMessage(prefix + "请求已发送.");
        target.sendMessage(prefix + "你可以输入 /rbq accept 成为 " + ((Player) sender).getDisplayName() + " 的 rbq.\n接受后无法取消.");
        return true;
    }

    public Double getMoney(Player p) {
        return Main.getMoney(p);
    }

    public void log(String s) {
        Bukkit.getLogger().info(s);
    }
}


//    private final Callback<String> callback;
//    public LookupRunnable(Callback<String> callback) {
//        this.callback = callback;
//    }
//                scheduler.scheduleAsyncDelayedTask(Mai, new Runnable() {
//                    @Override
//                    public void run() {
//                        if (Main.target == null) {
//                            sender.sendMessage(prefix + "传送失败.");
//                            return;
//                        }
//                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tp " + sender.getName() + " " + Main.target);
//                        sender.sendMessage(prefix + "传送成功.");
//                        Main.target = null;
//                    }
//                }, 20L);
////            if (!(Main.target == null)) {
////                sender.sendMessage(prefix + "请重试.");
////                return true;
////            }
////            BukkitRunnable r = new BukkitRunnable() {
////                @Override
////                public void run() {
////                }
////            };
////            r.runTaskAsynchronously(Main.getMain());