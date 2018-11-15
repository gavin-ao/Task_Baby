package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.ActivityService;
import data.driven.cm.dao.JDBCBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     *
     * @param wechatAccount 微信账号
     * @param keyWord 关键字
     * @param status 0是关闭，1是开启
     * @return key:[actId, pictureId];
     */
    @Override
    public Map<String, Object> getMacActivitySimpleInfo(String wechatAccount, String keyWord, Integer status) {
        int statusValue = 1;//默认活动开启
        if(status != null){
            statusValue = status;
        }
        String sql =
                "select act_id as actId ,picture_id as pictureId from  mat_activity where status =? and wechat_account=? and act_key_word=?";
        return dao.getMapResult(sql,statusValue,wechatAccount,keyWord);
    }
}
