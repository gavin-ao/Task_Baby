package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.WechatResponseService;
import data.driven.cm.business.taskBaby.WechatUserInfoService;
import data.driven.cm.component.WeChatContant;
import data.driven.cm.entity.taskBaby.MatActivityEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private WechatUserInfoService wechatUserInfoService;

    @Override
    public String notify(Map wechatEventMap) {
        if(checkActive(wechatEventMap)) {
        }
        return "success";
    }
    /**
     * 查看活动是否激活
     * @param wechatEventMap 传进来的微信时间消息
     * @return
     */
    @Override
    public boolean checkActive(Map wechatEventMap){
          return true;
    }
    /**
     * 消息分发，将接收到的微信消息，分发到各个服务中去
     * @param wechatEventMap 传进来的微信时间消息
     * @return 返回处理后的消息
     */
    private String dispatherAndReturn(Map<String,String> wechatEventMap){
       String eventName = wechatEventMap.get(WeChatContant.Event);
       String toUserName = wechatEventMap.get(WeChatContant.ToUserName);
       String fromUserName = wechatEventMap.get(WeChatContant.FromUserName);
       //1.用户发送关键字文本,如果文本是活动关键字，则进行关键字回复
//        if(StringUtils.isNotEmpty(eventName) && eventName == WeChatContant.RESP_MESSAGE_TYPE_TEXT)
//       switch (eventName) {
//           case WeChatContant.EVENT_TYPE_SUBSCRIBE: fansSubScribe()
//       }

        return "success";

    }

    /**
     * 根据微信公众号和关键字匹配激活的活动
     * @return
     */
     private boolean matchKeyWord(String keyWord, String wechatAccount){
         MatActivityEntity activityEntity = null;
         //TODO:根据根据关键字和微信账号,获取活动信息返回给activityEntity；
         if(activityEntity!=null){
             return  true;
         }
         return false;
     }

    /**
     *
     * @param wechatEventMap
     * @return
     */
    private String keyWordReply(Map wechatEventMap){


         String openId = "";//TODO：获取当前的OpenID
         String activityId ="";//TODO:获取当前的activityId
        //TODO: 调用带参数的二维码生成接口,返回带参数二维码的url
         Map<String,String> userPersonalInfo = null;//TODO:调用获取用户基本信息的接口
         String posterUrl ="";//TODO:获取用户原始的海报url
         userPersonalInfo.put("posterUrl",posterUrl);
         String url = getCombinedPosterUrl(userPersonalInfo);
         //TODO:发送文本消息：活动内容介绍
        //TODO：发送海报图片信息
        return "success";
    }

    /**
     * 返回原始海报的url
     * @param activityId
     * @return
     */

    private String  getOriginPosterUrl(String activityId){


        return "";

    }

    /**
     * 获取粉丝的个人信息，昵称，头像url等等
     * @param openId
     * @param wechatAccount
     * @return 个人信息
     */
    private Map<String,String> getFansPersonalInfo(String openId,String wechatAccount){
        return null;

    }

    /**
     *
     * @param personalInfoMap  headImgUrl, nickName, QRCodeUrl,posterUrl
     * @return
     */
    private String getCombinedPosterUrl(Map<String,String> personalInfoMap){
        return "";
    }

    /**
     *
     * @param wechatAccount 微信公众号的账号id
     * @param fansOpenId 粉丝的OpenId
     * @return
     */
    private boolean fansSubScribe(String wechatAccount, String fansOpenId){
            Map<String, String> userInfoMap = null;//TODO: 调用微信api获取用户基本信息的接口，返回给userInfoMap；

            //TODO:根据userInfoMap 如果是新来的粉丝，调用数据库接口插入粉丝数据，如果是之前关注过的，就更新关注状态
        return true;

    }

    private boolean fansUnsubscribe(String wechatAccount,String fansOpenId){
        //TODO:根据用户的openid和微信账号，将粉丝的关注状态置为未关注
        return true;
    }

}

