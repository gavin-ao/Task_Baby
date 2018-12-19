package data.driven.cm.Exception;

/**
 * @program: Task_Baby
 * @description: 空字段异常
 * @author: Logan
 * @create: 2018-12-19 18:52
 **/

public class NullFieldException extends RuntimeException {
    public NullFieldException(String msg){
        super(msg);
    }
}
