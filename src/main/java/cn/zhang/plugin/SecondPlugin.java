package cn.zhang.plugin;

import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

/**
 * 建立完插件后，需要对插件进行签名的动作，以告知插件拦截的是哪个对象的哪个方法
 * 	
 * @author zhang
 *
 */
@Intercepts({
	@Signature(type=StatementHandler.class,method="parameterize",args={java.sql.Statement.class})
})
public class SecondPlugin implements Interceptor {

	/**
	 * 拦截方法:指定拦截哪个对象的哪个方法
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		System.out.println("SecondPlugin.intercept()" + invocation.getMethod().getName());
		Object proceed = invocation.proceed();
		return proceed;
	}

	/**
	 * 每次创建四大对象都会执行的方法:
	 * 	包装目标对象:包装，为目标对象创建一个代理对象
	 */
	@Override
	public Object plugin(Object target) {
		System.out.println("SecondPlugin.plugin()" + target);
		// 我们可以借助Plugin的warp方法使用当前Interceptor包装我们的目标对象
		Object wrap = Plugin.wrap(target, this);
		return wrap;
	}

	/**
	 * 将插件进行注册时property属性设置进来
	 * 	通过这个方法的properties属性就可以获取到那些属性
	 */
	@Override
	public void setProperties(Properties properties) {
		System.out.println("参数:" + properties);
	}

}
