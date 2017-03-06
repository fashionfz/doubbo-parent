package org.doubbo.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import com.alibaba.fastjson.JSONObject;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * AOP记录日志
 * @author ibm
 *
 */
@Aspect
public class LogInterceptor {
	
	@Pointcut("execution(* org.doubbo.user.service.PmsUserService.*(..))")
	private void myMethod(){}

	@Around("myMethod()")  
	public Object log(ProceedingJoinPoint pjp) throws Throwable{
		Map<String,Object> param = new HashMap<>();
		System.out.println(JSONObject.toJSONString(pjp.getArgs()));
		Object object = pjp.proceed();//执行该方法  
		Signature s = pjp.getSignature();
	    MethodSignature ms = (MethodSignature)s;
	    Method m = ms.getMethod();
	    CtMethod cm = getMethod(this.getClass(), pjp.getTarget().getClass().getName(), m.getName());
	    String[] paramNames = getFieldsName(cm);
	    Object[] paramValues = pjp.getArgs();
	    for(int i=0;i<paramNames.length;i++){
	    	param.put(paramNames[i], paramValues[i]);
	    }
	    OptLog annotation = (OptLog) cm.getAnnotation(OptLog.class);
	    if(annotation!=null){
	    	 String log = parseLog(annotation.value(), param);
	    	 System.out.println("over log" + log);
	    }
		return object;
	}
	
	private String parseLog(String tmp, Map<String,Object> param){
		String result = tmp;
		String pattern ="\\{[\\w,\\d,\\.]*\\}";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(tmp);
		int i =0;
		while(m.find(i)){
			String item = m.group();
			String name = item.substring(1, item.length()-1);
			String[] tmps = name.split("\\.");
			if(param.containsKey(tmps[0])){
				Object v = param.get(tmps[0]);
				for(int n = 1; n < tmps.length ; n++){
					v = getObject(v, tmps[n]);
				}
				result = result.replace(item, v.toString());
			}
			i= m.end();
		}
		return result;
	}
	
	/**
	 * 获取字段的值
	 * @param obj
	 * @param fielName
	 * @return
	 */
	private Object getObject(Object obj, String fielName){
		try {
			Field field = obj.getClass().getDeclaredField(fielName);
			boolean tmp = field.isAccessible();
			field.setAccessible(true);
			Object v = field.get(obj);
			field.setAccessible(tmp);
			return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取方法
	 * @param cls
	 * @param clazzName
	 * @param methodName
	 * @return
	 * @throws NotFoundException
	 */
	private CtMethod getMethod(Class<?> cls, String clazzName, String methodName) throws NotFoundException{
        ClassPool pool = ClassPool.getDefault();  
        ClassClassPath classPath = new ClassClassPath(cls);  
        pool.insertClassPath(classPath);  
        CtClass cc = pool.get(clazzName);  
        CtMethod cm = cc.getDeclaredMethod(methodName);  
        return cm;
	}
	
	/**
	 * 获取方法的参数名称
	 * @param cm
	 * @return
	 * @throws NotFoundException
	 */
	private String[] getFieldsName(CtMethod cm) throws NotFoundException{  
        MethodInfo methodInfo = cm.getMethodInfo();  
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
        if (attr == null) {  
            // exception  
        }  
        String[] paramNames = new String[cm.getParameterTypes().length];  
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
        for (int i = 0; i < paramNames.length; i++){  
            paramNames[i] = attr.variableName(i + pos); //paramNames即参数名  
        }  
        return paramNames;  
    }  
}
