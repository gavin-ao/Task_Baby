package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.MatActivityEntity;
import data.driven.cm.entity.taskbaby.MatActivityStatusEntity;

import java.util.Map;

/**
 * Activyty数据服务
 */
public interface ActivityService {
       String KEY_ACT_ID = "actId";
       String KEY_PIC_ID = "pictureId";
       String KEY_shareCoypwritting = "shareCopywriting";
    /**
     *
     * @param wechatAccount 微信账号
     * @param keyWord 关键字
     * @param status 0是关闭，1是开启
     * @return 主键
     */
    public String getMatActivityId(String wechatAccount, String keyWord,Integer status);

    /**
     *
     * @param wechatAccount 微信账号
     * @param keyWord 关键字
     * @param status 0是关闭，1是开启
     * @return key:[actId, pictureId,startAt,endAt,status,shareCopywriting];
     */
    public Map<String,Object> getMacActivitySimpleInfo(String wechatAccount, String keyWord,Integer status);

    public Integer countActivedActivity(String wechatAccount);

    /**
     * 通过任务ID得到任务实体
     * @param actId 任务ID
     * @return MatActivityEntity
     */
    public MatActivityEntity getMatActivityEntityByActId(String actId);

    /**
     * 
     * @author:     Logan
     * @date:       2018/11/19 17:24
     * @params:     [actId]   
     * @return:     java.lang.Integer
    **/        
    public Integer getMacActivityType(String actId);
    /**根据活动Id获取活动状态实体
     *
     * @author:     Logan
     * @date:       2018/11/19 10:52
     * @params:     [actId]
     * @return:     data.driven.cm.entity.taskbaby.MatActivityStatusEntity
    **/
    public MatActivityStatusEntity getMacActivityStatusByActId(String actId);

    /**
     * 获取活动要求助力数
     * @author:     Logan
     * @date:       2018/11/17 05:18
     * @params:     [activityId]
     * @return:     java.lang.Integer
     **/
    public Integer getRequiredHelpCount(String activityId);
}
