package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 回复语音消息
 * @author: Logan
 * @create: 2018-11-20 23:41
 **/

public class ResponseVoiceMessage extends BaseResponseMessage {
    private VoiceDesc Voice;

    public VoiceDesc getVoice() {
        return Voice;
    }

    public void setVoice(VoiceDesc voice) {
        Voice = voice;
    }
}
