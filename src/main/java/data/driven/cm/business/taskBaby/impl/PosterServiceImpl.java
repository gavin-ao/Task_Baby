package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.PosterService;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.Map;

import static data.driven.cm.component.WeChatConstant.KEY_HEADIMG_URL;
import static data.driven.cm.component.WeChatConstant.KEY_NICKNAME;

/**
 * @program: Task_Baby
 * @description: 海报服务
 * @author: Logan
 * @create: 2018-11-15 12:01
 **/
@Service
public class PosterServiceImpl implements PosterService {
    private static final Logger log = LoggerFactory.getLogger(PosterServiceImpl.class);
    @Value("${file.download.path}")
    private String downloadPath;


    /**
     * @param personalInfo Map需要:
     *                     KEY_POSTER_URL:原始海报的url
     *                     KEY_HEADIMG_URL:用户头像url
     *                     KEY_QRCODE_URL
     *                     KEY_NICKNAME
     * @return
     */

    @Override
    public String getCombinedCustomiedPosterFilePath(Map<String, String> personalInfo) {
        String OriginlPosterUrl = personalInfo.get(TaskBabyConstant.KEY_POSTER_URL);
        String headImgUrl = personalInfo.get(KEY_HEADIMG_URL);
        String qrCodeUrl = personalInfo.get(TaskBabyConstant.KEY_QRCODE_URL);
        String nickName = personalInfo.get(KEY_NICKNAME);
        return getCombinedCustomiedPosterFilePath(OriginlPosterUrl, headImgUrl, qrCodeUrl, nickName);
    }

    /**
     * @param OriginlPosterUrl
     * @param headImgUrl
     * @param qrCodeUrl
     * @param nickName
     * @return
     */
    @Override
    public String getCombinedCustomiedPosterFilePath(String OriginlPosterUrl, String headImgUrl, String qrCodeUrl, String nickName) {
        if (StringUtils.isNotEmpty(OriginlPosterUrl) && StringUtils.isNotEmpty(headImgUrl) &&
                StringUtils.isNotEmpty(qrCodeUrl) && StringUtils.isNotEmpty(nickName)) {
            Font font = new Font("微软雅黑", Font.PLAIN, 50);
            StringBuilder tempFileNameBuider = new StringBuilder();
            tempFileNameBuider.append(downloadPath).append(File.separator).
                    append(UUIDUtil.getUUID()).append(".jpg");
            Graphics2D g = null;
            try {
                log.debug("--------加载原始海报, 路径：", OriginlPosterUrl, "--------------");
                BufferedImage imgPoster = getBufferedImage(OriginlPosterUrl);
                log.debug("--------加载二维码,路径：", qrCodeUrl, "----------------");
                BufferedImage imgQRCode = getBufferedImage(qrCodeUrl);
                log.debug("--------加载头像,路径：", headImgUrl, "----------------");
                BufferedImage imgHead = getBufferedImage(headImgUrl);
                //以原始海报作为模板
                g = imgPoster.createGraphics();
                // 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
                g.drawImage(imgQRCode, imgPoster.getWidth()-40-300, imgPoster.getHeight()-100-300,
                        300, 300, null);
                //在模版上添加头像(地址,左边距,上边距,图片宽度,图片高度,未知)
                g.drawImage(imgHead, 60, 60, 100, 100, null);
                // 设置文本样式
                g.setFont(font);
                g.setColor(Color.BLACK);
                // 截取用户名称的最后一个字符
//                    String lastChar = userName.substring(userName.length() - 1);
                // 拼接新的用户名称
//                    String newUserName = userName.substring(0, 1) + "**" + lastChar + " 的邀请二维码";
                // 添加用户名称
                g.drawString(nickName, 60 + 100 + 60, 130);
                // 完成模板修改
                g.dispose();
                // 获取新文件的地址
                File outputfile = new File(tempFileNameBuider.toString());
                // 生成新的合成过的用户二维码并写入新图片
                ImageIO.write(imgPoster, "png", outputfile);
                return tempFileNameBuider.toString();
            } catch (IOException e) {
                log.error("-----------拼接海报出错：", e.getMessage());
            }

        }
        return null;//TODO:暂时只返回二维码图片，后面要改成合成后的图片
    }

    /***
     * 将文件按照地址读进来，返回BufferedImage
     *
     * @author:     Logan
     * @date:       2018/11/17 01:27
     * @params:     [imgAddress]   支持以http开头的url和本地文件
     * @return:     java.awt.image.BufferedImage
    **/
    private BufferedImage getBufferedImage(String imgAddress){
        if(imgAddress.startsWith("http")){//imgAddres是url
            URL url = null;
            InputStream is= null;
            try {
                url = new URL(imgAddress);
                is = url.openConnection().getInputStream();
                BufferedImage image = ImageIO.read(is);
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        log.error("---------读取图片文件错误：url：",imgAddress,"---------");
                        log.error(e.getMessage());
                        return null;
                    }
                }

            }

        }else{//imgAddress是本地路径
            try {
                BufferedImage image = ImageIO.read(new File(imgAddress));
                return image;
            } catch (IOException e) {
                log.error("---------读取图片文件错误：本地路径：",imgAddress,"---------");
                log.error(e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * 将URL图片存在本地，并返回filepath
     *
     * @param imgUrl
     * @return 图片下载之后在本地的路径
     */

    private String getTempImgFilePathByUrl(String imgUrl) {
        URL url = null;
        OutputStream outPutStream = null;
        BufferedReader br = null;
        InputStreamReader inReader = null;
        try {
            url = new URL(imgUrl);
            String fileName = UUIDUtil.getUUID();//生成一个随机的文件名
            //将download根路径和随机文件名拼接成一个完整的filePath,作为临时文件的路径
            StringBuilder pathStrBuilder = new StringBuilder();
            pathStrBuilder.append(downloadPath).append(File.separator).append(fileName);
            File file = new File(pathStrBuilder.toString());
            outPutStream = new FileOutputStream(file);
            URLConnection conn = url.openConnection();
            inReader = new InputStreamReader(conn.getInputStream());
            br = new BufferedReader(inReader);
            String urlString = "";
            String current;
            while ((current = br.readLine()) != null) {
                urlString += current;
            }
            outPutStream.write(urlString.getBytes());
            return pathStrBuilder.toString();
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (outPutStream != null) {
                try {
                    outPutStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }

        }
        return "";
    }

    public boolean Clean(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
            return true;
        }
        return false;
    }
}
