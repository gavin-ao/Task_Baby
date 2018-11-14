package data.driven.cm.business.wechat.impl;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.wechat.WechatHelpDetailService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.wechat.WechatHelpDetailEntity;
import data.driven.cm.entity.wechat.WechatHelpInfoEntity;
import data.driven.cm.util.JSONUtil;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.vo.wechat.WechatHelpDetailUserVO;
import data.driven.cm.vo.wechat.WechatUserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 微信助力详情表
 * @author hejinkai
 * @date 2018/7/13
 */
@Service
public class WechatHelpDetailServiceImpl implements WechatHelpDetailService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    @Override
    public List<WechatHelpDetailEntity> findHelpDetailListByHelpId(String helpId) {
        String sql = "select form_id,form_wechat_user_id,to_id,to_wechat_user_id,help_effect,help_at from wechat_help_detail where help_id = ? and status = 1";
        List<WechatHelpDetailEntity> list = jdbcBaseDao.queryList(WechatHelpDetailEntity.class, sql, helpId);
        return list;
    }

    @Override
    public List<WechatHelpDetailUserVO> findHelpDetailUserListByHelpId(String helpId) {
        String sql = "select whd.help_effect,whd.help_at,u.nick_name,u.gender,u.language,u.city,u.province,u.country,u.avatar_url,u.create_at from wechat_help_detail whd" +
                " left join wechat_user_info u on u.wechat_user_id = whd.to_wechat_user_id where whd.help_id = ? and whd.status = 1";
        List<WechatHelpDetailUserVO> list = jdbcBaseDao.queryList(WechatHelpDetailUserVO.class, sql, helpId);
        return list;
    }

    @Override
    public String getHelpDetailId(String fromId, String toId, String helpId){
        String sql = "select help_detail_id from wechat_help_detail where help_id = ? and form_id = ? and to_id = ? limit 1";
        Object shareDetailId = jdbcBaseDao.getColumn(sql, helpId, fromId, toId);
        if(shareDetailId != null){
            return shareDetailId.toString();
        }
        return null;
    }

    @Override
    public String insertHelpDetail(WechatUserInfoVO fromUserInfo, WechatUserInfoVO toUserInfo, WechatHelpInfoEntity helpInfo) {
        Date nowDate = new Date();
        String helpDetailId = UUIDUtil.getUUID();
        WechatHelpDetailEntity helpDetailEntity = new WechatHelpDetailEntity();
        helpDetailEntity.setHelpDetailId(helpDetailId);
        helpDetailEntity.setHelpId(helpInfo.getHelpId());
        helpDetailEntity.setActId(helpInfo.getActId());
        helpDetailEntity.setStoreId(helpInfo.getStoreId());
        helpDetailEntity.setAppInfoId(helpInfo.getAppInfoId());
        helpDetailEntity.setFormId(fromUserInfo.getWechatMapId());
        helpDetailEntity.setFormWechatUserId(fromUserInfo.getWechatUserId());
        helpDetailEntity.setToId(toUserInfo.getWechatMapId());
        helpDetailEntity.setToWechatUserId(toUserInfo.getWechatUserId());
        helpDetailEntity.setHelpEffect(1);
        helpDetailEntity.setStatus(1);
        helpDetailEntity.setHelpAt(nowDate);
        jdbcBaseDao.insert(helpDetailEntity, "wechat_help_detail");
        return helpDetailId;
    }

    @Override
    public JSONObject doHelp(String fromId, String toId, String helpId) {
        return updateHelpDetailStatus(fromId, toId, helpId, 1);
    }

    @Override
    public JSONObject cancelHelp(String fromId, String toId, String helpId) {
        return updateHelpDetailStatus(fromId, toId, helpId, 0);
    }

    private JSONObject updateHelpDetailStatus(String fromId, String toId, String helpId, Integer status){
        String helpDetailId = getHelpDetailId(fromId, toId, helpId);
        if(helpDetailId == null){
            return JSONUtil.putMsg(false, "101", "助力详情不存在");
        }
        String sql = "update wechat_help_detail set status = ? and help_detail_id = ?";
        jdbcBaseDao.executeUpdate(sql, status, helpDetailId);
        return JSONUtil.putMsg(true, "200", "操作成功");
    }

    @Override
    public WechatHelpDetailEntity getWechatHelpDetailEntityByToUser(String actId, String toWechatUserId) {
        String sql = "select act_id,help_id,store_id,app_info_id,form_id,form_wechat_user_id,to_id,to_wechat_user_id,help_effect,help_at from wechat_help_detail where act_id = ? and to_wechat_user_id = ? and status = 1";
        List<WechatHelpDetailEntity> list = jdbcBaseDao.queryList(WechatHelpDetailEntity.class, sql, actId, toWechatUserId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
