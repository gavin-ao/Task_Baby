package data.driven.cm.business.verification.impl;

import data.driven.cm.business.verification.WechatStoreActVerificationMappingService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.verification.WechatStoreActVerificationMappingEntity;
import data.driven.cm.entity.wechat.WechatHelpDetailEntity;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 核销关联表Impl
 * @author lxl
 * @date 2018/11/8
 */
@Service
public class WechatStoreActVerificationMappingServiceImpl implements WechatStoreActVerificationMappingService{

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    /**
     *  新增 核销关联表信息
     * @param userId 系统用户id
     * @param storeId 门店 id
     * @param actId 活动 id
     * @param commandId 奖励口令id
     * @param wechatUserId 系统微信用户唯一 id
     * @param openId 应用小程序在微信中的唯一标识
     * @return
     */
    @Override
    public String insertWechatStoreActVerificationMapping(String userId, String storeId, String actId, String commandId, String wechatUserId, String openId) {
        Date nowDate = new Date();
        String verificationId = UUIDUtil.getUUID();
        WechatStoreActVerificationMappingEntity wechatStoreActVerificationMappingEntity = new WechatStoreActVerificationMappingEntity();
        wechatStoreActVerificationMappingEntity.setVerificationId(verificationId);
        wechatStoreActVerificationMappingEntity.setUserId(userId);
        wechatStoreActVerificationMappingEntity.setStoreId(storeId);
        wechatStoreActVerificationMappingEntity.setActId(actId);
        wechatStoreActVerificationMappingEntity.setCommandId(commandId);
        wechatStoreActVerificationMappingEntity.setWechatUserId(wechatUserId);
        wechatStoreActVerificationMappingEntity.setOpenId(openId);
        wechatStoreActVerificationMappingEntity.setCreateAt(nowDate);
        jdbcBaseDao.insert(wechatStoreActVerificationMappingEntity,"wechat_store_act_verification_mapping");
        return verificationId;
    }
}
