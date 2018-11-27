package data.driven.cm.business.taskbaby;

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
    public String getCombinedCustomiedPosterFilePath(
            String originPosterUrl,String headImgUrl, String qrCodeUrl, String nickName);

    public String getCombinedCustomiedPosterFilePath(Map<String,String> personalInfo);

}
