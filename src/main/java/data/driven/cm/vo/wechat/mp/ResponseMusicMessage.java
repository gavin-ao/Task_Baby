package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 回复音乐消息
 * @author: Logan
 * @create: 2018-11-20 23:49
 **/

public class ResponseMusicMessage extends BaseResponseMessage{
    private MusicDesc Music;

    public MusicDesc getMusic() {
        return Music;
    }

    public void setMusic(MusicDesc music) {
        Music = music;
    }
}
