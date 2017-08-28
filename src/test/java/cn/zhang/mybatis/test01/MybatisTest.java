package cn.zhang.mybatis.test01;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.zhang.beans.Employee;
import cn.zhang.dao.EmployeeMapper;

public class MybatisTest {

	public SqlSessionFactory getSqlSessionFactory() throws IOException {
		String resource = "mybatis-config.xml";
		InputStream in = Resources.getResourceAsStream(resource);
		return new SqlSessionFactoryBuilder().build(in);
	}

	/**
	 * 1、获取SqlSessionFactory对象:
	 * 解析每一个信息保存在Configuration对象中，返回包含Configuration的DefaultSqlSessionFactory对象
	 * 注意:MappedStatement:代表一个增删改查的详细信息
	 * 
	 * 2、获取SqlSession对象: 返回一个DefaultSqlSession对象，包含Executor和Configuration
	 * 这一个会创建Executor对象:注意:此处有一个InteceptorChain拦截器链
	 * 
	 * 3、根据接口类型返回对应的Mapper接口对象 返回一个MapperProxy代理对象，包含了一个DefaultSqlSession对象
	 * DefaultSqlSession又包含了（Executor）执行器对象
	 * 
	 * @throws IOException
	 */
	@Test
	public void test2() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
			Employee empById = mapper.getEmpById(1);
			System.out.println(empById);
		} finally {
			sqlSession.close();
		}
	}

	/**
	 * 插件原理 四大对象创建的时候 1、每个创建出来的对象不是直接返回的，而是调用
	 * interceptorChain.pluginAll(parameterHandler)
	 * 2、获取到所有的Interceptor(拦截器)[插件所需要实现的接口]
	 * 调用interceptor.plugin(target)返回target包装后的对象
	 * 3、插件机制:我们可以使用插件为目标对象创建一个代理对象:AOP(面向切面) 我们的插件可以为四大插件创建出代理对象
	 * 代理对象就可以拦截到四大对象的每一个执行 public Object pluginAll(Object target) { for
	 * (Interceptor interceptor : interceptors) { target =
	 * interceptor.plugin(target); } return target; }
	 */
	/**
	 * 开发插件的过程以及步骤 1、首先做出一个Interceptor的实现类 2、使用@Intercepts签名注解 3、将插件注册到全局配置文件中
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPlugin() throws IOException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
			PageHelper.startPage(2, 5);
			List<Employee> emps = mapper.getEmps();
			PageInfo<Employee> info = new PageInfo<>(emps);
			for (Employee emp : emps) {
				System.out.println(emp);
			}
			System.out.println(info.getPageNum());
			System.out.println(info.isIsFirstPage());
		} finally {
			sqlSession.close();
		}
	}

	@Test
	public void testBatchSqlSession() throws IOException, InterruptedException {
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		// 创建批量的SqlSession
		SqlSession sqlSession = sqlSessionFactory.openSession();
		long start = System.currentTimeMillis();
		try {
			EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
			for (int i = 0; i < 10000; i++) {
				mapper.addEmp(new Employee(null, UUID.randomUUID().toString().substring(0, 5), "A", "1"));
			}
			sqlSession.commit();
			long end = System.currentTimeMillis();
			System.out.println("插入10000条语句需要的时间:" + (end - start)); // 批量:1954     非批量:2824
		} finally {
			sqlSession.close();
		}
	}

}
