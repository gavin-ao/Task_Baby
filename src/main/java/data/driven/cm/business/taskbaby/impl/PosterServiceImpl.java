package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.PosterService;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
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
        String originPosterUrl = personalInfo.get(TaskBabyConstant.KEY_POSTER_URL);
        String headImgUrl = personalInfo.get(KEY_HEADIMG_URL);
        String qrCodeUrl = personalInfo.get(TaskBabyConstant.KEY_QRCODE_URL);
        String nickName = personalInfo.get(KEY_NICKNAME);
        return getCombinedCustomiedPosterFilePath(originPosterUrl, headImgUrl, qrCodeUrl, nickName);
    }

    /**
     * @param originPosterUrl
     * @param headImgUrl
     * @param qrCodeUrl
     * @param nickName
     * @return
     */
    @Override
    public String getCombinedCustomiedPosterFilePath(String originPosterUrl, String headImgUrl, String qrCodeUrl, String nickName) {
        if (StringUtils.isNotEmpty(originPosterUrl) && StringUtils.isNotEmpty(headImgUrl) &&
                StringUtils.isNotEmpty(qrCodeUrl) && StringUtils.isNotEmpty(nickName)) {
            Font font = new Font("微软雅黑", Font.PLAIN, 50);
            StringBuilder tempFileNameBuider = new StringBuilder();
            tempFileNameBuider.append(downloadPath).append(File.separator).
                    append(UUIDUtil.getUUID()).append(".jpg");
            Graphics2D g = null;
            try {
                long begin = System.currentTimeMillis();
                BufferedImage imgPoster = getBufferedImage(originPosterUrl);
                float duration = (System.currentTimeMillis()-begin)/1000f;
                BufferedImage imgQRCode = getBufferedImage(qrCodeUrl);
                BufferedImage imgHead = getBufferedImage(headImgUrl);
                //以原始海报作为模板
                g = imgPoster.createGraphics();
                // 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
                begin = System.currentTimeMillis();
                g.drawImage(imgQRCode, imgPoster.getWidth()-40-300, imgPoster.getHeight()-100-300,
                        300, 300, null);
                WeChatUtil.log(log,begin,"重绘二维码");
                //在模版上添加头像(地址,左边距,上边距,图片宽度,图片高度,未知)
                begin =System.currentTimeMillis();
                g.drawImage(imgHead, 60, 60, 100, 100, null);
                WeChatUtil.log(log,begin,"重绘头像");
                // 设置文本样式
                begin =System.currentTimeMillis();
                g.setFont(font);
                g.setColor(Color.BLACK);
                g.drawString(nickName, 60 + 100 + 30, 130);
               WeChatUtil.log(log,begin,"写入昵称");
                // 完成模板修改
                g.dispose();
                int type = imgPoster.getType() == 0? BufferedImage.TYPE_INT_ARGB : imgPoster.getType();
                imgPoster = resizeImageWithHint(imgPoster,type);
                // 获取新文件的地址
                File outputfile = new File(tempFileNameBuider.toString());
                begin =System.currentTimeMillis();
                // 生成新的合成过的用户二维码并写入新图片
                begin =System.currentTimeMillis();
                ImageIO.write(imgPoster, "png", outputfile);
                WeChatUtil.log(log,begin,"合成海报保存");
                return tempFileNameBuider.toString();
            } catch (IOException e) {
                log.error("-----------拼接海报出错：", e.getMessage());
            }

        }
        //TODO:暂时只返回二维码图片，后面要改成合成后的图片
        return null;
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
        log.info(String.format("-------根据图片地址加载图片，地址：%s",imgAddress));
        long beign = System.currentTimeMillis();
        //imgAddres是url
        if(imgAddress.startsWith("http")){
            URL url = null;
            InputStream is= null;
            try {
                url = new URL(imgAddress);
                is = url.openConnection().getInputStream();
                BufferedImage image = ImageIO.read(is);
                WeChatUtil.log(log,beign,"加载url图片");
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

                    }
                }

            }

        }else{//imgAddress是本地路径
            try {
                BufferedImage image = ImageIO.read(new File(imgAddress));
                WeChatUtil.log(log,beign,"加载本地图片");
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
            //生成一个随机的文件名
            String fileName = UUIDUtil.getUUID();
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

    public boolean cleanTempflie(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
            return true;
        }
        return false;
    }
    private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){

        BufferedImage resizedImage = new BufferedImage(510, 800, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 510, 800, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }
}
