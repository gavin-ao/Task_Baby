package data.driven.cm.business.taskbaby.impl;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import data.driven.cm.business.taskbaby.WechatUserInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.WechatUserInfoEntity;
import data.driven.cm.util.DateFormatUtil;
import data.driven.cm.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    private static final Logger logger = LoggerFactory.getLogger(WechatUserInfoServiceImpl.class);
    private static final String TODAY = "0";
    private static final String YESTERDAY = "-1";
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
     * @param msgType        消息类型
     * @param event          事件类型
     * @param eventKey       事件key值
     * @param ticket         二维码的ticket
     * @return wechatUserId
     */
    @Override
    public String insertWechatUserInfoEntity(Integer subscribe, String openId, String nickname, Integer sex,
                                             String country, String province, String language, String headimgurl,
                                             String unionid, String remark, String subscribeScene, String wechatAccount,
                                             Integer subscribeTime, String city, Integer qrScene, String qrSceneStr,
                                             String actId, String msgType, String event, String eventKey, String ticket) {
        logger.info("------------进入  用户新增");

        Date createUpdateAt = new Date();
        String wechatUserId = UUIDUtil.getUUID();
        String sql = "insert into wechat_user_info (wechat_user_id,subscribe,nick_name,sex,country,province,language," +
                "headimgurl,union_id,remark,subscribe_scene,wechat_account,subscribe_time,create_at,city,qr_scene," +
                "qr_scene_str,openid,act_id,msg_type,event,event_key,ticket) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?)";
        dao.executeUpdate(sql, wechatUserId, subscribe, nickname, sex, country, province, language, headimgurl, unionid, remark,
                subscribeScene, wechatAccount, subscribeTime, createUpdateAt, city, qrScene, qrSceneStr, openId, actId, msgType,
                event, eventKey, ticket);
        return wechatUserId;
    }

    /**
     * @param openId        微信用户在公众号中唯一的标示
     * @param wechatAccount 公众号信息表外键原始ID
     * @param actId         活动id，新增用户时插入活动id,当没有的时候，设置为空
     * @param msgType       消息类型
     * @param event         事件类型
     * @param eventKey      事件key值
     * @param ticket        二维码的ticket
     * @return wechatUserId
     * @description 用户取消关注时新增信息
     * @author lxl
     * @date 2018-12-04 17:29
     */
    @Override
    public String insertWechatUserInfoEntity(String openId, String wechatAccount, String actId, String msgType, String event, String eventKey, String ticket) {
        Date createUpdateAt = new Date();
        String wechatUserId = UUIDUtil.getUUID();
        String sql = "insert into wechat_user_info (wechat_user_id," +
                "wechat_account,create_at," +
                "openid,act_id,msg_type,event,event_key,ticket) VALUES (?,?,?,?,?,?,?,?,?)";
        dao.executeUpdate(sql, wechatUserId, wechatAccount, createUpdateAt, openId, actId, msgType,
                event, eventKey, ticket);
        return wechatUserId;
    }

    /**
     * @param actId 活动 id
     * @return activityAddNumber 活动拉新人数
     * @description 通过本次活动带来的新粉丝人数
     * @author lxl
     * @date 2018-12-03 15:26
     */
    @Override
    public Integer getActivityAddNumber(String actId) {
        String sql = "select count(distinct act_id,openid) from wechat_user_info where act_id = ? and  event = ? and event_key is not null";
        Integer activityAddNumber = dao.getCount(sql, actId, "subscribe");
        return activityAddNumber;
    }

    /**
     * @param actId 活动id
     * @return activityTakeOffNumber 活动取关人数
     * @description 参加本次活动活动
     * @author lxl
     * @date 2018-12-03 15:43
     */
    @Override
    public Integer getActivityTakeOffNumber(String actId) {
        String sql = "select count(distinct act_id,openid) from wechat_user_info where act_id = ? and  event = ? ";
        Integer activityTakeOffNumber = dao.getCount(sql, actId, "unsubscribe");
        return activityTakeOffNumber;
    }

    /**
     * @param wechatAccount 公众号原始 id
     * @param actId         活动id
     * @param day           当天、昨天、近七天
     * @return todayAddActivityNumber 今日拉新人数
     * @description 今日新拉新人数，开始和结束时间
     * @author lxl
     * @date 2018-12-03 16:00
     */
    @Override
    public Integer getTodayAddActivityNumber(String wechatAccount, String actId, String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(distinct act_id,openid) from wechat_user_info where wechat_account = ? and act_id = ?" +
                " and  event = ? and event_key is not null and create_at >= ? and create_at <= ?";
        Integer todayAddActivityNumber;

        if (day.equals(TODAY) || day.equals(YESTERDAY)) {
            todayAddActivityNumber = dao.getCount(sql, wechatAccount, actId, "subscribe", sdf.format(DateFormatUtil.getStartTime(Integer.valueOf(day))),
                    sdf.format(DateFormatUtil.getEndTime(Integer.valueOf(day))));
        } else {
            todayAddActivityNumber = dao.getCount(sql, wechatAccount, actId, "subscribe", sdf.format(DateFormatUtil.getStartTime(Integer.valueOf(day))),
                    sdf.format(DateFormatUtil.getEndTime(0)));
        }

        return todayAddActivityNumber;
    }

    /**
     * @param wechatAccount 公众号原始 id
     * @param actId         活动id
     * @param day           当天、昨天、近七天
     * @return todayActivityTakeOffNumber 取消关注人数
     * @description 今日取消关注活动人数 subscribe 为 0 ，开始和结束时间
     * @author lxl
     * @date 2018-12-03 17:03
     */
    @Override
    public Integer getTodayActivityTakeOffNumber(String wechatAccount, String actId, String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select count(distinct act_id,openid) from wechat_user_info where wechat_account = ? and act_id = ?" +
                " and  event = ? and create_at >= ? and create_at <= ?";
        Integer todayActivityTakeOffNumber;

        if (day.equals(TODAY) || day.equals(YESTERDAY)) {
            todayActivityTakeOffNumber = dao.getCount(sql, wechatAccount, actId, "unsubscribe", sdf.format(DateFormatUtil.getStartTime(Integer.valueOf(day))),
                    sdf.format(DateFormatUtil.getEndTime(Integer.valueOf(day))));
        } else {
            todayActivityTakeOffNumber = dao.getCount(sql, wechatAccount, actId, "unsubscribe", sdf.format(DateFormatUtil.getStartTime(Integer.valueOf(day))),
                    sdf.format(DateFormatUtil.getEndTime(0)));
        }
        return todayActivityTakeOffNumber;
    }

    /**
     * @param wechatAccount 公众号原始 id
     * @return totalFollowNumber 活动累计关注总数
     * @description 活动累计关注数, 活动id and 关注状态为(subscribe) 为 1
     * @author lxl
     * @date 2018-12-03 17:22
     */
    @Override
    public Integer getTotalFollowNumber(String wechatAccount) {
        //select subscribeCount - unsubscribeCount from (select count(distinct act_id,openid) as subscribeCount from wechat_user_info where wechat_account = 'gh_520baaa23160' and  event = "subscribe" and event_key is not null) subscribe,
//        (select count(distinct act_id,openid) as unsubscribeCount from wechat_user_info where wechat_account = 'gh_520baaa23160' and  event = "unsubscribe" ) unsubscribe;
        String sql = "select subscribeCount - unsubscribeCount from (select count(distinct act_id,openid)" +
                " as subscribeCount from wechat_user_info where wechat_account = ? and  " +
                "event = ? and event_key is not null) subscribe,(select count(distinct act_id,openid) " +
                "as unsubscribeCount from wechat_user_info where wechat_account = ? " +
                "and  event = ? ) unsubscribe";
        Integer totalFollowNumber = dao.getCount(sql, wechatAccount, "subscribe", wechatAccount, "unsubscribe");
        return totalFollowNumber;
    }

    /**
     * @description 获取微信用户信息，通过任务id和用户unionid
     * @author lxl
     * @date 2018-12-21 10:14
     * @param actId 任务id
     * @param unionid 用户在公众平台下的唯一id
     * @return
     */
    @Override
    public WechatUserInfoEntity getWechatUserInfoEntityByActIdAndUnionid(String actId, String unionid) {
        String sql = "select wechat_user_id,subscribe,nick_name,sex,country,province,language,headimgurl,union_id," +
                "remark,subscribe_scene,wechat_account,subscribe_time,create_at,city,qr_scene,qr_scene_str,openid," +
                "act_id,msg_type,event,event_key,ticket from wechat_user_info where act_id = ? and union_id = ? limit 1";
        List<WechatUserInfoEntity> wechatUserInfoEntities = dao.queryList(WechatUserInfoEntity.class,sql,actId,unionid);
        if (wechatUserInfoEntities != null && wechatUserInfoEntities.size() > 0){
            return wechatUserInfoEntities.get(0);
        }
        return null;
    }
}
