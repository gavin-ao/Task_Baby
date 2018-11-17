package data.driven.cm.entity.wechat;

import java.io.Serializable;

import static data.driven.cm.component.WeChatConstant.VALUE_CSMSG_TYPE_IMG;
import static data.driven.cm.component.WeChatConstant.VALUE_CSMSG_TYPE_TEXT;

/**
 * @program: Task_Baby
 * @description: 客服图片消息体
 * @author: Logan
 * @create: 2018-11-16 14:35
 **/

public class WechatCSImgMsgEntity implements Serializable {
    private String touser;
    private String msgtype;
    private WechatCSImgContentEntity image;
    public WechatCSImgMsgEntity(String touser,String media_id){
        this.touser = touser;
        this.msgtype = VALUE_CSMSG_TYPE_IMG;
        this.image = new WechatCSImgContentEntity(media_id);
    }
    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public WechatCSImgContentEntity getImage() {
        return image;
    }

    public void setImage(WechatCSImgContentEntity image) {
        this.image = image;
    }


}
