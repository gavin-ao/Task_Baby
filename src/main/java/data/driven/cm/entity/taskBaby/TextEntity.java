package data.driven.cm.entity.taskBaby;

/**
 * @Author: lxl
 * @describe 文本实体类
 * @Date: 2018/11/12 16:39
 * @Version 1.0
 */
//@XmlRootElement(name = "test")
public class TextEntity {

    /**
     * 开发者微信号
     */
    private String toUserName;

    /**
     * 发送方帐号（一个OpenID）
     */
    private String fromUserName;

    /**
     * 消息创建时间 （整型）
     */
    private Long createTime;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 文本消息内容
     */
    private String content;

    /**
     * 消息id，64位整型
     */
    private String msgId;



    public TextEntity(){}

    public TextEntity(String toUserName, String fromUserName, Long createTime, String msgType, String content, String msgId){
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.createTime = createTime;
        this.msgType = msgType;
        this.content = content;
        this.msgId = msgId;
    }

//    @XmlElement(name = "ToUserName")
    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

//    @XmlElement(name = "FromUserName")
    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

//    @XmlElement(name = "CreateTime")
    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

//    @XmlElement(name = "MsgType")
    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

//    @XmlElement(name = "Content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //    @XmlElement(name = "MsgId")
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

}
