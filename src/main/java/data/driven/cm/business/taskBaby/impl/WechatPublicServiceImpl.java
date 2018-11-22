package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.WechatPublicService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskBaby.WechatPublicEntity;
import data.driven.cm.entity.verification.WechatStoreVerificationAuthorizationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lxl
 * @describe 微信公众号Impl
 * @Date: 2018/11/15 15:34
 * @Version 1.0
 */
@Service
public class WechatPublicServiceImpl implements WechatPublicService{

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    /**
     * 通过 authorizationAppid(开发者ID)得到微信公众号信息表实体类
     * @param authorizationAppid
     * @return
     */
    @Override
    public WechatPublicEntity getEntityByAuthorizationAppid(String authorizationAppid) {
        String sql = "select wechat_public_id,authorization_appid,func_info,create_at from wechat_public where authorization_appid = ?";
        List<WechatPublicEntity> wechatPublicEntityList = jdbcBaseDao.queryList(WechatPublicEntity.class,sql,
                authorizationAppid);
        if (wechatPublicEntityList != null && wechatPublicEntityList.size() > 0) {
            return wechatPublicEntityList.get(0);
        }
        return null;
    }

    /**
     * 通过 wechat_public_id(微信公众号信息表id)得到微信公众号信息表实体类
     * @param wechatPublicId 微信公众号信息表id
     * @return WechatPublicEntity
     */
    @Override
    public WechatPublicEntity getEntityByWechatPublicId(String wechatPublicId) {
        String sql = "select wechat_public_id,authorization_appid,func_info,create_at from wechat_public where wechat_public_id = ?";
        List<WechatPublicEntity> wechatPublicEntityList = jdbcBaseDao.queryList(WechatPublicEntity.class,sql,wechatPublicId);
        if (wechatPublicEntityList != null && wechatPublicEntityList.size() > 0){
            return wechatPublicEntityList.get(0);
        }
        return null;
    }


    /**
     * 通过 wechatAccount(原始Id)得到微信公众号实体类
     * @param wechatAccount wechatAccount(原始Id)
     * @return WechatPublicEntity 实体
     */
    @Override
    public WechatPublicEntity getEntityByWechatAccount(String wechatAccount) {
        String sql = "select id,wechat_public_name,appid,secret,wechat_account,create_at,token from wechat_public where wechat_account = ? ";
        List<WechatPublicEntity> wechatPublicEntityList = jdbcBaseDao.queryList(WechatPublicEntity.class,sql,wechatAccount);
        if (wechatPublicEntityList != null && wechatPublicEntityList.size() > 0){
            return wechatPublicEntityList.get(0);
        }
        return null;
    }
}
