package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 音乐描述
 * @author: Logan
 * @create: 2018-11-20 23:47
 **/

public class MusicDesc {
    private String Title;
    private String Description;
    //音乐链接
    private String MusicUrl;
    //高质量音乐，wifi环境优先使用该链接播放音乐
    private String  HQMusicUrl;
    private String ThumMediaId;

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

    public String getMusicUrl() {
        return MusicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        MusicUrl = musicUrl;
    }

    public String getHQMusicUrl() {
        return HQMusicUrl;
    }

    public void setHQMusicUrl(String HQMusicUrl) {
        this.HQMusicUrl = HQMusicUrl;
    }

    public String getThumMediaId() {
        return ThumMediaId;
    }

    public void setThumMediaId(String thumMediaId) {
        ThumMediaId = thumMediaId;
    }
}
