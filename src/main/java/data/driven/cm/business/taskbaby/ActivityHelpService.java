package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.ActHelpEntity;

import java.util.Map;

/**
 * @Author: lxl
 * @describe 活动助力Service
 * @Date: 2018/11/16 16:25
 * @Version 1.0
 */
public interface ActivityHelpService {

    /**
     * 新增活动助力信息
     * @param actId 活动Id
     * @param wechatAccount 公众号原始ID
     * @param fansOpenId 被助力者ID
     * @param helpSuccessStatus 助力成功状态,0 助力未成功 1 助力成功
     * @param helpNumber 助力人数
     * @param subscribeScene 助力渠道
     * @return
     */
     String insertActivityHelpEntity(String actId,String wechatAccount,String fansOpenId,Integer helpSuccessStatus,Integer helpNumber,Integer subscribeScene);

    /**
     * 判断当前粉丝是不是已经参加了活动发起
     * @author:     Logan
     * @date:       2018/11/17 03:31
     * @params:     [openId, activityId]
     * @return:     boolean 如果参加了返回true，没参加返回false
     **/
     boolean checkFansInActivity(String openId,String activityId);

    /**
     * 根据被助力者的openid和activityId，返回HelpId
     * @author:     Logan
     * @date:       2018/11/17 03:55
     * @params:     [helpOpenId, activityId]
     * @return:     助力活动主表id
     **/
     String getHelpId(String helpOpenId,String activityId);

    /**
     * @description 得到用户的活动id,并且活动状态为 1
     * @author lxl
     * @date 2018-12-04 15:51
     * @param openId 用户id
     * @return act_id 活动id
     */
    String getActIdByOpenId(String openId);

    /**
     * @description 当A用户完成任务后修改助力状态
     * @author lxl
     * @date 2018-12-03 09:50
     * @param fansId 用户OpenID
     * @return
     */
    void updateHelpSuccessStatus(String fansId);


    /**
     * @description 通过活动id得到所有“活动参加人数(activityTotalNumber)”、“任务推广人数(activityPromotionNumber)”
     * 、“任务完成人数(activityCompletionNumber)”、“任务完成率(activityCompletionRate)”
     * @author lxl
     * @date 2018-12-05 17:31
     * @param actId 活动id
     * @return activityMap
     */
    Map<String,Object> getActivityData(String actId);

    /**
     * @description 通过活动id得到所有参加活动的总人数
     * @author lxl
     * @date 2018-12-03 15:02
     * @param actId 活动 Id
     * @return activityTotalNumber 活动参加人数
     */
    Integer getActivityTotalNumber(String actId);

    /**
     * @description 通过活动关键词主动参加活动的人，非传播带来的人,subscribe_scene 为 0
     * @author lxl
     * @date 2018-12-03 15:07
     * @param actId 活动 Id
     * @return activityPromotionNumber 任务推广人数
     */
    Integer getActivityPromotionNumber(String actId);

    /**
     * @description 达到活动助力门槛人数
     * @author lxl
     * @date 2018-12-03 15:14
     * @param actId 活动id
     * @return activityCompletionNumber 任务完成人数
     */
    Integer getActivityCompletionNumber(String actId);

    /**
    * 查找当前活动中,助力者还未成功助力的ActHelpEntity
    * @author Logan
    * @date 2018-12-21 12:26
    * @param actId
    * @param masterOpenId
    * @param detailOpenId

    * @return
    */
    ActHelpEntity getHelpEntityOfNoneHelpDetail(String actId,String masterOpenId,String detailOpenId);









































}
