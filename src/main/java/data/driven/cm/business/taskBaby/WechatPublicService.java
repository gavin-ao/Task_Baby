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
}
