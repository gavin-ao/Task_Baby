package data.driven.cm.business.behavioranalysis.impl;

import data.driven.cm.business.behavioranalysis.BehaviorAnalysisHelpCommandService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author hejinkai
 * @date 2018/9/19
 */
@Service
public class BehaviorAnalysisHelpCommandServiceImpl implements BehaviorAnalysisHelpCommandService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    private boolean exits(String helpId, String actId, String storeId, String appInfoId, String wechatUserId){
        String sql = "select id from behavior_analysis_help_command where help_id = ? and act_id = ? and store_id = ? and app_info_id = ? and wechat_user_id = ? ";
        Object objId = jdbcBaseDao.getColumn(sql, helpId, actId, storeId, appInfoId, wechatUserId);
        return (objId != null);
    }

    @Override
    public boolean openWindowInsert(String helpId, String actId, String storeId, String appInfoId, String wechatUserId) {
        if(!exits(helpId, actId, storeId, appInfoId, wechatUserId)){
            Date now = new Date();
            String sql = "insert into behavior_analysis_help_command(id, help_id, act_id, store_id, app_info_id, wechat_user_id, open_window, click_url, create_at) values(?,?,?,?,?,?,1,0,?)";
            jdbcBaseDao.executeUpdate(sql, UUIDUtil.getUUID(), helpId, actId, storeId, appInfoId, wechatUserId, now);
        }
        return true;
    }

    @Override
    public boolean updateClickUrl(String id) {
        if(!exitsId(id)){
            String sql = "update behavior_analysis_help_command set click_url = 1 where id = ?";
            jdbcBaseDao.executeUpdate(sql, id);
        }
        return true;
    }

    @Override
    public boolean exitsId(String id) {
        String sql = "select id from behavior_analysis_help_command where id = ? and click_url = 0";
        Object objId = jdbcBaseDao.getColumn(sql, id);
        return (objId != null);
    }
}
