package data.driven.cm.business.behavioranalysis.impl;

import data.driven.cm.business.behavioranalysis.BehaviorAnalysisHelpOpenUrlService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 行为分析_助力打开奖励窗口埋点service
 * @author hejinkai
 * @date 2018/9/26
 */
@Service
public class BehaviorAnalysisHelpOpenUrlServiceImpl implements BehaviorAnalysisHelpOpenUrlService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;
    @Override
    public boolean insert(String storeId, String appInfoId, String actId, String kfOpenId) {
        if(!exists(storeId, appInfoId, actId, kfOpenId)){
            String sql = "insert into behavior_analysis_help_open_url(id,store_id,app_info_id,act_id,kf_open_id,create_at) values(?,?,?,?,?,?)";
            jdbcBaseDao.executeUpdate(sql, UUIDUtil.getUUID(), storeId, appInfoId, actId, kfOpenId, new Date());
        }
        return true;
    }

    /**
     * 判断是否存在相同的数据
     * @param storeId
     * @param appInfoId
     * @param actId
     * @param kfOpenId
     * @return true-存在， false-不存在
     */
    private boolean exists(String storeId, String appInfoId, String actId, String kfOpenId){
        String sql = "select id from behavior_analysis_help_open_url where store_id = ? and app_info_id=? and act_id = ? and kf_open_id = ?";
        Object id = jdbcBaseDao.getColumn(sql, storeId, appInfoId, actId, kfOpenId);
        return id != null;
    }

}
