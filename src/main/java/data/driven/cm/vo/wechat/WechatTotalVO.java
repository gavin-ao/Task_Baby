package data.driven.cm.vo.wechat;

/**
 * 用于微信统计
 * @author hejinkai
 * @date 2018/7/5
 */
public class WechatTotalVO {
    /** 合计 **/
    private Long countNum;
    /** 时间 **/
    private String groupTime;

    public WechatTotalVO(Long countNum, String groupTime) {
        this.countNum = countNum;
        this.groupTime = groupTime;
    }
    public WechatTotalVO() {
    }
    public Long getCountNum() {
        return countNum;
    }
    public void setCountNum(Long countNum) {
        this.countNum = countNum;
    }
    public String getGroupTime() {
        return groupTime;
    }
    public void setGroupTime(String groupTime) {
        this.groupTime = groupTime;
    }
}
