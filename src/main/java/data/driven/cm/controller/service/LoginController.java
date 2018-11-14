package data.driven.cm.controller.service;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.sys.StoreService;
import data.driven.cm.business.user.UserInfoService;
import data.driven.cm.common.ApplicationSessionFactory;
import data.driven.cm.entity.sys.StoreEntity;
import data.driven.cm.entity.user.UserInfoEntity;
import data.driven.cm.util.JSONUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录
 * @author hejinkai
 * @date 2018/7/1
 */
@Controller
@RequestMapping(path = "/service")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private StoreService storeService;

//    @Autowired
//    private WechatStoreVerificationAuthorizationService wechatStoreVerificationAuthorizationService;

//    @RequestMapping(path = "/login")
//    public ModelAndView login(){
//        ModelAndView modelAndView = new ModelAndView("/service/login");
//
//        return modelAndView;
//    }

    @ResponseBody
    @RequestMapping(path = "/execuLogin")
    public JSONObject execuLogin(HttpServletRequest request, HttpServletResponse response, String userName, String pwd){
        UserInfoEntity user = userInfoService.getUser(userName, pwd);
        if(user == null){
            return JSONUtil.putMsg(false, "101", "登录失败");
        }
        ApplicationSessionFactory.setUser(request, response, user);
        return JSONUtil.putMsg(true, "200", "登录成功。\n用户昵称：" + user.getNickName());
    }

    @ResponseBody
    @RequestMapping(path = "/logout")
    public JSONObject logout(HttpServletRequest request, HttpServletResponse response){
        ApplicationSessionFactory.clearSession(request, response);
        return JSONUtil.putMsg(true, "200", "登出成功");
    }

    /**
     *  核销用户登录
     * @param userName 用户名
     * @param pwd 用户密码
     * @return 登录是否成功
     */
    @ResponseBody
    @RequestMapping(path = "/verification_login")
    public JSONObject login(String userName, String pwd){
        UserInfoEntity user = userInfoService.getUser(userName, pwd);
        if(user == null){
            return JSONUtil.putMsg(false, "101", "登录失败");
        }
        StoreEntity store = storeService.getStoreByUserId(user.getUserId());
        if (store == null){
            return JSONUtil.putMsg(false,"101","非门店用户");
        }
//        else{  去掉的原因是因为用户可以关闭授权功能
//            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = wechatStoreVerificationAuthorizationService.getEntityBystoreId(store.getStoreId());
//            if (wechatStoreVerificationAuthorizationEntity != null){
//                return JSONUtil.putMsg(false,"101","门店已存在微信用户");
//            }
//        }
//        ApplicationSessionFactory.setUser(request, response, user);
        return JSONUtil.putMsg(true, "200", "登录成功。\n用户昵称：" + user.getNickName(),user.getUserId(),store.getStoreId());
    }
}
