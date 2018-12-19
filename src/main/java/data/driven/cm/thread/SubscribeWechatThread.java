package data.driven.cm.thread;

import data.driven.cm.business.taskbaby.SubscribeWeChatResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: Task_Baby
 * @description: 订阅号的处理线程
 * @author: Logan
 * @create: 2018-12-19 14:51
 **/

public class SubscribeWechatThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskBabyThread.class);
    private SubscribeWeChatResponseService wechatResponseService;
    private Map<String,String> map;
    private String appid;
    private HttpServletRequest request;
    public SubscribeWechatThread(HttpServletRequest request, SubscribeWeChatResponseService wechatResponseService, Map<String,String> map, String appid){
        super();
        this.wechatResponseService = wechatResponseService;
        this.map = map;
        this.appid = appid;
        this.request = request;
    }
    @Override
    public void run(){
        logger.info("-----------------线程调用--------------------");
        wechatResponseService.notify(request,map,appid);
        logger.info("-----------------线程调用完成--------------------");
    }

}
