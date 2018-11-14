package data.driven.cm.controller.wechat;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.wechat.WechatAppInfoService;
import data.driven.cm.common.ApplicationSessionFactory;
import data.driven.cm.entity.user.UserInfoEntity;
import data.driven.cm.entity.wechat.WechatAppInfoEntity;
import data.driven.cm.util.JSONUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author hejinkai
 * @date 2018/7/5
 */
@Controller
@RequestMapping(path = "/wechat/appinfo")
public class WechatAppInfoController {

    private static final Logger logger = LoggerFactory.getLogger(WechatAppInfoController.class);

    @Autowired
    private WechatAppInfoService wechatAppInfoService;

    /**
     * 根据用户id查询小程序
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/findAppInfoListByUser")
    public JSONObject findAppInfoListByUser(HttpServletRequest request, HttpServletResponse response){
        UserInfoEntity userInfoEntity = ApplicationSessionFactory.getUser(request, response);
        if(userInfoEntity != null){
            List<WechatAppInfoEntity> wechatAppInfoEntityList = wechatAppInfoService.findAppInfoListByUser(userInfoEntity.getUserId());
            JSONObject result = JSONUtil.putMsg(true, "200", "查询成功");
            result.put("wechatAppInfoEntityList", wechatAppInfoEntityList);
            return result;
        }else{
            return JSONUtil.putMsg(false, "101", "查询失败，用户为空");
        }
    }


}
