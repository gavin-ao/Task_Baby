package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.ActivityHelpService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.util.UUIDUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lxl
 * @describe 活动助力Impl
 * @Date: 2018/11/16 16:25
 * @Version 1.0
 */
@Service
public class ActivityHelpServiceImpl implements ActivityHelpService {

    @Autowired
    JDBCBaseDao jdbcBaseDao;

    /**
     * 新增活动助力信息
     *
     * @param actId             活动Id
     * @param wechatAccount     公众号原始ID
     * @param fansOpenId        被助力者ID
     * @param helpSuccessStatus 助力成功状态,0 助力未成功 1 助力成功
     * @param helpNumber        助力人数
     * @param subscribeScene    助力渠道
     * @return
     */
    @Override
    public String insertActivityHelpEntity(String actId, String wechatAccount, String fansOpenId, Integer helpSuccessStatus, Integer helpNumber, Integer subscribeScene) {
        Date helpAt = new Date();
        String helpId = UUIDUtil.getUUID();
        String sql = "INSERT INTO act_help (help_id,act_id,wechat_account,fans_id,help_success_status," +
                "help_number,help_at,subscribe_scene) VALUES (?,?,?,?,?,?,?,?)";
        jdbcBaseDao.executeUpdate(sql, helpId, actId, wechatAccount, fansOpenId, helpSuccessStatus, helpNumber, helpAt, subscribeScene);
        return helpId;
    }

    /**
     * 判断当前粉丝是不是已经参加了活动发起
     *
     * @author: Logan
     * @date: 2018/11/17 03:31
     * @params: [openId, activityId]
     * @return: boolean 如果参加了返回true，没参加返回false
     **/
    @Override
    public boolean checkFansInActivity(String openId, String activityId) {
        String sql = "select count(1) from act_help where act_id=? and fans_id =?";
        Integer count = jdbcBaseDao.getCount(sql, activityId, openId);
        return count > 0;
    }

    /**
     * 根据被助力者的openid和activityId，返回HelpId
     *
     * @author: Logan
     * @date: 2018/11/17 03:55
     * @params: [helpOpenId, activityId]
     * @return: 助力活动主表id
     **/
    @Override
    public String getHelpId(String helpOpenId, String activityId) {
        String sql = "select help_id from act_help where act_id=? and fans_id=?";
        Object helpId = jdbcBaseDao.getColumn(sql, activityId, helpOpenId);
        if (helpId != null) {
            return helpId.toString();
        } else {
            return null;
        }
    }

    /**
     * @param openId 用户id
     * @return act_id 活动id
     * @description 得到用户的活动id, 并且活动状态为 1
     * @author lxl
     * @date 2018-12-04 15:51
     */
    @Override
    public String getActIdByOpenId(String openId) {
        String sql = "select mat.act_id from act_help act, mat_activity mat where  act.fans_id= ? and " +
                "act.act_id = mat.act_id and mat.status = 1  ORDER BY mat.create_at DESC limit 1";
        Object actId = jdbcBaseDao.getColumn(sql, openId);
        if (actId != null) {
            return actId.toString();
        }
        return null;
    }


    /**
     * @param fansId 用户OpenID
     * @return
     * @description 当A用户完成任务后修改助力状态
     * @author lxl
     * @date 2018-12-03 09:50
     */
    @Override
    public void updateHelpSuccessStatus(String fansId) {
        String sql = "UPDATE act_help set help_success_status = 1 where fans_id = ?";
        jdbcBaseDao.executeUpdate(sql, fansId);
    }

    /**
     * @description 通过活动id得到所有“活动参加人数(activityTotalNumber)”、“任务推广人数(activityPromotionNumber)”
     * 、“任务完成人数(activityCompletionNumber)”、“任务完成率(activityCompletionRate)”
     * @author lxl
     * @date 2018-12-05 17:31
     * @param actId 活动id
     * @return activityMap
     */
    @Override
    public Map<String, Object> getActivityData(String actId) {
        String sql = "select act_id,activityTotalNumber,activityPromotionNumber,activityCompletionNumber," +
                "activityCompletionRate from activity_data where act_id = ?";
        List<Map<String, Object>> activityDatas = jdbcBaseDao.queryMapList(sql, actId);
        Map<String, Object> activityDataMap =  new HashMap<>();
        if (activityDatas != null && activityDatas.size() > 0) {
            activityDataMap = activityDatas.get(0);
        }else{
            activityDataMap.put("activityTotalNumber","0");
            activityDataMap.put("activityPromotionNumber","0");
            activityDataMap.put("activityCompletionNumber","0");
            activityDataMap.put("activityCompletionRate","0.00%");
        }
        return activityDataMap;
    }

    /**
     * @param actId 活动 Id
     * @return activityTotalNumber 活动参加人数
     * @description 通过活动id得到所有参加活动的总人数
     * @author lxl
     * @date 2018-12-03 15:02
     */
    @Override
    public Integer getActivityTotalNumber(String actId) {
        String sql = "select count(1) from act_help where act_id = ?";
        Integer activityTotalNumber = jdbcBaseDao.getCount(sql, actId);

        return activityTotalNumber;
    }

    /**
     * @param actId 活动 Id
     * @return activityPromotionNumber 任务推广人数
     * @description 通过活动关键词主动参加活动的人，非传播带来的人,subscribe_scene 为 0
     * @author lxl
     * @date 2018-12-03 15:07
     */
    @Override
    public Integer getActivityPromotionNumber(String actId) {
        String sql = "select count(1) from act_help where act_id = ? and subscribe_scene = 0 ";
        Integer activityPromotionNumber = jdbcBaseDao.getCount(sql, actId);
        return activityPromotionNumber;
    }

    /**
     * @param actId 活动id
     * @return activityCompletionNumber 任务完成人数
     * @description 达到活动助力门槛人数
     * @author lxl
     * @date 2018-12-03 15:14
     */
    @Override
    public Integer getActivityCompletionNumber(String actId) {
        String sql = "select count(1) from act_help where act_id = ? and help_success_status = 1";
        Integer activityCompletionNumber = jdbcBaseDao.getCount(sql, actId);
        return activityCompletionNumber;
    }
}
