package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 客服配置Entity
 * @Date: 2019/1/8 9:51
 * @Version 1.0
 */
public class CustomerConfigureEntity {
    /**
     * 主键
     */
    private String customerConfigureId;

    /**
     * 系统用户id外键
     */
    private String sysUserId;
    /**
     * 消息显示顺序
     */
    private Integer order;

    /**
     * 关键词
     */
    private String keyWord;

    /**
     * 消息描述
     */
    private String describe;

    /**
     * 父关键词id
     */
    private String parentId;

    /**
     * 创建日期
     */
    private Date createAt;

    public String getCustomerConfigureId() {
        return customerConfigureId;
    }

    public void setCustomerConfigureId(String customerConfigureId) {
        this.customerConfigureId = customerConfigureId;
    }

    public String getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(String sysUserId) {
        this.sysUserId = sysUserId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}

















