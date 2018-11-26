package data.driven.cm.business.taskbaby;

import java.util.Map;

/**
 * @author lxl
 * 图片信息Service
 */
public interface SysPictureService {

    /**
     * 获取图片的简要信息
     * @param picId
     * @return Key：【picId, filePath,realName】
     */
    public Map<String,Object> getPictureSimpleInfo(String picId);

    /**
     *
     * @param picId
     * @return 返回图片的URL
     */
    public String getPictureURL(String picId);
}
