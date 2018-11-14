package data.driven.cm.common;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 何晋凯
 * @date 2018/07/01
 */
public class ApplicationSessionBean implements Serializable {

    private static final long serialVersionUID = 5457082755356746854L;

    private Map<String,Object> sessionMap = new java.util.HashMap<String,Object>();

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}