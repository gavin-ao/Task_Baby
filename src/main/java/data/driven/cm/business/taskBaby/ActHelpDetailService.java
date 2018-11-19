package data.driven.cm.business.taskBaby;

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
    public String insertActHelpDetailEntity(String helpId,Integer helpStatus,Integer fansStatus,String actId,String helpOpenid);
    /**
     * 更新助力详情表
     * @author:     Logan
     * @date:       2018/11/19 14:44
     * @params:     [helpDetailId, helpStatus, fansStatus]
     * @return:     int
    **/
    public int updateActHelpDetailEntity(String helpDetailId,Integer helpStatus,Integer fansStatus);
}
