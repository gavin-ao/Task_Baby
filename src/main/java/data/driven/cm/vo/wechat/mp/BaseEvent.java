package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 微信事件基类
 *
 * 特别注意：微信相关的消息和响应实体不能遵循Java的命名规则以小写字母开头，，
 * 因为要便于后面实体转XML（以大写字母开头）
 *
 * @author: Logan
 * @create: 2018-11-20 23:00
 **/

public class BaseEvent {
    //开发者微信号
    private String ToUserName;
    //发送方账号(一个OpenId)
    private String FromUserName;
    //消息创建时间
    private long CreateTime;
    //消息类型
    private String MsgType;
    //事件类型
    private String Event;

}
