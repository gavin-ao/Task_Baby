package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.ActivityRewardEntity;

/**
 * @Author: lxl
 * @describe 活动奖励表Service
 * @Date: 2018/11/19 9:48
 * @Version 1.0
 */
public interface ActivityRewardService {

    /**
     *  新增 活动奖励信息
     * @param actId 活动ID
     * @param wechatUserId 微信用户Id
     * @param wechatAccount 公众号原始ID
     * @param receiveStatus 领取状态,1 已领取 0 未领取
     * @param prizeId 活动奖品关联表
     * @return
     */
    public String insertActivityRewardEntity(String actId,String wechatUserId,String wechatAccount,Integer receiveStatus,String prizeId);

    /**
     * 获取 活动奖励实体
     * @param actId 活动Id
     * @param wechatUserId 用户OpenId
     * @return ActivityRewardEntity
     */
    public ActivityRewardEntity getEntityByActIdAndWechatUserId(String actId, String wechatUserId);
}
