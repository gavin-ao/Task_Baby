package data.driven.cm.vo.wechat.mp;

import com.fasterxml.jackson.databind.ser.Serializers;

/**
 * @program: Task_Baby
 * @description: 链接消息
 *
 * 特别注意：微信相关的消息和响应实体不能遵循Java的命名规则以小写字母开头，，
 * 因为要便于后面实体转XML（以大写字母开头）
 *
 * @author: Logan
 * @create: 2018-11-20 22:58
 **/

public class LinkMessage extends BaseMessge {
    //标题
    private String Title;
    //描述
    private String Description;
    //链接
    private String Url;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
