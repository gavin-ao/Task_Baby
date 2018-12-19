package data.driven.cm.controller.taskbaby;

import data.driven.cm.business.taskbaby.SubscribeWeChatResponseService;
import data.driven.cm.business.taskbaby.UnionidUserMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: lxl
 * @describe 订阅号Controller
 * @Date: 2018/12/19 14:20
 * @Version 1.0
 */
@Controller
@RequestMapping(value = "subscribe")
public class SubscribeController {
    Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @Autowired
    private SubscribeWeChatResponseService subscribeWeChatResponseService;

    @Autowired
    private UnionidUserMappingService unionidUserMappingService;
    
    
    /**
     * @description 微信用户给服务号授权后的回调URL
     * @author lxl
     * @date 2018-12-19 14:22
     * @param code code作为换取access_token的票据
     * @param state 参数,appid&&act_id&&unionid 说明：服务号appid、活动id、被助力者的unionid
     * @return 
     */
    @RequestMapping(value = "/authcallback",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String authorizeCallback(@RequestParam(value="code") String code,
                                    @RequestParam(value="state") String state){
        String[] strs = state.split("@@");
        //服务号appid
        String serviceAppId = strs[0].toString();
        //活动id
        String actId = strs[1].toString();
        //被助力者unionid
        String fromUnionid = strs[2].toString();

        String toUnionid = subscribeWeChatResponseService.getCodeByUnionid(code,serviceAppId);
        unionidUserMappingService.insertUnionidUserMappingEntity(actId,fromUnionid,toUnionid);
        return "保存数据成功！";

    }
}






















