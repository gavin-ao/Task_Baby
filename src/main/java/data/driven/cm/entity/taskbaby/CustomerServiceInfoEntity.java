package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 客服信息Entity
 * @Date: 2019/1/8 10:21
 * @Version 1.0
 */
public class CustomerServiceInfoEntity {

    /**
     * 主键
     */
    private String customerServiceInfoId;

    /**
     *  系统用户id外键
     */
    private String sysUserId;

    /**
     * 客服姓名
     */
    private String name;

    /**
     * 部门
     */
    private String department;

    /**
     * 二维码图片地址
     */
    private String qrCodePath;

    /**
     * 素材的mediaId
     */
    private String mediaId;

    /**
     * 创建日期
     */
    private Date createAt;

    public String getCustomerServiceInfoId() {
        return customerServiceInfoId;
    }

    public void setCustomerServiceInfoId(String customerServiceInfoId) {
        this.customerServiceInfoId = customerServiceInfoId;
    }

    public String getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(String sysUserId) {
        this.sysUserId = sysUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}





























