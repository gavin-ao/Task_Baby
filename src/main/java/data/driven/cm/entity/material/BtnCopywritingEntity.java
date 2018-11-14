package data.driven.cm.entity.material;

/**
 * 按钮文案
 * @author hejinkai
 * @date 2018/8/26
 */
public class BtnCopywritingEntity {
    /** 主键 **/
    private String id;
    /** 活动主键 **/
    private String actId;
    /** 按钮显示文本 **/
    private String btnText;
    /** 按钮编码 **/
    private String btnCode;
    /** 创建时间 **/
    private String createAt;
    /** 创建人 **/
    private String creator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnCode() {
        return btnCode;
    }

    public void setBtnCode(String btnCode) {
        this.btnCode = btnCode;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
