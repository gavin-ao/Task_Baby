package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.ActivityService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.MatActivityEntity;
import data.driven.cm.entity.taskbaby.MatActivityStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @program: Task_Baby
 * @description: MatActivity数据服务
 * @author: Logan
 * @create: 2018-11-15 18:14
 **/
@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private JDBCBaseDao dao;

    @Override
    public Integer countActivedActivity(String wechatAccount) {
        String sql = "select count(*) as activityCount from mat_activity where wechat_account=? ";
        Object result = dao.getColumn(sql, wechatAccount);
        if (result != null) {
            return Integer.valueOf(result.toString());
        }
        return 0;
    }

    /**
     * 通过任务ID得到任务实体
     *
     * @param actId 任务ID
     * @return MatActivityEntity
     */
    @Override
    public MatActivityEntity getMatActivityEntityByActId(String actId) {
        String sql = "select act_id,wechat_account,picture_id,act_share_copywriting,reward_url,act_type from mat_activity where act_id = ?";
        List<MatActivityEntity> MatActivityEntityList = dao.queryList(MatActivityEntity.class, sql, actId);
        if (MatActivityEntityList != null && MatActivityEntityList.size() > 0) {
            return MatActivityEntityList.get(0);
        }
        return null;
    }
    public Integer getMacActivityType(String actId){
        String sql = "select act_type from mat_activity where act_id=?";
        Object result = dao.getColumn(sql,actId);
        if(result == null){
            return null;
        }else{
            return new Integer(result.toString());
        }
    }
    /**
     * 根据活动id获取活动状态相关字段
     * @author:     Logan
     * @date:       2018/11/19 10:53
     * @params:     [actId]
     * @return:     data.driven.cm.entity.taskbaby.MatActivityStatusEntity
    **/
    @Override
    public MatActivityStatusEntity getMacActivityStatusByActId(String actId) {

    String sql = "select act_id,act_key_word,start_at,end_at,status from mat_activity where act_id = ?";
    List<MatActivityStatusEntity> matActivityStatusList = dao.queryList(MatActivityStatusEntity.class, sql, actId);
        if(matActivityStatusList !=null&&matActivityStatusList.size()>0)

    {
        return matActivityStatusList.get(0);
    }
        return null;
}
    @Override
    public String getMatActivityId(String wechatAccount, String keyWord, Integer status) {
        int statusValue = 1;//默认活动开启
        if(status != null){
           statusValue = status;
        }
        String sql = "select act_id from  mat_activity where status =? and wechat_account=? and act_key_word=?";
       Object actIdObject = dao.getColumn(sql,statusValue,wechatAccount,keyWord);
       if(actIdObject != null){
         return actIdObject.toString();
       }
        return null;
    }
    /**
     * 获取活动要求助力数
     * @author:     Logan
     * @date:       2018/11/17 05:18
     * @params:     [activityId]
     * @return:     java.lang.Integer
    **/
    @Override
    public Integer getRequiredHelpCount(String activityId){
        String sql ="select partake_num from mat_activity where act_id =?";
        Object count = dao.getColumn(sql,activityId);
        if(count != null){
            return Integer.parseInt(count.toString());
        }else{
            return null;
        }
    }

    /**
     *
     * @param wechatAccount 微信账号
     * @param keyWord 关键字
     * @param status 0是关闭，1是开启
     * @return key:[actId, pictureId,startAt,endAt,status,shareCopywriting];
     */
    @Override
    public Map<String, Object> getMacActivitySimpleInfo(String wechatAccount, String keyWord, Integer status) {
        int statusValue = 1;//默认活动开启
        if(status != null){
            statusValue = status;
        }
        String sql =
                "select act_id as actId, picture_id as pictureId, start_at as startAt ," +
                        "end_at as endAt,status,act_share_copywriting as shareCopywriting" +
                        " from  mat_activity where status =? and wechat_account=? and act_key_word=?";
        return dao.getMapResult(sql,statusValue,wechatAccount,keyWord);
    }

}
