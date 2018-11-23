package data.driven.cm.controller.TaskBaby;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @Author: lxl
 * @describe 第三方平平Controller
 * @Date: 2018/11/23 9:21
 * @Version 1.0
 */
@Controller
@RequestMapping(path = "/thirdParty")
public class ThirdPartyController {
    Logger logger = LoggerFactory.getLogger(ThirdPartyController.class);

    @Autowired
    ThirdPartyService thirdPartyService;

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
    @ResponseBody
    @RequestMapping(value = "/getPreAuthCode", method = {RequestMethod.GET})
    public String getPreAuthCode(){
        String componentloginpageURL ="https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=%s&pre_auth_code=%s&redirect_uri=%s&auth_type=3";
        String preAuthCode = WeChatUtil.getPreAuthCode();
        String.format(componentloginpageURL, WeChatConstant.THIRD_PARTY_APPID,preAuthCode,"http://easy7share.com/thirdParty/authcallback");
        return componentloginpageURL;
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
        log.info(String.format("-----------响应授权回调,AuthCode:%s----------------",authCode));

        thirdPartyService.saveCallbackAuthInfo(authCode);

    }

}
