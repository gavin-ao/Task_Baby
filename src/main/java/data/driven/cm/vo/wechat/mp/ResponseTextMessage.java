package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 回复文本消息
 * @author: Logan
 * @create: 2018-11-20 23:34
 **/

public class ResponseTextMessage extends  BaseResponseMessage {
    //消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
