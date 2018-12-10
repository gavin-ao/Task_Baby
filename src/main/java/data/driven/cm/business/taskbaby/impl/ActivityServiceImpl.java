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


    /**
     * 通过任务ID得到任务实体
     *
     * @param actId 任务ID
     * @return MatActivityEntity
     */
    @Override
    public MatActivityEntity getMatActivityEntityByActId(String actId) {
        String sql = "select act_id,wechat_account,picture_id,act_share_copywriting,reward_url,act_type from mat_activity where act_id = ?";
        List<MatActivityEntity> matActivityEntityList = dao.queryList(MatActivityEntity.class, sql, actId);
        if (matActivityEntityList != null && matActivityEntityList.size() > 0) {
            return matActivityEntityList.get(0);
        }
        return null;
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
    public String getMatActivityId(String wechatAccount, String keyWord) {

        String sql = "select act_id from  mat_activity where status =1 and wechat_account=? and act_key_word=?";
       Object actIdObject = dao.getColumn(sql,wechatAccount,keyWord);
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
     * @return key:[actId, pictureId,startAt,endAt,status,shareCopywriting];
     */
    @Override
    public Map<String, Object> getMacActivitySimpleInfo(String wechatAccount, String keyWord) {
        String sql =
                "select act_id as actId, picture_id as pictureId, start_at as startAt ," +
                        "end_at as endAt,status,act_share_copywriting as shareCopywriting" +
                        " from  mat_activity where status =1 and wechat_account=? and act_key_word=?";
        return dao.getMapResult(sql,wechatAccount,keyWord);
    }


    /**
     * @description 查询当前微信号是否有当前的关键字（不管是否生效）
     * @author Logan
     * @date 2018-11-28 10:20
     * @param content 用户输入的稳步消息
     * @param wechatAccount 微信公众号原始id

     * @return 是否存在关键字活动
     */
    @Override
    public Boolean keyWordExist(String content, String wechatAccount) {
       String sql = "select count(1) from mat_activity where wechat_account=? and act_key_word=?";
       return dao.getCount(sql,wechatAccount,content)>0;
    }
    /**
    * @description 根据activityId，判断该活动老粉丝是否能助力
    * @author Logan
    * @date 2018-11-30 10:04
    * @param activityId

    * @return
    */
    @Override
    public Boolean oldFansCanHelp(String activityId) {
        String sql = "select old_fans_can_help from mat_activity where act_id=?";
        Object status = dao.getColumn(sql,activityId);
        if(status !=null && Integer.parseInt(status.toString())==1){
            return true;
        }else{
            return false;
        }
    }
}
