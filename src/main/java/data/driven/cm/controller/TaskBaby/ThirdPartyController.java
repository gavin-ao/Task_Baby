package data.driven.cm.controller.TaskBaby;

import data.driven.cm.business.taskBaby.ThirdPartyService;
import data.driven.cm.business.taskBaby.WeChatService;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.util.WeChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author: lxl
 * @describe 第三方平平Controller
 * @Date: 2018/11/23 9:21
 * @Version 1.0
 */
@CrossOrigin
@Controller
@RequestMapping(path = "/thirdParty")
public class ThirdPartyController {
    Logger logger = LoggerFactory.getLogger(ThirdPartyController.class);

    @Autowired
    ThirdPartyService thirdPartyService;
    @Autowired
    WeChatService weChatService;

    /**
     * 微信第三方授权事件的接收
     */
    @RequestMapping(value = "/authorize", method = {RequestMethod.GET, RequestMethod.POST})
    public void acceptAuthorizeEvent(HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            logger.info("----------------------进入authorize----------------------");
            thirdPartyService.hadleAuthorize(request, response);
            PrintWriter pw = response.getWriter();
            pw.write("success");
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 得到扫码URL
     * @return
     */

    @RequestMapping(value = "/getPreAuthCode", method = {RequestMethod.GET})
    public ModelAndView getPreAuthCode(){
        logger.info("----------------------进入得到扫码URL----------------------");
        String componentloginpageURL ="https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=%s&pre_auth_code=%s&redirect_uri=%s&auth_type=3";
        String preAuthCode = WeChatUtil.getPreAuthCode();
        logger.info("----------------------得到的URL"+String.format(componentloginpageURL, WeChatConstant.THIRD_PARTY_APPID,preAuthCode,"http://easy7share.com/thirdParty/authcallback").toString()+"----------------------");
        ModelAndView modelAndView = new ModelAndView("/taskBaby/login");
        modelAndView.addObject("url","https://mp.weixin.qq.com/cgi-bin/componentloginpage?"+
                "component_appid="+WeChatConstant.THIRD_PARTY_APPID +
                "&pre_auth_code=" + preAuthCode +
                "&redirect_uri=" +"http://easy7share.com/thirdParty/authcallback"
        );
        return modelAndView;
    }

    @RequestMapping("/authcallback")
    /**
     * 第三个授权回调
     * @author:     Logan
     * @date:       2018/11/23 12:31
     * @params:     [authCode, expriesIn]
     * @return:     void
    **/
    @ResponseBody
    public String authorizeCallback(@RequestParam(value="auth_code") String authCode,
                                  @RequestParam(value="expires_in") String expriesIn){
        logger.info(String.format("-----------响应授权回调,AuthCode:%s----------------",authCode));

        thirdPartyService.saveCallbackAuthInfo(authCode);
        return "success";

    }
    /**
     * 此处是处理微信服务器的消息转发的
     * 处理微信服务器发来的post请求，进行签名的验证
     */
    @ResponseBody
    @PostMapping(value = "/wechat")
    public String processMsg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        if (WeChatUtil.checkSignature(signature, timestamp, nonce)){
            // 调用核心服务类接收处理请求
            return weChatService.processRequest(request,response);
        }
        return "";
    }

}
