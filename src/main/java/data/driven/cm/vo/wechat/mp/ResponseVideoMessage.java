package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 回复视频消息
 * @author: Logan
 * @create: 2018-11-20 23:45
 **/

public class ResponseVideoMessage extends BaseResponseMessage {
    private VideoDesc Video;

    public VideoDesc getVideo() {
        return Video;
    }

    public void setVideo(VideoDesc video) {
        Video = video;
    }
}
