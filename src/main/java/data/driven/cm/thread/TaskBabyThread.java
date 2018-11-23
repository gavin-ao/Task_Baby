package data.driven.cm.thread;

import data.driven.cm.business.taskBaby.WechatResponseService;
import data.driven.cm.business.taskBaby.impl.WechatResponseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author: lxl
 * @describe
 * @Date: 2018/11/20 14:50
 * @Version 1.0
 */
public class TaskBabyThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskBabyThread.class);
    private WechatResponseService wechatResponseService;
    private Map<String,String> map;


    public TaskBabyThread(WechatResponseService wechatResponseService,Map<String,String> map){
        super();
        this.wechatResponseService = wechatResponseService;
        this.map = map;
    }

    public void run(){
        logger.info("-----------------线程调用--------------------");
        wechatResponseService.notify(map);
        logger.info("-----------------线程调用完成--------------------");
    }


}
