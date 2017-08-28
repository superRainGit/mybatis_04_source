package cn.zhang.plugin;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * 建立完插件后，需要对插件进行签名的动作，以告知插件拦截的是哪个对象的哪个方法
 * 	
 * @author zhang
 *
 */
@Intercepts({
	@Signature(type=StatementHandler.class,method="parameterize",args={java.sql.Statement.class})
})
public class FirstPlugin implements Interceptor {
	
	
	private static final Logger logger = LogManager.getLogger(FirstPlugin.class.getName());

	/**
	 * 拦截方法:指定拦截哪个对象的哪个方法
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		logger.info("FirstPlugin.intercept()" + invocation.getMethod());
		// 进行一个简单的插件的开发:偷梁换柱
		Object target = invocation.getTarget();
		MetaObject forObject = SystemMetaObject.forObject(target);
		Object value = forObject.getValue("parameterHandler.parameterObject");
		logger.info("取到的参数为" + value);
		forObject.setValue("parameterHandler.parameterObject", 3);
		Object proceed = invocation.proceed();
		return proceed;
	}

	/**
	 * 每次创建四大对象都会执行的方法:
	 * 	包装目标对象:包装，为目标对象创建一个代理对象
	 */
	@Override
	public Object plugin(Object target) {
		logger.info("FirstPlugin.plugin()" + target);
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
		logger.info("参数:" + properties);
	}

}
