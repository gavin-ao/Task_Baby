package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.ActivityService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: Task_Baby
 * @description: 活动服务，处理活动服务逻辑
 * @author: Logan
 * @create: 2018-11-14 17:51
 **/
@Service
public class ActivityServiceImpl implements ActivityService {

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
    private String dispatherAndReturn(Map wechatEventMap){

        return "success";

    }


    /**
     *
     * @param wechatEventMap
     * @return
     */
    private String keyWordReply(Map wechatEventMap){
        return "success";
    }

    /**
     * 返回原始海报的url
     * @param activityId
     * @return
     */

    private String  getOriginPoster(String activityId){


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
}

