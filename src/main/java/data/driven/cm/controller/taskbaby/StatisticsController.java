package data.driven.cm.controller.taskbaby;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskbaby.ActivityHelpService;
import data.driven.cm.business.taskbaby.WechatUserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
     * @param actId 活动id
     * @return ModelAndView 跳转的页面并增加数
     * @description 通过活动id 得到当前活动的统计信息
     * @author lxl
     * @date 2018-12-03 17:48
     */
    @GetMapping(value = "/statistics/{wechatAccount}/{actId}/info")
    public ModelAndView dataStatistics(@PathVariable String wechatAccount, @PathVariable String actId) {
        //格式化小数
        DecimalFormat df = new DecimalFormat("0.00");

        ModelAndView mv = new ModelAndView("/taskbaby/data_statistics");
        /**
         * 活动参加人数,通过任何形式参加活动的人【活动关键词、海报扫码】
         */
        Integer activityTotalNumber = activityHelpService.getActivityTotalNumber(actId);
        mv.addObject("activityTotalNumber", activityTotalNumber);
        /**
         * 任务推广人数,通过活动关键词主动参加活动的人，非传播带来的人
         */
        Integer activityPromotionNumber = activityHelpService.getActivityPromotionNumber(actId);
        mv.addObject("activityPromotionNumber", activityPromotionNumber);

        /**
         * 任务完成人数,达到活动助力门槛
         */
        Integer activityCompletionNumber = activityHelpService.getActivityCompletionNumber(actId);
        mv.addObject("activityCompletionNumber", activityCompletionNumber);

        /**
         * 任务完成率, 任务完成人数/任务推广人数
         */
        String activityCompletionRate = "0%";
        if (activityPromotionNumber == 0) {
            mv.addObject("activityCompletionRate", activityCompletionRate);
        } else {
            activityCompletionRate = df.format((float) activityCompletionNumber / activityPromotionNumber * 100) + "%";
            mv.addObject("activityCompletionRate", activityCompletionRate);
        }

        /**
         * 活动统计数据，此信息来源于act_help表,现采用视图
         */
//        Map<String,Object> activityDataMap = activityHelpService.getActivityData(actId);
//        mv.addObject("activityTotalNumber",activityDataMap.get("activityTotalNumber"));
//        mv.addObject("activityPromotionNumber",activityDataMap.get("activityPromotionNumber"));
//        mv.addObject("activityCompletionNumber",activityDataMap.get("activityCompletionNumber"));
//        mv.addObject("activityCompletionRate",activityDataMap.get("activityCompletionRate"));

        /**
         * 活动拉新人数,通过本次活动带来的新粉丝
         * 微信用户表中加活动id，此id只显示通过扫码进来的，从新关注的时候需要重置活动id,如果没有活动id置空。
         */
        Integer activityAddNumber = wechatUserInfoService.getActivityAddNumber(actId);
        mv.addObject("activityAddNumber", activityAddNumber);

        /**
         * 活动取关人数,参加本次活动活动后取关的人数
         */
        Integer activityTakeOffNumber = wechatUserInfoService.getActivityTakeOffNumber(actId);
        mv.addObject("activityTakeOffNumber", activityTakeOffNumber);

        /**
         * 活动净增人数,净增人数= 拉新人数-取关人数,相当于本活动关注人数
         */
        mv.addObject("activityNetIncreaseNumber", activityAddNumber - activityTakeOffNumber);

        /**
         * 活动裂变率, 裂变率=拉新人数/活动推广人数
         */
        String activityFissionRate = "0%";
//        Integer activityPromotionNumber = Integer.parseInt(activityDataMap.get("activityPromotionNumber").toString());
        if (activityPromotionNumber == 0) {
            mv.addObject("activityFissionRate", activityFissionRate);
        } else {
            activityFissionRate = df.format((float) activityAddNumber / activityPromotionNumber * 100) + "%";
            mv.addObject("activityFissionRate", activityFissionRate);
        }

        /**
         * 累积关注
         * 用户表,原始id and 关注状态为1
         */
        Integer totalFollowNumber = wechatUserInfoService.getTotalFollowNumber(wechatAccount);
        mv.addObject("totalFollowNumber", totalFollowNumber);
        return mv;
    }

    /**
     * @return
     * @description 得到 今日、昨日、近7日数据
     * @author lxl
     * @date 2018-12-06 10:05
     */
    @ResponseBody
    @PostMapping(value = "/statistics/{wechatAccount}/{actId}/day")
    public JSONObject dayDataStatistics(@PathVariable String wechatAccount, @PathVariable String actId, @RequestParam(value = "type") String type) {
        JSONObject json = new JSONObject();
        Integer todayAddActivityNumber;
        Integer getTodayActivityTakeOffNumber;
        switch (type) {
            case "all":
                /**
                 * 活动拉新人数,通过本次活动带来的新粉丝
                 * 微信用户表中加活动id，此id只显示通过扫码进来的，从新关注的时候需要重置活动id,如果没有活动id置空。
                 */
                 todayAddActivityNumber = wechatUserInfoService.getActivityAddNumber(actId);
                json.put("todayAddActivityNumber", todayAddActivityNumber);
                /**
                 * 活动取关人数,参加本次活动活动后取关的人数
                 */
                getTodayActivityTakeOffNumber = wechatUserInfoService.getActivityTakeOffNumber(actId);
                json.put("getTodayActivityTakeOffNumber", getTodayActivityTakeOffNumber);
                /**
                 * 活动净增人数,净增人数= 拉新人数-取关人数,相当于本活动关注人数
                 */
                json.put("todayActivityNetIncreaseNumber", todayAddActivityNumber - getTodayActivityTakeOffNumber);
                return json;
            default:
                /**
                 * 今日拉新人数
                 * 用户表，通过原始id得到（今日开始和今日结束时间）
                 */
                todayAddActivityNumber = wechatUserInfoService.getTodayAddActivityNumber(wechatAccount,actId, type);
                json.put("todayAddActivityNumber", todayAddActivityNumber);
                /**
                 * 今日取消
                 * 用户表，通过原始id得到and 关注状态为0（今日开始和今日结束时间）
                 */
                getTodayActivityTakeOffNumber = wechatUserInfoService.getTodayActivityTakeOffNumber(wechatAccount,actId, type);
                json.put("getTodayActivityTakeOffNumber", getTodayActivityTakeOffNumber);
                /**
                 * 今日净增
                 * 用户表，通过原始id得到and 关注状态为1,新关注的（今日开始和今日结束时间）
                 */
                json.put("todayActivityNetIncreaseNumber", todayAddActivityNumber - getTodayActivityTakeOffNumber);
                return json;
        }
    }
}
