package data.driven.cm.entity.wechat;

import java.util.Date;

/**
 * 微信分享信息表
 * @author hejinkai
 * @date 2018/7/4
 */
public class WechatShareInfoEntity {
    /** 主键 **/
    private String shareId;
    /** 分享者id **/
    private String wechatUserId;
    /** 分享内容 **/
    private String content;
    /** 门店id **/
    private String storeId;
    /** 小程序id **/
    private String appInfoId;
    /** 分享创建时间 **/
    private Date createAt;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getWechatUserId() {
        return wechatUserId;
    }

    public void setWechatUserId(String wechatUserId) {
        this.wechatUserId = wechatUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getAppInfoId() {
        return appInfoId;
    }

    public void setAppInfoId(String appInfoId) {
        this.appInfoId = appInfoId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
