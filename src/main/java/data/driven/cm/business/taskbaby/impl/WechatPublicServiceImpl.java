package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.WechatPublicService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.WechatPublicEntity;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
     * @Author: lxl
     * @param authorizationAppid 开发者ID
     * @return WechatPublicEntity 公众号信息表Entity
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
     * 新增微信公众号信息表
     * @Author: lxl
     * @param authorizationAppid  开发者ID
     * @param funcInfo 授权给开发者的权限集列表,微信公众号给的是数组的，现改为用逗号存储
     * @return 微信公众号信息表id
     */
    @Override
    public String insertWechatPublicEntity(String authorizationAppid, String funcInfo) {
        Date createAt = new Date();
        String wechatPublicId = UUIDUtil.getUUID();
        String sql = "INSERT into wechat_public (wechat_public_id,authorization_appid,func_info,create_at) VALUES (?,?,?,?)";
        jdbcBaseDao.executeUpdate(sql,wechatPublicId,authorizationAppid,funcInfo,createAt);
        return  wechatPublicId;
    }

    /**
     *  更新公众号授权状态
     * @Author: lxl
     * @param authorizationAppid 公众号 appid
     * @param authorizationStatus 授权状态 0 未授权 1 已授权 2 更新授权
     */
    @Override
    public void updateWechatPublicEntity(String authorizationAppid, Integer authorizationStatus) {
        WechatPublicEntity wechatPublicEntity = getEntityByAuthorizationAppid(authorizationAppid);
        if (wechatPublicEntity != null){
            String updateSql = "UPDATE wechat_public set authorization_status = ? where authorization_appid = ?";
            jdbcBaseDao.executeUpdate(updateSql,authorizationStatus,authorizationAppid);
        }
    }
}
