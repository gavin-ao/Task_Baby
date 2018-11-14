package data.driven.cm.controller.wechatapi;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.wechat.WechatShareDetailService;
import data.driven.cm.business.wechat.WechatShareInfoService;
import data.driven.cm.business.wechat.WechatUserService;
import data.driven.cm.common.WechatApiSession;
import data.driven.cm.common.WechatApiSessionBean;
import data.driven.cm.entity.wechat.WechatShareInfoEntity;
import data.driven.cm.util.JSONUtil;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.vo.wechat.WechatUserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static data.driven.cm.util.JSONUtil.putMsg;

/**
 * 分享
 * @author hejinkai
 * @date 2018/7/4
 */
@Controller
@RequestMapping(path = "/wechatapi/share")
public class WechatShareApiController {
    private static final Logger logger = LoggerFactory.getLogger(WechatShareApiController.class);

    @Autowired
    private WechatShareInfoService wechatShareInfoService;
    @Autowired
    private WechatShareDetailService wechatShareDetailService;
    @Autowired
    private WechatUserService wechatUserService;

    @ResponseBody
    @RequestMapping(path = "/getShareId")
    public JSONObject getShareId(String sessionID){
        JSONObject result = JSONUtil.putMsg(true, "200", "获取成功");
        String shareId = UUIDUtil.getUUID();
        result.put("shareId", shareId);
        return result;
    }

    /**
     * 进行分享动作，记录分享
     * @param sessionID
     * @param content
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/execuShare")
    public JSONObject execuShare(String sessionID, String shareId, String content, String storeId){
        WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
        try{
            wechatShareInfoService.insertShare(shareId, wechatApiSessionBean.getUserInfo().getWechatUserId(), content, storeId, wechatApiSessionBean.getUserInfo().getAppInfoId());
            JSONObject result = JSONUtil.putMsg(true, "200", "分享成功");
            return result;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return putMsg(false, "101", "分享失败");
        }
    }

    @ResponseBody
    @RequestMapping(path = "/clickShareUrl")
    public JSONObject clickShareUrl(String sessionID, String shareId){
        WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
        try{
            WechatShareInfoEntity shareInfoEntity = wechatShareInfoService.getEntityById(shareId);
            if(shareInfoEntity != null){
                WechatUserInfoVO fromUserInfo = wechatUserService.getUserInfoByUserIdAndAppInfoId(shareInfoEntity.getWechatUserId(), shareInfoEntity.getAppInfoId());
                WechatUserInfoVO toUserInfo = wechatApiSessionBean.getUserInfo();
                wechatShareDetailService.insertShareDetail(fromUserInfo, toUserInfo, shareInfoEntity);
                JSONObject result = JSONUtil.putMsg(true, "200", "分享点击记录成功");
                return result;
            }else{
                return putMsg(false, "101", "分享点击记录失败");
            }
        }catch (Exception e){
            logger.error("clickShareUrl报错_"+e.getMessage(), e);
            return putMsg(false, "102", "分享点击记录失败");
        }
    }

}
