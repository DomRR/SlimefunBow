package qwq;

import com.google.common.collect.Maps;
import net.milkbowl.vault.economy.Economy;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {
    private static Economy econ = null;

    // 数据库
    private Connection connection;
    public static String host, database, username, password;
    private int port;
    public static Statement statement;

    // 配置文件
    public static String prefix;
    String world;
    public static int sfCooldown;
    String bow_0_KickReason;
    int bow_0_Chance, bow_1_Chance, bow_2_Chance, bow_3_Chance, bow_4_Chance, bow_4_Duration;
    boolean bow_0_Kick;
    double bow_1_TakeMoney;
    int saviorLevel;
    boolean takeEXP;
    int allexp;
    int all2exp;
    int all2Multiple;
    int rubyexp;
    int rubylevel;
    int sapphireexp;
    int sapphirelevel;
    int onyxexp;
    int onyxlevel;

    public static boolean debug = false;
    FileConfiguration config = this.getConfig();
    File mySQLConfig = new File("plugins\\SlimefunBow", "数据库.yml");
    FileConfiguration mysql = YamlConfiguration.loadConfiguration(mySQLConfig);
    public static Map<UUID, UUID> rbqs = Maps.newHashMap();
    public static Map<UUID, Double> rbqMoney = Maps.newHashMap();
    public static Map<UUID, Long> slimefunCooldown = Maps.newHashMap();
    private static Main main;
    public static String target;

    @Override
    public void onEnable() {
        main = this;
        setupEconomy();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new HackItems(), this);
        getServer().getPluginManager().registerEvents(new SlimefunCooldown(), this);
        Category rbq = new Category(new CustomItem(new MaterialData(Material.DIAMOND), "&4rbq", "", "&a> 点击打开"));
        SlimefunItem green_bow = new SlimefunItem(rbq, new CustomItem(new MaterialData(Material.BOW), ChatColor.GREEN + "绿弓", "&r耐久: &e233"), "GREEN_BOW", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{null, SlimefunItems.GRANDPAS_WALKING_STICK, SlimefunItems.BLADE_OF_VAMPIRES, SlimefunItems.GRANDPAS_WALKING_STICK, null, SlimefunItems.BLADE_OF_VAMPIRES, null, SlimefunItems.GRANDPAS_WALKING_STICK, SlimefunItems.BLADE_OF_VAMPIRES});
        SlimefunItem yellow_bow = new SlimefunItem(rbq, new CustomItem(new MaterialData(Material.BOW), ChatColor.YELLOW + "黄弓", "&r耐久: &e233"), "YELLOW_BOW", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{null, IS(Material.STICK, null, null), SlimefunItems.GOLD_24K_BLOCK, IS(Material.STICK, null, null), null, SlimefunItems.GOLD_24K_BLOCK, null, IS(Material.STICK, null, null), SlimefunItems.GOLD_24K_BLOCK});
        SlimefunItem red_bow = new SlimefunItem(rbq, new CustomItem(new MaterialData(Material.BOW), ChatColor.RED + "红弓", "&r耐久: &e233"), "RED_BOW", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{null, IS(Material.STICK, null, null), SlimefunItems.RUNE_FIRE, IS(Material.STICK, null, null), null, SlimefunItems.RUNE_FIRE, null, IS(Material.STICK, null, null), SlimefunItems.RUNE_FIRE});
        SlimefunItem purple_bow = new SlimefunItem(rbq, new CustomItem(new MaterialData(Material.BOW), ChatColor.DARK_PURPLE + "紫弓", "&r耐久: &e233"), "PURPLE_BOW", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{null, IS(Material.STICK, null, null), SlimefunItems.RUNE_ENDER, IS(Material.STICK, null, null), null, SlimefunItems.RUNE_ENDER, null, IS(Material.STICK, null, null), SlimefunItems.RUNE_ENDER});
        SlimefunItem gold_bow = new SlimefunItem(rbq, new CustomItem(new MaterialData(Material.BOW), ChatColor.GOLD + "橙弓", "&r耐久: &e233"), "PURPLE_BOW", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{null, IS(Material.STICK, null, null), SlimefunItems.RUNE_EARTH, IS(Material.STICK, null, null), null, SlimefunItems.RUNE_EARTH, null, IS(Material.STICK, null, null), SlimefunItems.RUNE_EARTH});
        green_bow.register();
        yellow_bow.register();
        red_bow.register();
        purple_bow.register();
        gold_bow.register();
        Research research$1 = new Research(27649, ChatColor.GREEN + "绿弓!", 100);
        research$1.addItems(green_bow);
        research$1.register();
        Research research$2 = new Research(27650, ChatColor.YELLOW + "黄弓!", 100);
        research$2.addItems(yellow_bow);
        research$2.register();
        Research research$3 = new Research(27651, ChatColor.RED + "红弓!", 100);
        research$3.addItems(red_bow);
        research$3.register();
        Research research$4 = new Research(27652, ChatColor.DARK_PURPLE + "紫弓!", 100);
        research$4.addItems(purple_bow);
        research$4.register();
        Research research$5 = new Research(27653, ChatColor.GOLD + "橙弓!", 100);
        research$5.addItems(gold_bow);
        research$5.register();
        config.addDefault("插件前缀", "&b[SlimefunBow] &r");
        config.addDefault("可用世界", "world");
        config.addDefault("Slimefun书冷却", 3);
        config.addDefault("绿弓踢出原因", "233");
        config.addDefault("绿弓效果几率", 10);
        config.addDefault("绿弓踢出", false);
        config.addDefault("黄弓效果几率", 5);
        config.addDefault("黄弓盗取游戏币数量", 2.33);
        config.addDefault("红弓效果几率", 5);
        config.addDefault("紫弓效果几率", 4);
        config.addDefault("橙弓效果几率", 4);
        config.addDefault("橙弓效果持续时间", 60);
        config.addDefault("Savior等级", 3);
        config.addDefault("花费经验", false);
        config.addDefault("All2倍数", 2);

        config.addDefault("AllEXP", 1000);
        config.addDefault("All2EXP", 2000);
        config.addDefault("RubyEXP", 2000);
        config.addDefault("RubyLevel", 3);
        config.addDefault("SapphireEXP", 2000);
        config.addDefault("SapphireLevel", 2);
        config.addDefault("OnyxEXP", 2000);
        config.addDefault("OnyxLevel", 2);
        config.options().copyDefaults(true);

        mysql.addDefault("主机", "127.0.0.1");
        mysql.addDefault("端口", 3306);
        mysql.addDefault("数据库", "mc");
        mysql.addDefault("用户名", "root");
        mysql.addDefault("密码", "123456");
        mysql.options().copyDefaults(true);
        try {
            mysql.save(mySQLConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveConfig();

        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("插件前缀"));
        world = getConfig().getString("可用世界");
        sfCooldown = getConfig().getInt("Slimefun书冷却");
        bow_0_KickReason = getConfig().getString("绿弓踢出原因");
        bow_0_Chance = getConfig().getInt("绿弓效果几率");
        bow_0_Kick = getConfig().getBoolean("绿弓踢出");
        bow_1_Chance = getConfig().getInt("黄弓效果几率");
        bow_1_TakeMoney = getConfig().getDouble("黄弓盗取游戏币数量");
        bow_2_Chance = getConfig().getInt("红弓效果几率");
        bow_3_Chance = getConfig().getInt("紫弓效果几率");
        bow_4_Chance = getConfig().getInt("橙弓效果几率");
        bow_4_Duration = getConfig().getInt("橙弓效果持续时间");
        saviorLevel = getConfig().getInt("Savior等级");
        takeEXP = getConfig().getBoolean("花费经验");
        all2Multiple = getConfig().getInt("All2倍数");

        allexp = getConfig().getInt("AllEXP");
        all2exp = getConfig().getInt("All2EXP");
        rubyexp = getConfig().getInt("RubyEXP");
        rubylevel = getConfig().getInt("RubyLevel");
        sapphireexp = getConfig().getInt("SapphireEXP");
        sapphirelevel = getConfig().getInt("SapphireLevel");
        onyxexp = getConfig().getInt("OnyxEXP");
        onyxlevel = getConfig().getInt("OnyxLevel");

        getLogger().info("配置加载.");

        host = mysql.getString("主机");
        port = mysql.getInt("端口");
        database = mysql.getString("数据库");
        username = mysql.getString("用户名");
        password = mysql.getString("密码");

        try {
            openConnection();
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.getCommand("rbq").setExecutor(new rbq());
        this.getCommand("RestoreHelmet").setExecutor(new RestoreHelmet());
        getLogger().info("插件加载.");

        if (debug) getLogger().info("Debug 已启用!");
    }

    @Override
    public void onDisable() {
        main = null;
        getLogger().info("插件卸载.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("RE")) {
            if (!(sender instanceof Player)) {
                if (args.length < 2) {
                    getLogger().info(prefix + "没有足够的参数!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("Ruby")) {
                    int i = new Random().nextInt(rubylevel);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Ruby " + (i + 1) + " " + args[1]);
                    return true;
                }
                if (args[0].equalsIgnoreCase("Sapphire")) {
                    int i = new Random().nextInt(sapphirelevel);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Sapphire " + (i + 1) + " " + args[1]);
                    return true;
                }
                if (args[0].equalsIgnoreCase("Onyx")) {
                    int i = new Random().nextInt(onyxlevel);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Onyx " + (i + 1) + " " + args[1]);
                    return true;
                }
                if (args[0].equalsIgnoreCase("Savior")) {
                    int i = new Random().nextInt(saviorLevel);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Savior " + (i + 1) + " " + args[1]);
                    return true;
                }
                if (args[0].equalsIgnoreCase("All")) {
                    int i = new Random().nextInt(4);
                    if (i == 0) {
                        int i$2 = new Random().nextInt(rubylevel);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Ruby " + (i$2 + 1) + " " + args[1]);
                    }
                    if (i == 1) {
                        int i$2 = new Random().nextInt(sapphirelevel);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Sapphire " + (i$2 + 1) + " " + args[1]);
                    }
                    if (i == 2) {
                        int i$2 = new Random().nextInt(onyxlevel);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Onyx " + (i$2 + 1) + " " + args[1]);
                    }
                    if (i == 3) {
                        int i$2 = new Random().nextInt(saviorLevel);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Savior " + (i$2 + 1) + " " + args[1]);
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("All2")) {
                    int i = new Random().nextInt(4);
                    if (i == 0) {
                        int i$2 = new Random().nextInt((int) ((rubylevel * all2Multiple) + 0.5));
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Ruby " + (i$2 + 1) + " " + args[1]);
                    }
                    if (i == 1) {
                        int i$2 = new Random().nextInt((int) ((sapphirelevel * all2Multiple) + 0.5));
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Sapphire " + (i$2 + 1) + " " + args[1]);
                    }
                    if (i == 2) {
                        int i$2 = new Random().nextInt((int) ((onyxlevel * all2Multiple) + 0.5));
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Onyx " + (i$2 + 1) + " " + args[1]);
                    }
                    if (i == 3) {
                        int i$2 = new Random().nextInt((int) ((saviorLevel * all2Multiple) + 0.5));
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "socket load Savior " + (i$2 + 1) + " " + args[1]);
                    }
                    return true;
                }
                getLogger().info(prefix + "没有这个宝石.");
                return true;
            } else {
                sender.sendMessage(prefix + "你必须是控制台!");
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("REReload")) {
            reloadConfig();
            prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("插件前缀"));
            world = getConfig().getString("可用世界");
            sfCooldown = getConfig().getInt("Slimefun书冷却");
            bow_0_KickReason = getConfig().getString("绿弓踢出原因");
            bow_0_Chance = getConfig().getInt("绿弓效果几率");
            bow_0_Kick = getConfig().getBoolean("绿弓踢出");
            bow_1_Chance = getConfig().getInt("黄弓效果几率");
            bow_1_TakeMoney = getConfig().getDouble("黄弓盗取游戏币数量");
            bow_2_Chance = getConfig().getInt("红弓效果几率");
            bow_3_Chance = getConfig().getInt("紫弓效果几率");
            bow_4_Chance = getConfig().getInt("橙弓效果几率");
            bow_4_Duration = getConfig().getInt("橙弓效果持续时间");
            saviorLevel = getConfig().getInt("Savior等级");
            takeEXP = getConfig().getBoolean("花费经验");
            all2Multiple = getConfig().getInt("All2倍数");

            allexp = getConfig().getInt("AllEXP");
            all2exp = getConfig().getInt("All2EXP");
            rubyexp = getConfig().getInt("RubyEXP");
            rubylevel = getConfig().getInt("RubyLevel");
            sapphireexp = getConfig().getInt("SapphireEXP");
            sapphirelevel = getConfig().getInt("SapphireLevel");
            onyxexp = getConfig().getInt("OnyxEXP");
            onyxlevel = getConfig().getInt("OnyxLevel");
            if (sender instanceof Player) {
                sender.sendMessage("重新加载成功.");
            } else getLogger().info("重新加载成功.");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("REDebug")) {
            if (debug) debug = false;
            else debug = true;
            if (sender instanceof Player) {
                sender.sendMessage("切换成功.");
            } else getLogger().info("切换成功.");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        // 判断是否为普通弓 防止名称为null 下面代码报错
        if (e.getBow().getItemMeta().getDisplayName() == null) return;
        if ((e.getBow().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "绿弓")) | (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "黄弓"))| (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.RED + "红弓")) | (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "紫弓")) | (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "橙弓"))) {
            if (debug) getLogger().info(e.getBow().getItemMeta().getLore().toString());
            String temp = ChatColor.stripColor(e.getBow().getItemMeta().getLore().toString());
            if (!temp.contains("耐久:")) {
                if (p.getInventory().getItemInMainHand().getType() == Material.BOW) {
                    p.getInventory().remove(e.getBow());
                }
                return;
            }
            String[] tempSplit = temp.split(" ");
            tempSplit = tempSplit[1].split("]");
            int i = Integer.parseInt(tempSplit[0]) - 1;
            if (i <= 0) {
                if (p.getInventory().getItemInMainHand().getType() == Material.BOW) {
                    p.getInventory().remove(e.getBow());
                }
                return;
            }
            e.getBow().setItemMeta(IM(e.getBow().getType(), e.getBow().getItemMeta().getDisplayName(), "&r耐久: &e" + Integer.toString(i)));
            if (debug) getLogger().info(p.toString());
            if (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "绿弓")) {
                e.getProjectile().setMetadata("Bow$0", new FixedMetadataValue(this, e.getProjectile()));
            }
            if (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "黄弓")) {
                e.getProjectile().setMetadata("Bow$1", new FixedMetadataValue(this, e.getProjectile()));
            }
            if (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.RED + "红弓")) {
                e.getProjectile().setMetadata("Bow$2", new FixedMetadataValue(this, e.getProjectile()));
            }
            if (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "紫弓")) {
                e.getProjectile().setMetadata("Bow$3", new FixedMetadataValue(this, e.getProjectile()));
            }
            if (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "橙弓")) {
                e.getProjectile().setMetadata("Bow$4", new FixedMetadataValue(this, e.getProjectile()));
            }
        }
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent e) {
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow)) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (debug)
            getLogger().info(p.getWorld() + "," + p.getWorld().toString() + "," + p.getWorld().getName().toString() + "," + world);
        if (!(p.getWorld().getName().toString().equals(world))) return;
        Arrow arrow = (Arrow) e.getDamager();
        Player pShooter = (Player) arrow.getShooter();
        if (arrow.hasMetadata("Bow$0")) {
            int i = new Random().nextInt(bow_0_Chance);
            if (debug) getLogger().info("随机: " + Integer.toString(i));
            if (i == 0) {
                if (p.getInventory().getHelmet() == null) return;
                ItemStack original_IS = p.getInventory().getHelmet();
                ItemStack new_IS = new ItemStack(Material.LEATHER_HELMET);
                LeatherArmorMeta temp_LAM = (LeatherArmorMeta) new_IS.getItemMeta();
                temp_LAM.setColor(Color.fromRGB(0,128,0));
                new_IS.setItemMeta(temp_LAM);
                new_IS.addEnchantment(Enchantment.BINDING_CURSE, 1);
                p.getInventory().setHelmet(new_IS);
                BukkitScheduler scheduler = getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        if (Bukkit.getServer().getPlayerExact(p.getName()) == null) {
                            getLogger().info("恢复失败, 玩家离线: " + p.getName());
                            getLogger().info(original_IS.toString());
                            File playerData = new File(getMain().getDataFolder(), File.separator + "恢复数据");
                            File file = new File(playerData, File.separator + p.getName() + ".yml");
                            FileConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(file);
                            String time = Long.toString(new Date().getTime());
                                try {
                                    playerDataConfig.createSection("恢复数据" + time);
                                    playerDataConfig.set("恢复数据" + time, original_IS);
                                    playerDataConfig.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            return;
                        }
                        p.getInventory().setHelmet(original_IS);
                    }
                }, 100L);
            }
        }
        if (arrow.hasMetadata("Bow$1")) {
            int i = new Random().nextInt(bow_1_Chance);
            if (i == 0) {
                if (econ.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId())) < bow_1_TakeMoney) {
                    if (debug) {
                        double temp = econ.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId()));
                        getLogger().info(Double.toString(temp));
                    }
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + ChatColor.stripColor(p.getName()));
                    return;
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco take " + ChatColor.stripColor(p.getName()) + " " + Double.toString(bow_1_TakeMoney));
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give " + ChatColor.stripColor(pShooter.getName()) + " " + Double.toString(bow_1_TakeMoney));
                return;
            }
        }
        if (arrow.hasMetadata("Bow$2")) {
            int i = new Random().nextInt(bow_2_Chance);
            if (i == 0) {
                Player enviar = p;
                String path = Bukkit.getServer().getClass().getPackage().getName();
                String version = path.substring(path.lastIndexOf(".") + 1, path.length());
                try {
                    Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
                    Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
                    Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");
                    Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(new Class[] { Integer.TYPE, Float.TYPE });
                    Object packet = playOutConstructor.newInstance(new Object[] { Integer.valueOf(5), Integer.valueOf(0) });
                    Object craftPlayerObject = craftPlayer.cast(enviar);
                    Method getHandleMethod = craftPlayer.getMethod("getHandle", new Class[0]);
                    Object handle = getHandleMethod.invoke(craftPlayerObject, new Object[0]);
                    Object pc = handle.getClass().getField("playerConnection").get(handle);
                    Method sendPacketMethod = pc.getClass().getMethod("sendPacket", new Class[] { Packet });
                    sendPacketMethod.invoke(pc, new Object[] { packet });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                pShooter.sendMessage(prefix + "操作成功.");
                return;
            }
        }
        if (arrow.hasMetadata("Bow$3")) {
            int i = new Random().nextInt(bow_3_Chance);
            if (debug) getLogger().info("随机: " + Integer.toString(i));
            if (i == 0) {
                Location location = p.getLocation();
                Location location1 = pShooter.getLocation();
                p.teleport(location1);
                pShooter.teleport(location);
            }
        }
        if (arrow.hasMetadata("Bow$4")) {
            int i = new Random().nextInt(bow_4_Chance);
            if (debug) getLogger().info("随机: " + Integer.toString(i));
            if (i == 0) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, bow_4_Duration * 20 ,2));
                pShooter.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, bow_4_Duration * 20 ,2));
            }
        }
    }

    public static ItemStack IS(Material mat, String name, String lore) {
        if ((name == null) && (lore == null)) {
            ItemStack is = new ItemStack(mat);
            return is;
        }
        if (name == null) {
            ItemStack is = new ItemStack(mat);
            ItemMeta im = is.getItemMeta();
            ArrayList<String> str = new ArrayList<String>();
            str.add(ChatColor.translateAlternateColorCodes('&', lore));
            im.setLore(str);
            is.setItemMeta(im);
            return is;
        } else {
            ItemStack is = new ItemStack(mat);
            ItemMeta im = is.getItemMeta();
            ArrayList<String> str = new ArrayList<String>();
            str.add(ChatColor.translateAlternateColorCodes('&', lore));
            im.setLore(str);
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            is.setItemMeta(im);
            return is;
        }
    }

    public static ItemMeta IM(Material mat, String name, String lore) {
        if (name == null && lore == null) {
            ItemStack is = new ItemStack(mat);
            ItemMeta im = is.getItemMeta();
            return im;
        } else {
            ItemStack is = new ItemStack(mat);
            ItemMeta im = is.getItemMeta();
            ArrayList<String> str = new ArrayList<String>();
            str.add(ChatColor.translateAlternateColorCodes('&', lore));
            im.setLore(str);
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            return im;
        }
    }

    public static ItemStack IM2IS(Material mat, ItemMeta im){
        ItemStack is = new ItemStack(mat);
        is.setItemMeta(im);
        return is;
    }

    public static void XP(int xp, String name) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:xp -" + xp + "L " + name);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    public static Double getMoney(Player p) {
        return econ.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId()));
    }

    public static Main getMain() {
        return main;
    }

    public static void loadConfig() {
    }
}


// //   字符串转换unicode
//    public static String stringToUnicode(String string) {
//        StringBuffer unicode = new StringBuffer();
//        for (int i = 0; i < string.length(); i++) {
//            char c = string.charAt(i);  // 取出每一个字符/            unicode.append("\\u" +Integer.toHexString(c));// 转换为unicode
//        }
//        return unicode.toString();
//    }
//        if(e.getHitEntity().getType().name().equals("PLAYER")){
////            Player p = (Player) e.getEntity();
////            p.kickPlayer("test");
////        }
////        Player p = (Player) e.getEntity();
////        getLogger().info(p.toString());
//
//
//
//
//
////        ItemStack is = new ItemStack(Material.DIAMOND);
////        ItemMeta im = is.getItemMeta();
////        ArrayList<String> str = new ArrayList<String>();
////        str.add("test");
////        im.setLore(str);
////        is.setItemMeta(im);
////        getLogger().info(Integer.toString(e.getEntity().getEntityId()));
////        getLogger().info(e.getEventName());
////        getLogger().info(e.getEntityType().name());
////        getLogger().info(e.getEntity().toString());
////        getLogger().info(e.getEntity().getKiller().toString());
////        getLogger().info(stringToUnicode(e.getBow().getItemMeta().getDisplayName()));
////        getLogger().info(stringToUnicode(ChatColor.GREEN+"绿弓"));
////        ItemStack is = new ItemStack(Material.DIAMOND);
////        ItemMeta im = is.getItemMeta();
////        ArrayList<String> str = new ArrayList<String>();
////        str.add("test");
////        im.setLore(str);
////        is.setItemMeta(im);
////        getLogger().info(e.getBow().toString());
////        getLogger().info(e.getBow().getItemMeta().getDisplayName());
////        Arrow arrow = (Arrow) e.getDamager();
////        Entity shooter = (Entity) arrow.getShooter();
////        if(!(shooter instanceof Player)) return;
////            getLogger().info("Yes");
////                Player p = (Player) e.getEntity();
////                if (p.getInventory().getItemInMainHand().getType() == Material.BOW) {
////                    p.set
////                }
//
//
////            ArrayList<String> str = new ArrayList<String>();
////            str.add();
////            im.setLore(str);
////            is.setItemMeta(im);
////            e.getBow().setItemMeta(e.getBow().getItemMeta().setLore(str));
////            getLogger().info(temp[0]);
////            getLogger().info(Integer.toString(i));
//            //getLogger().info(temp[1]);
////            e.getBow().getItemMeta().getLore().set("")
////            if ((!temp.contains(" ")) & (!temp.contains("]")) & (!temp.contains("耐久:"))) {
//////            getLogger().info(stringToUnicode(e.getBow().getItemMeta().getLore().toString()));
//////                e.getBow().setType(Material.AIR);
//
////                        if (x == 232) return;
////            if (b[x].equals(stringPlayer)) {
////                if (debug) getLogger().info("ok="+b[x]);
////                break;
////            }
////        }
////            if (debug) getLogger().info("b["+x+"]="+b[x]+"=="+stringPlayer);
//////                if (debug) getLogger().info("ok="+b[x]);
////            e.getProjectile().setMetadata("green_bow",new FixedMetadataValue(this, true));
//
////    @EventHandler(priority = EventPriority.HIGHEST)
////    public void onCommand(PlayerCommandPreprocessEvent event) {
////        String player = event.getPlayer().getName();
////        getLogger().info("PlayerCommandPreprocessEvent "+player);
////        String command = event.getMessage();
////        if ((command.split(" ")[0].toLowerCase().equals("/marry"))) {
////            getLogger().info("setCancelled");
////            event.setCancelled(true);
////        }
////    }
////        if ((e.getEntity().getType() != EntityType.PLAYER) & ((e.getDamager().getType() != EntityType.PLAYER))) {
////            if (debug) getLogger().info("EntityDamageByEntityEvent return;");
////            return;
////        }
////        if (!(e.getDamager() instanceof Arrow)) return;
////        if (!(e.getEntity() instanceof Player)) return;
////        getLogger().info(e.getEntity().getType().toString());
////        getLogger().info(e.getDamager().getType().toString());
////        getLogger().info("arrow fan hui");
//            getLogger().info("| fan hui");
//            getLogger().info("b["+x+"]="+b[x]+"=="+stringPlayer);
//        getLogger().info("1 check");
//        getLogger().info("2 check");
//
//                getLogger().info("3 check");
//
//                getLogger().info("4 check");//            // 记录变量 以便在下一次事件中使用
////            for (int x=0; x < 10; x++) {
////                if (b[x] == null) {
////                    if (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.GREEN+"绿弓")) {
////                        b[x] = p.toString() + ",0";
////                        return;
////                    }
////                    if (e.getBow().getItemMeta().getDisplayName().equals(ChatColor.YELLOW+"黄弓")) {
////                        b[x] = p.toString() + ",1";
////                        return;
////                    }
////                    break;
////                }
////            }
//        } else {
//            // 防止粘液弓切换造成的效果混乱 比如 绿弓 没有打到人 然后用黄弓 会有绿弓的效果
////            ClearArray(b, p);
////        if(e.getHitEntity() == null) {
////            ClearArray(b, p);
////            return;
////        }
//        // 防止玩家第一次使用粘液弓没有射到实体 第二次使用普通弓射中也有技能效果//        if (i==0) {
////            if (debug) getLogger().info(b[x]);
////            String[] tempSplit = b[x].split(",");
////            if (tempSplit.length == 0 & tempSplit[0] == null) return;
////            if (tempSplit[1].equals("0")) {
////                if (kick) {
////                    p.kickPlayer(reason);
////                    b[x] = null;
////                    return;
////                } else {
////                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + ChatColor.stripColor(p.getDisplayName()));
////                    b[x] = null;
////                    return;
////                }
////            }
//////            if (tempSplit[1].equals("1")) {
////            if (tempSplit[1].equals("1")) {
////                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco take " + ChatColor.stripColor(p.getDisplayName()) + " " + Double.toString(bow_1_TakeMoney));
////                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give " + ChatColor.stripColor(player.getDisplayName()) + " " + Double.toString(bow_1_TakeMoney));
////                b[x] = null;
////                return;
////            }
////        }//        if (e.getDamager() == null | e.getEntity() == null) return;
////        if (!(e.getDamager() instanceof Arrow) | !(e.getEntity().getType().toString().equals("PLAYER"))) {
////            return;
////        }
////        Arrow arrow = (Arrow) e.getDamager();
////        Player player = (Player) arrow.getShooter();
////        String stringPlayer = player.toString();
////        // 变量声明在这里 以便下面代码使用
////        int x=0;
////        // 判断是否是粘液弓射出的箭
////        if (stringPlayer == null) return;
////        Player p = (Player) e.getEntity();
////        if (p==null) return;
////        int i = new Random().nextInt(random);
////        if (debug) getLogger().info(Integer.toString(i));
//        if (!(e.getEntity() instanceof Arrow)) return;
//        if (!(e.getHitEntity() instanceof Player)) return;
//        Player p = (Player) e.getHitEntity();
//        Player pShooter = (Player) e.getEntity().getShooter();
//        if (e.getEntity().hasMetadata("Bow$0")) {
//            int i = new Random().nextInt(random);
//            if (i == 0) {
//                if (kick) {
//                    p.kickPlayer(reason);
//                    return;
//                } else {
//                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + ChatColor.stripColor(p.getDisplayName()));
//                    return;
//                }
//            }
//        }
//        if (e.getEntity().hasMetadata("Bow$1")) {
//            int i = new Random().nextInt(random);
//            if (i == 0) {
//                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco take " + ChatColor.stripColor(p.getDisplayName()) + " " + Double.toString(bow_1_TakeMoney));
//                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give " + ChatColor.stripColor(pShooter.getDisplayName()) + " " + Double.toString(bow_1_TakeMoney));
//                return;
//            }
//        }
//        if (debug) getLogger().info(e.getHitEntity().getType().name());//    public static void NoXP() {
////    }
////
////    public static void ClearArray(String[] array, Player p) {
////        for (int a = 0; a < 10; a++) {
////            if (!(array[a] == null)) {
////                if (array[a].contains(p.toString())) {
////                    array[a] = null;
////                }
////            }
////        }
////    }
////        SlimefunItem end = new SlimefunItem(rbq, new CustomItem(new MaterialData(Material.ENDER_PORTAL_FRAME), "&1末地传送门"), "ENDER_PORTAL_FRAME", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {IS(Material.BEDROCK, "&8Bedrock",""), IS(Material.BEDROCK, "&8Bedrock",""), IS(Material.BEDROCK, "&8Bedrock",""), IS(Material.BEDROCK, "&8Bedrock",""), new ItemStack(Material.END_CRYSTAL), IS(Material.BEDROCK, "&8Bedrock",""), IS(Material.BEDROCK, "&8Bedrock",""), IS(Material.BEDROCK, "&8Bedrock",""), IS(Material.BEDROCK, "&8Bedrock","")});
////        end.register();
////        Research research$2 = new Research(4444, "末地传送门方块!", 200); research$2.addItems(end); research$2.register();
////        research$2.addItems(end);
//                    // 这里 绝对不能用 player.getExpToLevel() 方法 被坑死了 这个是当前升到下一级还需要多少经验...
//                    // 我又被坑了 getTotalExperience() 是 玩家在这个服务器上获得的经验 如果附魔 或者其他方式扣除后 就不准确了
////                    if (takeEXP & (expMan.getCurrentExp() < rubyexp)) {
////                        sender.sendMessage(prefix + "经验不足.");
////                        return true;
////                    }
////                    if (takeEXP) expMan.setExp(expMan.getCurrentExp() - rubyexp);
////                    if (takeEXP & (expMan.getCurrentExp() < onyxexp)) {
////                        sender.sendMessage("经验不足.");
////                        return true;
////                    }
////                    if (takeEXP) expMan.setExp(expMan.getCurrentExp() - onyxexp);
////                    if (takeEXP & (expMan.getCurrentExp() < sapphireexp)) {
////                        sender.sendMessage(prefix + "经验不足.");
////                        return true;
////                    }
////                    if (takeEXP) expMan.setExp(expMan.getCurrentExp() - sapphireexp);
////                    if (takeEXP & (expMan.getCurrentExp() < allexp)) {
////                        sender.sendMessage("经验不足.");
////                        return true;
////                    }
////                    if (takeEXP) expMan.setExp(expMan.getCurrentExp() - allexp);//                    if (takeEXP & (expMan.getCurrentExp() < all2exp)) {
////                        sender.sendMessage("经验不足.");
////                        return true;
////                    }
////                    if (takeEXP) expMan.setExp(expMan.getCurrentExp() - all2exp);
////                Player player = (Player) sender;//                ExperienceManager expMan = new ExperienceManager(player);
//
////    @EventHandler
////    public void onPlayerLoginEvent(PlayerLoginEvent e) {
////        Player p = e.getPlayer();
////        for (int test=0; test < 233; test++) {
////            getItemName(p, test);
////        }
//////    }
////
////    interface FindOneCallback {
////        public void onQueryDone(Player result);
////    }
////                if (bow_0_Kick) {
////                    p.kickPlayer(bow_0_KickReason);
////                    return;
////                } else {
////                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + ChatColor.stripColor(p.getName()));
////                    return;
////                }
////            }
////            ArrayList<String> str = new ArrayList<String>();
////            str.add(ChatColor.translateAlternateColorCodes('&', lore));
////            im.setLore(str);
////                            try {
////                                String data_original_IS = original_IS.getTypeId() + "," + original_IS.getItemMeta().getDisplayName() + "," + original_IS.getItemMeta().getLore();
////                                statement.execute("INSERT INTO `" + database + "`.`slimefunbow_restore` (`player`, `log`, `data`) VALUES ('" + p.getName() + "', '" + original_IS.toString() + "', '" + data_original_IS + "');");
////                            } catch (SQLException e) {
////                                e.printStackTrace();
////                            }