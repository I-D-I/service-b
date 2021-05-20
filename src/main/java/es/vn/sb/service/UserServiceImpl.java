package es.vn.sb.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import brave.Span;
import brave.Tracer;
import es.vn.sb.model.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RestTemplate myRestTemplate;
    
    @Value("${service-c.user.url}")
    String userUrl;

    @Value("${service-c.users.url}")
    String usersUrl;
    
    @Autowired
	Tracer tracer;

	public User getUser(int id) {
		Span span = tracer.currentSpan();
		span.tag("service", "entrada al servicio");
		span.annotate(String.format("Llamada al servicio con url %s", userUrl));
		return myRestTemplate.getForEntity(userUrl+id, User.class).getBody();
	}
	
	public List<User> getUsers() {
		Span span = tracer.currentSpan();
		span.tag("service", "entrada al servicio");
		span.annotate(String.format("Llamada al servicio con url %s", usersUrl));
		return Arrays.asList(myRestTemplate.getForEntity(usersUrl, User[].class).getBody());
	}

    
}   
