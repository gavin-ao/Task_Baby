package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.ActivityPrizeMappingEntity;

/**
 * @Author: lxl
 * @describe 活动奖品关联表Service
 * @Date: 2018/11/19 16:23
 * @Version 1.0
 */
public interface ActivityPrizeMappingService {

    /**
     * 得到活动奖品关联表实体，只取一个
     * @param actId 活动Id
     * @return
     */
    public ActivityPrizeMappingEntity getEntityByActId(String actId);

    /**
     * 获取所剩的奖品数
     * @param actId 活动Id
     * @return
     */
    public Integer getRemainderPrize(String actId);
}