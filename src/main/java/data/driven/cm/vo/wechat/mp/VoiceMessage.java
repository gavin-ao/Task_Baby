package data.driven.cm.vo.wechat.mp;

/**
 * @program: Task_Baby
 * @description: 语音消息
 *
 * 特别注意：微信相关的消息和响应实体不能遵循Java的命名规则以小写字母开头，，
 * 因为要便于后面实体转XML（以大写字母开头）
 *
 * @author: Logan
 * @create: 2018-11-20 22:48
 **/

public class VoiceMessage extends BaseMessge {
    //语音消息媒体id，可以调用多媒体文件下载接口拉取数据。
    private String MediaId;
    //语音格式，如amr，speex等
    private String Format;
   //开通语音识别后XML会有此字段，语音识别结果
    private String Recognition;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getRecognition() {
        return Recognition;
    }

    public void setRecognition(String recognition) {
        Recognition = recognition;
    }
}
