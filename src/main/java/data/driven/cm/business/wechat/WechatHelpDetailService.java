package data.driven.cm.business.wechat;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.entity.wechat.WechatHelpDetailEntity;
import data.driven.cm.entity.wechat.WechatHelpInfoEntity;
import data.driven.cm.vo.wechat.WechatHelpDetailUserVO;
import data.driven.cm.vo.wechat.WechatUserInfoVO;

import java.util.List;

/**
 * 微信助力详情表
 * @author hejinkai
 * @date 2018/7/13
 */
public interface WechatHelpDetailService {

    /**
     * 根据助力id获取助力详情列表
     * @param helpId
     * @return
     */
    public List<WechatHelpDetailEntity> findHelpDetailListByHelpId(String helpId);

    /**
     * 根据助力id获取助力详情的助力用户信息集合
     * @param helpId
     * @return
     */
    public List<WechatHelpDetailUserVO> findHelpDetailUserListByHelpId(String helpId);

    /**
     * 根据form、to、helpId获取助力详情id
     * @param fromId
     * @param toId
     * @param helpId
     * @return
     */
    public String getHelpDetailId(String fromId, String toId, String helpId);

    /**
     * 助力详情，如果是多次点击则不给助力
     * @param fromUserInfo
     * @param toUserInfo
     * @param helpInfo
     * @return
     */
    public String insertHelpDetail(WechatUserInfoVO fromUserInfo, WechatUserInfoVO toUserInfo, WechatHelpInfoEntity helpInfo);

    /**
     * 取消后的确认助力
     * @param fromId
     * @param toId
     * @param helpId
     * @return
     */
    public JSONObject doHelp(String fromId, String toId, String helpId);

    /**
     * 取消助力
     * @param fromId
     * @param toId
     * @param helpId
     * @return
     */
    public JSONObject cancelHelp(String fromId, String toId, String helpId);

    /**
     * 根据toWechatUserId和活动id获取助力详情
     * @param actId
     * @param toWechatUserId
     * @return
     */
    public WechatHelpDetailEntity getWechatHelpDetailEntityByToUser(String actId, String toWechatUserId);
}
