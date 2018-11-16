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
     * @return
     */
    public String insertActHelpDetailEntity(String helpId,Integer helpStatus,Integer fansStatus,String actId);

}
