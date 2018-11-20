package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 自定义菜单事件
 * @author: Logan
 * @create: 2018-11-20 23:19
 **/

public class MenuEvent extends  BaseEvent {
    private String EventKey;

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }
}
