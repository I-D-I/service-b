package es.vn.sb.service;

import java.util.List;

import es.vn.sb.model.User;

public interface UserService {

	public User getUser(int id);
	public List<User> getUsers();

}