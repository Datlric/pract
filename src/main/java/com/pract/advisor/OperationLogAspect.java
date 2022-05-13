package com.pract.advisor;
//操作日志切面处理类

import com.pract.domain.OptLog;
import com.pract.mapper.OptLogMapper;
import com.pract.utils.IPUtils;
import com.pract.utils.JsonUtils;
import com.pract.utils.Result;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
public class OperationLogAspect {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    OptLogMapper optLogMapper;

    //在注解的位置切入代码
    @Pointcut("@annotation(com.pract.advisor.OperationLogAnnotation)")
    public void optLogPointCut() {
    }

    @AfterReturning(returning = "result", value = "optLogPointCut()")
    public void saveOptLog(JoinPoint joinPoint, Object result) throws Throwable {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        try {
            //将返回值转化为json,在转化为map
            String json = JsonUtils.objectToJson(result);
            Result pojo = JsonUtils.jsonToPojo(json, Result.class);
            OptLog optLog = new OptLog();
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在方法
            Method method = signature.getMethod();
            //获取操作
            OperationLogAnnotation annotation = method.getAnnotation(OperationLogAnnotation.class);
            if (annotation != null) {
                optLog.setModel(annotation.optModule());
                optLog.setType(annotation.optType());
                optLog.setDescription(annotation.optDesc());
            }
            //操作时间
            optLog.setOpt_time(Timestamp.valueOf(sdf.format(new Date())));
            //操作用户
            optLog.setUser_token(request.getHeader("userToken"));
            //IP信息
            optLog.setIp(IPUtils.getIpAddr(request));
            //返回值信息
            optLog.setResult("flag:" + pojo.getFlag() + ", code:" + pojo.getCode());
            //保存日志
            optLogMapper.saveOne(optLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
