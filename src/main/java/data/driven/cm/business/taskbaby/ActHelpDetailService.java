package data.driven.cm.business.taskbaby;

/**
 * @Author: lxl
 * @describe 活动助力详细表Service
 * @Date: 2018/11/16 18:25
 * @Version 1.0
 */
public interface ActHelpDetailService {


    /**
     *
     * @param helpId 活动助力表外键
     * @param helpStatus 助力状态
     * @param fansStatus 粉丝状态
     * @param actId 活动id
     * @param helpOpenid 助力者openid
     * @return
     */
     String insertActHelpDetailEntity(String helpId,Integer helpStatus,Integer fansStatus,String actId,String helpOpenid);
    /**
     * 更新助力详情表
     * @author:     Logan
     * @date:       2018/11/19 14:44
     * @params:     [helpDetailId, helpStatus, fansStatus]
     * @return:     int
    **/
     int updateActHelpDetailEntity(String helpDetailId,Integer helpStatus,Integer fansStatus);


    /**
     * 通过helpOpenId  和 活动id 来得到活动助力详细id
     * @param helpOpenId 助力者OpenId
     * @param actId 活动id
     * @return 返回 活动助力详细id
     */
     String getHelpDetailId(String helpOpenId, String actId);
}
