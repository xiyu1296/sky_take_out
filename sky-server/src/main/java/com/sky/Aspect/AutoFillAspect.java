package com.sky.Aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.sky.constant.AutoFillConstant.*;
import static com.sky.enumeration.OperationType.INSERT;
import static com.sky.enumeration.OperationType.UPDATE;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut(){};
    @Before("autoFillPointcut()")
    public void AutoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MethodSignature ms = (MethodSignature)joinPoint.getSignature();
        AutoFill autofill = ms.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autofill.value();

        Object[] args = joinPoint.getArgs();
        if(args == null)return ;
        Object entity = args[0];

        LocalDateTime ldt = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if(operationType == INSERT){


            Method setcreattime = entity.getClass().getDeclaredMethod(SET_CREATE_TIME, LocalDateTime.class);
            Method setcreatuser = entity.getClass().getDeclaredMethod(SET_CREATE_USER, Long.class);
            Method setupdatetime = entity.getClass().getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);
            Method setupdateuser = entity.getClass().getDeclaredMethod(SET_UPDATE_USER, Long.class);

            setcreattime.invoke(entity,ldt);
            setcreatuser.invoke(entity,currentId);
            setupdatetime.invoke(entity,ldt);
            setupdateuser.invoke(entity,currentId);

        }
        else if(operationType == UPDATE){


            Method setupdatetime = entity.getClass().getDeclaredMethod(SET_UPDATE_TIME, LocalDateTime.class);
            Method setupdateuser = entity.getClass().getDeclaredMethod(SET_UPDATE_USER, Long.class);


            setupdatetime.invoke(entity,ldt);
            setupdateuser.invoke(entity,currentId);

        }


    }


}
