package data.driven.cm.business.taskbaby;

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
    public String insertActivityHelpEntity(String actId,String wechatAccount,String fansOpenId,Integer helpSuccessStatus,Integer helpNumber,Integer subscribeScene);

    /**
     * 得到还差多少人，已经有多少人领取
     * @param fansId 被助力者Id
     * @param actId 活动Id
     * @return map 1.surplushHelpNumber 还需多少人 2.endTotal 已经有xx人领取 都是Integer
     */
    public Map<String,Integer> getTotalNumber(String fansId,String actId);

    /**
     * 通过任务Id得到当前完成的人数
     * @param actId 任务ID
     * @return 返回完成人数
     */
    public Integer getEndHelpCount(String actId);


    /**
     * 判断当前粉丝是不是已经参加了活动发起
     * @author:     Logan
     * @date:       2018/11/17 03:31
     * @params:     [openId, activityId]
     * @return:     boolean 如果参加了返回true，没参加返回false
     **/
    public boolean checkFansInActivity(String openId,String activityId);

    /**
     * 根据被助力者的openid和activityId，返回HelpId
     * @author:     Logan
     * @date:       2018/11/17 03:55
     * @params:     [helpOpenId, activityId]
     * @return:     助力活动主表id
     **/
    public String getHelpId(String helpOpenId,String activityId);

    /**
     * @description 当A用户完成任务后修改助力状态
     * @author lxl
     * @date 2018-12-03 09:50
     * @param fansId 用户OpenID
     * @return
     */
    void updateHelpSuccessStatus(String fansId);









































}
