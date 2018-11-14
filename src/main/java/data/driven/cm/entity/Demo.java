package data.driven.cm.entity;

import java.util.Date;

/**
 * @author 何晋凯
 * @date 2018/06/04
 */
public class Demo {

    private Integer id;
    private String name;
    private Date createAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
