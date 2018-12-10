package data.driven.cm.business.taskbaby.impl;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskbaby.*;
import data.driven.cm.component.RewardTypeEnum;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.ActivityPrizeMappingEntity;
import data.driven.cm.entity.taskbaby.ActivityRewardEntity;
import data.driven.cm.entity.taskbaby.MatActivityEntity;
import data.driven.cm.entity.taskbaby.MatActivityStatusEntity;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static data.driven.cm.component.WeChatConstant.*;

/**
 * @program: Task_Baby
 * @description: 活动服务，处理活动服务逻辑
 * @author: Logan
 * @create: 2018-11-14 17:51
 **/
@Service
public class WechatResponseServiceImpl implements WechatResponseService {
    private static final Logger logger = LoggerFactory.getLogger(WechatResponseServiceImpl.class);
    /**
     * 海报处理服务
     */
    @Autowired
    private PosterService posterService;
    /**
     * Activyty数据服务
     */
    @Autowired
    private ActivityService activityService;
    /**
     * 图片信息Service
     */
    @Autowired
    private SysPictureService sysPictureService;
    /**
     * 微信用户Service
     */
    @Autowired
    private WechatUserInfoService wechatUserInfoService;
    /**
     * 活动助力Service
     */
    @Autowired
    private ActivityHelpService activityHelpService;
    /**
     * 活动助力详细表
     */
    @Autowired
    private ActHelpDetailService actHelpDetailService;

    @Autowired
    private ActivityTrackerService activityTrackerService;

    /**
     * 活动奖励表Service
     */
    @Autowired
    private ActivityRewardService activityRewardService;
    /**
     * 活动奖品关联表Service
     */
    @Autowired
    private ActivityPrizeMappingService prizeMappingService;
    /**
     * 第三方平台Service
     */
    @Autowired
    private ThirdPartyService thirdPartyService;
    /**
     * 助力刚好成功
     */
    private static final String ACTIVITY_HELP_PROCESS_SUCCESS = "success";
    /**
     * 还需要继续助力
     */
    private static final String ACTIVITY_HELP_PROCESS_INPROCESS = "inProcess";
    /**
     * 助力超过要求
     */
    private static final String ACTIVITY_HELP_PROCESS_EXCEEDS = "exceeds";

    /**
     * 微信通知，将微信发送的xml转成map传进来
     *
     * @param wechatEventMap
     * @param appId          公众号appid
     * @return 返回微信发送的消息
     * @author lxl
     */
    @Override
    public String notify(Map wechatEventMap, String appId) {
        return dispatherAndReturn(wechatEventMap, appId);
    }

    /**
     * 查看活动是否有效
     * 1。活动状态字段是否有效，2。时间是否过期
     * 查看当前微信公众号下是否有激活的活动
     *
     * @author: Logan
     * @date: 2018/11/19 10:56
     * @params: [actId]   活动id
     * @return: boolean
     */
    private boolean checkActiveAvailable(String actId) {
        MatActivityStatusEntity statusEntity =
                activityService.getMacActivityStatusByActId(actId);
        if (statusEntity.getStatus() == null || statusEntity.getStatus() == 0) {
            return false;
        }
        Date nowTime = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(statusEntity.getStartAt());
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(statusEntity.getEndAt());
        if (date.after(startTime) && date.before(endTime)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 消息分发，将接收到的微信消息，分发到各个服务中去
     *
     * @param wechatEventMap 传进来的微信时间消息
     * @return 返回处理后的消息
     * @author lxl
     */
    private String dispatherAndReturn(Map<String, String> wechatEventMap, String appid) {
        logger.info(" ----------- 消息分发  appid " + appid);
        String accessToken = getAccessToken(appid);
        logger.info(" ----------- 消息分发  accessToken " + accessToken);
        if (accessToken == null) {
            return "";
        }
        //1.用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
        if (textEvent(wechatEventMap)) {
            return sendKeyCustomMsg(appid, wechatEventMap, accessToken);
        }
        //二维码关注
        if (scanQrCodeEvent(wechatEventMap)) {
//            return subscribeScanKeyCustomMsg(wechatEventMap, appid, accessToken);
            return handleScanQrscene(wechatEventMap, appid);
        }
        //搜索直接关注
        if (subscribeEvent(wechatEventMap)) {
            //新增用户信息
            logger.info(" ----------- 搜索直接关注 ");
            insertWechatUserInfo(wechatEventMap, appid, null);
            wechatEventMap.put(WeChatConstant.CONTENT, "谢谢您的关注！");
            return WeChatUtil.sendTextMsg(wechatEventMap);
        }
        //用户取消公众号关注
        if (unsubscribeEvent(wechatEventMap)) {
            logger.info("原始id " + getWechatAccount(wechatEventMap));
            logger.info("openId " + getFromUserName(wechatEventMap));
            logger.info(" ----------- 进入 用户取消公众号关注 ");
            String actId = activityHelpService.getActIdByOpenId(getFromUserName(wechatEventMap));
            logger.info(" ----------- actId --" + actId);
            if (actId != null) {
                insertWechatUserInfo(wechatEventMap, appid, actId);
            } else {
                insertWechatUserInfo(wechatEventMap, appid, null);
            }

        }
        return "success";

    }

    private String getWechatAccount(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.TO_USER_NAME);
    }

    private String getFromUserName(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.FROM_USER_NAME);
    }

    private String getMsgType(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.MSG_TYPE);
    }

    private String getMsgContent(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.CONTENT);
    }

    private String getEventKey(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.EVENT_KEY);
    }

    private String getEvent(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.EVENT);
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 扫描带参数二维码时，得到openId参数
     * @author Logan
     * @date 2018-12-10 12:10
     */
    private String getOpenIdInQrSceneStr(Map<String, String> wechatEventMap) {
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(eventKey)) {
            String[] keyArr = eventKey.split("&&");
            if (keyArr.length > 0) {
                //&&以前，第一个下划线之后的字符串是openId
                return keyArr[0].substring(keyArr[0].indexOf("_") + 1);
            }
        }
        return null;
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 扫描带参数二维码时，得到activityId参数
     * @author Logan
     * @date 2018-12-10 12:10
     */
    private String getActivityIdInQrSceneStr(Map<String, String> wechatEventMap) {
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(eventKey)) {
            String[] keyArr = eventKey.split("&&");
            if (keyArr.length > 0) {
                //&&之后的字符串为activityId
                return keyArr[1];
            }
        }
        return null;
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 扫带参数二维码事件
     * @author Logan
     * @date 2018-11-27 17:28
     */
    private boolean scanQrCodeEvent(Map<String, String> wechatEventMap) {
        String msgType = getMsgType(wechatEventMap);
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.REQ_MESSAGE_TYPE_EVENT.equals(msgType)
                && StringUtils.isNotEmpty(eventKey)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 判断是否输入文字事件
     * @author Logan
     * @date 2018-11-27 18:39
     */
    private boolean textEvent(Map<String, String> wechatEventMap) {
        String msgType = getMsgType(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && msgType.equals(WeChatConstant.RESP_MESSAGE_TYPE_TEXT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 判断是否直接关注事件 区分是否扫码关注的点是eventKey为空
     * @author Logan
     * @date 2018-11-27 18:39
     */
    private boolean subscribeEvent(Map<String, String> wechatEventMap) {
        String msgType = getMsgType(wechatEventMap);
        String event = getEvent(wechatEventMap);
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.REQ_MESSAGE_TYPE_EVENT.equals(msgType) &&
                StringUtils.isNotEmpty(event) && WeChatConstant.EVENT_TYPE_SUBSCRIBE.equals(event) && StringUtils.isEmpty(eventKey)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param wechatEventMap
     * @return
     * @description 判断是否取消关注事件
     * @author Logan
     * @date 2018-11-27 18:40
     */
    private boolean unsubscribeEvent(Map<String, String> wechatEventMap) {
        String msgType = getMsgType(wechatEventMap);
        String event = getEvent(wechatEventMap);
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.REQ_MESSAGE_TYPE_EVENT.equals(msgType) &&
                StringUtils.isNotEmpty(event) && WeChatConstant.EVENT_TYPE_UNSUBSCRIBE.equals(event) && StringUtils.isEmpty(eventKey)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 扫描带参数二维码并关注事件（新粉）
     * @author Logan
     * @date 2018-12-10 12:41
     */
    private boolean scanQrCodeAndSubscribeEvent(Map<String, String> wechatEventMap) {
        String msgType = getMsgType(wechatEventMap);
        String event = getEvent(wechatEventMap);
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.REQ_MESSAGE_TYPE_EVENT.equals(msgType) &&
                StringUtils.isNotEmpty(event) && WeChatConstant.EVENT_TYPE_SUBSCRIBE.equals(event) && StringUtils.isNotEmpty(eventKey)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 老粉丝扫描带参数二维码事件
     * @author Logan
     * @date 2018-12-10 12:43
     */
    private boolean fansScanQrCodeEvent(Map<String, String> wechatEventMap) {
        String msgType = getMsgType(wechatEventMap);
        String event = getEvent(wechatEventMap);
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.REQ_MESSAGE_TYPE_EVENT.equals(msgType) &&
                StringUtils.isNotEmpty(event) && WeChatConstant.EVENT_TYPE_SCAN.equals(event) && StringUtils.isNotEmpty(eventKey)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
     *
     * @param appid          公众号appid
     * @param wechatEventMap 发送过来的Map信息
     * @param accessToken    公众号accessToken
     * @return 返回的信息
     * @author lxl
     */
    private String sendKeyCustomMsg(String appid, Map<String, String> wechatEventMap, String accessToken) {
        String wechatAccount = getWechatAccount(wechatEventMap);
        String msgContent = getMsgContent(wechatEventMap);
        String fromUserName = getFromUserName(wechatEventMap);
        String actId = matchKeyWord(msgContent, wechatAccount);
        //判读活动是否有效
        if (actId != null &&
                checkActiveAvailable(actId)) {
            insertWechatUserInfo(wechatEventMap, appid, actId);
            return keyWordReplyPoster(wechatEventMap, appid);
        } else {
            Map<String, String> msgReply = new HashMap<>();
            msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
            msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
            String msg = null;
            if (activityService.keyWordExist(msgContent, wechatAccount)) {
                msg = "很抱歉，您参加的活动已经结束，请持续关注我们，更多精彩的活动马上就来~";
            } else {
                msg = "没找到您想要的活动，请持续关注我们，更多活动马上就来~";
            }
            msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, msg);
            WeChatUtil.sendCustomMsg(msgReply, accessToken);
        }

        return "success";
    }

    /**
     * @param wechatEventMap
     * @param appid
     * @return
     * @description 处理扫描带参数的二维码
     * @author Logan
     * @date 2018-12-10 11:56
     */
    private String handleScanQrscene(Map<String, String> wechatEventMap, String appid) {
        String openIdOfScene = getOpenIdInQrSceneStr(wechatEventMap);
        String activityId = getActivityIdInQrSceneStr(wechatEventMap);
        if (StringUtils.isEmpty(openIdOfScene) || StringUtils.isEmpty(activityId)) {
            logger.error("扫描带参数二维码出错，没有获取到activityId或者openId");
            return "";
        }
        //新增用户信息
        insertWechatUserInfo(wechatEventMap, appid, activityId);
        //得到扫描者的openId
        String openIdWhoScan = getFromUserName(wechatEventMap);
        //判断活动是否过期
        if (!checkActiveAvailable(activityId)) {
            Map<String, String> msgReply = new HashMap<>();
            msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, openIdWhoScan);
            msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
            msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, "对不起，本次活动已结束！");
            WeChatUtil.sendCustomMsg(msgReply, getAccessToken(appid));
            return "";
        }

        //扫描自己的海报
        if (openIdOfScene.equals(openIdWhoScan)) {
            logger.info("--------扫描自己的海报-----------");
            //给扫码者发送活动进度
            sendMyActivityStatus(wechatEventMap,appid);
        } else {
            //扫描他人的海报
            logger.info("-----------扫描他人海报--------------------");
            if (alreadyHelpSomeone(activityId, openIdWhoScan, appid)) {
                //如果扫码人已经助力过，则直接返回
                return "success";
            }
            boolean oldFansCanHelp = activityService.oldFansCanHelp(activityId);
            if (oldFansCanHelp) {
                logger.info("----------老粉丝可以助力---------------");
                if (fansScanQrCodeEvent(wechatEventMap)) {
                    //老粉助力
                    oldFansHelp(wechatEventMap, appid);
                } else {
                    //新粉助力
                    newFansHelp(wechatEventMap, appid);
                }
            } else {
                //新粉助力
                newFansHelp(wechatEventMap, appid);
            }
        }


        return "";
    }

    /**
     * @param activityId
     * @param openIdWhoScan
     * @param appId
     * @return
     * @description 判断是否已经给他人助力过，如果已经给他人助力则发送提示
     * @author Logan
     * @date 2018-12-10 16:05
     */
    private boolean alreadyHelpSomeone(String activityId, String openIdWhoScan, String appId) {
        String helpDetailId = actHelpDetailService.getHelpDetailId(openIdWhoScan, activityId);
        if (StringUtils.isNotEmpty(helpDetailId)) {
            logger.info("--------已经助力过他人，不能再次助力-----------------------");
            //已经助力过他人，不能再次助力
            Map<String, String> replyMap = new HashMap<>();
            replyMap.put(KEY_CSMSG_TOUSER, openIdWhoScan);
            replyMap.put(KEY_CSMSG_CONTENT, "本次活动，您已成功为好友助力过一次，不能再次助力，下次活动再来哦");
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
            WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));
            return true;
        } else {
            return false;
        }
    }

    private void newFansHelp(Map<String, String> wechatEventMap, String appId) {
        if (scanQrCodeAndSubscribeEvent(wechatEventMap)) {
            logger.info("---------收到新粉丝助力------------------");
            String activityId = getActivityIdInQrSceneStr(wechatEventMap);
            insertWechatUserInfo(wechatEventMap, appId, activityId);
            String openIdOfScene = getOpenIdInQrSceneStr(wechatEventMap);
            String openIdWhoScan = getFromUserName(wechatEventMap);
            String helpId = activityHelpService.getHelpId(openIdOfScene, activityId);
            String helpDetailId = actHelpDetailService.insertActHelpDetailEntity(helpId, 1, 1, activityId, openIdWhoScan);
            helpAction(wechatEventMap, helpDetailId, appId);
        }
    }

    private void oldFansHelp(Map<String, String> wechatEventMap, String appId) {
        if (fansScanQrCodeEvent(wechatEventMap)) {
            logger.info("-------------收到老粉丝助力-----------");
            String activityId = getActivityIdInQrSceneStr(wechatEventMap);
            String openIdOfScene = getOpenIdInQrSceneStr(wechatEventMap);
            String helpId = activityHelpService.getHelpId(openIdOfScene, activityId);
            //得到扫描者的openId
            String openIdWhoScan = getFromUserName(wechatEventMap);
            String helpDetailId = actHelpDetailService.insertActHelpDetailEntity(helpId, 1, 0, activityId, openIdWhoScan);
            helpAction(wechatEventMap, helpDetailId, appId);
        }
    }

    /**
     * @param openIdOfScene 带参数二维码上的openID，即被助力的openId
     * @param appId
     * @return
     * @description 发送给某人助力成功的提示消息
     * @author Logan
     * @date 2018-12-10 15:54
     */
    private void sendHelpSuccessMsg(String openIdOfScene, String appId) {
        Map<String, String> userHelpPersonalInfoMap = WeChatUtil.getUserInfo(openIdOfScene, getAccessToken(appId));
        String msgHelpTemplate = "您已经成功为好友%s助力一次";
        String msg = String.format(msgHelpTemplate,
                userHelpPersonalInfoMap.get(WeChatConstant.KEY_NICKNAME));
        Map<String, String> replyMap = new HashMap<String, String>();
        replyMap.put(KEY_CSMSG_TOUSER, openIdOfScene);
        replyMap.put(KEY_CSMSG_CONTENT, msg);
        replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
        WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));

    }

    /**
     * @param wechatEventMap
     * @param helpDetailId
     * @param appId
     * @return
     * @description 助力动作，1.发介绍海报，2，扫码者自动加入活动，
     * 3，跟中助力进度，4.给扫码者发送助力成功提示
     * @author Logan
     * @date 2018-12-10 16:03
     */
    private void helpAction(Map<String, String> wechatEventMap, String helpDetailId, String appId) {
        String openIdOfScene = getOpenIdInQrSceneStr(wechatEventMap);
        String activityId = getActivityIdInQrSceneStr(wechatEventMap);
        //得到扫描者的openId
        String openIdWhoScan = getFromUserName(wechatEventMap);
        //发送活动介绍
        logger.info("------助力成功，发送活动介绍----------------");
        introduceActivity(wechatEventMap, appId, activityId);

        String wechatAccount = getWechatAccount(wechatEventMap);
        //扫码者自动加入活动
        logger.info("--------助力成功，自动加入活动--------");
        joinActivity(wechatAccount, openIdWhoScan, activityId, 0);
        //跟踪活动状态
        logger.info("--------助力成功，跟踪活动进度--------");
        trackActive(openIdOfScene, helpDetailId, activityId, getAccessToken(appId));
        //扫码人自己收到一个助力成功的提示
        logger.info("--------助力成功，发送给默认助力成功提示--------");
        sendHelpSuccessMsg(openIdOfScene, appId);
    }

    /**
     * @param wechatEventMap
     * @return
     * @description 发送我的活动进度
     * @author Logan
     * @date 2018-12-10 16:11
     */
    private void sendMyActivityStatus(Map<String, String> wechatEventMap, String appId) {
        String activityId = getActivityIdInQrSceneStr(wechatEventMap);
        String openIdWhoScan = getFromUserName(wechatEventMap);
        String helpId = activityHelpService.getHelpId(openIdWhoScan,activityId);
        Map<String, Integer> helpCountMap = activityTrackerService.getHelpCount(helpId, activityId);
        Integer remain = Integer.parseInt(helpCountMap.get("remain").toString());
        String msgUnSuccessTemplate = "已经有%s位好友成功为你助力，还需要%s位好友支持哟~";
        String msgSuccessTemplate = "您已经完成助力任务领取奖励，%s。%s";
        Map<String, String> replyMap = new HashMap<String, String>();
        replyMap.put(KEY_CSMSG_TOUSER, openIdWhoScan);
        replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
        String msg = "";
        if (remain > 0) {
            msg = String.format(msgUnSuccessTemplate,
                    Integer.parseInt(helpCountMap.get("help").toString()), remain > 0 ? remain : 0);
        } else {
            ActivityRewardEntity rewardEntity =
                    activityRewardService.getEntityByActIdAndWechatUserId(activityId, openIdWhoScan);
            if (rewardEntity != null) {
                ActivityPrizeMappingEntity prizeMappingEntity =
                        prizeMappingService.getEntityByPrizeId(rewardEntity.getPrizeId());
                String rewardInfo = getRewardInfo(prizeMappingEntity);
                //发送奖品消息
                if (StringUtils.isNotEmpty(rewardInfo)) {
                    MatActivityEntity activityEntity =
                            activityService.getMatActivityEntityByActId(activityId);
                    msg = String.format(msgSuccessTemplate,
                            activityEntity.getRewardUrl(), rewardInfo.replaceAll("openid", openIdWhoScan).replace("actId", activityId));
                }
            }
        }
       //回复信息
        replyMap.put(KEY_CSMSG_CONTENT, msg);
        WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));
    }

    /**
     * @param wechatEventMap 发送过来的Map信息
     * @param appid          公众号appid
     * @param accessToken    公众号accessToken
     * @return 返回的信息
     * @author lxl
     */
    private String subscribeScanKeyCustomMsg(Map<String, String> wechatEventMap, String appid, String
            accessToken) {
        logger.info("----------------扫二维码进入------------------------");

        logger.info("----------------插入新增用户信息------------------------");
        //得到传参的信息
        //事件KEY值，qrscene_为前缀，后面为二维码的参数值
        String eventKeyValue = getEventKey(wechatEventMap);
        String helpOpenId = eventKeyValue.split("&&")[0];
        logger.info(String.format("----------助力的helpid:%s----------", helpOpenId));
        //第一个下划线后面的就是openId
        helpOpenId = helpOpenId.substring(helpOpenId.indexOf("_") + 1);
        String actId = eventKeyValue.split("&&")[1];
        //新增用户信息
        insertWechatUserInfo(wechatEventMap, appid, actId);
        String fromUserName = getFromUserName(wechatEventMap);
        if (!checkActiveAvailable(actId)) {
            Map<String, String> msgReply = new HashMap<>();
            msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
            msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
            msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, "对不起，本次活动已结束！");
            WeChatUtil.sendCustomMsg(msgReply, accessToken);
            return "";
        }
        //2. 通过openId+actId得到当前用户是否已参加助力
        String helpId = activityHelpService.getHelpId(helpOpenId.toString(), actId);
        //通过fromUserName+actId得到当前用户是否已参加助力,如果id存在则是老用户否则为新用户
        String fromHelpId = activityHelpService.getHelpId(fromUserName, actId);
        String actHelpDetailId = null;
        if (StringUtils.isNotEmpty(fromHelpId)) {
            //老用户
            logger.info("---------------老用户------------");
            actHelpDetailService.insertActHelpDetailEntity(helpId, 0, 0, actId, fromUserName);
            Map<String, String> replyMap = new HashMap<>();
            if (fromUserName.equals(helpOpenId)) {
                logger.info("---------------当老用户再次扫码时发送信息------------start");
                //跟踪助力数据统计，一共需要多少助力的(require)，已经助力多少了(help)，还剩下(remain)
                // fromHelpId 以前用的是helpId 此处有问题，先头考虑到用户只扫自己的码，
                // 没有考虑到用户会扫别人的码，这样就造成用户扫别人的码后显示是他人的统计信息
                Map<String, Integer> helpCountMap = activityTrackerService.getHelpCount(fromHelpId, actId);
                Integer remain = Integer.parseInt(helpCountMap.get("remain").toString());
                String msgSuccessTemplate = "已经有%s位好友成功为你助力，还需要%s位好友支持哟~";
                if (remain > 0) {
                    String msg = String.format(msgSuccessTemplate,
                            Integer.parseInt(helpCountMap.get("help").toString()), remain > 0 ? remain : 0);
                    replyMap.put(KEY_CSMSG_TOUSER, fromUserName);
                    replyMap.put(KEY_CSMSG_CONTENT, msg);
                    replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
                } else {
                    replyMap.put(KEY_CSMSG_TOUSER, fromUserName);
                    replyMap.put(KEY_CSMSG_CONTENT, "您已成功领取奖品，无需再次扫码～");
                    replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
                }

                logger.info("---------------当老用户再次扫码时发送信息------------end");
            } else {
                replyMap.put(KEY_CSMSG_TOUSER, fromUserName);
                replyMap.put(KEY_CSMSG_CONTENT, "本次活动，您已成功为好友助力过一次，不能再次助力，下次活动再来哦");
                replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
            }
            WeChatUtil.sendCustomMsg(replyMap, accessToken);

        } else {
            //新用户
            logger.info("---------------新用户------------");
            actHelpDetailId = actHelpDetailService.insertActHelpDetailEntity(helpId, 1, 1, actId, fromUserName);
            MatActivityEntity matActivityEntity = activityService.getMatActivityEntityByActId(actId);
            wechatEventMap.put(ActivityService.KEY_SHARECOYPWRITTING,
                    matActivityEntity.getActShareCopywriting());
            //需要调用生成海报接口
            wechatEventMap.put(ActivityService.KEY_ACT_ID, actId);
            wechatEventMap.put(ActivityService.KEY_PIC_ID, matActivityEntity.getPictureId());
            activityReply(wechatEventMap, accessToken, helpOpenId);
            String processStatus = trackActive(helpOpenId, actHelpDetailId, actId, accessToken);
        }
        return "";
    }

    private String getRewardInfo(ActivityPrizeMappingEntity prizeMappingEntity) {
        String prizeMsg = "";
        if (prizeMappingEntity == null) {
            return null;
        }
        String actId = prizeMappingEntity.getActId();
        MatActivityEntity activityEntity = activityService.getMatActivityEntityByActId(actId);
        RewardTypeEnum rewardType = null;
        for (int i = 0; i <= RewardTypeEnum.values().length - 1; i++) {
            if (RewardTypeEnum.values()[i].getIndex() == activityEntity.getActType()) {
                rewardType = RewardTypeEnum.values()[i];
            }
        }

        switch (rewardType) {
            case TOKEN:
                prizeMsg = String.format("口令：%s", prizeMappingEntity.getToken());
                break;
            case GOODS:
                prizeMsg = String.format("\n%s", prizeMappingEntity.getLinkUrl());
                break;
            case SECURECODE:
                prizeMsg = String.format("地址:%s,口令:%s",
                        prizeMappingEntity.getLinkUrl(), prizeMappingEntity.getToken());
                break;
            default:
                break;
        }
        return prizeMsg;
    }

    /**
     * 新增微信用户方法
     *
     * @param wechatEventMap
     */
    public void insertWechatUserInfo(Map<String, String> wechatEventMap, String appid, String actId) {

        try {
            logger.info(" ----------- 进入 新增用户 appid " + appid);
            String accessToken = thirdPartyService.getAuthAccessToken(appid);
            logger.info(" ----------- 进入 新增用户 accessToken " + accessToken);

//            String eventKey = null;
//            String ticket = null;
//            if (wechatEventMap.containsKey("EventKey") && StringUtils.isNotEmpty(wechatEventMap.get("EventKey").toString())) {
//                logger.info(" ----------- 进入 新增用户 EventKey 存在");
//                Map<String, String> userInfo = WeChatUtil.getUserInfo(wechatEventMap.get(WeChatConstant.FROM_USER_NAME), accessToken);
//                wechatUserInfoService.insertWechatUserInfoEntity(Integer.parseInt(userInfo.get("subscribe")), userInfo.get("openid"), userInfo.get("nickname"), Integer.parseInt(userInfo.get("sex")),
//                        userInfo.get("country"), userInfo.get("province"), userInfo.get("language"), userInfo.get("headimgurl"), userInfo.get("unionid"), userInfo.get("remark"),
//                        userInfo.get("subscribe_scene"), wechatEventMap.get(WeChatConstant.TO_USER_NAME), Integer.parseInt(userInfo.get("subscribe_time")), userInfo.get("city"), Integer.parseInt(userInfo.get("qr_scene")),
//                        userInfo.get("qr_scene_str"), actId, wechatEventMap.get("MsgType"), wechatEventMap.get("Event"), wechatEventMap.get("EventKey"), wechatEventMap.get("Ticket"));
//
//            }
            /**
             * 用户取消关注
             */
            if (wechatEventMap.containsKey("Event") && WeChatConstant.EVENT_TYPE_UNSUBSCRIBE.equals(wechatEventMap.get("Event"))) {
                logger.info(" ----------- 进入 取消时增加用户信息");
                wechatUserInfoService.insertWechatUserInfoEntity(wechatEventMap.get(WeChatConstant.FROM_USER_NAME), wechatEventMap.get(WeChatConstant.TO_USER_NAME), actId, wechatEventMap.get("MsgType"), wechatEventMap.get("Event"),
                        null, null);
            } else {
                logger.info(" ----------- 进入 新增用户 EventKey 存在");
                Map<String, String> userInfo = WeChatUtil.getUserInfo(wechatEventMap.get(WeChatConstant.FROM_USER_NAME), accessToken);
                wechatUserInfoService.insertWechatUserInfoEntity(Integer.parseInt(userInfo.get("subscribe")), userInfo.get("openid"), userInfo.get("nickname"), Integer.parseInt(userInfo.get("sex")),
                        userInfo.get("country"), userInfo.get("province"), userInfo.get("language"), userInfo.get("headimgurl"), userInfo.get("unionid"), userInfo.get("remark"),
                        userInfo.get("subscribe_scene"), wechatEventMap.get(WeChatConstant.TO_USER_NAME), Integer.parseInt(userInfo.get("subscribe_time")), userInfo.get("city"), Integer.parseInt(userInfo.get("qr_scene")),
                        userInfo.get("qr_scene_str"), actId, wechatEventMap.get("MsgType"), wechatEventMap.get("Event"), wechatEventMap.get("EventKey"), wechatEventMap.get("Ticket"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据微信公众号和关键字匹配激活的活动
     *
     * @author: Logan
     * @date: 2018/11/19 11:08
     * @params: [keyWord, wechatAccount]
     * @return: 活动id；
     **/
    private String matchKeyWord(String keyWord, String wechatAccount) {
        String activityId = activityService.getMatActivityId(wechatAccount, keyWord);
        if (StringUtils.isNotEmpty(activityId)) {
            return activityId;
        }
        return null;
    }

    /**
     * @param wechatEventMap
     * @param appId
     * @param activityId
     * @return 返回userPersonalInfoMap
     * @description
     * @author Logan
     * @date 2018-12-10 14:50
     */
    private Map<String, String> introduceActivity(Map<String, String> wechatEventMap, String appId, String activityId) {
        String openId = getFromUserName(wechatEventMap);
        String wechatAccount = getWechatAccount(wechatEventMap);
        //获取粉丝个人信息存入到userPersonalInfoMap
        Map<String, String> userPersonalInfoMap = WeChatUtil.getUserInfo(openId, getAccessToken(appId));
        //获取带参数的二维码
        MatActivityEntity matActivityEntity = activityService.getMatActivityEntityByActId(activityId);
        StringBuilder sceneStrBuilder = new StringBuilder();
        sceneStrBuilder.append(openId).append(TaskBabyConstant.SEPERATOR_QRSCEAN).append(activityId);
        //将活动的原始海报的url放入到userPersonalInfoMap中
        String picId = matActivityEntity.getPictureId();
        String posterUrl = sysPictureService.getPictureURL(picId);
        userPersonalInfoMap.put(TaskBabyConstant.KEY_POSTER_URL, posterUrl);
        userPersonalInfoMap.put(WeChatConstant.REPLY_TO_USER_NAME, openId);
        userPersonalInfoMap.put(WeChatConstant.REPLY_FROM_USER_NAME, wechatAccount);
        //回复信息
        Map<String, String> replyMap = new HashMap<>();
        replyMap.put(KEY_CSMSG_TOUSER, openId);
        Object shareCoppywritting = matActivityEntity.getActShareCopywriting();
        if (shareCoppywritting != null) {
            //用客服消息发送活动介绍
            replyMap.put(KEY_CSMSG_CONTENT, "Hi，" + userPersonalInfoMap.get(WeChatConstant.KEY_NICKNAME) + ",欢迎参加活动~\n" + shareCoppywritting.toString());
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
            WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));
        }
        String qrCodeUrl = WeChatUtil.getWXPublicQRCode(WeChatUtil.QR_TYPE_TEMPORARY,
                WeChatUtil.QR_MAX_EXPIREDTIME, WeChatUtil.QR_SCENE_NAME_STR, sceneStrBuilder.toString(), getAccessToken(appId));
        //将二维码url put到userPersonalInfoMap中
        userPersonalInfoMap.put(TaskBabyConstant.KEY_QRCODE_URL, qrCodeUrl);

        //得到合成图片的filePath
        String customizedPosterPath = posterService.getCombinedCustomiedPosterFilePath(userPersonalInfoMap);
        //发送个性化海报
        if (customizedPosterPath != null) {
            replyMap.put(KEY_FILE_PATH, customizedPosterPath);
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_IMG);
            WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));
        }
        return userPersonalInfoMap;
    }

    /**
     * @param wechatEventMap
     * @param appId
     * @return
     * @description 根据关键字回复海报
     * @author Logan
     * @date 2018-12-10 14:51
     */
    private String keyWordReplyPoster(Map<String, String> wechatEventMap, String appId) {
        String openId = getFromUserName(wechatEventMap);
        String wechatAccount = getWechatAccount(wechatEventMap);
        String keyWord = getMsgContent(wechatEventMap);
        if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(wechatAccount)) {
            return "";
        }
        String activityId = activityService.getMatActivityId(wechatAccount, keyWord);
        //发送海报
        introduceActivity(wechatEventMap, appId, activityId);
        //扫码者自动加入活动
        joinActivity(wechatAccount, openId, activityId, 1);
        return "success";
    }

    /**
     * 粉丝发关键字，微信公众号发送客服信息
     * 回复活动内容介绍和个性化海报
     *
     * @author: Logan
     * @date: 2018/11/17 12:45
     * @params: [wechatEventMap]
     * @return: java.lang.String
     **/
    private String keyWordReply(Map<String, String> wechatEventMap, String accessToken) {
        long start = System.currentTimeMillis();
        String openId = getFromUserName(wechatEventMap);
        String wechatAccount = getWechatAccount(wechatEventMap);
        String keyWord = getMsgContent(wechatEventMap);
        if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(wechatAccount)) {
            return "";
        }

        //获取粉丝个人信息存入到userPersonalInfoMap
        Map<String, String> userPersonalInfoMap = WeChatUtil.getUserInfo(openId, accessToken);
        long begin = System.currentTimeMillis();
        //获取带参数的二维码
        Map<String, Object> activitySimpleInfoMap =
                activityService.getMacActivitySimpleInfo(wechatAccount, keyWord);
        WeChatUtil.log(logger, begin, "getMacActivitySimpleInfo");
        if (activitySimpleInfoMap == null) {
            return "";
        }

        String activityId = activitySimpleInfoMap.get(ActivityService.KEY_ACT_ID).toString();
        StringBuilder sceneStrBuilder = new StringBuilder();
        sceneStrBuilder.append(openId).append(TaskBabyConstant.SEPERATOR_QRSCEAN).append(activityId);

        //将活动的原始海报的url放入到userPersonalInfoMap中
        begin = System.currentTimeMillis();
        String picId = activitySimpleInfoMap.get(ActivityService.KEY_PIC_ID).toString();
        String posterUrl = sysPictureService.getPictureURL(picId);
        WeChatUtil.log(logger, begin, "getPictureURL");
        userPersonalInfoMap.put(TaskBabyConstant.KEY_POSTER_URL, posterUrl);

        userPersonalInfoMap.put(WeChatConstant.REPLY_TO_USER_NAME, openId);
        userPersonalInfoMap.put(WeChatConstant.REPLY_FROM_USER_NAME, wechatAccount);
        WeChatUtil.log(logger, start, "回复消息前的准备工作");

        //回复信息
        Map<String, String> replyMap = new HashMap<>();

        replyMap.put(KEY_CSMSG_TOUSER, openId);
        Object shareCoppywritting = activitySimpleInfoMap.get(ActivityService.KEY_SHARECOYPWRITTING);
        //发送活动介绍
        if (shareCoppywritting != null) {
            logger.info("--------发送活动介绍-----------");
            replyMap.put(KEY_CSMSG_CONTENT, "Hi，" + userPersonalInfoMap.get(WeChatConstant.KEY_NICKNAME) + ",欢迎参加活动~\n" + shareCoppywritting.toString());
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
            WeChatUtil.sendCustomMsg(replyMap, accessToken);
            WeChatUtil.log(logger, start, "回复文字信息全部动作");
        }
        logger.info("--------发送图片中。。。。。。----------------");
        begin = System.currentTimeMillis();
        String qrCodeUrl = WeChatUtil.getWXPublicQRCode(WeChatUtil.QR_TYPE_TEMPORARY,
                WeChatUtil.QR_MAX_EXPIREDTIME, WeChatUtil.QR_SCENE_NAME_STR, sceneStrBuilder.toString(), accessToken);
        long urlbegin = System.currentTimeMillis();
        WeChatUtil.log(logger, urlbegin, "获取带参数的二维码的url");
        //将二维码url put到userPersonalInfoMap中
        userPersonalInfoMap.put(TaskBabyConstant.KEY_QRCODE_URL, qrCodeUrl);

        //得到合成图片的filePath
        String customizedPosterPath = posterService.getCombinedCustomiedPosterFilePath(userPersonalInfoMap);
        WeChatUtil.log(logger, begin, "3张图片合成");
        //发送个性化海报
        if (customizedPosterPath != null) {
            replyMap.put(KEY_FILE_PATH, customizedPosterPath);
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_IMG);
            begin = System.currentTimeMillis();
            WeChatUtil.sendCustomMsg(replyMap, accessToken);
            WeChatUtil.log(logger, begin, "发送图片全部动作");
        }
        logger.info("--------插入粉丝加入活动的数据---------------");
        joinActivity(wechatAccount, openId, activityId, 0);
        return "success";
    }

    private String getAccessToken(String appid) {
        logger.debug("----------获取AccessToken-------");
        long begin = System.currentTimeMillis();

        String accessToken = null;
        try {
            accessToken = thirdPartyService.getAuthAccessToken(appid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WeChatUtil.log(logger, begin, "生成AccessToken");
        return accessToken;
    }

    /**
     * 粉丝发关键字，微信公众号发送客服信息
     * 回复活动内容介绍和个性化海报
     *
     * @param helpOpenId 当已帮A助力成功后，需要给B发送信息，告诉已帮A助力成功
     * @author: Logan
     * @date: 2018/11/17 12:45
     * @params: [wechatEventMap]
     * @return: java.lang.String
     **/
    private String activityReply(Map<String, String> wechatEventMap, String accessToken, String helpOpenId) {
        String msgSuccessTemplate = "您已经成功为好友%s助力一次~";
        String openId = wechatEventMap.get(WeChatConstant.FROM_USER_NAME);
        String wechatAccount = wechatEventMap.get(WeChatConstant.TO_USER_NAME);
        if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(wechatAccount)) {
            return "";
        }

        if (accessToken == null) {
            return "";
        }
        //获取粉丝个人信息存入到userPersonalInfoMap
        Map<String, String> userPersonalInfoMap = WeChatUtil.getUserInfo(openId, accessToken);
        String activityId = wechatEventMap.get(ActivityService.KEY_ACT_ID);


        userPersonalInfoMap.put(WeChatConstant.REPLY_TO_USER_NAME, openId);
        userPersonalInfoMap.put(WeChatConstant.REPLY_FROM_USER_NAME, wechatAccount);

        logger.info("发送帮A助力成功。。。start");
        /**
         * 得到被助力者的用户信息
         */
        Map<String, String> replyAMap = new HashMap<>();
        Map<String, String> userHelpPersonalInfoMap = WeChatUtil.getUserInfo(helpOpenId, accessToken);
        String msg = String.format(msgSuccessTemplate,
                userHelpPersonalInfoMap.get(WeChatConstant.KEY_NICKNAME).toString());
        replyAMap.put(KEY_CSMSG_TOUSER, openId);
        replyAMap.put(KEY_CSMSG_CONTENT, msg);
        replyAMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
        WeChatUtil.sendCustomMsg(replyAMap, accessToken);
        logger.info("发送帮A助力成功。。。end");

        //回复信息
        Map<String, String> replyMap = new HashMap<>();
        logger.info("发送文本中。。。");
        replyMap.put(KEY_CSMSG_TOUSER, openId);
        Object shareCoppywritting = wechatEventMap.get(ActivityService.KEY_SHARECOYPWRITTING);
        //发送活动介绍
        if (shareCoppywritting != null) {
            replyMap.put(KEY_CSMSG_CONTENT, "Hi，" + userPersonalInfoMap.get(WeChatConstant.KEY_NICKNAME) + ",欢迎参加活动~\n" + shareCoppywritting.toString());
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
            WeChatUtil.sendCustomMsg(replyMap, accessToken);
        }
        logger.info("发送文本完成。。。");
        logger.info("发送海报中。。。");
        //获取带参数的二维码

        StringBuilder sceneStrBuilder = new StringBuilder();
        sceneStrBuilder.append(openId).append(TaskBabyConstant.SEPERATOR_QRSCEAN).append(activityId);
        String qrCodeUrl = WeChatUtil.getWXPublicQRCode(WeChatUtil.QR_TYPE_TEMPORARY,
                WeChatUtil.QR_MAX_EXPIREDTIME, WeChatUtil.QR_SCENE_NAME_STR, sceneStrBuilder.toString(), accessToken);

        //将二维码url put到userPersonalInfoMap中
        userPersonalInfoMap.put(TaskBabyConstant.KEY_QRCODE_URL, qrCodeUrl);
        //将活动的原始海报的url放入到userPersonalInfoMap中
        String picId = wechatEventMap.get(ActivityService.KEY_PIC_ID);
        String posterUrl = sysPictureService.getPictureURL(picId);
        userPersonalInfoMap.put(TaskBabyConstant.KEY_POSTER_URL, posterUrl);
        //得到合成图片的filePath
        String customizedPosterPath = posterService.getCombinedCustomiedPosterFilePath(userPersonalInfoMap);
        //发送个性化海报
        if (customizedPosterPath != null) {
            replyMap.put(KEY_FILE_PATH, customizedPosterPath);
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_IMG);
            WeChatUtil.sendCustomMsg(replyMap, accessToken);
        }
        logger.info("发送海报完成。。。");
        joinActivity(wechatAccount, openId, activityId, 1);
        return "success";
    }

    /**
     * 加入活动
     *
     * @author: Logan
     * @date: 2018/11/17 03:50
     * @params: [wechatAccount, openId, activityId]
     * @return: void
     **/
    private void joinActivity(String wechatAccount, String openId, String activityId, Integer subscribeScene) {
        if (!activityHelpService.checkFansInActivity(openId, activityId)) {
            activityHelpService.insertActivityHelpEntity(
                    activityId, wechatAccount, openId, 0, 0, subscribeScene);
        }
    }

    /**
     * @author: Logan
     * @date: 2018/11/19 15:43
     * @params: [touser, helpDetailId, activityId, access_token]
     * @return: 如果助力跟踪状态，success，inProcess，exceeded；
     **/
    private String trackActive(String touser, String helpDetailId, String activityId, String accessToken) {
        String processStatus = ACTIVITY_HELP_PROCESS_INPROCESS;
        String msgTemplate = "收到%s的助力，还差%d人完成助力";
        String msgSuccessTemplate = "收到%s的助力，%s。%s";
        String msg = "";
        Map<String, Object> trackResult = activityTrackerService.getTrackInfo(helpDetailId, activityId, accessToken);
        if (trackResult != null) {
            int remain = Integer.parseInt(
                    trackResult.get(ActivityTrackerService.KEY_HELP_REMAIN).toString());
            if (remain > 0) {
                msg = String.format(msgTemplate,
                        trackResult.get(WeChatConstant.KEY_NICKNAME).toString(), remain);
                //回复信息
                sendMsg(msg, touser, accessToken);
                processStatus = ACTIVITY_HELP_PROCESS_INPROCESS;
            } else {
                MatActivityEntity activityEntity =
                        activityService.getMatActivityEntityByActId(activityId);
                if (remain == 0) {

                    processStatus = ACTIVITY_HELP_PROCESS_SUCCESS;
                    ActivityPrizeMappingEntity prizeMappingEntity =
                            prizeMappingService.getEntityByActId(activityId);
                    String rewardInfo = getRewardInfo(prizeMappingEntity);
                    //插入领奖信息表
                    activityRewardService.insertActivityRewardEntity(activityId, touser,
                            activityEntity.getWechatAccount(), 1, prizeMappingEntity.getPrizeId());
                    //发送奖品消息
                    if (StringUtils.isNotEmpty(rewardInfo)) {
                        msg = String.format(msgSuccessTemplate,
                                trackResult.get(WeChatConstant.KEY_NICKNAME).toString(),
                                activityEntity.getRewardUrl(), rewardInfo.replaceAll("openid", touser).replace("actId", activityId));
                    }
                    //回复信息
                    sendMsg(msg, touser, accessToken);
                    //当A用户完成任务后修改助力状态
                    activityHelpService.updateHelpSuccessStatus(touser);
                    JSONObject msgJson = new JSONObject();
                } else {
                    processStatus = ACTIVITY_HELP_PROCESS_EXCEEDS;
//                    actHelpDetailService.updateActHelpDetailEntity(helpDetailId, 0, 1); //当超出一助力人数后,在有用户助力的话就没法累加多少人助力，所以先去掉，等王总回来问问。
                }
            }
        }
        return processStatus;
    }

    /**
     * @param msg         回复的信息
     * @param touser      接收用户的openid
     * @param accessToken 令牌
     * @return
     * @description 回复信息
     * @author lxl
     * @date 2018-12-01 16:42
     */
    private void sendMsg(String msg, String touser, String accessToken) {
        //回复信息
        if (StringUtils.isNotEmpty(msg)) {
            Map<String, String> replyMap = new HashMap<>();
            replyMap.put(KEY_CSMSG_TOUSER, touser);
            replyMap.put(KEY_CSMSG_CONTENT, msg);
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
            WeChatUtil.sendCustomMsg(replyMap, accessToken);
        }
    }
}