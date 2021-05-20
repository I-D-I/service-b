package es.vn.sb.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import brave.Span;
import brave.Tracer;
import es.vn.sb.model.User;
import es.vn.sb.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	Tracer tracer;
	
	@Autowired
	UserService userService;
	
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public HttpEntity<User> getUser(@PathVariable("id") Integer id) {
		log.info("START getUser():");
		Span span = tracer.currentSpan();
		span.tag("controller", "entrada al controller");
		return new ResponseEntity<User>(userService.getUser(id), HttpStatus.OK);
	}

	@RequestMapping(path = "/users", method = RequestMethod.GET)
	public HttpEntity<List<User>> getUsers(@RequestHeader Map<String, String> headers) {
		log.info("START getUsers():");
		Span span = tracer.currentSpan();
		span.tag("controller", "entrada al controller");
		return new ResponseEntity<List<User>>(userService.getUsers(), HttpStatus.OK);
	}
}
