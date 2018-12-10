package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @program: Task_Baby
 * @description: 活动有效性判断所需字段的实体
 * @author: Logan
 * @create: 2018-11-19 10:38
 **/

public class MatActivityStatusEntity {
    /**
     * 活动id
     */
    private String actId;
    /**
     * 活动关键词
     */
    private String actKeyWord;
    /**
     * 活动开始日期
     */
    private Date startAt;

    /**
     * 活动结束日期
     */
    private Date endAt;

    /**
     * 活动状态
     */
    private Integer status;
    /**
     * 老粉丝是否可以助力
     */
    private Integer oldFansCanHelp;

    public Integer getOldFansCanHelp() {
        return oldFansCanHelp;
    }

    public void setOldFansCanHelp(Integer oldFansCanHelp) {
        this.oldFansCanHelp = oldFansCanHelp;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActKeyWord() {
        return actKeyWord;
    }

    public void setActKeyWord(String actKeyWord) {
        this.actKeyWord = actKeyWord;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



}
