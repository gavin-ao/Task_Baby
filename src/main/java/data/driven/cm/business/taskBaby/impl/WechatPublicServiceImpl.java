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
