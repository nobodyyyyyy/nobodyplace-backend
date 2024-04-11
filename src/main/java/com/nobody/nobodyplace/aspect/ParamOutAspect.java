package com.nobody.nobodyplace.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Aspect
@Slf4j
public class ParamOutAspect {


    //对包下所有的controller结尾的类的所有方法增强
    private final String executeExpr = "execution(* com.nobody.nobodyplace.controller.*Controller.*(..))";


    /**
     * @param joinPoint:
     * @Author: TheBigBlue
     * @Description: 环绕通知，拦截controller，输出请求参数、响应内容和响应时间
     * @Date: 2019/6/17
     * @Return:
     **/
//    @Around(executeExpr)
//    public Object processLog(ProceedingJoinPoint joinPoint) {
//        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        //获取方法名称
//        String methodName = method.getName();
//        //获取参数名称
//        LocalVariableTableParameterNameDiscoverer paramNames = new LocalVariableTableParameterNameDiscoverer();
//        String[] params = paramNames.getParameterNames(method);
//        //获取参数
//        Object[] args = joinPoint.getArgs();
//        //过滤掉request和response,不能序列化
//        List<Object> filteredArgs = Arrays.stream(args)
//                .filter(arg -> (!(arg instanceof HttpServletResponse)))
//                .collect(Collectors.toList());
//        JSONObject rqsJson = new JSONObject();
//        rqsJson.put("rqsMethod", methodName);
//        rqsJson.put("rqsTime", DateUtil.getCurrentFormatDateLong19());
//        if (ObjectIsNullUtil.isNullOrEmpty(filteredArgs)) {
//            rqsJson.put("rqsParams", null);
//        } else {
//            //拼接请求参数
//            Map<String, Object> rqsParams = IntStream.range(0, filteredArgs.size())
//                    .boxed()
//                    .collect(Collectors.toMap(j -> params[j], j -> filteredArgs.get(j)));
//            rqsJson.put("rqsParams", rqsParams);
//        }
//        LOGGER.info(methodName + "请求信息为：" + rqsJson.toJSONString());
//        Object resObj = null;
//        long startTime = System.currentTimeMillis();
//        try {
//            //执行原方法
//            resObj = joinPoint.proceed(args);
//        } catch (Throwable e) {
//            LOGGER.error(methodName + "方法执行异常!", e);
//            throw new BusinessException(methodName + "方法执行异常!");
//        }
//        long endTime = System.currentTimeMillis();
//        // 打印耗时的信息
//        this.printExecTime(methodName, startTime, endTime);
//        if (resObj != null) {
//            if (resObj instanceof JsonResponse) {
//                //输出响应信息
//                JsonResponse resJson = (JsonResponse) resObj;
//                LOGGER.info(methodName + "响应信息为：" + resJson.toString());
//                return resJson;
//            } else {
//                return resObj;
//            }
//        } else {
//            return JsonResponse.success();
//        }
//    }

}
