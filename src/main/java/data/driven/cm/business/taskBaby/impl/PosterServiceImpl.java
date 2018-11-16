package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.PosterService;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

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
     * TODO
     * @param OriginlPosterUrl 客户的原始海报url
     * @param headImgUrl  粉丝头像url
     * @param QRCodeUrl  带参数的二维码url
     * @param nickName
     * @return
     */
    @Override
    public String getCombinedCustomizedPosterUrl(String OriginlPosterUrl, String headImgUrl, String QRCodeUrl, String nickName) {
        return null;
    }

    /**
     *
     * @param personalInfo
     *   Map需要:
     *   KEY_POSTER_URL:原始海报的url
     *   KEY_HEADIMG_URL:用户头像url
     *   KEY_QRCODE_URL
     *   KEY_NICKNAME
     * @return
     */

    @Override
    public String getCombinedCustomiedPosterFilePath(Map<String, String> personalInfo) {
        String OriginlPosterUrl = personalInfo.get(TaskBabyConstant.KEY_POSTER_URL);
        String headImgUrl = personalInfo.get(WeChatUtil.KEY_HEADIMG_URL);
        String qrCodeUrl = personalInfo.get(TaskBabyConstant.KEY_QRCODE_URL);
        String nickName = personalInfo.get(WeChatUtil.KEY_NICKNAME);
        return getCombinedCustomiedPosterFilePath(OriginlPosterUrl,headImgUrl,qrCodeUrl,nickName);
    }

    /**
     * TODO:暂时返回带参数的二维码的文件名便于测试，后面要改成合成之后的海报
     * @param OriginlPosterUrl
     * @param headImgUrl
     * @param qrCodeUrl
     * @param nickName
     * @return
     */
    @Override
    public String getCombinedCustomiedPosterFilePath(String OriginlPosterUrl, String headImgUrl, String qrCodeUrl, String nickName) {
        if(StringUtils.isEmpty(OriginlPosterUrl)||StringUtils.isEmpty(headImgUrl)||
                StringUtils.isEmpty(qrCodeUrl)||StringUtils.isEmpty(nickName)){
            return null;
        }
        return getTempImgFilePathByUrl(qrCodeUrl);//TODO:暂时只返回二维码图片，后面要改成合成后的图片
    }


    /**
     * 将URL图片存在本地，并返回filepath
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
            pathStrBuilder.append(downloadPath).append(File.pathSeparator).append(fileName);
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

    public boolean Clean(String filePath){
        File file=new File(filePath);
        if(file.exists()&&file.isFile()) {
            file.delete();
            return true;
        }
        return false;
    }
}
