package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.WechatUserInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskBaby.WechatUserInfoEntity;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @program: Task_Baby
 * @description: 从数据库里面获取微信粉丝的服务
 * @author: Logan
 * @create: 2018-11-15 11:49
 **/
@Service
public class WechatUserInfoServiceImpl implements WechatUserInfoService {
    @Autowired
    private JDBCBaseDao dao;

    /**
     * 新增微信用户信息
     * @param subscribe 是否订阅公众号,1 是 0 否
     * @param nickname 用户昵称
     * @param sex 性别,1 男 2 女 0 未知
     * @param country 国家
     * @param province 省份
     * @param language 语言
     * @param headimgurl 头像URL
     * @param unionid 用户在公众号中的唯一id,只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
     * @param remark 备注,公众号对粉丝的备注
     * @param subscribeScene 渠道来源
     * @param wechatAccount 公众号信息表外键原始ID
     * @param subscribeTime 用户关注时间
     * @param city 城市
     * @param qrScene 二维码扫码场景id
     * @param qrSceneStr 二维码扫码场景描述
     * @param openId 微信用户在公众号中唯一的标示
     * @return
     */
    @Override
    public String insertWechatUserInfoEntity(Integer subscribe,String openId, String nickname, Integer sex, String country, String province, String language, String headimgurl, String unionid, String remark, String subscribeScene, String wechatAccount, Integer subscribeTime, String city, Integer qrScene, String qrSceneStr) {
        Date createUpdateAt = new Date();
        String wechatUserId = getUserInfoById(wechatAccount,openId);
        if(wechatUserId != null){
            String sql = "update wechat_user_info set subscribe = ? where wechat_user_id = ?";
            dao.executeUpdate(sql, subscribe,wechatUserId);
        }else{
            wechatUserId = UUIDUtil.getUUID();
//            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = new WechatStoreVerificationAuthorizationEntity();
            WechatUserInfoEntity wechatUserInfoEntity = new WechatUserInfoEntity();
            wechatUserInfoEntity.setWechatUserId(wechatUserId);
            wechatUserInfoEntity.setSubscribe(subscribe);
            wechatUserInfoEntity.setNickname(nickname);
            wechatUserInfoEntity.setSex(sex);
            wechatUserInfoEntity.setCountry(country);
            wechatUserInfoEntity.setProvince(province);
            wechatUserInfoEntity.setLanguage(language);
            wechatUserInfoEntity.setHeadimgurl(headimgurl);
            wechatUserInfoEntity.setUnionid(unionid);
            wechatUserInfoEntity.setRemark(remark);
            wechatUserInfoEntity.setSubscribeScene(subscribeScene);
            wechatUserInfoEntity.setWechatAccount(wechatAccount);
            wechatUserInfoEntity.setSubscribeTime(subscribeTime);
            wechatUserInfoEntity.setCreateAt(createUpdateAt);
            wechatUserInfoEntity.setCity(city);
            wechatUserInfoEntity.setQrScene(qrScene);
            wechatUserInfoEntity.setQrSceneStr(qrSceneStr);
            wechatUserInfoEntity.setOpenid(openId);

            dao.insert(wechatUserInfoEntity, "wechat_user_info");
        }
        return wechatUserId;
    }

    /**
     *  微信用户取消关注后需要修改用户 是否订阅公众号状态
     * @param wechatAccount 公众号信息表外键原始ID
     * @param openId 微信用户在公众号中唯一的标示
     * @param subscribe 是否订阅公众号,1 是 0 否
     * @return
     */
    @Override
    public String updateSubscribe(String wechatAccount, String openId, Integer subscribe) {
        String wechatUserId = getUserInfoById(wechatAccount,openId);
        if(wechatUserId != null){
            String sql = "update wechat_user_info set subscribe = ? where wechat_user_id = ?";
            dao.executeUpdate(sql, subscribe,wechatUserId);
        }
        return wechatUserId;
    }

    /**
     * 得到微信用户的 id
     * @param wechatAccount 公众号原始ID
     * @param openId 微信用户在公众号中唯一标示
     * @return wechatUserId 微信用户id
     */
    public String getUserInfoById(String wechatAccount, String openId) {
        String sql = "SELECT wechat_user_id from wechat_user_info where wechat_account = ? and openid = ?";
        Object wechatUserId = dao.getColumn(sql, wechatAccount,openId);
        if(wechatUserId != null){
            return wechatUserId.toString();
        }
        return null;
    }

    /**
     *  得到微信用户实体类
     * @param wechatAccount 公众号原始ID
     * @param openId 微信用户在公众号中唯一标示
     * @return WechatUserInfoEntity 实体类
     */
    @Override
    public WechatUserInfoEntity getWechatUserInfoEntityByAcountOpenId(String wechatAccount, String openId) {
        String sql = "SELECT wechat_user_id,subscribe,nickname,sex,country,province,`language`,headimgurl,unionid,remark,subscribe_scene,wechat_account,create_at,city,qr_scene,qr_scene_str,openid from wechat_user_info where wechat_account = ? and openid = ? ";
        List<WechatUserInfoEntity> WechatUserInfoEntityList = dao.queryList(WechatUserInfoEntity.class,sql,openId);
        if (WechatUserInfoEntityList != null && WechatUserInfoEntityList.size() > 0){
            return WechatUserInfoEntityList.get(0);
        }
        return null;
    }

}
