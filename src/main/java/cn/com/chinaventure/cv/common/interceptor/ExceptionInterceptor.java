package cn.com.chinaventure.cv.common.interceptor;

import cn.com.chinaventure.cv.entity.vo.JsonResult;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by YZP on 2018/6/5.
 */
public class ExceptionInterceptor  implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        Controller controller = invocation.getController();
        try {
            invocation.invoke();
        }
        catch(Exception e) {
            String msg = formatException(e);
            controller.renderJson(new JsonResult<Void>("1", msg));
        }
    }


    /**
     * 格式化异常信息，用于友好响应用户
     * @param e
     * @return
     */
    private static String formatException(Exception e){
        String message = null;
        Throwable ourCause = e;
        while ((ourCause = e.getCause()) != null) {
            e = (Exception) ourCause;
        }
        String eClassName = e.getClass().getName();
        //一些常见异常提示
        if("java.lang.NumberFormatException".equals(eClassName)){
            message = "请输入正确的数字";
        }else if ( e instanceof RuntimeException) {
            message = e.getMessage();
            if(StringUtils.isBlank(message))message = e.toString();
        }

        //获取默认异常提示
        if (StringUtils.isBlank(message)){
            message = "请求错误,请联系管理员";
        }
        //替换特殊字符
        message = message.replaceAll("\"", "'");
        return message;
    }
}
