package data.driven.cm.entity.wechat;

/**
 * @program: Task_Baby
 * @description: 微信客服文本消息内容实体
 * @author: Logan
 * @create: 2018-11-16 14:27
 **/

public class WechatCSTxtContentEntity {
    private  String content;
    public WechatCSTxtContentEntity(String content){
        this.content =content;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
