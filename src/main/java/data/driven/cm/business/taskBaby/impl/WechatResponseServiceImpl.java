package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.*;
import data.driven.cm.component.TaskBabyConstant;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskBaby.WechatPublicEntity;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
    @Override
    public String notify(Map wechatEventMap) {
        if(checkActive(wechatEventMap)) {
           return dispatherAndReturn(wechatEventMap);
        }
        return "success";
    }
    /**
     * 查看活动是否激活
     * 查看当前微信公众号下是否有激活的活动
     * @param wechatEventMap 传进来的微信时间消息
     * @return
     */
    private boolean checkActive(Map<String,String> wechatEventMap){
        String fromUserName = wechatEventMap.get(WeChatConstant.FromUserName);
        if(fromUserName != null){
            return activityService.countActivedActivity(fromUserName) > 0;
        }

        return false;
    }
    /**
     * 消息分发，将接收到的微信消息，分发到各个服务中去
     * @param wechatEventMap 传进来的微信时间消息
     * @return 返回处理后的消息
     */
    private String dispatherAndReturn(Map<String,String> wechatEventMap){
       String toUserName = wechatEventMap.get(WeChatConstant.ToUserName);
       String fromUserName = wechatEventMap.get(WeChatConstant.FromUserName);
       String msgType = wechatEventMap.get(WeChatConstant.MsgType);
       String msgContent =wechatEventMap.get(WeChatConstant.Content);
       //1.用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
        if(StringUtils.isNotEmpty(msgType) &&
                msgType == WeChatConstant.RESP_MESSAGE_TYPE_TEXT){
            if(StringUtils.isNotEmpty(msgContent) && StringUtils.isNotEmpty(fromUserName) &&
                    StringUtils.isNotEmpty(toUserName) && StringUtils.isNotEmpty(msgContent) &&
                    matchKeyWord(msgContent,toUserName)){
                return keyWordReply(wechatEventMap);
            }
        }
        //2.用户扫描海报二维码，有eventKey，并且可以是以qrscene_开头的
        String eventKey = wechatEventMap.get(WeChatConstant.EventKey);
        if(StringUtils.isNotEmpty(eventKey)&& eventKey.startsWith(WeChatConstant.QREventKeyPrefix)){
            int helpCount = activityHelp(wechatEventMap);
            sendActivtyStateMsg(wechatEventMap,helpCount);//发送模版消息提示助力状态
        }

        return "success";

    }

    /**
     * 根据微信公众号和关键字匹配激活的活动
     * @return
     */
     private boolean matchKeyWord(String keyWord, String wechatAccount){
         String activityId = activityService.getMatActivityId(wechatAccount,keyWord,null);
         if(StringUtils.isNotEmpty(activityId)){
             return  true;
         }
         return false;
     }

    /**
     *
     * @param wechatEventMap
     * @return
     */
    private String keyWordReply(Map<String,String> wechatEventMap){


         String openId = wechatEventMap.get(WeChatConstant.FromUserName);
         String wechatAccount = wechatEventMap.get(WeChatConstant.ToUserName);
         String keyWord = wechatEventMap.get(WeChatConstant.Content);
         if(StringUtils.isEmpty(openId) || StringUtils.isEmpty(wechatAccount)){
             return "";
         }
        WechatPublicEntity wechatPublicEntity = wechatPublicService.getEntityByWechatAccount(wechatAccount);
        if(wechatPublicEntity == null){
            return "";
        }
        String appId = wechatPublicEntity.getAppid();
        String secretCode = wechatPublicEntity.getSecret();
        //获取粉丝个人信息存入到userPersonalInfoMap
        Map<String,String> userPersonalInfoMap = WeChatUtil.getUserInfo(openId,appId,secretCode);

        //获取带参数的二维码
        Map<String, Object> activitySimpleInfoMap =
                activityService.getMacActivitySimpleInfo(wechatAccount,keyWord,null);
        if(activitySimpleInfoMap == null){
            return "";
        }
        String activityId = activitySimpleInfoMap.get(ActivityService.KEY_ACT_ID).toString();
        StringBuilder sceneStrBuilder = new StringBuilder();
        sceneStrBuilder.append(openId).append(TaskBabyConstant.SEPERATOR_QRSCEAN).append(activityId);
        String qrCodeUrl = WeChatUtil.getWXPublicQRCode(WeChatUtil.QR_TYPE_TEMPORARY,
                WeChatUtil.QR_MAX_EXPIREDTIME,WeChatUtil.QR_SCENE_NAME_STR,sceneStrBuilder.toString(),appId,secretCode);

        //将二维码url put到userPersonalInfoMap中
        userPersonalInfoMap.put(TaskBabyConstant.KEY_QRCODE_URL,qrCodeUrl);
        //将活动的原始海报的url放入到userPersonalInfoMap中
        String picId = activitySimpleInfoMap.get(ActivityService.KEY_PIC_ID).toString();
        String posterUrl = sysPictureService.getPictureURL(picId);
        userPersonalInfoMap.put(TaskBabyConstant.KEY_POSTER_URL,posterUrl);

        //得到合成图片的filePath
        userPersonalInfoMap.put(WeChatConstant.Reply_ToUserName,openId);
        userPersonalInfoMap.put(WeChatConstant.Reply_FromUserName,wechatAccount);
        String customizedPosterPath=posterService.getCombinedCustomiedPosterFilePath(userPersonalInfoMap);
        userPersonalInfoMap.put(WeChatUtil.KEY_FILE_PATH,customizedPosterPath);
        userPersonalInfoMap.put(WeChatUtil.KEY_APP_ID,appId);
        userPersonalInfoMap.put(WeChatUtil.KEY_SECRET_CODE,secretCode);

        return WeChatUtil.sendTemporaryImageMsg(userPersonalInfoMap);

         //TODO:发送文本消息：活动内容介绍

        //TODO：记录fans_Join表
    }




    /**
     *
     * @param wechatAccount 微信公众号的账号id
     * @param fansOpenId 粉丝的OpenId
     * @return
     */
    private boolean fansSubScribe(String wechatAccount, String fansOpenId){
            Map<String, String> userInfoMap = null;//TODO: 调用微信api获取用户基本信息的接口，返回给userInfoMap；

            //TODO:根据userInfoMap 如果是新来的粉丝，调用数据库接口插入粉丝数据 如果是之前关注过的，就更新关注状态
        return true;

    }

    private boolean fansUnsubscribe(String wechatAccount,String fansOpenId){
        //TODO:根据用户的openid和微信账号，将粉丝的关注状态置为未关注
        return true;
    }

    /**
     * TODO：活动被助力了
     * @param wechatEventMap
     * @return
     */
    private int activityHelp(Map<String,String> wechatEventMap){
        return 0;
    }

    /**TODO：调用模版消息接口
     * 助力成功，助力中两种情况
     * @param wechatEventMap
     * @param helpcount 被助力个数
     */
    private void sendActivtyStateMsg(Map<String,String> wechatEventMap,int helpcount){
        //
    }

}

