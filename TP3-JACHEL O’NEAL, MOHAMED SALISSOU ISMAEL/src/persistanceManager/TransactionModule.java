package persistanceManager;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import persistanceManager.Annotation.Transactional;

public class TransactionModule extends AbstractModule {

	@Override
	protected void configure() {
		TransManager tm = new TransManager();
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), tm);
	}
}
