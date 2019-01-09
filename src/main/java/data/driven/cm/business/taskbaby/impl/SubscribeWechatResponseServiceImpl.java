package data.driven.cm.business.taskbaby.impl;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskbaby.*;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.component.RewardTypeEnum;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.*;
import data.driven.cm.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

import static com.alibaba.fastjson.JSON.parseObject;
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
    private static final String EVENT_MAP_KEY_MASTER_UNIONID = "masterUnionId";
    private static final String EVENT_MAP_KEY_DETAIL_UNIONID = "detailUnionId";
    /**
     * 第三方平台Service
     */
    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private ActivityService activityService;


    @Autowired
    private ActivityTrackerService activityTrackerService;
    /**
     * 海报处理服务
     */
    @Autowired
    private PosterService posterService;
    @Autowired
    private UnionidUserMappingService unionidUserMappingService;


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

    @Autowired
    private SubscribeServiceMappingService subscribeServiceMappingService;
    /**
     * 活动助力详细表
     */
    @Autowired
    private ActHelpDetailService actHelpDetailService;

    /**
     * 图片信息Service
     */
    @Autowired
    private SysPictureService sysPictureService;

    @Autowired
    private ActivityPrizeMappingService prizeMappingService;

    /**
     * 活动奖励表Service
     */
    @Autowired
    private ActivityRewardService activityRewardService;
    /**
     * 客服配置Service
     */
    @Autowired
    private CustomerConfigureService customerConfigureService;

    /**
     * 客户服务
     */
    @Autowired
    private CustomerService customerService;

    /**
     * 公众号详细信息表Service
     */
    @Autowired
    private WechatPublicDetailService wechatPublicDetailService;


    @Value("${file.download.path}")
    private String downloadPath;

    @Value("${rootUrl}")
    private String rootUrl;

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
     * 接收微信的消息，然后派发出去
     *
     * @param wechatEventMap
     * @param appId
     * @return
     * @author Logan
     * @date 2018-12-19 14:26
     */
    @Override
    public String notify(Map wechatEventMap, String appId) {
        return dispatherAndReturn(wechatEventMap, appId);
    }

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
        //直接关注
        if (subscribeEvent(wechatEventMap)) {
            ActHelpEntity helpEntity = findHelpEntityOfNoneHelp(wechatEventMap, appid);
            if (helpEntity != null) {
                //是助力之后的关注
                Map<String, String> fakeQrCodeSubscribeEventMap =
                        makeQRCodeSubscribeEvent(wechatEventMap, helpEntity.getActId(), helpEntity.getFansId());
                return helpProcess(fakeQrCodeSubscribeEventMap, appid);
            } else {
                //是普通的关注
                //新增用户信息
                logger.info(" ----------- 搜索直接关注 ");
                insertWechatUserInfo(wechatEventMap, appid, null);
//                return customerService.sendFollowMsg(appid, wechatEventMap, accessToken);
                wechatEventMap.put(WeChatConstant.CONTENT, "谢谢您的关注！");
                return WeChatUtil.sendTextMsg(wechatEventMap);
            }
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
     * @param fromUserName 微信用户openid
     * @param appid        公众号appid
     * @return
     * @description 判断微信用户是否已接收到回复消息，一小时内只发一次回复消息
     * @author lxl
     * @date 2019-01-09 11:24
     */
    private boolean getReceiveCustomerNews(String fromUserName, String appid) {
        String key = appid + fromUserName + "Customer";
        //1.从缓存中取
        String receiveCustomerNews = RedisFactory.get(key);
        if (StringUtils.isNotEmpty(receiveCustomerNews)) {
            return false;
        } else {
            RedisFactory.setString( key, "existence", 60 * 60 * 1000);
            return true;
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
     * @param wechatEventMap 微信推送的信息转成map
     * @return
     * @description 自定义菜单点击事件
     * @author lxl
     * @date 2019-01-08 14:47
     */
    private boolean clickEvent(Map<String, String> wechatEventMap) {
        String msgType = getMsgType(wechatEventMap);
        String event = getEvent(wechatEventMap);
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && StringUtils.isNotEmpty(eventKey) && "CLICK".equals(event)
                && "Customer_Service_Click".equals(eventKey)) {
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
     *
     * @param appid
     * @param wechatEventMap
     * @param accessToken
     * @return
     * @author Logan
     * @date 2018-12-19 14:31
     */
    private String sendKeyCustomMsg(String appid, Map<String, String> wechatEventMap, String accessToken) {
        String wechatAccount = getWechatAccount(wechatEventMap);
        String msgContent = getMsgContent(wechatEventMap);
        String fromUserName = getFromUserName(wechatEventMap);
        String actId = matchKeyWord(msgContent, wechatAccount);
        //判断活动是否有效
        if (actId != null &&
                checkActiveAvailable(actId)) {
            ActHelpEntity helpEntity =
                    findHelpEntityOfNoneHelp(wechatEventMap, actId, appid);
            if (helpEntity != null) {
                //是助力扫码之后的回复关键字,模拟服务号的老用户扫码事件
                Map<String, String> fakeFansScanQREvent =
                        makeQRCodeScanEvent(wechatEventMap, actId, helpEntity.getFansId());
                helpProcess(fakeFansScanQREvent, appid);
            } else {
                insertWechatUserInfo(wechatEventMap, appid, actId);
                //单纯的发关键字参加活动
                return keyWordReplyPoster(wechatEventMap, appid);
            }

        } else {
//            Map<String, String> msgReply = new HashMap<>();
//            msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
//            msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
//            String msg = null;
//            if (activityService.keyWordExist(msgContent, wechatAccount)) {
//                msg = "很抱歉，您参加的活动已经结束，请持续关注我们，更多精彩的活动马上就来~";
//            } else {
//                msg = "没找到您想要的活动，请持续关注我们，更多活动马上就来~";
//            }
//            msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, msg);
//            WeChatUtil.sendCustomMsg(msgReply, accessToken);
            //判断微信用户是否已接收到回复消息，一小时内只发一次回复消息
            String nickName = wechatPublicDetailService.getNickNameByAppId(appid);
            if (getReceiveCustomerNews(fromUserName,appid)){
                Map<String, String> msgReply = new HashMap<>();
                msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
                msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
                List<CustomerConfigureEntity> customerConfigureEntities = customerConfigureService.getCustomerConfigureEntites(appid);
                StringBuffer msg = new StringBuffer();
                if (customerConfigureEntities.size() > 0) {
                    msg.append("欢迎关注"+nickName+"~");
                    for (CustomerConfigureEntity customerConfigureEntity : customerConfigureEntities) {
                        msg.append(customerConfigureEntity.getDescribe() + "\n");
                    }
                    msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, msg.toString());
                    WeChatUtil.sendCustomMsg(msgReply, accessToken);
                }
            }
            customerService.call(wechatEventMap, appid);
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
     * @date: 2018-12-19  14:32
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
    private String keyWordReplyPoster(Map<String, String> wechatEventMap, String appId) {
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
        introduceActivity(wechatEventMap, appId, activityId);
        //扫码者自动加入活动
        joinActivity(wechatAccount, openId, activityId, 1);
        return "success";
    }

    private String createAuthQRCode(String fromOpenId, String subscribeAppId, String subscribeWechatAccount, String actid) {

        //绑定的服务号appid
        String serviceWechatAppId =
                subscribeServiceMappingService.getServiceWechatAppId(subscribeWechatAccount);
        Map<String, String> userInfo = WeChatUtil.getUserInfo(fromOpenId, getAccessToken(subscribeAppId));
        String unionId = userInfo.get("unionid");
        String url = WeChatUtil.getWebPageAuthUrl(rootUrl, serviceWechatAppId, subscribeWechatAccount, unionId, actid);
        StringBuilder fileNameStrBuilder = new StringBuilder(downloadPath);
        fileNameStrBuilder.append(File.separator).append("temp").append(File.separator);
        File file = new File(fileNameStrBuilder.toString());
        if (!file.exists()) {
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
        //生成临时二维码
        String qrCodeTempFileName = createAuthQRCode(openId, appId, wechatAccount, activityId);
        //将二维码url put到userPersonalInfoMap中
        userPersonalInfoMap.put(TaskBabyConstant.KEY_QRCODE_URL, qrCodeTempFileName);

        //得到合 成图片的filePath
        String customizedPosterPath = getCustomizedPosterPath(userPersonalInfoMap, activityId, openId);

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
     *
     * @param userInfoMap
     * @param activityId
     * @param openId
     * @return
     * @author Logan
     * @date 2018-12-19 17:26
     */
    private String getCustomizedPosterPath(Map<String, String> userInfoMap, String activityId, String openId) {
        String cacheKey = getCustomizedPosterPathKey(activityId, openId);
        boolean reCreate = false;
        String customizedPosterPath = RedisFactory.get(cacheKey);
        if (StringUtils.isEmpty(customizedPosterPath)) {
            logger.info("--------海报路径缓存没有--------------");
            reCreate = true;
        } else {
            File file = new File(customizedPosterPath);
            if (file.exists()) {
                logger.info("--------海报路径缓存有,并且文件存在--------------");
                reCreate = false;
            } else {
                logger.info("--------海报路径缓存有,并且文件不存在--------------");
                reCreate = true;
            }
        }

        if (reCreate) {
            //得到合 成图片的filePathx
            StringBuilder outputFile = new StringBuilder(downloadPath);
            outputFile.append(File.separator).append("subscribePoster").append(File.separator).append(UUIDUtil.getUUID()).append(".jpg");
            customizedPosterPath = outputFile.toString();
            logger.info(String.format("--------重新生成海报文件,路径:%s--------------", customizedPosterPath));
            posterService.combinedCustomizedPosterFilePath(customizedPosterPath, userInfoMap);
            //缓存个性化海报图片
            cacheCustomizedPosterPath(activityId, openId, customizedPosterPath);
        }
        return customizedPosterPath;
    }

    private String getCustomizedPosterPathKey(String activityId, String openId) {
        return String.format("customizedPosterPath-%s-%s", activityId, openId);
    }

    private void cacheCustomizedPosterPath(String activityId, String openId, String customizedPosterPath) {
        String key = getCustomizedPosterPathKey(activityId, openId);
        //缓存60秒
        long expired = 60 * 1000;
        RedisFactory.setString(key, customizedPosterPath, expired);
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
     * @param code  code作为换取access_token的票据
     * @param appid 服务号的appid
     * @return
     * @description 获取用户unionid
     * @author lxl
     * @date 2018-12-19 15:43
     */
    @Override
    public String getCodeByUnionid(String code, String appid) {
        //第一步通过code获取用户的access_token  start
//        String getUserAccessTokenURL = WeChatConstant.getUserAccessTokenURL(appid,code, WeChatUtil.getComponentAccessToken());
//        String userAccessTokenResultStr = HttpUtil.doGetSSL(getUserAccessTokenURL);
//        JSONObject userAccessTokenResultJson = parseObject(userAccessTokenResultStr);
        String getUserAccessToken = WeChatConstant.getUserAccessTokenURL(appid, code, WeChatUtil.getComponentAccessToken());
        //第一步通过code获取用户的access_token  end
        JSONObject userAccessTokenResultJson = parseObject(getUserAccessToken);
        if (userAccessTokenResultJson.containsKey("errcode")) {
            logger.info("获取用户的access_token失败 " + userAccessTokenResultJson.toString());
        } else {
            String getUserInfoURL = WeChatConstant.getUserInfoURL(userAccessTokenResultJson.getString("access_token"), userAccessTokenResultJson.getString("openid"));
            String userInfoResultStr = HttpUtil.doGetSSL(getUserInfoURL);
            JSONObject userInfoResultJson = parseObject(userInfoResultStr);
            if (userInfoResultJson.containsKey("errcode")) {
                logger.info("获取用户的unionid失败 " + userInfoResultJson.toString());
            } else {
                return userInfoResultJson.getString("unionid");
            }
        }
        return null;
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
        String event = getEvent(wechatEventMap);
        logger.info(String.format("----------EventKey:%s---------", eventKey));
        logger.info(String.format("----------wechatEventMap:%s---------", wechatEventMap));
        if (StringUtils.isNotEmpty(eventKey)) {
            String[] keyArr = eventKey.split("&&");
            if (keyArr.length > 0) {
                if (event.equals(WeChatConstant.EVENT_TYPE_SCAN)) {
                    return keyArr[0];
                } else {
                    return keyArr[0].substring(keyArr[0].indexOf("_") + 1);
                }

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
     * @param activityId
     * @param openIdWhoScan
     * @param appId
     * @return
     * @description 判断是否已经给他人助力过，如果已经给他人助力则发送提示
     * @author Logan
     * @date 2018-12-10 16:05
     */
    private boolean alreadyHelpSomeone(String activityId, String openIdWhoScan, String appId) {
        logger.info("---------判断是否已经助力--------");
        logger.info(String.format(
                "-------------activityId:%s,openIdWhoScan:%s", activityId, openIdWhoScan));
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


    /**
     * @param wechatEventMap
     * @return
     * @description 老粉丝扫描带参数二维码事件
     * @author Logan
     * @date 2018-12-10 12:43
     */
    private boolean fansScanQrCodeEvent(Map<String, String> wechatEventMap) {
        logger.info(String.format("-----------wechatEventMap:%s----------------------", wechatEventMap.toString()));
        String msgType = getMsgType(wechatEventMap);
        String event = getEvent(wechatEventMap);
        String eventKey = getEventKey(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && WeChatConstant.REQ_MESSAGE_TYPE_EVENT.equals(msgType) &&
                StringUtils.isNotEmpty(event) && WeChatConstant.EVENT_TYPE_SCAN.equals(event) && StringUtils.isNotEmpty(eventKey)) {
            logger.info("---------------是老用户扫描二维码事件---------------");
            return true;
        } else {
            logger.info("---------------不是老用户扫描二维码事件---------------");
            return false;
        }
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
                prizeMsg = prizeMappingEntity.getToken();
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
     * @author: Logan
     * @date: 2018/11/19 15:43
     * @params: [touser, helpDetailId, activityId, access_token]
     * @return: 如果助力跟踪状态，success，inProcess，exceeded；
     **/
    private String trackActive(String touser, String helpDetailId, String activityId, String accessToken) {
        String processStatus = ACTIVITY_HELP_PROCESS_INPROCESS;
        String msgTemplate = "收到%s的助力，还差%d人完成助力";
        String msgSuccessTemplate = "收到%s的助力，%s%s";
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
                    logger.info(String.format("-----------活动跟踪信息:%s----", msg));
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
     * @param openIdOfScene 带参数二维码上的openID，即被助力的openId
     * @param appId
     * @return
     * @description 发送给某人助力成功的提示消息
     * @author Logan
     * @date 2018-12-10 15:54
     */
    private void sendHelpSuccessMsg(String openIdOfScene, String openIdWhoScan, String appId) {
        Map<String, String> userHelpPersonalInfoMap = WeChatUtil.getUserInfo(openIdOfScene, getAccessToken(appId));
        String msgHelpTemplate = "您已经成功为好友%s助力一次";
        String msg = String.format(msgHelpTemplate,
                userHelpPersonalInfoMap.get(WeChatConstant.KEY_NICKNAME));
        Map<String, String> replyMap = new HashMap<String, String>();

        replyMap.put(KEY_CSMSG_TOUSER, openIdWhoScan);
        replyMap.put(KEY_CSMSG_CONTENT, msg);
        replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
        WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));

    }

    /**
     * 助力后,根据助力结果标记助力关系状态,
     * 更新unionid_user_mapping的status [1:助力成功,2:助力失败]
     *
     * @param wechatEventMap
     * @param actId          活动id
     * @param masterOpenId   被助力者openId
     * @param detailOpenId   助力者openId
     * @param status         [0:初始状态,1:助力成功,2:助力失败]
     * @return
     * @author Logan
     * @date 2018-12-24 14:34
     */
    private void markRelationShipStatus(Map<String, String> wechatEventMap, String appId, String actId, String masterOpenId, String detailOpenId, int status) {
        logger.info(String.format("------------给助力关系表unionMapping表打上标志:%d--------------", status));
        String masterUnionId = wechatEventMap.get(EVENT_MAP_KEY_MASTER_UNIONID);
        String detailUnionId = wechatEventMap.get(EVENT_MAP_KEY_DETAIL_UNIONID);
        if (StringUtils.isEmpty(masterOpenId)) {
            Map<String, String> masterInfo = WeChatUtil.getUserInfo(masterOpenId, getAccessToken(appId));
            masterUnionId = masterInfo.get(WeChatConstant.API_JSON_KEY_UNIONID);
        }
        if (StringUtils.isEmpty(detailUnionId)) {
            Map<String, String> detailInfo = WeChatUtil.getUserInfo(detailOpenId, getAccessToken(appId));
            detailUnionId = detailInfo.get(WeChatConstant.API_JSON_KEY_UNIONID);
        }
        if (StringUtils.isNotEmpty(masterUnionId) && StringUtils.isNotEmpty(detailUnionId)) {
            unionidUserMappingService.updateStatus(status, actId, masterUnionId, detailUnionId);
        }
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
        //扫码人自己收到一个助力成功的提示
        logger.info("--------助力成功，扫码人自己收到一个助力成功的提--------");
        sendHelpSuccessMsg(openIdOfScene, openIdWhoScan, appId);
        //标记助力关系表unionMapping表,已经助力
        markRelationShipStatus(wechatEventMap, appId, activityId, openIdOfScene, openIdWhoScan, 1);
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

    }

    private void oldFansHelp(Map<String, String> wechatEventMap, String appId) {
        if (fansScanQrCodeEvent(wechatEventMap)) {
            logger.info("-------------收到老粉丝助力-----------");
            String activityId = getActivityIdInQrSceneStr(wechatEventMap);
            String openIdOfScene = getOpenIdInQrSceneStr(wechatEventMap);
            String helpId = activityHelpService.getHelpId(openIdOfScene, activityId);
            //得到扫描者的openId
            String openIdWhoScan = getFromUserName(wechatEventMap);
            logger.info("--------插入助力明细---------");
            logger.info(String.format("--------HelpId:%s---------", helpId));
            logger.info(String.format("--------openIdOfScene:%s---------", openIdOfScene));
            String helpDetailId = actHelpDetailService.insertActHelpDetailEntity(helpId, 1, 0, activityId, openIdWhoScan);
            helpAction(wechatEventMap, helpDetailId, appId);
        }
    }


    private void newFansHelp(Map<String, String> wechatEventMap, String appId) {
        if (scanQrCodeAndSubscribeEvent(wechatEventMap)) {
            String activityId = getActivityIdInQrSceneStr(wechatEventMap);
            String openIdOfScene = getOpenIdInQrSceneStr(wechatEventMap);
            String openIdWhoScan = getFromUserName(wechatEventMap);
            String helpId = activityHelpService.getHelpId(openIdOfScene, activityId);
            logger.info("--------插入助力明细---------");
            String helpDetailId = actHelpDetailService.insertActHelpDetailEntity(helpId, 1, 1, activityId, openIdWhoScan);
            helpAction(wechatEventMap, helpDetailId, appId);
        }
    }


    /**
     * 助力流程类
     *
     * @param wechatEventMap
     * @param appid
     * @return
     * @author Logan
     * @date 2018-12-21 10:58
     */
    private String helpProcess(Map<String, String> wechatEventMap, String appid) {
        String openIdOfScene = getOpenIdInQrSceneStr(wechatEventMap);
        String activityId = getActivityIdInQrSceneStr(wechatEventMap);
        if (StringUtils.isEmpty(openIdOfScene) || StringUtils.isEmpty(activityId)) {
            logger.error("订阅号助力参数获取失败,没有openIdOfScene或activityId");
        }
        //新增用户信息
        insertWechatUserInfo(wechatEventMap, appid, activityId);
        //得到扫描者的openId
        String openIdWhoScan = getFromUserName(wechatEventMap);
        logger.info(String.format("---------------openIdOfScene:%s", openIdOfScene));
        logger.info(String.format("---------------openIdWhoScan:%s", openIdWhoScan));
        //扫描自己的海报
        if (openIdOfScene.equals(openIdWhoScan)) {
            logger.info("--------扫描自己的海报-----------");
            //给扫码者发送活动进度
            sendMyActivityStatus(wechatEventMap, appid);
        } else {
            //扫描他人的海报
            //判断活动是否过期
            if (!checkActiveAvailable(activityId)) {
                Map<String, String> msgReply = new HashMap<>();
                msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, openIdWhoScan);
                msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
                msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, "对不起，本次活动已结束！");
                WeChatUtil.sendCustomMsg(msgReply, getAccessToken(appid));
                return "";
            }
            logger.info("-----------扫描他人海报--------------------");
            if (alreadyHelpSomeone(activityId, openIdWhoScan, appid)) {
                //如果扫码人已经助力过，则直接返回
                //去掉之前的匹配关系
                //发送活动介绍
                logger.info("------提示已经助力他人后,发送活动介绍----------------");
                introduceActivity(wechatEventMap, appid, activityId);
                String wechatAccount = getWechatAccount(wechatEventMap);
                //扫码者自动加入活动
                logger.info("--------提示已经助力他人后,加入活动--------");
                joinActivity(wechatAccount, openIdWhoScan, activityId, 0);
                //标记助力关系表unionMapping表,助力失败
                markRelationShipStatus(wechatEventMap, appid, activityId, openIdOfScene, openIdWhoScan, 2);
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
     * @param wechatEventMap
     * @return
     * @description 发送我的活动进度
     * @author Logan
     * @date 2018-12-10 16:11
     */
    private void sendMyActivityStatus(Map<String, String> wechatEventMap, String appId) {
        String activityId = getActivityIdInQrSceneStr(wechatEventMap);
        String openIdWhoScan = getFromUserName(wechatEventMap);
        String helpId = activityHelpService.getHelpId(openIdWhoScan, activityId);
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
     * 找到待助力的act_help实体
     * 逻辑规则:1.获取当前粉丝的unionId,
     * 2.在unionid_user_mapping中,通过当前粉丝的unionid,actvitiId,匹配出被助力者的unioId
     * 3.拿被助力者的unioinId换成openId
     * 4.判断当前粉丝是否已经助力成功过,如果没有,返回act_Help的主表实体,否则要么是已经是助力过的,返回null
     *
     * @param wechatEventMap 微信推过来的消息Map
     * @param actId          活动id
     * @param appId          订阅号的appId
     * @return 活动助力主表记录 act_help的记录
     * @author Logan
     * @date 2018-12-21 10:20
     */
    private ActHelpEntity findHelpEntityOfNoneHelp(Map<String, String> wechatEventMap, String actId, String appId) {
        String openIdWhoInput = getFromUserName(wechatEventMap);
        String wechatAccount = getWechatAccount(wechatEventMap);
        Map<String, String> userInfoWhoInput = WeChatUtil.getUserInfo(openIdWhoInput, getAccessToken(appId));
        logger.info(String.format("-----------userInfoWhoInput:%s----------", userInfoWhoInput.toString()));
        String unionIdWhoInput = userInfoWhoInput.get(WeChatConstant.API_JSON_KEY_UNIONID);
        List<String> fromUnionList = unionidUserMappingService.getFormUnionIdList(actId, unionIdWhoInput);
        if (fromUnionList != null && fromUnionList.size() > 0) {
            //找到了扫码匹配的助力和被助力者unionid的多条记录,接下来匹配act_help
            ActHelpEntity helpEntity = null;
            for (String fromUnionId : fromUnionList) {
                String fromOpenId = wechatUserInfoService.getOpenId(wechatAccount, fromUnionId);
                helpEntity =
                        activityHelpService.getHelpEntityWithNoneHelpDetail(actId, fromOpenId, openIdWhoInput);
                logger.info(String.format("------------找到助力主笔记录:helpid:%s,fansId:%s-----------------------", helpEntity.getHelpId(), helpEntity.getFansId()));
                if (helpEntity != null) {
                    wechatEventMap.put(EVENT_MAP_KEY_MASTER_UNIONID, fromUnionId);
                    wechatEventMap.put(EVENT_MAP_KEY_DETAIL_UNIONID, unionIdWhoInput);
                    break;
                }
            }
            return helpEntity;

        } else {
            return null;
        }

    }

    /**
     * 找到待助力的act_help实体
     * 逻辑规则:1.获取当前粉丝的unionId,
     * 2.在unionid_user_mapping中,通过当前粉丝的unionid,actvitiId,匹配出被助力者的unioId
     * 3.拿被助力者的unioinId换成openId
     * 4.判断当前粉丝是否已经助力成功过,如果没有,返回act_Help的主表实体,否则要么是已经是助力过的,返回null
     *
     * @param wechatEventMap
     * @param appId
     * @return
     * @author Logan
     * @date 2018-12-21 16:14
     */
    private ActHelpEntity findHelpEntityOfNoneHelp(Map<String, String> wechatEventMap, String appId) {
        String openIdWhoSubscribe = getFromUserName(wechatEventMap);
        String wechatAccount = getWechatAccount(wechatEventMap);
        Map<String, String> userInfoWhoInput = WeChatUtil.getUserInfo(openIdWhoSubscribe, getAccessToken(appId));
        String unionIdWhoSubscribe = userInfoWhoInput.get(WeChatConstant.API_JSON_KEY_UNIONID);
        logger.info(String.format("----------userInfoWhoInput:%s-----------------", userInfoWhoInput));
        List<UnionidUserMappingEntity> unionidUserMappingList = unionidUserMappingService.getUnionidUserMappingList(wechatAccount, unionIdWhoSubscribe);

        if (unionidUserMappingList != null && unionidUserMappingList.size() > 0) {
            logger.info(String.format("----------找到匹配的unionid,size:%d-----------------------", unionidUserMappingList.size()));
            //找到了扫码匹配的助力和被助力者unionid的多条记录,接下来匹配act_help
            ActHelpEntity helpEntity = null;
            for (UnionidUserMappingEntity unionidUserMapping : unionidUserMappingList) {
                String fromOpenId = wechatUserInfoService.getOpenId(wechatAccount, unionidUserMapping.getFromUnionid());
                logger.info(String.format("-----------fromOpenId:%s----------------", fromOpenId));
                helpEntity =
                        activityHelpService.getHelpEntityWithNoneHelpDetail(unionidUserMapping.getActId(), fromOpenId, openIdWhoSubscribe);
                if (helpEntity != null) {
                    logger.info("----------找到了help主实体-----------");
                    wechatEventMap.put(EVENT_MAP_KEY_MASTER_UNIONID, unionidUserMapping.getFromUnionid());
                    wechatEventMap.put(EVENT_MAP_KEY_DETAIL_UNIONID, unionidUserMapping.getToUnionid());
                    break;
                }
            }
            return helpEntity;

        } else {
            logger.info("----------没找到help主实体-----------");
            return null;
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

    private String makeFakeSceneStr(boolean scanEvent, String openIdOfScene, String activityId) {
        StringBuilder sceneStrBuilder = new StringBuilder();
        if (scanEvent) {
            sceneStrBuilder.append(openIdOfScene).append(TaskBabyConstant.SEPERATOR_QRSCEAN).append(activityId);
        } else {
            sceneStrBuilder.append("qrscene_").append(openIdOfScene).append(TaskBabyConstant.SEPERATOR_QRSCEAN).append(activityId);
        }
        logger.info(String.format("----------------sceneStr:%s------------", sceneStrBuilder.toString()));
        return sceneStrBuilder.toString();
    }

    /**
     * 伪造一个老用户扫带参数二维码事件,因为是订阅号,没有扫码带参数二维码事件,所以伪造一个
     *
     * @param originWechatEventMap
     * @param activityId
     * @param openIdOfScene
     * @return
     * @author Logan
     * @date 2018-12-21 12:56
     */
    private Map<String, String> makeQRCodeScanEvent(Map<String, String> originWechatEventMap, String activityId, String openIdOfScene) {

        String sceneStr = makeFakeSceneStr(true, openIdOfScene, activityId);
        Map<String, String> qrCodeScanEventMap = new HashMap<String, String>();
        qrCodeScanEventMap.putAll(originWechatEventMap);
        qrCodeScanEventMap.put(WeChatConstant.EVENT, WeChatConstant.EVENT_TYPE_SCAN);
        qrCodeScanEventMap.put(WeChatConstant.MSG_TYPE, WeChatConstant.REQ_MESSAGE_TYPE_EVENT);
        qrCodeScanEventMap.put(WeChatConstant.EVENT_KEY, sceneStr);
        logger.info(String.format("------------qrCodeScanEventMap:%s------------", qrCodeScanEventMap.toString()));
        return qrCodeScanEventMap;
    }

    private Map<String, String> makeQRCodeSubscribeEvent(Map<String, String> originWechatEventMap, String activityId, String openIdOfScene) {
        String sceneStr = makeFakeSceneStr(false, openIdOfScene, activityId);
        Map<String, String> qrCodeSubscribeEventMap = new HashMap<String, String>();
        qrCodeSubscribeEventMap.putAll(originWechatEventMap);
        qrCodeSubscribeEventMap.put(WeChatConstant.EVENT, WeChatConstant.EVENT_TYPE_SUBSCRIBE);
        qrCodeSubscribeEventMap.put(WeChatConstant.MSG_TYPE, WeChatConstant.REQ_MESSAGE_TYPE_EVENT);
        qrCodeSubscribeEventMap.put(WeChatConstant.EVENT_KEY, sceneStr);
        logger.info(String.format("------------qrCodeSubscribeEventMap:%s------------", qrCodeSubscribeEventMap.toString()));
        return qrCodeSubscribeEventMap;
    }

    /**
     * @param appId      订阅号的appId
     * @param activityId 活动id
     * @param openIdWho  用户在订阅号中的openid
     * @return
     * @description 订阅号用户扫自己的二维码发送我的活动进度
     * @author lxl
     * @date 2018-12-21 10:51
     */
    @Override
    public void subscribeSendMyActivityStatus(String appId, String activityId, String openIdWho) {
        logger.info("进入给自己发送消息");
        String helpId = activityHelpService.getHelpId(openIdWho, activityId);
        Map<String, Integer> helpCountMap = activityTrackerService.getHelpCount(helpId, activityId);
        Integer remain = Integer.parseInt(helpCountMap.get("remain").toString());
        String msgUnSuccessTemplate = "已经有%s位好友成功为你助力，还需要%s位好友支持哟~";
        String msgSuccessTemplate = "您已经完成助力任务领取奖励，%s。%s";
        Map<String, String> replyMap = new HashMap<String, String>();
        replyMap.put(KEY_CSMSG_TOUSER, openIdWho);
        replyMap.put(KEY_CSMSG_TYPE, VALUE_CSMSG_TYPE_TEXT);
        String msg = "";
        if (remain > 0) {
            msg = String.format(msgUnSuccessTemplate,
                    Integer.parseInt(helpCountMap.get("help").toString()), remain > 0 ? remain : 0);
        } else {
            ActivityRewardEntity rewardEntity =
                    activityRewardService.getEntityByActIdAndWechatUserId(activityId, openIdWho);
            if (rewardEntity != null) {
                ActivityPrizeMappingEntity prizeMappingEntity =
                        prizeMappingService.getEntityByPrizeId(rewardEntity.getPrizeId());
                String rewardInfo = getRewardInfo(prizeMappingEntity);
                //发送奖品消息
                if (StringUtils.isNotEmpty(rewardInfo)) {
                    MatActivityEntity activityEntity =
                            activityService.getMatActivityEntityByActId(activityId);
                    msg = String.format(msgSuccessTemplate,
                            activityEntity.getRewardUrl(), rewardInfo.replaceAll("openid", openIdWho).replace("actId", activityId));
                }
            }
        }
        //回复信息
        replyMap.put(KEY_CSMSG_CONTENT, msg);
        WeChatUtil.sendCustomMsg(replyMap, getAccessToken(appId));
    }
}
