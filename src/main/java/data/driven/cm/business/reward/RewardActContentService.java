package data.driven.cm.business.reward;

import data.driven.cm.entity.reward.RewardActContentEntity;

/**
 * 活动奖励文案Service
 * @author hejinkai
 * @date 2018/8/8
 */
public interface RewardActContentService {

    /**
     * 根据活动id和奖励类型获取文案
     * @param actId
     * @param commandType
     * @return
     */
    public RewardActContentEntity getRewardActContentByActAndType(String actId, Integer commandType);

}
