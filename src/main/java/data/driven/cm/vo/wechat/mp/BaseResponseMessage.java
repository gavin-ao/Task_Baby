package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 消息响应基类
 * @author: Logan
 * @create: 2018-11-20 23:31
 **/

public class BaseResponseMessage {
    //接收者账号（一个OpenId）
    private String ToUserName;
    //开发者微信号
    private String FromUserName;
    //消息创建时间
    private long CreateTime;
    //消息类型（text/imge/music/news)
    private String MsgType;

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(long createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }
}
