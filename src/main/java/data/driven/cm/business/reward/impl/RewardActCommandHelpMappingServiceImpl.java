package data.driven.cm.business.reward.impl;

import data.driven.cm.business.reward.RewardActCommandHelpMappingService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.reward.RewardActCommandHelpMappingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动助力奖励关联表Impl
 * @author lxl
 * @date 2018/11/7
 */
@Service
public class RewardActCommandHelpMappingServiceImpl implements RewardActCommandHelpMappingService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    /**
     * 通过 mapId 得到活动助力奖励关联表实体
     * @param mapId 活动助力奖励关联表id
     * @return RewardActCommandHelpMappingEntity 活动助力奖励关联表实体
     */
    @Override
    public RewardActCommandHelpMappingEntity getEntityByMapId(String mapId) {
        String sql = "select map_id,help_id,command_id,act_id,store_id,app_info_id,wechat_user_id,create_at from reward_act_command_help_mapping where map_id = ?";
        List<RewardActCommandHelpMappingEntity> rewardActCommandHelpMappingEntityList = jdbcBaseDao.queryList(RewardActCommandHelpMappingEntity.class,sql,mapId);
        if (rewardActCommandHelpMappingEntityList != null && rewardActCommandHelpMappingEntityList.size() > 0){
            return rewardActCommandHelpMappingEntityList.get(0);
        }
        return null;
    }
}
