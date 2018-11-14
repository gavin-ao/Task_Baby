package data.driven.cm.component;

/**
 * @Author: lxl
 * @describe 微信公众号信息类型
 * @Date: 2018/11/12 16:23
 * @Version 1.0
 */
public enum MsgType {
    text("文本"),image("图片"),voice("语音"),video("视频"),shortvideo("小视频"),location("地理位置"),link("连接"),event("事件"),news("图文信息");

    private String value;

    public String getValue() {
        return value;
    }

    private MsgType(String value) {
        this.value = value;
    }
}
