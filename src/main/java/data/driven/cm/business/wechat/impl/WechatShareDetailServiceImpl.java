package data.driven.cm.business.wechat.impl;

import data.driven.cm.business.wechat.WechatShareDetailService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.wechat.WechatShareDetailEntity;
import data.driven.cm.entity.wechat.WechatShareInfoEntity;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.vo.wechat.WechatUserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 分享详情Service
 * @author hejinkai
 * @date 2018/7/4
 */
@Service
public class WechatShareDetailServiceImpl implements WechatShareDetailService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    @Override
    public String insertShareDetail(WechatUserInfoVO fromUserInfo, WechatUserInfoVO toUserInfo, WechatShareInfoEntity shareInfo) {
        Date nowDate = new Date();
        String shareDetailId = exitsShareDetailId(fromUserInfo.getWechatMapId(), toUserInfo.getWechatMapId(), shareInfo.getShareId());
        if(shareDetailId != null){
            String sql = "update wechat_share_detail set frequency = frequency + 1, last_share_at = ? where share_detail_id = ?";
            jdbcBaseDao.executeUpdate(sql, nowDate, shareDetailId);
        }else{
            shareDetailId = UUIDUtil.getUUID();
            WechatShareDetailEntity shareDetailEntity = new WechatShareDetailEntity();
            shareDetailEntity.setShareDetailId(shareDetailId);
            shareDetailEntity.setShareId(shareInfo.getShareId());
            shareDetailEntity.setStoreId(shareInfo.getStoreId());
            shareDetailEntity.setAppInfoId(shareInfo.getAppInfoId());
            shareDetailEntity.setFormId(fromUserInfo.getWechatMapId());
            shareDetailEntity.setFormWechatUserId(fromUserInfo.getWechatUserId());
            shareDetailEntity.setToId(toUserInfo.getWechatMapId());
            shareDetailEntity.setToWechatUserId(toUserInfo.getWechatUserId());
            shareDetailEntity.setFrequency(1);
            shareDetailEntity.setShareAt(nowDate);
            shareDetailEntity.setLastShareAt(nowDate);
            jdbcBaseDao.insert(shareDetailEntity, "wechat_share_detail");
        }
        return shareDetailId;
    }

    /**
     * 判断当前分享内容是否存在，用于判断是否重复点开
     * @param fromId
     * @param toId
     * @param shareId
     * @return
     */
    private String exitsShareDetailId(String fromId, String toId, String shareId){
        String sql = "select share_detail_id from wechat_share_detail where share_id = ? and form_id = ? and to_id = ? order by frequency desc limit 1";
        Object shareDetailId = jdbcBaseDao.getColumn(sql, shareId, fromId, toId);
        if(shareDetailId != null){
            return shareDetailId.toString();
        }
        return null;
    }

}
