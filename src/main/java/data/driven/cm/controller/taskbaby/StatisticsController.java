package data.driven.cm.controller.taskbaby;

import data.driven.cm.business.taskbaby.ActivityHelpService;
import data.driven.cm.business.taskbaby.WechatUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: lxl
 * @describe 相关统计Controller
 * @Date: 2018/12/3 17:39
 * @Version 1.0
 */
@Controller
@RequestMapping(path = "/wechatapi")
public class StatisticsController {

    /**
     * 活动助力Service
     */
    @Autowired
    private ActivityHelpService activityHelpService;

    /**
     * 微信用户Service
     */
    @Autowired
    private WechatUserInfoService wechatUserInfoService;

    /**
     * @description 通过活动id 得到当前活动的统计信息
     * @author lxl
     * @date 2018-12-03 17:48
     * @param actId 活动id
     * @return ModelAndView 跳转的页面并增加数
     */
    @GetMapping(value = "/statistics/{wechatAccount}/{actId}")
    public ModelAndView dataStatistics(@PathVariable String wechatAccount,@PathVariable String actId){
        /**
         * 活动参加人数
         */
        Integer activityTotalNumber = activityHelpService.getActivityTotalNumber(actId);
        /**
         * 任务推广人数
         */
        Integer activityPromotionNumber = activityHelpService.getActivityPromotionNumber(actId);

        /**
         * 任务完成人数
         */
        Integer activityCompletionNumber = activityHelpService.getActivityCompletionNumber(actId);

        /**
         * 任务完成率, 任务完成人数/任务推广人数
         */

        /**
         * 活动拉新人数
         */
        Integer activityAddNumber = wechatUserInfoService.getActivityAddNumber(actId);

        /**
         * 活动取关人数
         */
        Integer activityTakeOffNumber = wechatUserInfoService.getActivityTakeOffNumber(actId);

        /**
         * 活动净增人数
         */
        Integer activityNetIncreaseNumber = wechatUserInfoService.getActivityNetIncreaseNumber(actId);

        /**
         * 活动裂变率, 裂变率=拉新人数/活动推广人数
         */

        /**
         * 今日拉新人数
         */
        Integer todayAddActivityNumber = wechatUserInfoService.getTodayAddActivityNumber(wechatAccount);

        /**
         * 今日取消
         */
        Integer getTodayActivityTakeOffNumber = wechatUserInfoService.getTodayActivityTakeOffNumber(wechatAccount);

        /**
         * 今日净增
         */
        Integer todayActivityNetIncreaseNumber = wechatUserInfoService.getTodayActivityNetIncreaseNumber(wechatAccount);

        /**
         * 累计关注
         */
        Integer totalFollowNumber = wechatUserInfoService.getTotalFollowNumber(wechatAccount);

        /**
         * 昨日拉新人数
         */
        Integer yesterdayddActivityNumber = wechatUserInfoService.getYesterdayAddActivityNumber(wechatAccount);

        /**
         * 昨日取消
         */
        Integer yesterdayActivityTakeOffNumber = wechatUserInfoService.getYesterdayActivityTakeOffNumber(wechatAccount);

        /**
         * 昨日净增
         */
        Integer yesterdayActivityNetIncreaseNumber = wechatUserInfoService.getYesterdayActivityNetIncreaseNumber(wechatAccount);

        /**
         * 昨昨累计关注
         */
        Integer yesterdayTotalFollowNumber = wechatUserInfoService.getYesterdayTotalFollowNumber(wechatAccount);

        return null;
    }
}
