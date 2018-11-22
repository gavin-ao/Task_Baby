package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.*;
import data.driven.cm.component.RewardTypeEnum;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskBaby.ActivityPrizeMappingEntity;
import data.driven.cm.entity.taskBaby.MatActivityEntity;
import data.driven.cm.entity.taskBaby.MatActivityStatusEntity;
import data.driven.cm.entity.taskBaby.WechatPublicEntity;
import data.driven.cm.util.WXUtil;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
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
    @Autowired
    private PosterService posterService;
    @Autowired
    private WechatPublicService wechatPublicService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private SysPictureService sysPictureService;
    @Autowired
    private WechatUserInfoService wechatUserInfoService; //微信用户Service
    @Autowired
    private ActivityHelpService activityHelpService;
    @Autowired
    private ActHelpDetailService actHelpDetailService; //活动助力详细表

    @Autowired
    private ActivityTrackerService activityTrackerService;

    @Autowired
    private ActivityRewardService activityRewardService;
    @Autowired
    private ActivityPrizeMappingService prizeMappingService;

    private static final String ACTIVITY_HELP_PROCESS_SUCCESS = "success"; //助力刚好成功
    private static final String ACTIVITY_HELP_PROCESS_INPROCESS = "inProcess";//还需要继续助力
    private static final String ACTIVITY_HELP_PROCESS_EXCEEDS = "exceeds";//助力超过要求

    @Override
    public String notify(Map wechatEventMap) {
//        if(checkActive(wechatEventMap)) { // 当用户只是关注并没有参加活动的话，采用 checkActive方法就会拦截，用户就不能与公众号进行交互了
        return dispatherAndReturn(wechatEventMap);
//        }
//        return "success";
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
     */
    private String dispatherAndReturn(Map<String, String> wechatEventMap) {
        String wechatAccount = wechatEventMap.get(WeChatConstant.ToUserName);
        String fromUserName = wechatEventMap.get(WeChatConstant.FromUserName);
        String msgType = wechatEventMap.get(WeChatConstant.MsgType);
        String msgContent = wechatEventMap.get(WeChatConstant.Content);
        String accessToken = getAccessToken(wechatAccount);
        if (accessToken == null) {
            return "";
        }
        //1.用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
        if (StringUtils.isNotEmpty(msgType) && msgType.equals(WeChatConstant.RESP_MESSAGE_TYPE_TEXT) &&
                StringUtils.isNotEmpty(msgContent) && StringUtils.isNotEmpty(fromUserName) &&
                StringUtils.isNotEmpty(wechatAccount)) {
            String actId = matchKeyWord(msgContent, wechatAccount);
            if (actId != null &&
                    checkActiveAvailable(actId)) {//判读活动是否有效
                insertWechatUserInfo(wechatEventMap);
                return keyWordReply(wechatEventMap);
            } else {//发送活动已经结束的消息
                Map<String, String> msgReply = new HashMap<String, String>();
                msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
                msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
                msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, "对不起，活动不存在或者本次活动已结束！");
                WeChatUtil.sendCustomMsg(msgReply, accessToken);
            }
        }
        String eventKey = wechatEventMap.get(WeChatConstant.EventKey);

        String event = wechatEventMap.get(WeChatConstant.Event);
        //3.用户关注公众号两种形式,具体如下：
        //     1.搜索公众号直接进行关注
        //    2.通过带参数二维进行关注
        if (StringUtils.isNoneEmpty(msgType) && "event".equals(msgType) &&
                event.equals(WeChatConstant.EVENT_TYPE_SUBSCRIBE) &&
                StringUtils.isNoneEmpty(eventKey)) { //二维码关注
            logger.info("----------------扫二维码进入------------------------");
            //新增用户信息
            insertWechatUserInfo(wechatEventMap);
            logger.info("----------------插入新增用户信息------------------------");
            logger.info(String.format("------------EventKey%s-------------------",eventKey));
            //增加助力详细表
            String eventKeyValue = wechatEventMap.get(WeChatConstant.EventKey); //得到传参的信息
            String helpOpenId = eventKeyValue.split("&&")[0];
            logger.info(String.format("----------助力的helpid:%s----------",helpOpenId));
            helpOpenId = helpOpenId.substring(helpOpenId.indexOf("_") + 1);//第一个下划线后面的就是openId
            String actId = eventKeyValue.split("&&")[1];
            if (!checkActiveAvailable(actId)) {
                Map<String, String> msgReply = new HashMap<String, String>();
                msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
                msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
                msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, "对不起，本次活动已结束！");
                WeChatUtil.sendCustomMsg(msgReply, accessToken);
                return "";
            }
            //调用活动助力表信息，需要调用两次

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
                logger.info("---------------老用户------------");
                actHelpDetailId = actHelpDetailService.insertActHelpDetailEntity(helpId, 1, 1, actId, fromUserName);
                MatActivityEntity matActivityEntity = activityService.getMatActivityEntityByActId(actId);
                wechatEventMap.put(ActivityService.KEY_shareCoypwritting,
                        matActivityEntity.getActShareCopywriting());
                //需要调用生成海报接口
                wechatEventMap.put(ActivityService.KEY_ACT_ID,actId);
                wechatEventMap.put(ActivityService.KEY_PIC_ID,matActivityEntity.getPictureId());
                activityReply(wechatEventMap);
                String processStatus = trackActive(helpOpenId, actHelpDetailId, actId, accessToken);
            }

        } else if (StringUtils.isNoneEmpty(msgType) && "event".equals(msgType) && event.equals(WeChatConstant.EVENT_TYPE_SUBSCRIBE) && "".equals(eventKey)) { //搜索直接关注
            //新增用户信息
            insertWechatUserInfo(wechatEventMap);
            wechatEventMap.put(WeChatConstant.Content, "谢谢您的关注！");
            return WeChatUtil.sendTextMsg(wechatEventMap);
        }

        //4. 用户取消公众号关注

        if (StringUtils.isNoneEmpty(msgType) && "event".equals(msgType) && event.equals(WeChatConstant.EVENT_TYPE_UNSUBSCRIBE)) {
            wechatUserInfoService.updateSubscribe(wechatEventMap.get(WeChatConstant.ToUserName), wechatEventMap.get(WeChatConstant.FromUserName), 0);
        }
        return "success";

    }

    private String getRewardInfo(ActivityPrizeMappingEntity prizeMappingEntity){
        String prizeMsg="";
        if(prizeMappingEntity == null){
            return null;
        }
        String actId = prizeMappingEntity.getActId();
        MatActivityEntity activityEntity = activityService.getMatActivityEntityByActId(actId);
        RewardTypeEnum rewardType = null;
        for(int i=0; i<=RewardTypeEnum.values().length - 1; i++){
            if(RewardTypeEnum.values()[i].getIndex()==activityEntity.getActType()){
                rewardType = RewardTypeEnum.values()[i];
            }
        }

        switch (rewardType){
            case TOKEN:
                  prizeMsg=String.format("口令：%s",prizeMappingEntity.getToken());
                  break;
            case SECURECODE:
                  prizeMsg=String.format("地址:%s,口令:%s",
                          prizeMappingEntity.getLinkUrl(),prizeMappingEntity.getToken());
        }
        return prizeMsg;
    }
    /**
     * 新增微信用户方法
     *
     * @param wechatEventMap
     */
    public void insertWechatUserInfo(Map<String, String> wechatEventMap) {
        WechatPublicEntity wechatPublicEntity = wechatPublicService.getEntityByWechatAccount(wechatEventMap.get(WeChatConstant.ToUserName));
        Map<String, String> userInfo = WeChatUtil.getUserInfo(wechatEventMap.get(WeChatConstant.FromUserName), wechatPublicEntity.getAppid(), wechatPublicEntity.getSecret());
        wechatUserInfoService.insertWechatUserInfoEntity(Integer.parseInt(userInfo.get("subscribe")), userInfo.get("openid"), userInfo.get("nickname"), Integer.parseInt(userInfo.get("sex")),
                userInfo.get("country"), userInfo.get("province"), userInfo.get("language"), userInfo.get("headimgurl"), userInfo.get("unionid"), userInfo.get("remark"),
                userInfo.get("subscribe_scene"), wechatEventMap.get(WeChatConstant.ToUserName), Integer.parseInt(userInfo.get("subscribe_time")), userInfo.get("city"), Integer.parseInt(userInfo.get("qr_scene")),
                userInfo.get("qr_scene_str"));
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
        String activityId = activityService.getMatActivityId(wechatAccount, keyWord, null);
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
    private String keyWordReply(Map<String, String> wechatEventMap) {
        long start = System.currentTimeMillis();
        String openId = wechatEventMap.get(WeChatConstant.FromUserName);
        String wechatAccount = wechatEventMap.get(WeChatConstant.ToUserName);
        String keyWord = wechatEventMap.get(WeChatConstant.Content);
        if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(wechatAccount)) {
            return "";
        }

        //获取粉丝个人信息存入到userPersonalInfoMap
        String access_token = getAccessToken(wechatAccount);
        Map<String, String> userPersonalInfoMap = WeChatUtil.getUserInfo(openId, access_token);
        long begin = System.currentTimeMillis();
        //获取带参数的二维码
        Map<String, Object> activitySimpleInfoMap =
                activityService.getMacActivitySimpleInfo(wechatAccount, keyWord, null);
        WeChatUtil.log(logger,begin,"getMacActivitySimpleInfo");
        if (activitySimpleInfoMap == null) {
            return "";
        }

        String activityId = activitySimpleInfoMap.get(ActivityService.KEY_ACT_ID).toString();
        StringBuilder sceneStrBuilder = new StringBuilder();
        sceneStrBuilder.append(openId).append(TaskBabyConstant.SEPERATOR_QRSCEAN).append(activityId);
        begin=System.currentTimeMillis();

        //将活动的原始海报的url放入到userPersonalInfoMap中
        begin = System.currentTimeMillis();
        String picId = activitySimpleInfoMap.get(ActivityService.KEY_PIC_ID).toString();
        String posterUrl = sysPictureService.getPictureURL(picId);
        WeChatUtil.log(logger,begin,"getPictureURL");
        userPersonalInfoMap.put(TaskBabyConstant.KEY_POSTER_URL, posterUrl);

        userPersonalInfoMap.put(WeChatConstant.Reply_ToUserName, openId);
        userPersonalInfoMap.put(WeChatConstant.Reply_FromUserName, wechatAccount);
        WeChatUtil.log(logger,start, "回复消息前的准备工作");

        //回复信息
        Map<String, String> replyMap = new HashMap<String, String>();

        replyMap.put(KEY_CSMSG_TOUSER, openId);
        Object shareCoppywritting = activitySimpleInfoMap.get(ActivityService.KEY_shareCoypwritting);
        if (shareCoppywritting != null) {//发送活动介绍
            logger.info("--------发送活动介绍-----------");
            begin = System.currentTimeMillis();
            replyMap.put(KEY_CSMSG_CONTENT, shareCoppywritting.toString());
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
            WeChatUtil.sendCustomMsg(replyMap, access_token);
            WeChatUtil.log(logger,start,"回复文字信息全部动作");
        }
        logger.info("--------发送图片中。。。。。。----------------");
        begin = System.currentTimeMillis();
        String qrCodeUrl = WeChatUtil.getWXPublicQRCode(WeChatUtil.QR_TYPE_TEMPORARY,
                WeChatUtil.QR_MAX_EXPIREDTIME, WeChatUtil.QR_SCENE_NAME_STR, sceneStrBuilder.toString(), access_token);
         long urlbegin = System.currentTimeMillis();
        WeChatUtil.log(logger,urlbegin,"获取带参数的二维码的url");
        //将二维码url put到userPersonalInfoMap中
        userPersonalInfoMap.put(TaskBabyConstant.KEY_QRCODE_URL, qrCodeUrl);

        //得到合成图片的filePath
        String customizedPosterPath = posterService.getCombinedCustomiedPosterFilePath(userPersonalInfoMap);
        WeChatUtil.log(logger,begin,"3张图片合成");
        if ( customizedPosterPath!= null) {//发送个性化海报
            replyMap.put(KEY_FILE_PATH, customizedPosterPath);
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_IMG);
            begin = System.currentTimeMillis();
            WeChatUtil.sendCustomMsg(replyMap, access_token);
            WeChatUtil.log(logger,begin,"发送图片全部动作");
        }
        logger.info("--------插入粉丝加入活动的数据---------------");
        joinActivity(wechatAccount, openId, activityId);
        return "success";
    }

    private String getAccessToken(String wechatAccount) {
        logger.debug("----------获取AccessToken-------");
        long begin = System.currentTimeMillis();
        WechatPublicEntity wechatEntity =
                wechatPublicService.getEntityByWechatAccount(wechatAccount);

        if (wechatEntity != null) {
            String appId = wechatEntity.getAppid();
            String secretCode = wechatEntity.getSecret();
            String accessToken = WXUtil.getAccessToken(appId, secretCode).getString("access_token");
            WeChatUtil.log(logger,begin,"生成AccessToken");
            return accessToken;
        } else {
            return null;
        }


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
    private String activityReply(Map<String, String> wechatEventMap) {
        String openId = wechatEventMap.get(WeChatConstant.FromUserName);
        String wechatAccount = wechatEventMap.get(WeChatConstant.ToUserName);
        if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(wechatAccount)) {
            return "";
        }
        WechatPublicEntity wechatPublicEntity = wechatPublicService.getEntityByWechatAccount(wechatAccount);
        if (wechatPublicEntity == null) {
            return "";
        }
        String accessToken = getAccessToken(wechatAccount);
        if (accessToken == null) {
            return "";
        }
        //获取粉丝个人信息存入到userPersonalInfoMap
        Map<String, String> userPersonalInfoMap = WeChatUtil.getUserInfo(openId, accessToken);
        String activityId = wechatEventMap.get(ActivityService.KEY_ACT_ID).toString();


        userPersonalInfoMap.put(WeChatConstant.Reply_ToUserName, openId);
        userPersonalInfoMap.put(WeChatConstant.Reply_FromUserName, wechatAccount);

        //回复信息
        Map<String, String> replyMap = new HashMap<String, String>();
        logger.info("发送文本中。。。");
        replyMap.put(KEY_CSMSG_TOUSER, openId);
        Object shareCoppywritting = wechatEventMap.get(ActivityService.KEY_shareCoypwritting);
        if (shareCoppywritting != null) {//发送活动介绍
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
        String picId = wechatEventMap.get(ActivityService.KEY_PIC_ID).toString();
        String posterUrl = sysPictureService.getPictureURL(picId);
        userPersonalInfoMap.put(TaskBabyConstant.KEY_POSTER_URL, posterUrl);
        //得到合成图片的filePath
       String customizedPosterPath = posterService.getCombinedCustomiedPosterFilePath(userPersonalInfoMap);

        if (customizedPosterPath != null) {//发送个性化海报
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
    private String trackActive(String touser, String helpDetailId, String activityId, String access_token) {
        String processStatus = ACTIVITY_HELP_PROCESS_INPROCESS;
        String msgTemplate = "收到%s的助力，还差%d人完成助力";
        String msgSuccessTemplate = "收到%s的助力,%s,%s";
        String msg = "";
        Map<String, Object> trackResult = activityTrackerService.getTrackInfo(helpDetailId, activityId);
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
                            activityEntity.getWechatAccount(), 1,prizeMappingEntity.getPrizeId());
                    //发送奖品消息
                    if (StringUtils.isNotEmpty(rewardInfo)) {
                        msg = String.format(msgSuccessTemplate,
                                trackResult.get(WeChatConstant.KEY_NICKNAME).toString(),
                                activityEntity.getRewardUrl(),rewardInfo);
                    }
                } else {
                    processStatus = ACTIVITY_HELP_PROCESS_EXCEEDS;
                    actHelpDetailService.updateActHelpDetailEntity(helpDetailId,0,1);
                }
            }
            //回复信息
            if (StringUtils.isNotEmpty(msg)) {
                Map<String, String> replyMap = new HashMap<String, String>();
                replyMap.put(KEY_CSMSG_TOUSER, touser);
                replyMap.put(KEY_CSMSG_CONTENT, msg);
                replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
                WeChatUtil.sendCustomMsg(replyMap, access_token);
            }

        }
        return processStatus;
    }


    /**
     * @param wechatAccount 微信公众号的账号id
     * @param fansOpenId    粉丝的OpenId
     * @return
     */
    private boolean fansSubScribe(String wechatAccount, String fansOpenId) {
        Map<String, String> userInfoMap = null;//TODO: 调用微信api获取用户基本信息的接口，返回给userInfoMap；

        //TODO:根据userInfoMap 如果是新来的粉丝，调用数据库接口插入粉丝数据 如果是之前关注过的，就更新关注状态
        return true;

    }

    private boolean fansUnsubscribe(String wechatAccount, String fansOpenId) {
        //TODO:根据用户的openid和微信账号，将粉丝的关注状态置为未关注
        return true;
    }

    /**
     * TODO：活动被助力了
     *
     * @param wechatEventMap
     * @return
     */
    private int activityHelp(Map<String, String> wechatEventMap) {
        return 0;
    }

    /**
     * TODO：调用模版消息接口
     * 助力成功，助力中两种情况
     *
     * @param wechatEventMap
     * @param helpcount      被助力个数
     */
    private void sendActivtyStateMsg(Map<String, String> wechatEventMap, int helpcount) {
        //
    }

}

