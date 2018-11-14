package data.driven.cm.entity.user;

import java.util.Date;

/**
 * 系统用户信息
 * @author hejinkai
 * @date 2018/7/1
 */
public class UserInfoEntity {
    /** 主键 **/
    private String userId;
    /** 用户名 **/
    private String userName;
    /** 密码 **/
    private String pwd;
    /** 昵称 **/
    private String nickName;
    /** 真实姓名 **/
    private String realName;
    /** 邮箱地址 **/
    private String email;
    /** 手机号码 **/
    private String mobilePhone;
    /** 微信号 **/
    private String wechatNumber;
    /** 性别 1 - 男，2 - 女， 3 - 未知 **/
    private Integer sex;
    /** 公司名称 **/
    private String companyName;
    /** 职位 **/
    private String job;
    /** 创建时间 **/
    private Date createAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getWechatNumber() {
        return wechatNumber;
    }

    public void setWechatNumber(String wechatNumber) {
        this.wechatNumber = wechatNumber;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
