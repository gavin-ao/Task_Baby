package data.driven.cm.entity.wechat;

import java.io.Serializable;

import static data.driven.cm.component.WeChatConstant.KEY_CSMSG_TYPE_TEXT;

/**
 * @program: Task_Baby
 * @description: 微信客服消息实体
 * @author: Logan
 * @create: 2018-11-16 14:33
 **/

public class WechatCSTxtMsgEntity implements Serializable {
    private String touser;
    private String msgtype;
    private WechatCSTxtContentEntity text;
    public WechatCSTxtMsgEntity(String touser,String content){
        this.msgtype = KEY_CSMSG_TYPE_TEXT;
        this.touser = touser;
        this.text = new WechatCSTxtContentEntity(content);
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

    public WechatCSTxtContentEntity getText() {
        return text;
    }

    public void setText(WechatCSTxtContentEntity text) {
        this.text = text;
    }
}
