package data.driven.cm.entity.taskBaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 图片文件信息Entity
 * @Date: 2018/11/15 11:11
 * @Version 1.0
 */
public class sysPictureEntity {

    /**
     * 图片主键
     */
    private String pictureId;

    /**
     * 图片存储路径
     */
    private String filePath;

    /**
     * 文件真实名称
     */
    private String realName;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建日期
     */
    private Date createAt;

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
