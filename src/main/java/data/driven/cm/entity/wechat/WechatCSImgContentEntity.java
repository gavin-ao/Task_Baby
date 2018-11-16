package data.driven.cm.entity.wechat;

import java.io.Serializable;

/**
 * @program: Task_Baby
 * @description: 微信客服图片消息内容实体
 * @author: Logan
 * @create: 2018-11-16 14:30
 **/

public class WechatCSImgContentEntity implements Serializable {
    private String media_id;
    public WechatCSImgContentEntity(String media_id){
        this.media_id = media_id;
    }
    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }
}
