package es.vn.sb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import brave.Span;
import brave.Tracer;

@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    private RestTemplate myRestTemplate;
    
    @Value("${service-c.url}")
    String url;
    
    @Autowired
    Tracer tracer;

	public String helloDirect() {
		Span span = tracer.currentSpan();
		span.tag("service", "entrada al servicio");
		span.annotate(String.format("Llamada al servicio con url %s", url));
		return myRestTemplate.getForEntity(url, String.class).getBody();
	}
    
}   
