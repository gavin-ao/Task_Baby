package data.driven.cm.business.taskbaby;

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
     * @return wechatUserId
     */
    String insertWechatUserInfoEntity(Integer subscribe, String openId, String nickname, Integer sex, String country,
                                      String province, String language, String headimgurl,String unionid, String remark,
                                      String subscribeScene, String wechatAccount, Integer subscribeTime, String city,
                                      Integer qrScene, String qrSceneStr,String actId);

    /**
     * 微信用户取消关注后需要修改用户 是否订阅公众号状态
     *
     * @param wechatAccount 公众号信息表外键原始ID
     * @param openId        微信用户在公众号中唯一的标示
     * @param subscribe     是否订阅公众号,1 是 0 否
     * @return wechatUserId
     */
    String updateSubscribe(String wechatAccount, String openId, Integer subscribe);

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
     * @description 净增人数= 拉新人数-取关人数,subscribe 为 1
     * @author lxl
     * @date 2018-12-03 15:49
     * @param actId 活动id
     * @return  activityNetIncreaseNumber 净增人数
     */
    Integer getActivityNetIncreaseNumber(String actId);

    /**
     * @description 今日新拉新人数，开始和结束时间
     * @author lxl
     * @date 2018-12-03 16:00
     * @param wechatAccount 公众号原始 id
     * @return  todayAddActivityNumber 今日拉新人数
     */
    Integer getTodayAddActivityNumber(String wechatAccount);
    
    /**
     * @description 今日取消关注活动人数 subscribe 为 0 ，开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:03
     * @param wechatAccount 公众号原始 id
     * @return todayActivityTakeOffNumber 取消关注人数
     */
    Integer getTodayActivityTakeOffNumber(String wechatAccount);

    /**
     * @description 今日净增人数= 拉新人数-取关人数,subscribe 为 1,开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:17
     * @param wechatAccount 公众号原始 id
     * @return  todayActivityNetIncreaseNumber 今日净增人数
     */
    Integer getTodayActivityNetIncreaseNumber(String wechatAccount);

    /**
     * @description 累计关注数,活动id and 关注状态为(subscribe) 为 1
     * @author lxl
     * @date 2018-12-03 17:22
     * @param wechatAccount 公众号原始 id
     * @return totalFollowNumber 累计关注总数
     */
    Integer getTotalFollowNumber(String wechatAccount);

    /**
     * @description 昨日新拉新人数，昨天开始和结束时间
     * @author lxl
     * @date 2018-12-03 16:00
     * @param wechatAccount 公众号原始 id
     * @return  yesterdayddActivityNumber 今日拉新人数
     */
    Integer getYesterdayAddActivityNumber(String wechatAccount);

    /**
     * @description 昨日取消关注活动人数 subscribe 为 0 ，昨天开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:03
     * @param wechatAccount 公众号原始 id
     * @return yesterdayActivityTakeOffNumber 昨日取消关注人数
     */
    Integer getYesterdayActivityTakeOffNumber(String wechatAccount);

    /**
     * @description 昨日净增人数= 昨日拉新人数-昨日取关人数,subscribe 为 1,昨日开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:17
     * @param wechatAccount 公众号原始 id
     * @return  yesterdayActivityNetIncreaseNumber 昨日净增人数
     */
    Integer getYesterdayActivityNetIncreaseNumber(String wechatAccount);

    /**
     * @description 昨天活动累计关注数,活动id and 关注状态为(subscribe) 为 1
     * @author lxl
     * @date 2018-12-03 17:22
     * @param wechatAccount 公众号原始 id
     * @return yesterdayTotalFollowNumber 昨天活动累计关注总数
     */
    Integer getYesterdayTotalFollowNumber(String wechatAccount);

}
