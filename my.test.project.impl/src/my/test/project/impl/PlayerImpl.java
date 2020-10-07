package my.test.project.impl;

import my.test.project.Ball;
import my.test.project.Player;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class PlayerImpl implements Player {

	@Reference
	Ball ball;
	
	@Override
	public void kickBall() {
		ball.kick();
	}

	@Override
	public Ball getBall() {
		return ball;
	}
}
