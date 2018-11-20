package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 微信消息基类
 *
 * 特别注意：微信相关的消息和响应实体不能遵循Java的命名规则以小写字母开头，，
 * 因为要便于后面实体转XML（以大写字母开头）
 *
 * @author: Logan
 * @create: 2018-11-20 22:29
 **/

public class BaseMessge {
    //开发者微信号
    private String ToUserName;
    //发送方账号(一个OpenId)
    private String FromUserName;
    //消息创建时间
    private long CreateTime;
    //消息类型（text/image/location/link/voice）
    private String MsgType;
    //消息ID，
    private long MsgId;

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

    public long getMsgId() {
        return MsgId;
    }

    public void setMsgId(long msgId) {
        MsgId = msgId;
    }

}
