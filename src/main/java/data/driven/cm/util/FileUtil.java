package data.driven.cm.util;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * 文件工具类
 * @author 何晋凯
 * @date 2018/06/05
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static JSONObject uploadFile(byte[] file, String fileName) throws Exception {
        JSONObject result = new JSONObject();
        String yearStr = DateFormatUtil.getLocal("yyyyMM").format(new Date());
        Path path = Paths.get(Constant.FILE_UPLOAD_PATH + yearStr);
        File f = path.toFile();
        if(!f.exists()){
            f.mkdirs();
        }
        fileName = yearStr + "/" + fileName;
        String newFilePath = Constant.FILE_UPLOAD_PATH + fileName;
        FileOutputStream out = new FileOutputStream(newFilePath);
        out.write(file);
        out.flush();
        out.close();
        result.put("relativePath", fileName);
        result.put("newFilePath", newFilePath);
        result.put("success", true);
        return result;
    }

    public static boolean createFile(String fileName, byte[] b){
        FileOutputStream outStream = null;
        try{
            Path path = Paths.get(fileName.substring(0, fileName.lastIndexOf("/")));
            File folder = path.toFile();
            if(!folder.exists()){
                folder.mkdirs();
            }
            //new一个文件对象用来保存文件
            File imageFile = new File(fileName);
            //创建输出流
            outStream = new FileOutputStream(imageFile);
            //写入数据
            outStream.write(b);
            outStream.flush();
        }catch (FileNotFoundException e){
            logger.error(e.getMessage(), e);
        }catch (IOException e){
            logger.error(e.getMessage(), e);
        }finally {
            if(outStream != null){
                try {
                    outStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    //读取文件
    public static byte[] readInputStream(InputStream inStream) throws IOException{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    public static String getFileHeader( MultipartFile file) {
        InputStream is = null;
        String value = null;
        try {
            is = file.getInputStream();
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println(builder.toString());
        return builder.toString();
    }
}
