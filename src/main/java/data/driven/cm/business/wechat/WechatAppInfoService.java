package data.driven.cm.business.wechat;

import data.driven.cm.entity.wechat.WechatAppInfoEntity;

import java.util.List;

/**
 * 小程序信息service
 * @author hejinkai
 * @date 2018/6/27
 */
public interface WechatAppInfoService {

    /**
     * 根据appid查询对象
     * @param appid
     * @return
     */
    public WechatAppInfoEntity getAppInfo(String appid);

    /**
     * 根据用户id查询小程序
     * @param userInfoId
     * @return
     */
    public List<WechatAppInfoEntity> findAppInfoListByUser(String userInfoId);
}
