package data.driven.cm.business.verification;


import data.driven.cm.entity.verification.WechatStoreVerificationAuthorizationEntity;

/**
 * 微信门店核销权限Service
 * @author lxl
 * @date 2018/11/6
 */

public interface WechatStoreVerificationAuthorizationService {

    /**
     *  新增微信门店核销内容
     * @param userId 用户id
     * @param storeId 门店id
     * @param openId 用户对应小程序在微信中的唯一标识
     * @return 返回 微信门店核销Id
     */
    public String insertWechatStoreVerificationAuthorization(String userId,String storeId,String openId);

    /**
     *  通过openId 查询 微信门店核销内容是否存在
     * @param openId 用户对应小程序在微信中的唯一标识
     * @return 返回 微信微信门店核销对象
     */
    public WechatStoreVerificationAuthorizationEntity getEntityByOpenId(String openId);

    /**
     * 通过storeID 查询 微信门店核销内容是否存在 ，用来判断当前店是否已经有微信用户核销的权限
     * @param storeID 门店ID
     * @return 返回 微信门店核销对象
     */
//    public WechatStoreVerificationAuthorizationEntity getEntityBystoreId(String storeID);
}
