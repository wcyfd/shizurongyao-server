package com.randioo.shizurongyao_server.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

//@Aspect
//@Service
public class GameTaskAspect {

	private static final String FILTER = "execution(public * com.randioo.sihao_server.module..*Action.*(..))"
			+ " && !execution(public void com.randioo.sihao_server.module..*Impl.init())"
			+ " && !execution(public void com.randioo.sihao_server.module..*Impl.initService())";

	private static final String FILTER2 = "execution(public * com.randioo.sihao_server.module..*Action.*(..))";
	
//	@After(FILTER2)
	public void after(JoinPoint joinPoint) {
//		String methodName = joinPoint.getSignature().getName();
//		System.out.println("任务检查处理 " + methodName);
	}
	
//	@Around("execution(public * com.randioo.sihao_server.module..*Action.*(..))")
	public void money(ProceedingJoinPoint pjp){
		try {
			System.out.println("money before");
			pjp.proceed();
			System.out.println("money after");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}
