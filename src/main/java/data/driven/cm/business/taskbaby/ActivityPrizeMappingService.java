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
     ActivityPrizeMappingEntity getEntityByActId(String actId);

    /**
     * 获取所剩的奖品数
     * @param actId 活动Id
     * @return
     */
     Integer getRemainderPrize(String actId);
     /**
     * @description 获取获奖详情
     * @author Logan
     * @date 2018-12-10 16:45
     * @param prizeId

     * @return
     */
    ActivityPrizeMappingEntity getEntityByPrizeId(String prizeId);
}
