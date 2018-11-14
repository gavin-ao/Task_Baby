package data.driven.cm.util;

import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 何晋凯
 * @date 2018/06/04
 */
public class UUIDUtil {

    public static String getUUID(){
        return new ObjectId().toString();
    }

    public static void main(String[] args)throws Exception {
        String sql = "INSERT INTO `reward_act_command` (`command_id`, `command`, `command_type`, `act_id`, `user_id`, `app_info_id`, `used`, `create_at`) VALUES ";
        StringBuilder sb = new StringBuilder(sql);
        SimpleDateFormat sdf = DateFormatUtil.getLocal("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        for (int i = 0; i < 50000; i++){
            String b = "('" + getUUID() + "', '￥nj2RbgY67zn￥', '2', '12', 'fkxg', '5b699c9171c8a90ec8201702', '0', '" + date + "')";
            sb.append(b).append(",");
        }
        System.out.println(sb.delete(sb.length()-1,sb.length()));
        String pathStr = "E://sql.sql";
        Path path = Files.createFile(Paths.get(pathStr));
        File file = path.toFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(sb.toString().getBytes());


//        System.out.println(getUUID());

//        String pathA = "E:/tempbj/a.txt";
//        String pathB = "E:/tempbj/b.txt";
//
//        String a = Files.readAllLines(Paths.get(pathA)).get(0);
//        String b = Files.readAllLines(Paths.get(pathB)).get(0);
//
//        System.out.println(a.length());
//        System.out.println(b.length());

    }
}
