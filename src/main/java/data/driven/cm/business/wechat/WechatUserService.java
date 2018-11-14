package data.driven.cm.business.wechat;

import data.driven.cm.entity.wechat.WechatUserInfoEntity;
import data.driven.cm.vo.wechat.WechatUserInfoVO;

/**
 * 微信用户信息service
 * @author hejinkai
 * @date 2018/6/27
 */
public interface WechatUserService {

    /**
     * 根据openId 查询用户信息
     * @param openId
     * @return
     */
    public WechatUserInfoVO getUserInfoByOpenId(String openId);

    /**
     * 根据unionId查询用户信息
     * @param unionId
     * @return
     */
    public WechatUserInfoVO getUserInfoByUnionId(String unionId);

    /**
     * 新增用户- 返回userId
     * @param userInfo
     * @return
     */
    public String addUserInfo(WechatUserInfoEntity userInfo);

    /**
     * 新增用户- 返回userId
     * @return
     */
    public String addUserInfo2();

    /**
     * 新增app与用户的关联
     * @param appInfoId
     * @param userInfoId
     * @param openId
     * @return
     */
    public String addUserAndAppMap(String appInfoId, String userInfoId, String openId);

    /**
     * 根据小程序和用户查询用户详情
     * @param userInfoId
     * @param appInfoId
     * @return
     */
    public WechatUserInfoVO getUserInfoByUserIdAndAppInfoId(String userInfoId, String appInfoId);

}
