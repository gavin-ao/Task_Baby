package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 回复图片消息
 * @author: Logan
 * @create: 2018-11-20 23:35
 **/

public class ResponseImageMessage extends  BaseResponseMessage {
    private ImageDesc Image;

    public ImageDesc getImage() {
        return Image;
    }

    public void setImage(ImageDesc image) {
        Image = image;
    }
}
