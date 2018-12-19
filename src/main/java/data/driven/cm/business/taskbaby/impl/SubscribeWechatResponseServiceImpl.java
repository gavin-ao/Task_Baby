package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.*;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.MatActivityEntity;
import data.driven.cm.entity.taskbaby.MatActivityStatusEntity;
import data.driven.cm.util.FileUtil;
import data.driven.cm.util.QRCodeUtil;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static data.driven.cm.component.WeChatConstant.*;

/**
 * @program: Task_Baby
 * @description: 订阅号服务类
 * @author: Logan
 * @create: 2018-12-19 14:23
 **/
@Service
public class SubscribeWechatResponseServiceImpl implements SubscribeWeChatResponseService {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeWechatResponseServiceImpl.class);
    /**
     * 第三方平台Service
     */
    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private ActivityService activityService;


    /**
     * 海报处理服务
     */
    @Autowired
    private PosterService posterService;



    /**
     * 活动助力Service
     */
    @Autowired
    private ActivityHelpService activityHelpService;

    /**
     * 微信用户Service
     */
    @Autowired
    private WechatUserInfoService wechatUserInfoService;
    // TODO: 2018/12/19 autowired
    private SubscribeServiceMappingService subscribeServiceMappingService;

    /**
     * 图片信息Service
     */
    @Autowired
    private SysPictureService sysPictureService;
    @Value("${file.download.path}")
    private String downloadPath;
    /**
    * 接收微信的消息，然后派发出去
    * @author Logan 
    * @date 2018-12-19 14:26
    * @param wechatEventMap
    * @param appId
    
    * @return 
    */       
    @Override
    public String notify(HttpServletRequest request, Map wechatEventMap, String appId) {
        return dispatherAndReturn(request,wechatEventMap, appId);
    }

    private String dispatherAndReturn(HttpServletRequest request, Map<String, String> wechatEventMap, String appid){
        logger.info(" ----------- 消息分发  appid " + appid);
        String accessToken = getAccessToken(appid);
        logger.info(" ----------- 消息分发  accessToken " + accessToken);
        if (accessToken == null) {
            return "";
        }
        //1.用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
        if (textEvent(wechatEventMap)) {
            return sendKeyCustomMsg(request,appid, wechatEventMap, accessToken);
        }


        return "";
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
  * 用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
  * @author Logan
  * @date 2018-12-19 14:31
  * @param appid
  * @param wechatEventMap
  * @param accessToken

  * @return
  */
    private String sendKeyCustomMsg(HttpServletRequest request,String appid, Map<String, String> wechatEventMap, String accessToken) {
        String wechatAccount = getWechatAccount(wechatEventMap);
        String msgContent = getMsgContent(wechatEventMap);
        String fromUserName = getFromUserName(wechatEventMap);
        String actId = matchKeyWord(msgContent, wechatAccount);
        //判断活动是否有效
        if (actId != null &&
                checkActiveAvailable(actId)) {
            insertWechatUserInfo(wechatEventMap, appid, actId);
            return keyWordReplyPoster(request,wechatEventMap, appid);
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
     * 查看活动是否有效
     * 1。活动状态字段是否有效，2。时间是否过期
     * 查看当前微信公众号下是否有激活的活动
     *
     * @author: Logan
     * @date:  2018-12-19  14:32
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
     * 新增微信用户方法
     *
     * @param wechatEventMap
     */
    public void insertWechatUserInfo(Map<String, String> wechatEventMap, String appid, String actId) {

        try {
            logger.info(" ----------- 进入 新增用户 appid " + appid);
            String accessToken = thirdPartyService.getAuthAccessToken(appid);
            logger.info(" ----------- 进入 新增用户 accessToken " + accessToken);

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
     * @param wechatEventMap
     * @param appId
     * @return
     * @description 根据关键字回复海报
     * @author Logan
     * @date 2018-12-10 14:51
     */
    private String keyWordReplyPoster(HttpServletRequest request,Map<String, String> wechatEventMap, String appId) {
        logger.info("---------wechatEventMap----------");
        logger.info(wechatEventMap.toString());
        String openId = getFromUserName(wechatEventMap);
        String wechatAccount = getWechatAccount(wechatEventMap);
        String keyWord = getMsgContent(wechatEventMap);
        if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(wechatAccount)) {
            return "";
        }
        String activityId = activityService.getMatActivityId(wechatAccount, keyWord);
        //发送海报
        introduceActivity(request,wechatEventMap, appId, activityId);
        //扫码者自动加入活动
        joinActivity(wechatAccount, openId, activityId, 1);
        return "success";
    }

    private String createAuthQRCode(HttpServletRequest request,String subscribeAppId,String subscribeWechatAccount,String actid){

        //绑定的服务号appid
        String serviceWechatAppId =
                subscribeServiceMappingService.getServiceWechatAppId(subscribeAppId);
        Map<String,String> userInfo = WeChatUtil.getUserInfo(subscribeWechatAccount,getAccessToken(subscribeAppId));
        String unionId = userInfo.get("unionid");
        String url = WeChatUtil.getWebPageAuthUrl(request,serviceWechatAppId,unionId,actid);
        StringBuilder fileNameStrBuilder = new StringBuilder(downloadPath);
        fileNameStrBuilder.append(File.separator).append("temp").append(File.separator);
        File file = new File(fileNameStrBuilder.toString());
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            QRCodeUtil.createQRCode(url,
                    fileNameStrBuilder.append(UUIDUtil.getUUID()).toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        return fileNameStrBuilder.append(".jpg").toString();
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
    private Map<String, String> introduceActivity(HttpServletRequest request, Map<String, String> wechatEventMap, String appId, String activityId) {
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
        //生成临时二维码
        String qrCodeTempFileName = createAuthQRCode(request,appId,wechatAccount,activityId);
        //将二维码url put到userPersonalInfoMap中
        userPersonalInfoMap.put(TaskBabyConstant.KEY_QRCODE_URL, qrCodeTempFileName);

        //得到合 成图片的filePath
        String customizedPosterPath = getCustomizedPosterPath(userPersonalInfoMap,activityId,openId);

        // 合成海报之后，删除二维码原始图片
        FileUtil.deleteFile(qrCodeTempFileName);
        //发送个性化海报
        if (customizedPosterPath != null) {
            replyMap.put(KEY_FILE_PATH, customizedPosterPath);
            replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_IMG);
            WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));
        }
        return userPersonalInfoMap;
    }
    /**
    * 把用户的合成海报的地址放缓存里面，优先从缓存里面取
    * @author Logan
    * @date 2018-12-19 17:26
    * @param userInfoMap
    * @param activityId
    * @param openId

    * @return
    */
    private String getCustomizedPosterPath(Map<String,String> userInfoMap,String activityId,String openId){
        String cacheKey = getCustomizedPosterPath(activityId,openId);
        boolean reCreate = false;
        String customizedPosterPath = RedisFactory.get(cacheKey);
        if(StringUtils.isEmpty(customizedPosterPath)){
            reCreate = true;
        }else{
            File file = new File(customizedPosterPath);
            if(file.exists()){
                reCreate = false;
            }else{
               reCreate = true;
            }
        }

        if(reCreate){
            //得到合 成图片的filePath
            customizedPosterPath = posterService.getCombinedCustomiedPosterFilePath(userInfoMap);
            //缓存个性化海报图片
            cacheCustomizedPosterPath(activityId,openId,customizedPosterPath);
        }
        return customizedPosterPath;
    }
    private String getCustomizedPosterPath(String activityId,String openId){
          return String.format("customizedPosterPath-%s-%s",activityId,openId);
    }

    private void cacheCustomizedPosterPath(String activityId,String openId,String customizedPosterPath){
        String key =getCustomizedPosterPath(activityId,openId);
        //缓存10天
        long expired = 10*24*60*60*1000;
        RedisFactory.setString(key,customizedPosterPath,expired);
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
}
