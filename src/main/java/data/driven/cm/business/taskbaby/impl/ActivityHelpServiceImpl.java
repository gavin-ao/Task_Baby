package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.ActivityHelpService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
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
     * @param actId 活动Id
     * @param wechatAccount 公众号原始ID
     * @param fansOpenId 被助力者ID
     * @param helpSuccessStatus 助力成功状态,0 助力未成功 1 助力成功
     * @param helpNumber 助力人数
     * @return
     */
    @Override
    public String insertActivityHelpEntity(String actId, String wechatAccount, String fansOpenId, Integer helpSuccessStatus, Integer helpNumber) {
        Date helpAt = new Date();
        String helpId = UUIDUtil.getUUID();
        String sql = "INSERT INTO act_help (help_id,act_id,wechat_account,fans_id,help_success_status,help_number,help_at) VALUES (?,?,?,?,?,?,?)";
        jdbcBaseDao.executeUpdate(sql, helpId,actId,wechatAccount,fansOpenId,helpSuccessStatus,helpNumber,helpAt);
        return helpId;
    }

    /**
     * 得到还差多少人，已经有多少人领取
     * @param fansId 被助力者Id
     * @param actId 活动Id
     * @return map 1.surplushHelpNumber 还需多少人 2.endTotal 已经有xx人领取 都是Integer
     */
    @Override
    public Map<String, Integer> getTotalNumber(String fansId, String actId) {
        String surplusHelpSql = "select (m.prize_num-a.help_number) as surplus_help_number from act_help a ,mat_activity m where a.act_id = m.act_id and a.fans_id = ? and a.act_id= ?";
        Integer surplusHelpNumber = jdbcBaseDao.getCount(surplusHelpSql, fansId, actId);

        String endTotalSql = "SELECT count(1) as end_total from act_help where help_success_status = 1 and fans_id = ? and act_id= ?";
        Integer endTotal = jdbcBaseDao.getCount(endTotalSql, fansId, actId);
        Map<String,Integer> map = new HashMap<>();
        map.put("surplusHelpNumber",surplusHelpNumber);
        map.put("endTotal",endTotal);
        return map;
    }

    /**
     * 通过任务Id得到当前完成的人数
     * @param actId 任务ID
     * @return 返回完成人数
     */
    @Override
    public Integer getEndHelpCount(String actId) {
        String endHelpCountSql = "select count(1) from act_help where act_id = ? and help_success_status = 1";
        Integer endHelpCount = jdbcBaseDao.getCount(endHelpCountSql,actId);
        return endHelpCount;
    }
    /**
     * 判断当前粉丝是不是已经参加了活动发起
     * @author:     Logan
     * @date:       2018/11/17 03:31
     * @params:     [openId, activityId]
     * @return:     boolean 如果参加了返回true，没参加返回false
    **/
    @Override
    public boolean checkFansInActivity(String openId,String activityId){
        String sql = "select count(1) from act_help where act_id=? and fans_id =?";
        Integer count = jdbcBaseDao.getCount(sql,activityId,openId);
        return count > 0;
    }

    /**
     * 根据被助力者的openid和activityId，返回HelpId
     * @author:     Logan
     * @date:       2018/11/17 03:55
     * @params:     [helpOpenId, activityId]
     * @return:     助力活动主表id
    **/
    @Override
    public String getHelpId(String helpOpenId,String activityId){
        String sql = "select help_id from act_help where act_id=? and fans_id=?";
        Object helpId = jdbcBaseDao.getColumn(sql,activityId,helpOpenId);
        if(helpId != null){
            return helpId.toString();
        }else{
            return null;
        }
    }
}
