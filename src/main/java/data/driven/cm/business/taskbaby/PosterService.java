package data.driven.cm.business.taskbaby;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * @author  Logan
 * 海报处理服务
 */
public interface PosterService {
    /**
     *
     * @param originPosterUrl
     * @param headImgUrl
     * @param qrCodeUrl
     * @param nickName
     * @return 合成后海报在本地路径
     */
     String getCombinedCustomiedPosterFilePath(
            String originPosterUrl,String headImgUrl, String qrCodeUrl, String nickName);

     String getCombinedCustomiedPosterFilePath(Map<String,String> personalInfo);
    /***
     * 将文件按照地址读进来，返回BufferedImage
     *
     * @author:     Logan
     * @date:       2018/11/17 01:27
     * @params:     [imgAddress]   支持以http开头的url和本地文件
     * @return:     java.awt.image.BufferedImage
     **/
     BufferedImage getBufferedImage(String imgAddress);
}
