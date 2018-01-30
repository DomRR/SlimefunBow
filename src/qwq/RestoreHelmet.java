package qwq;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.sql.Statement;
import java.util.List;

public final class RestoreHelmet implements CommandExecutor{
    Statement statement = Main.statement;
    String prefix = Main.prefix;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("RestoreHelmet")) {
            boolean debug = Main.debug;
            if (!(sender instanceof Player)) {
                log("你必须是一个玩家!");
                return true;
            }
            int count = 0;
            File playerData = new File(Main.getMain().getDataFolder(), File.separator + "恢复数据");
            File file = new File(playerData, File.separator + sender.getName() + ".yml");
            if (!file.exists() || file.length() == 0) {
                sender.sendMessage(prefix + "没有需要恢复的数据.");
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(prefix + "警告, 恢复会替换当前头盔.");
                readTxtFile(file.toString(), (Player) sender);
                return true;
            }
            FileConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(file);
            ItemStack iS = playerDataConfig.getItemStack("恢复数据" + args[0]);
            ((Player) sender).getInventory().setHelmet(iS);
            playerDataConfig.set("恢复数据" + args[0], null);
            try {
                playerDataConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sender.sendMessage(prefix + "恢复成功.");
        }
        return true;
    }

    public void log(String s) {
        Bukkit.getLogger().info(s);
    }

    /**
     * 递归查找文件
     * @param baseDirName  查找的文件夹路径
     * @param targetFileName  需要查找的文件名
     * @param fileList  查找到的文件集合
     */
    public static void findFiles(String baseDirName, String targetFileName, List fileList) {

        File baseDir = new File(baseDirName);       // 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if(tempFile.isDirectory()){
                findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
            }else if(tempFile.isFile()){
                tempName = tempFile.getName();
                if(wildcardMatch(targetFileName, tempName)){
                    // 匹配成功，将文件名添加到结果集
                    fileList.add(tempFile.getAbsoluteFile());
                }
            }
        }
    }

    /**
     * 通配符匹配
     * @param pattern    通配符模式
     * @param str    待匹配的字符串
     * @return    匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                //通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                //通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    //表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }
    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
    public static void readTxtFile(String filePath, Player p){
        try {
            String encoding = "Utf8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    p.sendMessage(lineTxt);
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

}


//                Gson gson = new Gson();
//                ItemStack original_IS = new ItemStack(Material.AIR);
//                original_IS.deserialize(gson.fromJson(original_IS_String, Map.class));
////            try {
////                ResultSet result = statement.executeQuery("SELECT COUNT(1) FROM `minecraft`.`slimefunbow_restore` WHERE player='" + sender.getName() + "';");
////                while (result.next()) {
////                    count = result.getInt(1);
////                }
////                if (count == 0) {
////                    sender.sendMessage(prefix + "没有需要恢复的数据.");
////                    return true;
////                }
////                result = statement.executeQuery("SELECT * FROM `minecraft`.`slimefunbow_restore` WHERE player='" + sender.getName() + "';");
////                if (debug) Bukkit.getLogger().info(result.toString());
////                String original_IS_String = "";
////                while (result.next()) {
////                    original_IS_String  = result.getString("data");
////                }
////                if (debug) log(original_IS_String);
////                String[] s = original_IS_String.split(",");
//////                ((Player) sender).getInventory().setHelmet(Main.IS(Integer.parseInt(s[0]), s[1], s[2]));
////            } catch (SQLException e) {
////                e.printStackTrace();
////            }//    在此目录中找文件
////            String baseDIR = Main.getMain().getDataFolder().toString();
////            //    找扩展名为txt的文件
////            String fileName = sender.getName() + "*.yml";
////            List resultList = new ArrayList();
////            findFiles(baseDIR, fileName,resultList);
////            if (resultList.size() == 0) {
////                log("No File Fount.");
////            } else {
////                for (int i = 0; i < resultList.size(); i++) {
////                    log(resultList.get(i).toString());//显示查找结果。
////                }
////            }
////            if (debug == false) return true;