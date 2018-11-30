package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.*;
import data.driven.cm.component.RewardTypeEnum;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.ActivityPrizeMappingEntity;
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

        String accessToken = getAccessToken(appid);
        if (accessToken == null) {
            return "";
        }
        //1.用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
        if (textEvent(wechatEventMap)) {
            return sendKeyCustomMsg(appid, wechatEventMap, accessToken);
        }
        //二维码关注
        if (scanQrCodeEvent(wechatEventMap)) {
            return subscribeScanKeyCustomMsg(wechatEventMap, appid, accessToken);
        }
        //搜索直接关注
        if (subscribeEvent(wechatEventMap)) {
            //新增用户信息
            insertWechatUserInfo(wechatEventMap, appid);
            wechatEventMap.put(WeChatConstant.CONTENT, "谢谢您的关注！");
            return WeChatUtil.sendTextMsg(wechatEventMap);
        }
        //用户取消公众号关注
        if (unsubscribeEvent(wechatEventMap)) {
            wechatUserInfoService.updateSubscribe(
                    getWechatAccount(wechatEventMap),
                    getFromUserName(wechatEventMap), 0);
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
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.EVENT_TYPE_SUBSCRIBE.equals(msgType)
                && StringUtils.isEmpty(eventKey)) {
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
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.EVENT_TYPE_UNSUBSCRIBE.equals(msgType)
                && StringUtils.isEmpty(eventKey)) {
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
            insertWechatUserInfo(wechatEventMap, appid);
            return keyWordReply(wechatEventMap, accessToken);
        } else{
            Map<String, String> msgReply = new HashMap<>();
            msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
            msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
            String msg =null;
            if(activityService.keyWordExist(msgContent, wechatAccount)){
                msg ="很抱歉，您参加的活动已经结束，请持续关注我们，更多精彩的活动马上就来~";
            }else{
                msg="没找到您想要的活动，请持续关注我们，更多活动马上就来~";
            }
            msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, msg);
            WeChatUtil.sendCustomMsg(msgReply, accessToken);
        }

        return "success";
    }

    /**
     * @param wechatEventMap 发送过来的Map信息
     * @param appid          公众号appid
     * @param accessToken    公众号accessToken
     * @return 返回的信息
     * @author lxl
     */
    private String subscribeScanKeyCustomMsg(Map<String, String> wechatEventMap, String appid, String accessToken) {
        logger.info("----------------扫二维码进入------------------------");
        //新增用户信息
        insertWechatUserInfo(wechatEventMap, appid);
        logger.info("----------------插入新增用户信息------------------------");
        //得到传参的信息
        //事件KEY值，qrscene_为前缀，后面为二维码的参数值
        String eventKeyValue = getEventKey(wechatEventMap);
        String helpOpenId = eventKeyValue.split("&&")[0];
        logger.info(String.format("----------助力的helpid:%s----------", helpOpenId));
        //第一个下划线后面的就是openId
        helpOpenId = helpOpenId.substring(helpOpenId.indexOf("_") + 1);
        String actId = eventKeyValue.split("&&")[1];
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
        //通过openId+actId得到当前用户是否已参加助力,如果id存在则是老用户否则为新用户
        String fromHelpId = activityHelpService.getHelpId(fromUserName, actId);
        String actHelpDetailId = null;
        if (StringUtils.isNotEmpty(fromHelpId)) {
            //老用户
            logger.info("---------------老用户------------");
            actHelpDetailService.insertActHelpDetailEntity(helpId, 0, 0, actId, fromUserName);
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
            activityReply(wechatEventMap, accessToken);
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
    public void insertWechatUserInfo(Map<String, String> wechatEventMap, String appid) {
        try {
            String accessToken = thirdPartyService.getAuthAccessToken(appid);
            Map<String, String> userInfo = WeChatUtil.getUserInfo(wechatEventMap.get(WeChatConstant.FROM_USER_NAME), accessToken);
            wechatUserInfoService.insertWechatUserInfoEntity(Integer.parseInt(userInfo.get("subscribe")), userInfo.get("openid"), userInfo.get("nickname"), Integer.parseInt(userInfo.get("sex")),
                    userInfo.get("country"), userInfo.get("province"), userInfo.get("language"), userInfo.get("headimgurl"), userInfo.get("unionid"), userInfo.get("remark"),
                    userInfo.get("subscribe_scene"), wechatEventMap.get(WeChatConstant.TO_USER_NAME), Integer.parseInt(userInfo.get("subscribe_time")), userInfo.get("city"), Integer.parseInt(userInfo.get("qr_scene")),
                    userInfo.get("qr_scene_str"));
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
            replyMap.put(KEY_CSMSG_CONTENT, shareCoppywritting.toString());
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
        joinActivity(wechatAccount, openId, activityId);
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
     * @author: Logan
     * @date: 2018/11/17 12:45
     * @params: [wechatEventMap]
     * @return: java.lang.String
     **/
    private String activityReply(Map<String, String> wechatEventMap, String accessToken) {
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

        //回复信息
        Map<String, String> replyMap = new HashMap<>();
        logger.info("发送文本中。。。");
        replyMap.put(KEY_CSMSG_TOUSER, openId);
        Object shareCoppywritting = wechatEventMap.get(ActivityService.KEY_SHARECOYPWRITTING);
        //发送活动介绍
        if (shareCoppywritting != null) {
            replyMap.put(KEY_CSMSG_CONTENT, shareCoppywritting.toString());
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
        joinActivity(wechatAccount, openId, activityId);
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
    private void joinActivity(String wechatAccount, String openId, String activityId) {
        if (!activityHelpService.checkFansInActivity(openId, activityId)) {
            activityHelpService.insertActivityHelpEntity(
                    activityId, wechatAccount, openId, 0, 0);
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
        String msgSuccessTemplate = "收到%s的助力,%s,%s";
        String msg = "";
        Map<String, Object> trackResult = activityTrackerService.getTrackInfo(helpDetailId, activityId, accessToken);
        if (trackResult != null) {
            int remain = Integer.parseInt(
                    trackResult.get(ActivityTrackerService.KEY_HELP_REMAIN).toString());
            if (remain > 0) {
                msg = String.format(msgTemplate,
                        trackResult.get(WeChatConstant.KEY_NICKNAME).toString(), remain);
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
                                activityEntity.getRewardUrl(), rewardInfo.replaceAll("openid", touser).replace("actId",activityId));
                    }
                } else {
                    processStatus = ACTIVITY_HELP_PROCESS_EXCEEDS;
                    actHelpDetailService.updateActHelpDetailEntity(helpDetailId, 0, 1);
                }
            }
            //回复信息
            if (StringUtils.isNotEmpty(msg)) {
                Map<String, String> replyMap = new HashMap<>();
                replyMap.put(KEY_CSMSG_TOUSER, touser);
                replyMap.put(KEY_CSMSG_CONTENT, msg);
                replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
                WeChatUtil.sendCustomMsg(replyMap, accessToken);
            }

        }
        return processStatus;
    }
}