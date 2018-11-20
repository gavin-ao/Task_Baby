package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 图文内容
 * @author: Logan
 * @create: 2018-11-20 23:51
 **/

public class ArticleDesc {
    private String Title;
    private String Description;
    //图片链接。支持JPG，PNG，效果较好的大图640pix*320pix，小图80pix*80pix
    private String PicUrl;
    //点击图文消息跳转
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

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
