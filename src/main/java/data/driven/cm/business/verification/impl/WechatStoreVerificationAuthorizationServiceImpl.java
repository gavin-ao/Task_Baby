package data.driven.cm.business.verification.impl;

import data.driven.cm.business.verification.WechatStoreVerificationAuthorizationService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.verification.WechatStoreVerificationAuthorizationEntity;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 微信门店核销权限Impl
 * @author lxl
 * @date 2018/11/6
 */
@Service
public class WechatStoreVerificationAuthorizationServiceImpl  implements WechatStoreVerificationAuthorizationService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    /**
     *  新增微信门店核销内容
     * @param userId 用户id
     * @param storeId 门店id
     * @param openId 用户对应小程序在微信中的唯一标识
     * @return 返回 微信门店核销Id
     */
    @Override
    public String insertWechatStoreVerificationAuthorization(String userId, String storeId, String openId) {
        Date createUpdateAt = new Date();
        String wsvaId = getEntityBystoreId(storeId);
        if(wsvaId != null){
            String sql = "update wechat_store_verification_authorization set user_id = ?, open_id = ?, store_id = ?,update_at = ? where id = ?";
            jdbcBaseDao.executeUpdate(sql, userId, openId,storeId,createUpdateAt,wsvaId);
        }else{
            wsvaId = UUIDUtil.getUUID();
            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = new WechatStoreVerificationAuthorizationEntity();
            wechatStoreVerificationAuthorizationEntity.setId(wsvaId);
            wechatStoreVerificationAuthorizationEntity.setCreateAt(createUpdateAt);
            wechatStoreVerificationAuthorizationEntity.setOpenId(openId);
            wechatStoreVerificationAuthorizationEntity.setUserId(userId);
            wechatStoreVerificationAuthorizationEntity.setStoreId(storeId);
            jdbcBaseDao.insert(wechatStoreVerificationAuthorizationEntity, "wechat_store_verification_authorization");
        }
        return wsvaId;
    }

    /**
     *  通过openId 查询 微信门店核销内容是否存在
     * @param openId 用户对应小程序在微信中的唯一标识
     * @return 返回 微信微信门店核销对象
     */
    @Override
    public WechatStoreVerificationAuthorizationEntity getEntityByOpenId(String openId) {
        String sql = "select id,user_id,open_id,store_id,create_at from wechat_store_verification_authorization w where w.open_id = ?";
        List<WechatStoreVerificationAuthorizationEntity> wechatStoreVerificationAuthorizationEntityList = jdbcBaseDao.queryList(WechatStoreVerificationAuthorizationEntity.class,sql,openId);
        if (wechatStoreVerificationAuthorizationEntityList != null && wechatStoreVerificationAuthorizationEntityList.size() > 0){
            return wechatStoreVerificationAuthorizationEntityList.get(0);
        }
        return null;
    }

    /**
     * 通过storeID 查询 微信门店核销内容是否存在 ，用来判断当前店是否已经有微信用户核销的权限
     * @param storeID 门店ID
     * @return 返回 微信门店核销对象的id
     */
    public String getEntityBystoreId(String storeID) {
        String sql = "select id from wechat_store_verification_authorization w where w.store_id = ?";
        Object id = jdbcBaseDao.getColumn(sql, storeID);
        if(id != null){
            return id.toString();
        }
        return null;
//        List<WechatStoreVerificationAuthorizationEntity> wechatStoreVerificationAuthorizationEntityList = jdbcBaseDao.queryList(WechatStoreVerificationAuthorizationEntity.class,sql,storeID);
//        if (wechatStoreVerificationAuthorizationEntityList != null && wechatStoreVerificationAuthorizationEntityList.size() > 0){
//            return wechatStoreVerificationAuthorizationEntityList.get(0);
//        }
//        return null;
    }
}
