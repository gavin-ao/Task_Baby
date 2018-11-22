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
     * 通过 authorizationAppid(开发者ID)得到微信公众号信息表实体类
     * @param authorizationAppid 开发者ID
     * @return WechatPublicEntity
     */
    public WechatPublicEntity getEntityByAuthorizationAppid(String authorizationAppid);

    /**
     * 通过 wechat_public_id(微信公众号信息表id)得到微信公众号信息表实体类
     * @param wechatPublicId 微信公众号信息表id
     * @return WechatPublicEntity
     */
    public WechatPublicEntity getEntityByWechatPublicId(String wechatPublicId);

    /**
     * 新增微信公众号信息表
     * @param authorizationAppid  开发者ID
     * @param funcInfo 授权给开发者的权限集列表,微信公众号给的是数组的，现改为用逗号存储
     * @return 微信公众号信息表id
     */
    public String insertWechatPublicEntity(String authorizationAppid,String funcInfo);


    /**
     * 通过 wechatAccount(原始Id)得到微信公众号实体类
     * @param wechatAccount wechatAccount(原始Id)
     * @return WechatPublicEntity 实体
     */
    public WechatPublicEntity getEntityByWechatAccount(String wechatAccount);
}
