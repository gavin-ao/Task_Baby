package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.WechatUserInfoEntity;

/**
 * @Author: lxl
 * @describe 微信用户Service
 * @Date: 2018/11/12 16:51
 * @Version 1.0
 */
public interface WechatUserInfoService {

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
     * @param msgType        消息类型
     * @param event          事件类型
     * @param eventKey       事件key值
     * @param ticket         二维码的ticket
     * @return wechatUserId
     */
    String insertWechatUserInfoEntity(Integer subscribe, String openId, String nickname, Integer sex, String country,
                                      String province, String language, String headimgurl,String unionid, String remark,
                                      String subscribeScene, String wechatAccount, Integer subscribeTime, String city,
                                      Integer qrScene, String qrSceneStr,String actId,String msgType,String event,
                                      String eventKey,String ticket);
    /**
     * @description 用户取消关注时新增信息
     * @author lxl
     * @date 2018-12-04 17:29
     * @param openId         微信用户在公众号中唯一的标示
     * @param wechatAccount  公众号信息表外键原始ID
     * @param actId          活动id，新增用户时插入活动id,当没有的时候，设置为空
     * @param msgType        消息类型
     * @param event          事件类型
     * @param eventKey       事件key值
     * @param ticket         二维码的ticket
     * @return wechatUserId
     */
    String insertWechatUserInfoEntity(String openId, String wechatAccount,String actId,String msgType,String event,
                                      String eventKey,String ticket);
    /**
     * @description 通过本次活动带来的新粉丝人数,其实可以从act_help表中求数
     * @author lxl
     * @date 2018-12-03 15:26
     * @param actId 活动 id
     * @return activityAddNumber 活动拉新人数
     */
    Integer getActivityAddNumber(String actId);
    
    /**
     * @description 参加本次活动活动后取关的人数
     * @author lxl
     * @date 2018-12-03 15:43
     * @param actId 活动id
     * @return activityTakeOffNumber 活动取关人数
     */
    Integer getActivityTakeOffNumber(String actId);

    /**
     * @description 今日新拉新人数，开始和结束时间
     * @author lxl
     * @date 2018-12-03 16:00
     * @param wechatAccount 公众号原始 id
     * @param actId 活动id
     * @param day 当天、昨天、近七天
     * @return  todayAddActivityNumber 今日拉新人数
     */
    Integer getTodayAddActivityNumber(String wechatAccount,String actId,String day);

    /**
     * @description 今日取消关注活动人数 subscribe 为 0 ，开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:03
     * @param wechatAccount 公众号原始 id
     * @param actId 活动id
     * @param day 当天、昨天、近七天
     * @return todayActivityTakeOffNumber 取消关注人数
     */
    Integer getTodayActivityTakeOffNumber(String wechatAccount,String actId,String day);

    /**
     * @description 累计关注数,活动id and 关注状态为(subscribe) 为 1
     * @author lxl
     * @date 2018-12-03 17:22
     * @param wechatAccount 公众号原始 id
     * @return totalFollowNumber 累计关注总数
     */
    Integer getTotalFollowNumber(String wechatAccount);

    /**
     * @description 获取微信用户信息，通过任务id和用户unionid
     * @author lxl
     * @date 2018-12-21 10:14
     * @param actId 任务id
     * @param unionid 用户在公众平台下的唯一id
     * @return
     */
    WechatUserInfoEntity getWechatUserInfoEntityByActIdAndUnionid(String actId,String unionid);

    /**
    * 根据微信原始id和粉丝的unionid获得该粉丝的openid
    * @author Logan
    * @date 2018-12-21 15:48
    * @param wechatAccount
    * @param unionId

    * @return 粉丝的openId
    */
    String getOpenId(String wechatAccount,String unionId);
}
