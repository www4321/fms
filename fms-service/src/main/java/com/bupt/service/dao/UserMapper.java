package com.bupt.service.dao;

import com.bupt.service.bean.User;

import java.util.List;



public interface UserMapper {
	
	List<User> getAll();
	
	User getOne(String userName);

	void insert(User user);

	void update(User user);

	void delete(String userName);
}
