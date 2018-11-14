package data.driven.cm.component;

import java.util.List;

/**
 * 分页
 * @author 何晋凯
 * @date 2018/06/04
 */
public class Page<T> {

    private List<T> result;
    private PageBean pageBean;

    public Page() {
    }

    public Page(List<T> lstResult, PageBean pageBean)
    {
        this.result = lstResult;
        this.pageBean = pageBean;
    }

    public List getResult()
    {
        return this.result;
    }

    public void setResult(List lstResult)
    {
        this.result = lstResult;
    }

    public PageBean getPageBean()
    {
        return this.pageBean;
    }

    public void setPageBean(PageBean pageBean)
    {
        this.pageBean = pageBean;
    }
}
