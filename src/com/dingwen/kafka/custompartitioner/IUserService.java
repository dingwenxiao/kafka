package com.dingwen.kafka.custompartitioner;

import java.util.List;

public interface IUserService {
	public Integer findUserId(String userName);
	public List<String> findAllUsers();
}
