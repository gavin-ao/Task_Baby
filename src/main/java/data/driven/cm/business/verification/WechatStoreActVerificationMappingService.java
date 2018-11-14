package data.driven.cm.business.verification;

/**
 * 核销关联表Service
 * @author lxl
 * @date 2018/11/8
 */
public interface WechatStoreActVerificationMappingService {

    /**
     *  新增 核销关联表信息
     * @param userId 系统用户id
     * @param storeId 门店 id
     * @param actId 活动 id
     * @param commandId 奖励口令id
     * @param wechatUserId 系统微信用户唯一 id
     * @param openId 应用小程序在微信中的唯一标识
     * @return
     */
    public String insertWechatStoreActVerificationMapping(String userId,String storeId,String actId,String commandId,String wechatUserId,String openId);
}
