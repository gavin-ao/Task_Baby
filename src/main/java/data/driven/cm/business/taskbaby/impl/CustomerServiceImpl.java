package data.driven.cm.business.taskbaby.impl;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskbaby.CustomerConfigureService;
import data.driven.cm.business.taskbaby.CustomerService;
import data.driven.cm.business.taskbaby.CustomerServiceInfoService;
import data.driven.cm.business.taskbaby.ThirdPartyService;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.CustomerConfigureEntity;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    * 响应客户服务
    * @author Logan
    * @date 2019-01-08 16:24
    * @param wechatEventMap
    * @param appId

    * @return
    */
    @Override
    public String call(Map<String, String> wechatEventMap,String appId) {
        if(isCustomerServiceMenuClick(wechatEventMap)){
            //当前是客户点击服务菜单，发送服务项清单
            sendCustomServiceItemList(wechatEventMap,null, appId);
        }
        String customerConfigId = chooseCustomService(wechatEventMap,appId);
        if(StringUtils.isNotEmpty(customerConfigId)){
            //当前是客户输入客服项关键字
            doCustomerServiceChoose(wechatEventMap,customerConfigId,appId);
        }
        return null;
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
        return true;
    }
    private String getCustomerServiceMeunMsg(List<CustomerConfigureEntity> customerConfigureList){
        return "";
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
    private void sendCustomServiceItemList(Map<String,String> wechatEventMap,List<CustomerConfigureEntity> customerConfigureList,String appId){
       String touser = getFromUserName(wechatEventMap);
        String menuMsg = getCustomerServiceMeunMsg(customerConfigureList);
        String accessToken = getAccessToken(appId);
        WeChatUtil.sendCustomTxtMsg(touser,menuMsg,accessToken);
    }

    private String doCustomerServiceChoose(Map<String,String> wechatEventMap,String customerServiceConfigId,String appId){
       List<CustomerConfigureEntity> customerConfigureList = customerConfigureService.getChildMenu(customerServiceConfigId);;
        if(customerConfigureList.size()>0){
            // 如果有子菜单，就发送子菜单的服务项，供客户继续选择  Logan 2019-01-08  16:46
            sendCustomServiceItemList(wechatEventMap,customerConfigureList,appId);
        }else{
            //没有子菜单了，发送客服名片  Logan 2019-01-08  16:49
            String option = getContent(wechatEventMap);
            sendNameCard(wechatEventMap,option,appId);
        }
        return "";
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
}
