package bassis_test;

import org.bs.model.User;

import bassis.bassis_bean.annotation.Aop;

public class UserServiceImpl implements UserService {

	@Override
	public User find() {
		// TODO Auto-generated method stub
		System.out.println("find=====");
		return new User();
	}

}
