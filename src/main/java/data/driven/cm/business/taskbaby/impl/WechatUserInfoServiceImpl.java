package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.WechatUserInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.WechatUserInfoEntity;
import data.driven.cm.util.DateFormatUtil;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

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
     *
     * @param subscribe      是否订阅公众号,1 是 0 否
     * @param openId         微信用户在公众号中唯一的标示
     * @param nickname       用户昵称
     * @param sex            性别,1 男 2 女 0 未知
     * @param country        国家
     * @param province       省份
     * @param language       语言
     * @param headimgurl     头像URL
     * @param unionid        用户在公众号中的唯一id,只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
     * @param remark         备注,公众号对粉丝的备注
     * @param subscribeScene 渠道来源
     * @param wechatAccount  公众号信息表外键原始ID
     * @param subscribeTime  用户关注时间
     * @param city           城市
     * @param qrScene        二维码扫码场景id
     * @param qrSceneStr     二维码扫码场景描述
     * @param actId          活动id，新增用户时插入活动id,当没有的时候，设置为空
     * @return wechatUserId
     */
    @Override
    public String insertWechatUserInfoEntity(Integer subscribe,String openId, String nickname, Integer sex,
                                             String country, String province, String language, String headimgurl,
                                             String unionid, String remark, String subscribeScene, String wechatAccount,
                                             Integer subscribeTime, String city, Integer qrScene, String qrSceneStr,
                                             String actId) {
        Date createUpdateAt = new Date();
        String wechatUserId = getUserInfoById(wechatAccount,openId);
        if(wechatUserId != null){
            String sql = "update wechat_user_info set subscribe = ?,act_id = ? where wechat_user_id = ?";
            dao.executeUpdate(sql, subscribe,actId,wechatUserId);
        }else{
            wechatUserId = UUIDUtil.getUUID();
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
            wechatUserInfoEntity.setActId(actId);

            dao.insert(wechatUserInfoEntity, "wechat_user_info");
        }
        return wechatUserId;
    }

    /**
     *  微信用户取消关注后需要修改用户 是否订阅公众号状态
     * @param wechatAccount 公众号信息表外键原始ID
     * @param openId 微信用户在公众号中唯一的标示
     * @param subscribe 是否订阅公众号,1 是 0 否
     * @return wechatUserId
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
    private String getUserInfoById(String wechatAccount, String openId) {
        String sql = "SELECT wechat_user_id from wechat_user_info where wechat_account = ? and openid = ?";
        Object wechatUserId = dao.getColumn(sql, wechatAccount,openId);
        if(wechatUserId != null){
            return wechatUserId.toString();
        }
        return null;
    }

    /**
     * @description 通过本次活动带来的新粉丝人数
     * @author lxl
     * @date 2018-12-03 15:26
     * @param actId 活动 id
     * @return activityAddNumber 活动拉新人数
     */
    @Override
    public Integer getActivityAddNumber(String actId) {
        String sql = "select count(1) from wechat_user_info where act_id = ?" ;
        Integer activityAddNumber = dao.getCount(sql,actId);
        return activityAddNumber;
    }

    /**
     * @description 参加本次活动活动后取关的人数 subscribe 为 0
     * @author lxl
     * @date 2018-12-03 15:43
     * @param actId 活动id
     * @return activityTakeOffNumber 活动取关人数
     */
    @Override
    public Integer getActivityTakeOffNumber(String actId) {
        String sql = "select count(1) from wechat_user_info where act_id = ? and subscribe = 0";
        Integer activityTakeOffNumber = dao.getCount(sql,actId);
        return activityTakeOffNumber;
    }

    /**
     * @description 净增人数= 拉新人数-取关人数,subscribe 为 1
     * @author lxl
     * @date 2018-12-03 15:49
     * @param actId 活动id
     * @return  activityNetIncreaseNumber 净增人数
     */
    @Override
    public Integer getActivityNetIncreaseNumber(String actId) {
        String sql = "select count(1) from wechat_user_info where act_id = ? and subscribe = 1";
        Integer activityNetIncreaseNumber = dao.getCount(sql,actId);
        return activityNetIncreaseNumber;
    }

    /**
     * @description 今日新拉新人数，开始和结束时间
     * @author lxl
     * @date 2018-12-03 16:00
     * @param wechatAccount 公众号原始 id
     * @return  todayAddActivityNumber 今日拉新人数
     */
    @Override
    public Integer getTodayAddActivityNumber(String wechatAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(1) from wechat_user_info where wechat_account = ? and create_at >= ? and create_at <= ?";
        Integer todayAddActivityNumber = dao.getCount(sql,wechatAccount,sdf.format(DateFormatUtil.getStartTime(0)),
                sdf.format(DateFormatUtil.getEndTime(0)));
        return todayAddActivityNumber;
    }

    /**
     * @description 今日取消关注活动人数 subscribe 为 0 ，开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:03
     * @param wechatAccount 公众号原始 id
     * @return todayActivityTakeOffNumber 取消关注人数
     */
    @Override
    public Integer getTodayActivityTakeOffNumber(String wechatAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(1) from wechat_user_info where wechat_account = ? and subscribe = 0 and create_at >= ? and create_at <= ?";
        Integer todayActivityTakeOffNumber = dao.getCount(sql,wechatAccount,sdf.format(DateFormatUtil.getStartTime(0)),
                sdf.format(DateFormatUtil.getEndTime(0)));
        return todayActivityTakeOffNumber;
    }

    /**
     * @description 今日净增人数= 拉新人数-取关人数,subscribe 为 1,开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:17
     * @param wechatAccount 公众号原始 id
     * @return  todayActivityNetIncreaseNumber 今日净增人数
     */
    @Override
    public Integer getTodayActivityNetIncreaseNumber(String wechatAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(1) from wechat_user_info where wechat_account = ? and subscribe = 1 and create_at >= ? and create_at <= ?";
        Integer todayActivityNetIncreaseNumber = dao.getCount(sql,wechatAccount,sdf.format(DateFormatUtil.getStartTime(0)),
                sdf.format(DateFormatUtil.getEndTime(0)));
        return todayActivityNetIncreaseNumber;
    }

    /**
     * @description 活动累计关注数,活动id and 关注状态为(subscribe) 为 1
     * @author lxl
     * @date 2018-12-03 17:22
     * @param wechatAccount 公众号原始 id
     * @return totalFollowNumber 活动累计关注总数
     */
    @Override
    public Integer getTotalFollowNumber(String wechatAccount) {
        String sql = "select count(1) from wechat_user_info where wechat_account = ? ";
        Integer totalFollowNumber = dao.getCount(sql,wechatAccount);
        return totalFollowNumber;
    }

    /**
     * @description 昨日新拉新人数，昨天开始和结束时间
     * @author lxl
     * @date 2018-12-03 16:00
     * @param wechatAccount 公众号原始 id
     * @return  yesterdayddActivityNumber 今日拉新人数
     */
    @Override
    public Integer getYesterdayAddActivityNumber(String wechatAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(1) from wechat_user_info where wechat_account = ? and create_at >= ? and create_at <= ?";
        Integer yesterdayAddActivityNumber = dao.getCount(sql,wechatAccount,sdf.format(DateFormatUtil.getStartTime(-1)),
                sdf.format(DateFormatUtil.getEndTime(-1)));
        return yesterdayAddActivityNumber;
    }

    /**
     * @description 昨日取消关注活动人数 subscribe 为 0 ，昨天开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:03
     * @param wechatAccount 公众号原始 id
     * @return yesterdayActivityTakeOffNumber 昨日取消关注人数
     */
    @Override
    public Integer getYesterdayActivityTakeOffNumber(String wechatAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(1) from wechat_user_info where wechat_account = ? and subscribe = 0 and create_at >= ? and create_at <= ?";
        Integer yesterdayActivityTakeOffNumber = dao.getCount(sql,wechatAccount,sdf.format(DateFormatUtil.getStartTime(-1)),
                sdf.format(DateFormatUtil.getEndTime(-1)));
        return yesterdayActivityTakeOffNumber;
    }

    /**
     * @description 昨日净增人数= 昨日拉新人数-昨日取关人数,subscribe 为 1,昨日开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:17
     * @param wechatAccount 公众号原始 id
     * @return  yesterdayActivityNetIncreaseNumber 昨日净增人数
     */
    @Override
    public Integer getYesterdayActivityNetIncreaseNumber(String wechatAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(1) from wechat_user_info where wechat_account = ? and subscribe = 1 and create_at >= ? and create_at <= ?";
        Integer yesterdayActivityNetIncreaseNumber = dao.getCount(sql,wechatAccount,sdf.format(DateFormatUtil.getStartTime(-1)),
                sdf.format(DateFormatUtil.getEndTime(-1)));
        return yesterdayActivityNetIncreaseNumber;
    }

    /**
     * @description 昨天活动累计关注数,活动id and 关注状态为(subscribe) 为 1
     * @author lxl
     * @date 2018-12-03 17:22
     * @param wechatAccount 公众号原始 id
     * @return yesterdayTotalFollowNumber 昨天活动累计关注总数
     */
    @Override
    public Integer getYesterdayTotalFollowNumber(String wechatAccount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(1) from wechat_user_info where wechat_account = ? and create_at < ?";
        Integer yesterdaytotalFollowNumber = dao.getCount(sql,wechatAccount,sdf.format(DateFormatUtil.getStartTime(0)));
        return yesterdaytotalFollowNumber;
    }


}
