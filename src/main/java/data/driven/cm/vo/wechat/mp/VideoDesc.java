package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 视频消息内容
 * @author: Logan
 * @create: 2018-11-20 23:44
 **/

public class VideoDesc {
    //媒体文件Id
    private String MediaId;
    //缩略图媒体Id
    private String ThumMediaId;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getThumMediaId() {
        return ThumMediaId;
    }

    public void setThumMediaId(String thumMediaId) {
        ThumMediaId = thumMediaId;
    }
}
