package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.*;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.ActHelpDetailEntity;
import data.driven.cm.entity.taskbaby.ActHelpEntity;
import data.driven.cm.entity.taskbaby.WechatPublicEntity;
import data.driven.cm.util.WeChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: Task_Baby
 * @description: 活动跟踪服务
 * @author: Logan
 * @create: 2018-11-17 17:08
 **/
@Service
public class ActivityTrackerServiceImpl implements ActivityTrackerService {
    private static Logger log = LoggerFactory.getLogger(ActivityTrackerServiceImpl.class);
    @Autowired
    private JDBCBaseDao jdbcBaseDao;
    @Autowired
    private ActivityService activityService;

    /**
     * 跟踪助力数据统计，一共需要多少助力的(require)，已经助力多少了(help)，还剩下(remain)
     * @author:     Logan
     * @date:       2018/11/17 05:10
     * @params:     [helpId,activityId]
     * @return:     java.util.Map<java.lang.String,java.lang.Integer>
    **/
    @Override
    public Map<String, Integer> getHelpCount(String helpId,String activityId) {
        Integer require = activityService.getRequiredHelpCount(activityId);
        String sql="select count(1) from act_help_detail where help_id=? and help_status=1";
        Integer help = jdbcBaseDao.getCount(sql,helpId);
        Map<String,Integer> result = new HashMap<String,Integer>();
        int remain = 0;
        if (require == null){
            log.error("----------活动需要助力数未定义-----------");
            return null;
        }
        result.put(KEY_HELP_REQUIRE,require);
        result.put(KEY_HELP_HELP,help);
        result.put(KEY_HELP_REMAIN,require-help);
       return result;
    }

    @Override
    public ActHelpDetailEntity getTrackDetail(String helpIdDetialId){
        String sql = "select act_help_detail_id,help_id,help_status,help_openid," +
                "fans_status,act_id,create_at,help_openid " +
                "from act_help_detail where act_help_detail_id =?";
        ActHelpDetailEntity detailEntity =
                jdbcBaseDao.executeQuery(ActHelpDetailEntity.class,sql,helpIdDetialId);
        return detailEntity;

    }

    @Override
    public ActHelpEntity getTrack(String helpId){
        String sql = "select help_id,act_id,wechat_account,fans_id,help_at," +
                "help_success_status,help_number from act_help where help_id=?";
        ActHelpEntity actHelpEntity = jdbcBaseDao.executeQuery(ActHelpEntity.class,sql,helpId);
        return actHelpEntity;
    }
    @Override
    public Map<String,Object> getTrackInfo(String helpDetailId, String activityId,String accessToken){
        ActHelpDetailEntity detailEntity = getTrackDetail(helpDetailId);
        if(detailEntity == null){
            return null;
        }
        Map<String,Integer> helpCountMap = getHelpCount(detailEntity.getHelpId(),activityId);
        if(helpCountMap == null){
            return null;
        }

        ActHelpEntity helpEntity = getTrack(detailEntity.getHelpId());
        if(helpEntity == null){
            return null;
        }
        //获取粉丝个人信息存入到userPersonalInfoMap
        Map<String,String> userPersonalInfoMap = WeChatUtil.getUserInfo(detailEntity.getHelpOpenId(), accessToken);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put(KEY_HELP_REQUIRE,helpCountMap.get(KEY_HELP_REQUIRE));
        resultMap.put(KEY_HELP_HELP,helpCountMap.get(KEY_HELP_HELP));
        resultMap.put(KEY_HELP_REMAIN,helpCountMap.get(KEY_HELP_REMAIN));
        resultMap.put(WeChatConstant.KEY_NICKNAME,userPersonalInfoMap.get(WeChatConstant.KEY_NICKNAME));
        resultMap.put(KEY_HELP_FANS_OPENID,helpEntity.getFansId());
        resultMap.put(KEY_HELP_HELP_OPENID,detailEntity.getHelpOpenId());
        resultMap.put(KEY_HELP_HELP_ID,helpEntity.getHelpId());
        log.info(String.format("---------------活动助力统计resultMap：%s--------",resultMap));
        return resultMap;
    }
    @Override
    public void updateActHelpStatus(String helpId,int status){
        String sql = "update act_help set help_success_status=? where help_id=?";
        jdbcBaseDao.executeUpdate(sql,status,helpId);
    }

}
