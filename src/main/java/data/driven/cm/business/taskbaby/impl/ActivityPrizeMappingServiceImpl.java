package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.ActivityPrizeMappingService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskBaby.ActivityPrizeMappingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lxl
 * @describe 活动奖品关联表Impl
 * @Date: 2018/11/19 16:23
 * @Version 1.0
 */
@Service
public class ActivityPrizeMappingServiceImpl implements ActivityPrizeMappingService{
    @Autowired
    JDBCBaseDao jdbcBaseDao;

    /**
     * 得到活动奖品关联表实体，只取一个
     * @param actId 活动Id
     * @return
     */
    @Override
    public ActivityPrizeMappingEntity getEntityByActId(String actId) {
        String sql = "select prize_id,act_id,token,link_url,token_status,creator,create_at from activity_prize_mapping where act_id = ? and token_status = 0 limit 1";
        List<ActivityPrizeMappingEntity> activityPrizeMappingEntityList = jdbcBaseDao.queryList(ActivityPrizeMappingEntity.class,sql,actId);
        if (activityPrizeMappingEntityList != null && activityPrizeMappingEntityList.size() > 0){
            ActivityPrizeMappingEntity activityPrizeMappingEntity = activityPrizeMappingEntityList.get(0);
            String updateSql = "update activity_prize_mapping set token_status = 1 where prize_id = ?";
            jdbcBaseDao.executeUpdate(updateSql,activityPrizeMappingEntity.getPrizeId());
            return activityPrizeMappingEntity;
        }
        return null;
    }

    /**
     * 获取所剩的奖品数
     * @param actId 活动Id
     * @return
     */
    @Override
    public Integer getRemainderPrize(String actId) {
        String sql = "select count(1) as remainder_count from activity_prize_mapping where act_id = ? and token_status = 0 ";
        return jdbcBaseDao.getCount(sql,actId);
    }
}
