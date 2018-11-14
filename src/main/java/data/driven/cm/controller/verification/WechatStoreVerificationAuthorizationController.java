package data.driven.cm.controller.verification;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.reward.RewardActCommandHelpMappingService;
import data.driven.cm.business.reward.RewardActCommandService;
import data.driven.cm.business.reward.RewardActContentService;
import data.driven.cm.business.verification.WechatStoreActVerificationMappingService;
import data.driven.cm.business.verification.WechatStoreVerificationAuthorizationService;
import data.driven.cm.common.WechatApiSession;
import data.driven.cm.common.WechatApiSessionBean;
import data.driven.cm.entity.reward.RewardActCommandEntity;
import data.driven.cm.entity.reward.RewardActCommandHelpMappingEntity;
import data.driven.cm.entity.reward.RewardActContentEntity;
import data.driven.cm.entity.verification.WechatStoreVerificationAuthorizationEntity;
import data.driven.cm.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static data.driven.cm.util.JSONUtil.putMsg;

/**
 * @Author: lxl
 * @describe 微信门店核销权限Controller
 * @Date: 2018/11/8 15:12
 * @Version 1.0
 */
@CrossOrigin
@Controller
@RequestMapping(path = "/wechatapi/wsva")
public class WechatStoreVerificationAuthorizationController {
    private static final Logger logger = LoggerFactory.getLogger(WechatStoreVerificationAuthorizationController.class);

    //微信门店核销权限Service
    @Autowired
    private WechatStoreVerificationAuthorizationService wechatStoreVerificationAuthorizationService;

    //活动奖励文案Service
    @Autowired
    private RewardActContentService rewardActContentService;

    //活动助力奖励关联表Service
    @Autowired
    private RewardActCommandHelpMappingService rewardActCommandHelpMappingService;

    //活动奖励口令service
    @Autowired
    private RewardActCommandService rewardActCommandService;

    //核销关联表Service
    @Autowired
    private WechatStoreActVerificationMappingService wechatStoreActVerificationMappingService;

    /**
     * 微信门店核销权限绑定
     * @param sessionID sessionID
     * @param userId 用户id
     * @param storeId 门店id
     * @return 绑定是否成功
     */
    @ResponseBody
    @RequestMapping(path = "/execuVerificationBind")
    public JSONObject execuWechatStoreVerificationAuthorization(String sessionID, String userId, String storeId) {
        WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
        try {
//            System.out.println("sessionID "+ sessionID);
//            System.out.println("userId "+ userId);
//            System.out.println("storeId "+ storeId);
//            System.out.println("OpenId "+wechatApiSessionBean.getUserInfo());
            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = wechatStoreVerificationAuthorizationService.getEntityByOpenId(wechatApiSessionBean.getUserInfo().getOpenId());
            if (wechatStoreVerificationAuthorizationEntity == null){
                //增加 如果店id 存在就做update,不存在就insert
                wechatStoreVerificationAuthorizationService.insertWechatStoreVerificationAuthorization(userId, storeId, wechatApiSessionBean.getUserInfo().getOpenId());
                return JSONUtil.putMsg(true, "200", "绑定成功");
            }else {
                return putMsg(false,"101","此微信已绑定门店");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return putMsg(false, "101", "绑定失败");
        }
    }


    /**
     * 通过sessionID 得到OpenId,去授权表查，当前微信用户是否有权限
     * @param sessionID sessionID
     * @return 返回当前微信用户是否有核销权限
     */
    @ResponseBody
    @RequestMapping(path = "/getAuthorization")
    public JSONObject getAuthorization(String sessionID){
        WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
        try {
            //增加 如果店id 存在就做update,不存在就insert
            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = wechatStoreVerificationAuthorizationService.getEntityByOpenId(wechatApiSessionBean.getUserInfo().getOpenId());
            if (wechatStoreVerificationAuthorizationEntity != null){
                return JSONUtil.putMsg(true, "200", "已授权");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return putMsg(false, "101", "未授权");
        }
        return putMsg(false, "101", "未授权");
    }

    /**
     * 核销扫码接口，通过扫码可以知道此码的优惠信息
     * * 核销扫码接口涉及到的信息
     * 1.通过mapId到reward_act_command_help_mapping得到command_id
     * 2.通过command_id 到 reward_cat_command 表中得到 act_id 和 command_type 字段
     * 3.然后通过act_id 和 command_type 到　reward_act_content　查找 remark
     * @param mapId 活动助力奖励关联表id
     * @param sessionID session id
     * @return 返回 JSONObjec  putMsg(false,"200","二维码已使用") 或 putMsg(false,"101","扫码失败") 或 putMsg(false,"200","非本店二维码")
     */
    @ResponseBody
    @RequestMapping(path ="/execuVerificationScan")
    public JSONObject execuVerificationScan(String mapId,String sessionID){
        WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
        try{
            RewardActCommandHelpMappingEntity rewardActCommandHelpMappingEntity = rewardActCommandHelpMappingService.getEntityByMapId(mapId);
            //通过openId 得微信门店核销权限表实体类
            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = wechatStoreVerificationAuthorizationService.getEntityByOpenId(wechatApiSessionBean.getUserInfo().getOpenId());
//            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = wechatStoreVerificationAuthorizationService.getEntityByOpenId("oeVLE5KTy4PF-OIniayEM4mNh6zE");

            //判断当前优惠码的门店Id 是否与 当前门店的 id 相等，不等则返回此码不是本店的
            if (wechatStoreVerificationAuthorizationEntity.getStoreId().equals(rewardActCommandHelpMappingEntity.getStoreId())){

                RewardActCommandEntity rewardActCommandEntity = rewardActCommandService.getEntityByCommandId(rewardActCommandHelpMappingEntity.getCommandId());

                if (rewardActCommandEntity.getBeingUsed() == null){ //使用状态不等于1 或是 Null 就显示优惠信息，否则显示“二维码已使用”
                        RewardActContentEntity rewardActContentEntity = rewardActContentService.getRewardActContentByActAndType(rewardActCommandEntity.getActId(),rewardActCommandEntity.getCommandType());
                        return putMsg(true, "200", rewardActContentEntity.getRemark());
                }else if (rewardActCommandEntity.getBeingUsed() != 1){//使用状态不等于1 或是 Null 就显示优惠信息，否则显示“二维码已使用”
                        RewardActContentEntity rewardActContentEntity = rewardActContentService.getRewardActContentByActAndType(rewardActCommandEntity.getActId(),rewardActCommandEntity.getCommandType());
                        return putMsg(true, "200", rewardActContentEntity.getRemark());
                }else{ //如果已使用需要展示优惠信息及已核销
                    RewardActContentEntity rewardActContentEntity = rewardActContentService.getRewardActContentByActAndType(rewardActCommandEntity.getActId(),rewardActCommandEntity.getCommandType());
                    return putMsg(false,"200",rewardActContentEntity.getRemark() + ",已核销");
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return putMsg(false,"101","扫码失败");
        }
        return putMsg(false,"200","非本店二维码");
    }

    /**
     * 核销码立即使用接口
     *  1.修改码的使用状态 表reward_cat_command 中 把 being_used 设置成 1 - 已使用 ， 0 - 未使用
     *  2.在 wechat_store_act_verification_mapping 中新增数据
     *
     *  * 立即使用接口涉及到的信息
     * 1.wechat_store_act_verification_mapping 核销表
     * 2.表中的字段有id(核销id)、wechat_user_id(微信用户id待确定)、store_id(门店id)、act_id(活动id)、command_id(奖励id),这些信息都在reward_act_command_help_mapping得到
     *   user_id 需要到 reward_cat_command 通过reward_cat_command_help_mapping 中的command_id 得到用户id
     * 3.把以得到的信息插入verification_info 核销表
     * 4.修改码的使用状态，这个需要在 reward_cat_command 表中增加一个字段，表示已使用  是否已使用  1 - 已使用 ， 0 - 未使用 现表中used(的意思 是否领)
     * @param mapId 活动助力奖励关联表id
     * @param sessionID Session id
     * @return 返回 JSONObject putMsg(true,"200","使用成功") 或 putMsg(false,"101","使用失败");
     */
    @ResponseBody
    @RequestMapping(path = "/execuVerificationUse")
    public JSONObject execuVerificationUse(String mapId,String sessionID){
        WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
        try{
            RewardActCommandHelpMappingEntity rewardActCommandHelpMappingEntity = rewardActCommandHelpMappingService.getEntityByMapId(mapId);
            int state = rewardActCommandService.updateRewardActCommandBeingUsed(rewardActCommandHelpMappingEntity.getCommandId()); //将奖励口令状态修改为已使用

            if (state == 1){ //判断 奖励口令状态修改状态，1 为 修改成功
                //通过openId 得微信门店核销权限表实体类
            WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = wechatStoreVerificationAuthorizationService.getEntityByOpenId(wechatApiSessionBean.getUserInfo().getOpenId());
//                WechatStoreVerificationAuthorizationEntity wechatStoreVerificationAuthorizationEntity = wechatStoreVerificationAuthorizationService.getEntityByOpenId("oeVLE5KTy4PF-OIniayEM4mNh6zE");
                wechatStoreActVerificationMappingService.insertWechatStoreActVerificationMapping(wechatStoreVerificationAuthorizationEntity.getUserId(),
                        wechatStoreVerificationAuthorizationEntity.getStoreId(),
                        rewardActCommandHelpMappingEntity.getActId(),
                        rewardActCommandHelpMappingEntity.getCommandId(),
                    wechatApiSessionBean.getUserInfo().getWechatUserId(),wechatApiSessionBean.getUserInfo().getOpenId());
//                        "123123","oeVLE5KTy4PF-OIniayEM4mNh6zE");
                return JSONUtil.putMsg(true,"200","使用成功");
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return putMsg(false,"101","使用失败");
        }
        return putMsg(false,"101","使用失败");
    }

    @ResponseBody
    @RequestMapping(path = "/test")
    public JSONObject test(){
        return putMsg(true,"200","测试成功");
    }
}
