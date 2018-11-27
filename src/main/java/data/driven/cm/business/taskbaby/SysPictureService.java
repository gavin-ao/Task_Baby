package data.driven.cm.business.taskbaby;

/**
 * @author lxl
 * 图片信息Service
 */
public interface SysPictureService {

    /**
     * 通过图片id返回图片的URL
     * @author lxl
     * @param picId 图片id,主键
     * @return 返回图片的URL
     */
     String getPictureURL(String picId);
}
