package data.driven.cm.controller.wechatapi.nologin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.behavioranalysis.BehaviorAnalysisHelpOpenUrlService;
import data.driven.cm.business.material.MatActivityService;
import data.driven.cm.business.wechat.WechatAppInfoService;
import data.driven.cm.business.wechat.WechatHelpDetailService;
import data.driven.cm.business.wechat.WechatUserService;
import data.driven.cm.entity.wechat.WechatAppInfoEntity;
import data.driven.cm.vo.material.MatActivityVO;
import data.driven.cm.vo.wechat.WechatHelpDetailUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static data.driven.cm.util.JSONUtil.putMsg;

/**
 * 助力
 * @author hejinkai
 * @date 2018/7/13
 */
@Controller
@RequestMapping(path = "/wechatapi/nologin/help")
public class WechatHelpApiNoLoginController {
    private static final Logger logger = LoggerFactory.getLogger(WechatHelpApiNoLoginController.class);

    @Autowired
    private WechatHelpDetailService wechatHelpDetailService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private MatActivityService matActivityService;
    @Autowired
    private BehaviorAnalysisHelpOpenUrlService behaviorAnalysisHelpOpenUrlService;
    @Autowired
    private WechatAppInfoService wechatAppInfoService;


    /**
     * 获取助力的微信用户信息集合
     * @param helpId
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/findHelpDetailUserList")
    public JSONObject findHelpDetailUserList(String helpId){
        try{
            List<WechatHelpDetailUserVO> wechatHelpDetailUserVOList = wechatHelpDetailService.findHelpDetailUserListByHelpId(helpId);
            if(wechatHelpDetailUserVOList != null && wechatHelpDetailUserVOList.size() > 0){
                JSONObject result = putMsg(true, "200", "查询成功");
                result.put("data", JSONArray.parseArray(JSONArray.toJSONString(wechatHelpDetailUserVOList)));
                return result;
            }else{
                JSONObject result = putMsg(false, "101", "助力用户查询失败");
                result.put("data", new Object[0]);
                return result;
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            JSONObject result =  putMsg(false, "102", "助力用户查询失败");
            result.put("data", new Object[0]);
            return result;
        }
    }

    /**
     * 发送消息到前端页面
     * @param response
     * @param msg
     */
    private void printMsg(HttpServletResponse response, String msg){
        try{
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(msg);
        }catch (IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 打开奖励链接，记录并跳转
     * @param response
     * @param appid
     * @param openid
     */
    @RequestMapping(path = "/openHelpRewardUrl")
    public void openHelpRewardUrl(HttpServletResponse response, String appid, String openid, String storeId){
        if(appid == null || openid == null){
            printMsg(response, "访问错误，错误代码105");
            return;
        }
        WechatAppInfoEntity appInfoEntity = wechatAppInfoService.getAppInfo(appid);
        if(appInfoEntity == null){
            logger.info("openHelpRewardUrl方法记录异常，未找到存在的appid，参数信息：appid:{0},openid:{1}", appid, openid);
            printMsg(response, "访问错误，错误代码101");
            return;
        }
        MatActivityVO matActivityInfo = matActivityService.getAnyMatActivityInfoByStore(storeId);
        if(matActivityInfo != null){
            if(matActivityInfo.getRewardUrl() != null && matActivityInfo.getRewardUrl().trim().length() > 0){
                try {
                    behaviorAnalysisHelpOpenUrlService.insert(storeId, appInfoEntity.getAppInfoId(), matActivityInfo.getActId(), openid);
                    response.sendRedirect(matActivityInfo.getRewardUrl());
                }catch (Exception e){
                    logger.error(e.getMessage(), e);
                    printMsg(response, "访问错误，错误代码102");
                    return;
                }
            }else{
                printMsg(response, "访问错误，错误代码103");
                return;
            }
        }else{
            printMsg(response, "访问错误，错误代码104");
            return;
        }
    }

    /**
     * 测试
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/addUserInfo2")
    public JSONObject addUserInfo2(){
        try{
            return putMsg(true, "200", wechatUserService.addUserInfo2());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return putMsg(false, "102", "助力用户查询失败");
        }
    }

}
