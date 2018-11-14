package data.driven.cm.business.user;

import data.driven.cm.entity.user.UserInfoEntity;

/**
 * 系统用户service
 * @author hejinkai
 * @date 2018/7/1
 */

public interface UserInfoService {

    /**
     * 根据用户和密码获取用户信息
     * @param userName
     * @param pwd
     * @return
     */
    public UserInfoEntity getUser(String userName, String pwd);

//    /**
//     * 插入系统用户与微信用户的关系
//     * @param userId 用户id
//     * @param wechatUserId 微信用户id
//     * @return
//     */
//    public String insertSysUserWechatMapping(String userId,String wechatUserId);

}
