package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.ActivityRewardService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskBaby.ActivityRewardEntity;
import data.driven.cm.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: lxl
 * @describe 活动奖励表Impl
 * @Date: 2018/11/19 9:52
 * @Version 1.0
 */
@Service
public class ActivityRewardServiceImpl implements ActivityRewardService {

    @Autowired
    JDBCBaseDao jdbcBaseDao;

    /**
     *  新增 活动奖励信息
     * @param actId 活动ID
     * @param wechatUserId 微信用户Id openId
     * @param wechatAccount 公众号原始ID
     * @param receiveStatus 领取状态,1 已领取 0 未领取
     * @param prizeId 活动奖品关联表
     * @return
     */
    @Override
    public String insertActivityRewardEntity(String actId, String wechatUserId, String wechatAccount, Integer receiveStatus,String prizeId) {
        Date caretAt = new Date();
        String rewardId = getRewardIdByActIdAndWechatUserId(actId,wechatUserId);
        if (rewardId == null){
            rewardId = UUIDUtil.getUUID();
            String sql = "insert into activity_reward (reward_id,act_id,wechat_user_id,wechat_account,receive_status,create_at,prize_id) VALUES (?,?,?,?,?,?,?)";
            jdbcBaseDao.executeUpdate(sql,rewardId,actId,wechatUserId,wechatAccount,receiveStatus,caretAt,prizeId);
        }
        return rewardId;
    }

    /**
     *  获取活动奖励Id
     * @param actId 任务Id
     * @param wechatUserId 用户OpeinId
     * @return
     */
    public String getRewardIdByActIdAndWechatUserId(String actId,String wechatUserId){
        String sql = "select reward_id from activity_reward where act_id = ? and wechat_user_id = ? ";
        Object id = jdbcBaseDao.getColumn(sql,actId,wechatUserId);
        if (StringUtils.isNotEmpty(id.toString())){
            return id.toString();
        }
        return null;
    }

    /**
     * 获取 活动奖励实体
     * @param actId 活动Id
     * @param wechatUserId 用户OpenId
     * @return ActivityRewardEntity
     */
    @Override
    public ActivityRewardEntity getEntityByActIdAndWechatUserId(String actId,String wechatUserId){
        String sql = "select reward_id,act_id,wechat_user_id,wechat_account,receive_status,create_at,prize_id from activity_reward where act_id = ? and wechat_user_id = ?";
        List<ActivityRewardEntity> activityRewardEntityList = jdbcBaseDao.queryList(ActivityRewardEntity.class,sql,actId,wechatUserId);
        if (activityRewardEntityList != null && activityRewardEntityList.size() > 0){
            return activityRewardEntityList.get(0);
        }
        return null;
    }
}
