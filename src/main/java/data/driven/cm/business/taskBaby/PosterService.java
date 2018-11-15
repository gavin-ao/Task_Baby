package data.driven.cm.business.taskBaby;

import java.util.Map;

/**
 * 海报处理服务
 */
public interface PosterService {
    /**
     * 个性化海报合成
     * @param OriginlPosterUrl 客户的原始海报url
     * @param headImgUrl  粉丝头像url
     * @param QRCodeUrl  带参数的二维码url
     * @param nickName
     * @return  合成后海报的url
     */
    public String getCombinedCustomizedPosterUrl(
            String OriginlPosterUrl,String headImgUrl, String QRCodeUrl, String nickName);

    /**
     *
     * @param OriginlPosterUrl
     * @param headImgUrl
     * @param qrCodeUrl
     * @param nickName
     * @return 合成后海报在本地路径
     */
    public String getCombinedCustomiedPosterFilePath(
            String OriginlPosterUrl,String headImgUrl, String qrCodeUrl, String nickName);

    public String getCombinedCustomiedPosterFilePath(Map<String,String> personalInfo);

}
