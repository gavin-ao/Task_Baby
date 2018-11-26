package data.driven.cm.util.wx;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: Task_Baby
 * @description: XML解析工具类
 * @author: Logan
 * @create: 2018-11-25 20:10
 **/

public class XMLUtil {
    public static Map<String,String> parseXML(HttpServletRequest request) throws Exception{
        Map<String,String> map = new HashMap<String,String>();
        InputStream inputStream = request.getInputStream();
        try {
            //读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            //得到XML根元素
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            //遍历所有子节点
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
            return map;
        }finally {
            inputStream.close();
            inputStream = null;
        }
    }
}
