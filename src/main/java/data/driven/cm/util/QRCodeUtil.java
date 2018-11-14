package data.driven.cm.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Hashtable;

/**
 * 二维码工具类
 * @author 何晋凯
 * @date 2018/06/05
 */
public class QRCodeUtil {

    private static final int width = 430; // 二维码图片宽度
    private static final int height = 430; // 二维码图片高度
    private static final String fileType = "jpg"; // 二维码图片类型

    public static void main(String[] args)throws Exception {
//        String content = "http://101.201.34.29/tourism/scenery/findSceneryList?scenicSpotId=5b17a5bba9cbc1379427d3ba";
//        content = decodeQRCode(filePath);
//        System.out.println(content);
        String str = decodeQRCode( "e://a.jpg");
        System.out.println(str);
//        createQRCode(content, "e://a.jpg");
    }

    /**
     * 生成二维码
     * @param content   二维码内容
     * @param filePath  二维码生成路径，包含文件名，不带后缀
     * @throws Exception
     */
    public static void createQRCode(String content, String filePath) throws Exception{
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");	// 内容所使用字符集编码
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        // 生成二维码
        MatrixToImageWriter.writeToPath(bitMatrix, fileType, Paths.get(filePath + "." + fileType));
    }
    /**
     * 生成二维码 - 输出流
     * @param content   二维码内容
     * @param stream  二维码输出流
     * @throws Exception
     */
    public static void createQRCode(String content, OutputStream stream) throws Exception{
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");	// 内容所使用字符集编码
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        // 生成二维码
        MatrixToImageWriter.writeToStream(bitMatrix, fileType, stream);
    }

    /**
     * 根据二维码获取内容
     * @param filePath  二维码生成路径
     * @throws Exception
     */
    public static String decodeQRCode(String filePath) throws Exception{
        BufferedImage image = ImageIO.read(Paths.get(filePath).toFile());
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Result result = multiFormatReader.decode(bitmap, hints);
        String content = result.getText();
        return content;
    }
}
