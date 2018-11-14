package data.driven.cm.vo.wechat;

import java.util.Date;
import java.util.List;

/**
 * 用于微信统计 - 轨迹图
 * @author hejinkai
 * @date 2018/7/18
 */
public class WechatTotalTrajectoryVO {
    private String totalId;
    private String fromUserId;
    private String fromUser;
    private String toUserId;
    private String toUser;
    private Date totalDate;
    private Integer frequency;
    private List<WechatTotalTrajectoryVO> childList;

    public String getTotalId() {
        return totalId;
    }

    public void setTotalId(String totalId) {
        this.totalId = totalId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public Date getTotalDate() {
        return totalDate;
    }

    public void setTotalDate(Date totalDate) {
        this.totalDate = totalDate;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public List<WechatTotalTrajectoryVO> getChildList() {
        return childList;
    }

    public void setChildList(List<WechatTotalTrajectoryVO> childList) {
        this.childList = childList;
    }
}
