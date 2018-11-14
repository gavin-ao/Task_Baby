package data.driven.cm.business.reward;

import data.driven.cm.entity.reward.RewardActCommandHelpMappingEntity;

/**
 * 活动助力奖励关联表Service
 * @author lxl
 * @date 2018/11/7
 */

public interface RewardActCommandHelpMappingService {

    /**
     * 通过 mapId 得到活动助力奖励关联表实体
     * @param mapId 活动助力奖励关联表id
     * @return RewardActCommandHelpMappingEntity
     */
    public RewardActCommandHelpMappingEntity getEntityByMapId(String mapId);
}
