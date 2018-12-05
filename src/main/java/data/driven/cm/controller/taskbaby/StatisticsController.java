package data.driven.cm.controller.taskbaby;

import data.driven.cm.business.taskbaby.ActivityHelpService;
import data.driven.cm.business.taskbaby.WechatUserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;

/**
 * @Author: lxl
 * @describe 相关统计Controller
 * @Date: 2018/12/3 17:39
 * @Version 1.0
 */
@Controller
@RequestMapping(path = "/wechatapi")
public class StatisticsController {
    Logger logger = LoggerFactory.getLogger(StatisticsController.class);

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
        //格式化小数
        DecimalFormat df = new DecimalFormat("0.00");

        ModelAndView mv = new ModelAndView("/taskbaby/data_statistics");
        /**
         * 活动参加人数,通过任何形式参加活动的人【活动关键词、海报扫码】
         */
        Integer activityTotalNumber = activityHelpService.getActivityTotalNumber(actId);
        mv.addObject("activityTotalNumber",activityTotalNumber);
        /**
         * 任务推广人数,通过活动关键词主动参加活动的人，非传播带来的人
         */
        Integer activityPromotionNumber = activityHelpService.getActivityPromotionNumber(actId);
        mv.addObject("activityPromotionNumber",activityPromotionNumber);

        /**
         * 任务完成人数,达到活动助力门槛
         */
        Integer activityCompletionNumber = activityHelpService.getActivityCompletionNumber(actId);
        mv.addObject("activityCompletionNumber",activityCompletionNumber);

        /**
         * 任务完成率, 任务完成人数/任务推广人数
         */
        String activityCompletionRate = "0%";
        if (activityPromotionNumber == 0){
            mv.addObject("activityCompletionRate",activityCompletionRate);
        }else{
            activityCompletionRate = df.format((float)activityCompletionNumber/activityPromotionNumber*100)+"%";
            mv.addObject("activityCompletionRate",activityCompletionRate);
        }
        /**
         * 活动拉新人数,通过本次活动带来的新粉丝
         * 微信用户表中加活动id，此id只显示通过扫码进来的，从新关注的时候需要重置活动id,如果没有活动id置空。
         */
        Integer activityAddNumber = wechatUserInfoService.getActivityAddNumber(actId);
        mv.addObject("activityAddNumber",activityAddNumber);

        /**
         * 活动取关人数,参加本次活动活动后取关的人数
         */
        Integer activityTakeOffNumber = wechatUserInfoService.getActivityTakeOffNumber(actId);
        mv.addObject("activityTakeOffNumber",activityTakeOffNumber);

        /**
         * 活动净增人数,净增人数= 拉新人数-取关人数,相当于本活动关注人数
         */
        mv.addObject("activityNetIncreaseNumber",activityAddNumber-activityTakeOffNumber);

        /**
         * 活动裂变率, 裂变率=拉新人数/活动推广人数
         */
        String activityFissionRate = "0%";
        if (activityPromotionNumber == 0){
            mv.addObject("activityFissionRate",activityFissionRate);
        }else{
            activityFissionRate = df.format((float)activityAddNumber/activityPromotionNumber*100)+"%";
            mv.addObject("activityFissionRate",activityFissionRate);
        }
        /**
         * 今日拉新人数
         * 用户表，通过原始id得到（今日开始和今日结束时间）
         */
        Integer todayAddActivityNumber = wechatUserInfoService.getTodayAddActivityNumber(wechatAccount);
        mv.addObject("todayAddActivityNumber",todayAddActivityNumber);
        /**
         * 今日取消
         * 用户表，通过原始id得到and 关注状态为0（今日开始和今日结束时间）
         */
        Integer getTodayActivityTakeOffNumber = wechatUserInfoService.getTodayActivityTakeOffNumber(wechatAccount);
        mv.addObject("getTodayActivityTakeOffNumber",getTodayActivityTakeOffNumber);
        /**
         * 今日净增
         * 用户表，通过原始id得到and 关注状态为1,新关注的（今日开始和今日结束时间）
         */
        mv.addObject("todayActivityNetIncreaseNumber",todayAddActivityNumber-getTodayActivityTakeOffNumber);

        /**
         * 累积关注
         * 用户表,原始id and 关注状态为1
         */
        Integer totalFollowNumber = wechatUserInfoService.getTotalFollowNumber(wechatAccount);
        mv.addObject("totalFollowNumber",totalFollowNumber);

        /**
         * 昨日拉新人数
         * 通过原始id（昨天开始和昨天结束时间)
         */
        Integer yesterdayddActivityNumber = wechatUserInfoService.getYesterdayAddActivityNumber(wechatAccount);
        mv.addObject("yesterdayddActivityNumber",yesterdayddActivityNumber);
        /**
         * 昨日取消
         * 通过原始id（昨天开始和昨天结束时间)
         */
        Integer yesterdayActivityTakeOffNumber = wechatUserInfoService.getYesterdayActivityTakeOffNumber(wechatAccount);
        mv.addObject("yesterdayActivityTakeOffNumber",yesterdayActivityTakeOffNumber);
        /**
         * 昨日净增
         * 通过原始id（昨天开始和昨天结束时间)
         */
        mv.addObject("yesterdayActivityNetIncreaseNumber",yesterdayddActivityNumber-yesterdayActivityTakeOffNumber);
        /**
         * 昨日累积关注
         * 通过原始id 今天开始时间
         */
        Integer yesterdayTotalFollowNumber = wechatUserInfoService.getYesterdayTotalFollowNumber(wechatAccount);
        mv.addObject("yesterdayTotalFollowNumber",yesterdayTotalFollowNumber);
        return mv;
    }
}
