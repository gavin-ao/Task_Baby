package data.driven.cm.component;

/**
 * @Author: lxl
 * @describe 微信公众号发送信息去重
 * @Date: 2018/11/16 10:50
 * @Version 1.0
 */
public class DuplicateRemovalMessage {
    private String MsgId;

    private String FromUserName;

    private String CreateTime;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((CreateTime == null) ? 0 : CreateTime.hashCode());
        result = prime * result + ((FromUserName == null) ? 0 : FromUserName.hashCode());
        result = prime * result + ((MsgId == null) ? 0 : MsgId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DuplicateRemovalMessage other = (DuplicateRemovalMessage) obj;
        if (CreateTime == null) {
            if (other.CreateTime != null)
                return false;
        } else if (!CreateTime.equals(other.CreateTime))
            return false;
        if (FromUserName == null) {
            if (other.FromUserName != null)
                return false;
        } else if (!FromUserName.equals(other.FromUserName))
            return false;
        if (MsgId == null) {
            if (other.MsgId != null)
                return false;
        } else if (!MsgId.equals(other.MsgId))
            return false;
        return true;
    }


    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DuplicateRemovalMessage [MsgId=");
        builder.append(MsgId);
        builder.append(", FromUserName=");
        builder.append(FromUserName);
        builder.append(", CreateTime=");
        builder.append(CreateTime);
        builder.append("]");
        return builder.toString();
    }
}
