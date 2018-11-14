package data.driven.cm.vo.material;

import data.driven.cm.entity.material.MatActivityEntity;

/**
 * 活动信息VO
 * @author hejinkai
 * @date 2018/8/8
 */
public class MatActivityVO extends MatActivityEntity {
    /** 活动图片地址 **/
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
