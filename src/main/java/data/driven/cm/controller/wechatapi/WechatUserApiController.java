package data.driven.cm.controller.wechatapi;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.common.WechatApiSessionBean;
import data.driven.cm.common.WechatApiSession;
import data.driven.cm.util.JSONUtil;
import data.driven.cm.vo.wechat.WechatUserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hejinkai - 可删除
 * @date 2018/6/29
 */
@Controller
@RequestMapping(path = "/wechatapi/user")
public class WechatUserApiController {
    private static final Logger logger = LoggerFactory.getLogger(WechatUserApiController.class);

    /**
     * 获取登录用户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/getUserInfoId")
    public JSONObject getUserInfoId(String sessionID){
        WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
        WechatUserInfoVO userInfo = wechatApiSessionBean.getUserInfo();
        userInfo.clearSensitiveInfo();
        JSONObject result = JSONUtil.putMsg(true, "200", "调用成功");
        result.put("userInfo", userInfo);
        return result;
    }
}
