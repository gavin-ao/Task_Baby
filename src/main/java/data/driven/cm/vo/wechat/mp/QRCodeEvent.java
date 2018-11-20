package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 扫带参数二维码事件
 * MsgType =event
 * Event in {subscribe(为关注)，scan(已关注)}
 * @author: Logan
 * @create: 2018-11-20 23:06
 **/

public class QRCodeEvent extends BaseEvent {
    //事件Key值 事件KEY值，qrscene_为前缀，后面为二维码的参数值
    private String EventKey;
    //二维码的ticket，可用来换取二维码图片
    private String Ticket;

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }

    public String getTicket() {
        return Ticket;
    }

    public void setTicket(String ticket) {
        Ticket = ticket;
    }
}
