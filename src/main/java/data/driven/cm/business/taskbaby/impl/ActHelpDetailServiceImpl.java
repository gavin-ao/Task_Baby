package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.ActHelpDetailService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 活动助力详细表Impl
 * @Date: 2018/11/16 18:26
 * @Version 1.0
 */
@Service
public class ActHelpDetailServiceImpl implements ActHelpDetailService{
    @Autowired
    JDBCBaseDao jdbcBaseDao;
    /**
     * 新增活动助力信息
     * @param helpId 活动助力表外键
     * @param helpStatus 助力状态
     * @param fansStatus 粉丝状态
     * @param actId 活动id
     * @param helpOpenid 助力者openid
     * @return
     */
    @Override
    public String insertActHelpDetailEntity(String helpId, Integer helpStatus, Integer fansStatus, String actId,String helpOpenid) {
        Date createAt = new Date();
        String actHelpDetailId = getEntityById(helpOpenid,actId);
        if(actHelpDetailId == null){
            actHelpDetailId = UUIDUtil.getUUID();
            String sql = "INSERT INTO act_help_detail (act_help_detail_id,help_id,help_status,fans_status,act_id,create_at,help_openid) VALUES (?,?,?,?,?,?,?)";
            jdbcBaseDao.executeUpdate(sql, actHelpDetailId,helpId,helpStatus,fansStatus,actId,createAt,helpOpenid);
            return actHelpDetailId;
        }
        return null;
    }

    @Override
    public int updateActHelpDetailEntity(String helpDetailId, Integer helpStatus, Integer fansStatus) {
        String sql = "update act_help_detail set help_status=?,fans_status=? where act_help_detail_id =?";
        return jdbcBaseDao.executeUpdate(sql,helpStatus,fansStatus,helpDetailId);

    }

    /**
     * 通过helpOpenId  和 活动id 来得到活动助力详细id
     * @param helpOpenId 助力者OpenId
     * @param actId 活动id
     * @return 返回 活动助力详细id
     */
    public String getEntityById(String helpOpenId,String actId) {
        String sql = "select act_help_detail_id from act_help_detail where act_id = ? and help_openid = ?";
        Object id = jdbcBaseDao.getColumn(sql, actId,helpOpenId);
        if(id != null){
            return id.toString();
        }
        return null;
    }
}
