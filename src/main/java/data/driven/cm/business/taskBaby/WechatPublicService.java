package data.driven.cm.business.taskBaby;

import data.driven.cm.entity.taskBaby.WechatPublicEntity;

/**
 * @Author: lxl
 * @describe 微信公众号 Service层
 * @Date: 2018/11/15 15:01
 * @Version 1.0
 */
public interface WechatPublicService {


    /**
     * 通过 wechatAccount(原始Id)得到微信公众号实体类
     * @param wechatAccount wechatAccount(原始Id)
     * @return WechatPublicEntity 实体
     */
    public WechatPublicEntity getEntityByWechatAccount(String wechatAccount);

    /**
     * 通过 authorizationAppid(开发者ID)得到微信公众号信息表实体类
     * @param authorizationAppid
     * @return WechatPublicEntity
     */
    public WechatPublicEntity getEntityByAuthorizationAppid(String authorizationAppid);

    /**
     * 通过 wechat_public_id(微信公众号信息表id)得到微信公众号信息表实体类
     * @param wechatPublicId 微信公众号信息表id
     * @return WechatPublicEntity
     */
    public WechatPublicEntity getEntityByWechatPublicId(String wechatPublicId);
}
