package my.test.project.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import my.test.project.Ball;
import my.test.project.Player;

@ExtendWith(ServiceExtension.class)
@ExtendWith(BundleContextExtension.class)
public class PlayerTest {

	static Ball b;
	
	@InjectBundleContext
	BundleContext bc;
	
	@BeforeAll
	static void beforeAll(@InjectBundleContext BundleContext staticBC) {
		b = mock(Ball.class);
		staticBC.registerService(Ball.class, b, null);
		
	}
	
	@InjectService
	Player p;
	
	@Test
	void myTest() {
		assertThat(p).isNotNull();
		assertThat(p.getBall()).isSameAs(b);
		verifyNoInteractions(b);
		p.kickBall();
		verify(b).kick();
	}
	
	static class DummyBall implements Ball {

		@Override
		public void inflate() {
		}

		@Override
		public void kick() {
		}
	}
	
	@Test
	void myServiceAwareTest(@InjectService(cardinality = 0) ServiceAware<Ball> ball) {
		assertThat(ball.getServices()).hasSize(1);
		bc.registerService(Ball.class, new DummyBall(), null);
		assertThat(ball.getServices()).hasSize(2);
		bc.registerService(Ball.class, new DummyBall(), null);
		assertThat(ball.getServices()).hasSize(3);
	}
	
	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	class TwoBalls {
		Ball b2;
		
		@BeforeAll
		void beforeAll(@InjectBundleContext BundleContext bc) {
			b2 = mock(Ball.class);
//			BundleContext unmanaged = FrameworkUtil.getBundle(TwoBalls.class).getBundleContext();
//			unmanaged.registerService(Ball.class, b2, null);
			bc.registerService(Ball.class, b2, null);
		}

		@Test
		void twoBallTest(@InjectService(cardinality = 2) List<Ball> services) {
			assertThat(services)
				.containsExactlyInAnyOrder(b, b2);
		}
	}

	// This test should fail - proves that the second ball registered
	// in the nested test gets cleaned up.
//	@Test
//	void myDoubleTest(@InjectService(cardinality = 2) List<Ball> balls) {
//	}
}
