package data.driven.cm.entity.taskBaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 公众号信息表Entity
 * @Date: 2018/11/15 11:13
 * @Version 1.0
 */
public class WechatPublicEntity {


    /**
     * 主键
     */
    private String wechatPublicId;

    /**
     * 开发者ID
     */
    private String authorizationAppid;

    /**
     * 授权给开发者的权限集列表,微信公众号给的是数组的，现改为用逗号存储
     */
    private String funcInfo;
    /**
     * 创建时间
     */
    private Date createAt;

    public String getWechatPublicId() {
        return wechatPublicId;
    }

    public void setWechatPublicId(String wechatPublicId) {
        this.wechatPublicId = wechatPublicId;
    }

    public String getAuthorizationAppid() {
        return authorizationAppid;
    }

    public void setAuthorizationAppid(String authorizationAppid) {
        this.authorizationAppid = authorizationAppid;
    }

    public String getFuncInfo() {
        return funcInfo;
    }

    public void setFuncInfo(String funcInfo) {
        this.funcInfo = funcInfo;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
