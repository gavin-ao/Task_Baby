package data.driven.cm.business.wechat;

import data.driven.cm.entity.wechat.WechatShareInfoEntity;

/**
 * 分享Service
 * @author hejinkai
 * @date 2018/6/27
 */
public interface WechatShareInfoService {

    /**
     * 新增分享内容
     * @param shareId
     * @param wechatUserId
     * @param content
     * @param storeId
     * @param appInfoId
     */
    public String insertShare(String shareId, String wechatUserId, String content, String storeId, String appInfoId);

    /**
     * 根据分享id获取分享信息
     * @param shareId
     * @return
     */
    public WechatShareInfoEntity getEntityById(String shareId);
}
