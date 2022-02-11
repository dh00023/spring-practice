package dh0023.springmvc.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect Oriented Programming(@Aspect)
 * 공통 관심 사항(cross-cutting concern)을 분리하여 관리
 */
@Component
@Aspect
public class TimeTraceAop {


    /**
     * 패키지 하위 전체 실행
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* dh0023.springmvc..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        System.out.println("START : " + joinPoint.toString());
        try{
            Object result = joinPoint.proceed();
            return result;
        }finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END : " + joinPoint.toString() + " "+ timeMs +"ms");
        }

    }

}
