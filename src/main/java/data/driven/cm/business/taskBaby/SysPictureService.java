package data.driven.cm.business.taskBaby;

import java.util.Map;

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
