package data.driven.cm.business.taskbaby.impl;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskbaby.*;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.CustomerConfigureEntity;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: Task_Baby
 * @description: 客户服务实现
 * @author: Logan
 * @create: 2019-01-08 16:17
 **/
@Service
public class CustomerServiceImpl implements CustomerService {
    private static Logger  logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerServiceInfoService customerServiceInfoService;
    @Autowired
    private CustomerConfigureService customerConfigureService;
    @Autowired
    private ThirdPartyService thirdPartyService;
    /**
     * 公众号详细信息表Service
     */
    @Autowired
    private WechatPublicDetailService wechatPublicDetailService;


    /**
    * 响应客户服务,
     * 如果当前消息是客户服务特有的消息，处理完后返回true，否则返回false
    * @author Logan
    * @date 2019-01-08 16:24
    * @param wechatEventMap
    * @param appId

    * @return
    */
    @Override
    public boolean call(Map<String, String> wechatEventMap,String appId) {
        if(isCustomerServiceMenuClick(wechatEventMap)){
            //当前是客户点击服务菜单，发送服务项清单
            List<CustomerConfigureEntity> customerConfigureList =
                    customerConfigureService.getCustomerConfigureEntites(appId);
            return sendCustomServiceItemList(wechatEventMap,customerConfigureList, appId);
        }
        if(textEvent(wechatEventMap)) {
            String customerConfigId = chooseCustomService(wechatEventMap, appId);
            if (StringUtils.isNotEmpty(customerConfigId)) {
                //当前是客户输入客服项关键字
                doCustomerServiceChoose(wechatEventMap, customerConfigId, appId);
                return true;
            }
        }
        //搜索关注
        if(subscribeEvent(wechatEventMap)){
            String accessToken = getAccessToken(appId);
            sendFollowMsg(appId, wechatEventMap, accessToken);
            return true;
        }
        return false;
    }

    /**
    * 是否为用户输入选择客服选项
     * 1.必须是输入文字事件
     * 2.能在客服选项中找到此关键字
    * @author Logan
    * @date 2019-01-08 16:25
    * @param wechatEventMap

    * @return
    */
    private String chooseCustomService(Map<String,String> wechatEventMap,String appId){
       if(textEvent(wechatEventMap)){
           String content = getContent(wechatEventMap);
           return customerConfigureService.getCustomerServiceConfigId(appId,content);
       }
       return null;
    }
    /**
    * 是否为客服菜单响应事件
    * @author Logan
    * @date 2019-01-08 16:29
    * @param wechatEventMap

    * @return
    */
    private boolean isCustomerServiceMenuClick(Map<String,String> wechatEventMap){
        String clickEvent = getEventKey(wechatEventMap);
        if(clickEvent != null && clickEvent.equals("Customer_Service_Click")){
            return true;
        }
        return false;
    }

    /**
    * 获取客服菜单消息
    * @author Logan
    * @date 2019-01-09 16:01
    * @param customerConfigureList

    * @return
    */
    private String getCustomerServiceMeunMsg(List<CustomerConfigureEntity> customerConfigureList){
        StringBuffer msg = new StringBuffer();
        if (customerConfigureList.size() > 0 ){
            for (CustomerConfigureEntity customerConfigureEntity : customerConfigureList){
                msg.append(customerConfigureEntity.getDescribe()+"\n");
            }
        }else{
            msg.append("欢迎关注~");
        }
        return msg.toString();
    }
    /**
    * 发送客服菜单
    * 如果是上级服务项，就要发送属于这个上级服务的所有子服务项
    * @author Logan
    * @date 2019-01-08 16:30
    * @param wechatEventMap
    * @param customerConfigureList 客服选项
    * @param appId

    * @return
    */
    private boolean sendCustomServiceItemList(Map<String,String> wechatEventMap,List<CustomerConfigureEntity> customerConfigureList,String appId){
        if(customerConfigureList == null || customerConfigureList.size()==0){
            return false;
        }
       String touser = getFromUserName(wechatEventMap);
        String menuMsg = getCustomerServiceMeunMsg(customerConfigureList);
        String accessToken = getAccessToken(appId);
        WeChatUtil.sendCustomTxtMsg(touser,menuMsg,accessToken);
        return true;
    }

    /**
    * 处理用户输入客服关键字
    * @author Logan
    * @date 2019-01-09 16:02
    * @param wechatEventMap
    * @param customerServiceConfigId
    * @param appId

    * @return
    */
    private void doCustomerServiceChoose(Map<String,String> wechatEventMap,String customerServiceConfigId,String appId){
       List<CustomerConfigureEntity> customerConfigureList = customerConfigureService.getChildMenu(customerServiceConfigId);
        if(customerConfigureList.size()>0){
            // 如果有子菜单，就发送子菜单的服务项，供客户继续选择  Logan 2019-01-08  16:46
            sendCustomServiceItemList(wechatEventMap,customerConfigureList,appId);
        }else{
            //没有子菜单了，发送客服名片  Logan 2019-01-08  16:49
            String option = getContent(wechatEventMap);
            sendNameCard(wechatEventMap,option,appId);
        }
    }


    /**
    * 获取微信文字信息的内容
    * @author Logan
    * @date 2019-01-08 16:50
    * @param wechatEventMap
    
    * @return 
    */       
    private String getContent(Map<String,String> wechatEventMap){
        return wechatEventMap.get(WeChatConstant.CONTENT);
    }


  /**
  * 发送客服名片
  * @author Logan
  * @date 2019-01-08 16:51
  * @param wechatEventMap
  * @param option
  * @param appId

  * @return
  */
  @Override
    public void sendNameCard(Map<String,String> wechatEventMap,String option,String appId){
      String mediaId = getMediaId(option, appId);
      if (StringUtils.isNotEmpty(mediaId)) {
          String fromUserName = getFromUserName(wechatEventMap);
          String accessToken = getAccessToken(appId);
          WeChatUtil.sendCustomImageMsg(fromUserName, mediaId, accessToken);
          logger.info("发送名片成功");
      }else{
          logger.info("没有发送名片，可能当前客服选项没有配置名片");
      }
    }

    private String getMediaId(String option,String appId){
        JSONObject result = customerServiceInfoService.getRandomMediaId(appId, option);
        if(result.getBoolean("success")){
            logger.info("获取名片mediaId成功");
            return result.getString("data");
        }else{
            logger.error(result.getString("msg"));
            return null;
        }
    }

    private String getFromUserName(Map<String,String> wechatEventMap){
        return wechatEventMap.get(WeChatConstant.FROM_USER_NAME);
    }

    private String getAccessToken(String appId){
        logger.debug("----------获取AccessToken-------");
        long begin = System.currentTimeMillis();

        String accessToken = null;
        try {
            accessToken = thirdPartyService.getAuthAccessToken(appId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WeChatUtil.log(logger, begin, "生成AccessToken");
        return accessToken;
    }
    private String getMsgType(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.MSG_TYPE);
    }

    private boolean textEvent(Map<String,String> wechatEventMap){
        String msgType = getMsgType(wechatEventMap);
        if (StringUtils.isNotEmpty(msgType) && msgType.equals(WeChatConstant.RESP_MESSAGE_TYPE_TEXT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用户搜索关注后如果存在自定义回复的信息则发送
     *
     * @param appid          公众号appid
     * @param wechatEventMap 发送过来的Map信息
     * @param accessToken    公众号accessToken
     * @return 返回的信息
     * @author lxl
     */
    @Override
    public String sendFollowMsg(String appid, Map<String, String> wechatEventMap, String accessToken) {
        logger.info("进入用户搜索关注后发送自定义消息 start");
        String fromUserName = getFromUserName(wechatEventMap);
        String nickName = wechatPublicDetailService.getNickNameByAppId(appid);

        Map<String, String> msgReply = new HashMap<>();
        msgReply.put(WeChatConstant.KEY_CSMSG_TOUSER, fromUserName);
        msgReply.put(WeChatConstant.KEY_CSMSG_TYPE, WeChatConstant.VALUE_CSMSG_TYPE_TEXT);
        List<CustomerConfigureEntity> customerConfigureEntities = customerConfigureService.getCustomerConfigureEntites(appid);
        StringBuffer msg = new StringBuffer();
        if (customerConfigureEntities.size() > 0 ){
            msg.append("欢迎关注"+nickName+"~\n");
            for (CustomerConfigureEntity customerConfigureEntity : customerConfigureEntities){
                msg.append(customerConfigureEntity.getDescribe()+"\n");
            }
        }else{
            msg.append("欢迎关注"+nickName+"~");
        }
        msgReply.put(WeChatConstant.KEY_CSMSG_CONTENT, msg.toString());
        WeChatUtil.sendCustomMsg(msgReply, accessToken);
        logger.info("进入用户搜索关注后发送自定义消息 end");
        return "success";
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

    private String getEvent(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.EVENT);
    }


    private String getEventKey(Map<String, String> wechatEventMap) {
        return wechatEventMap.get(WeChatConstant.EVENT_KEY);
    }

    /**
     * @description 被动回复，如果存在发送客服信息，不存在不发送
     * @author lxl
     * @date 2019-01-09 15:29
     * @param fromUserName 微信用户OpenId
     * @param appid 公众号appid
     * @param accessToken 公众号的token
     * @return
     */
    @Override
    public String sendCustomServiceMsg(String appid, String fromUserName, String accessToken) {
        //判断微信用户是否已接收到回复消息，一小时内只发一次回复消息
        String nickName = wechatPublicDetailService.getNickNameByAppId(appid);
        StringBuffer msg = new StringBuffer();
        if (getReceiveCustomerNews(fromUserName,appid)){
            List<CustomerConfigureEntity> customerConfigureEntities = customerConfigureService.getCustomerConfigureEntites(appid);
            if (customerConfigureEntities.size() > 0) {
                msg.append("欢迎关注"+nickName+"~");
                for (CustomerConfigureEntity customerConfigureEntity : customerConfigureEntities) {
                    msg.append(customerConfigureEntity.getDescribe() + "\n");
                }
                return msg.toString();
            }else{
                msg.append("没找到您想要的活动，请持续关注我们，更多活动马上就来");
                return msg.toString();
            }
        }
        return null;
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
}
