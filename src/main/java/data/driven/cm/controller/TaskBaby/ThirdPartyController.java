package data.driven.cm.controller.TaskBaby;

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
public class ThirdPartyController {


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

    public void authorizeCallback(@RequestParam(value="autho_code") String authCode,
                                  @RequestParam(value="expires_in") String expriesIn){
        
    }
}
