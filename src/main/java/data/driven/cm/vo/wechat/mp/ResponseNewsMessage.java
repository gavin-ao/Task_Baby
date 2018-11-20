package data.driven.cm.vo.wechat.mp;

import java.util.List;

/**
 * @program: Task_Baby
 * @description: 回复图文消息
 * @author: Logan
 * @create: 2018-11-20 23:51
 **/

public class ResponseNewsMessage extends BaseResponseMessage {
    private int ArticleCount;
    private List<ArticleDesc> Articles;

    public int getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }

    public List<ArticleDesc> getArticles() {
        return Articles;
    }

    public void setArticles(List<ArticleDesc> articles) {
        Articles = articles;
    }
}
