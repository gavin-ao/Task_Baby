package data.driven.cm.business.wechat.impl;

import data.driven.cm.business.wechat.WechatHelpInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.wechat.WechatHelpInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 助力service
 * @author hejinkai
 * @date 2018/7/13
 */
@Service
public class WechatHelpInfoServiceImpl implements WechatHelpInfoService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    @Override
    public String insertHelp(String helpId, String actId, String wechatUserId, String storeId, String appInfoId) {
        Date createAt = new Date();
        String sql = "insert into wechat_help_info(help_id,act_id,wechat_user_id,store_id,app_info_id,create_at) values(?,?,?,?,?,?)";
        jdbcBaseDao.executeUpdate(sql, helpId, actId, wechatUserId, storeId, appInfoId, createAt);
        return helpId;
    }

    @Override
    public WechatHelpInfoEntity getEntityById(String helpId) {
        String sql = "select help_id,act_id,wechat_user_id,store_id,app_info_id,create_at from wechat_help_info where help_id = ?";
        List<WechatHelpInfoEntity> list = jdbcBaseDao.queryList(WechatHelpInfoEntity.class, sql, helpId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public WechatHelpInfoEntity getHelpInfoByActId(String actId, String wechatUserId) {
        String sql = "select help_id,act_id,wechat_user_id,store_id,app_info_id,create_at from wechat_help_info where wechat_user_id = ? and act_id = ?";
        List<WechatHelpInfoEntity> list = jdbcBaseDao.queryList(WechatHelpInfoEntity.class, sql, wechatUserId, actId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
