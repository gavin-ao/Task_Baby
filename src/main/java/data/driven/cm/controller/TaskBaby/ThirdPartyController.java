package data.driven.cm.controller.TaskBaby;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskBaby.ThirdPartyService;
import data.driven.cm.util.WeChatUtil;
import jdk.nashorn.internal.runtime.options.LoggingOption;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lxl
 * @describe 第三方平平Controller
 * @Date: 2018/11/23 9:21
 * @Version 1.0
 */
@Controller
public class ThirdPartyController {
    private static Logger log = LoggerFactory.getLogger(ThirdPartyController.class);
    @Autowired
     private ThirdPartyService thirdPartyService;

    /**
     * 微信第三方授权事件的接收
     */
    @RequestMapping(value="/authorize",method = {RequestMethod.GET,RequestMethod.POST})
    public void acceptAuthorizeEvent(HttpServletRequest request,
                                     HttpServletResponse response){
        try{

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @RequestMapping("/authcallback")
    /**
     * 第三个授权回调
     * @author:     Logan
     * @date:       2018/11/23 12:31
     * @params:     [authCode, expriesIn]
     * @return:     void
    **/
    public void authorizeCallback(@RequestParam(value="autho_code") String authCode,
                                  @RequestParam(value="expires_in") String expriesIn){
        log.info("-----------响应授权回调----------------");
        String authInfoStr = WeChatUtil.getAuthoInfo(authCode);
        if(StringUtils.isEmpty(authInfoStr)){
            log.error("-------授权回调返回空串---------------");
        }
        log.info("----------返回授权信息：-------------------");
        log.info(authInfoStr);
        thirdPartyService.saveCallbackAuthInfo(authInfoStr,authCode);
    }
}
