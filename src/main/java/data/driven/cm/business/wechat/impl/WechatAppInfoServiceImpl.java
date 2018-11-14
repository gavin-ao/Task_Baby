package data.driven.cm.business.wechat.impl;

import data.driven.cm.business.wechat.WechatAppInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.wechat.WechatAppInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hejinkai
 * @date 2018/6/28
 */
@Service
public class WechatAppInfoServiceImpl implements WechatAppInfoService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    @Override
    public WechatAppInfoEntity getAppInfo(String appid) {
        String sql = "select app_info_id,app_name,appid,secret,create_at,creator from wechat_app_info where appid = ?";
        List<WechatAppInfoEntity> list = jdbcBaseDao.queryList(WechatAppInfoEntity.class, sql, appid);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<WechatAppInfoEntity> findAppInfoListByUser(String userInfoId) {
        String sql = "select wai.app_info_id,wai.app_name,wai.appid,wai.secret,wai.create_at,wai.creator from wechat_app_info wai" +
                " left join sys_user_wechat_app_mapping suwam on suwam.app_info_id = wai.app_info_id where suwam.user_id = ?";
        List<WechatAppInfoEntity> list = jdbcBaseDao.queryList(WechatAppInfoEntity.class, sql, userInfoId);
        return list;
    }
}
