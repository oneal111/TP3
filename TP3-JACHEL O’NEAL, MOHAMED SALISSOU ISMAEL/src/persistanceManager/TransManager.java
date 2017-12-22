package persistanceManager;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TransManager implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("transaction...");
		PersistanceManager db = Reflection.getInstance().getDB();
		db.getConnection().setAutoCommit(false);
		Object ob = invocation.proceed();
		db.getConnection().commit();
		db.getConnection().setAutoCommit(true);
		return ob;
	}
}
