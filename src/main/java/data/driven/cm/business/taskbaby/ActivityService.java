package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.MatActivityEntity;
import data.driven.cm.entity.taskbaby.MatActivityStatusEntity;

import java.util.Map;

/**
 * @author  lxl
 * Activyty数据服务
 */
public interface ActivityService {
       String KEY_ACT_ID = "actId";
       String KEY_PIC_ID = "pictureId";
       String KEY_SHARECOYPWRITTING = "shareCopywriting";
    /**
     *
     * @param wechatAccount 微信账号
     * @param keyWord 关键字
     * @return 主键
     */
     String getMatActivityId(String wechatAccount, String keyWord);

    /**
     *
     * @param wechatAccount 微信账号
     * @param keyWord 关键字
     * @return key:[actId, pictureId,startAt,endAt,status,shareCopywriting];
     */
     Map<String,Object> getMacActivitySimpleInfo(String wechatAccount, String keyWord);


    /**
     * 通过任务ID得到任务实体
     * @param actId 任务ID
     * @return MatActivityEntity
     */
     MatActivityEntity getMatActivityEntityByActId(String actId);

    /**根据活动Id获取活动状态实体
     *
     * @author:     Logan
     * @date:       2018/11/19 10:52
     * @params:     [actId]
     * @return:     data.driven.cm.entity.taskbaby.MatActivityStatusEntity
    **/
     MatActivityStatusEntity getMacActivityStatusByActId(String actId);

    /**
     * 获取活动要求助力数
     * @author:     Logan
     * @date:       2018/11/17 05:18
     * @params:     [activityId]
     * @return:     java.lang.Integer
     **/
     Integer getRequiredHelpCount(String activityId);
}
