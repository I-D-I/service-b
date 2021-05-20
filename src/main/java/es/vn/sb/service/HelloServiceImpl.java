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
    String urlServiceC;
    
    @Value("${service-python.url}")
    String urlServicePython;
    
    @Autowired
    Tracer tracer;

	public String helloDirect() throws Exception {
		Span span = tracer.currentSpan();
		span.tag("service", "entrada al servicio");
		span.annotate(String.format("Llamada al servicio con url %s", urlServiceC));
		
		StringBuffer retornoServicios = new StringBuffer(myRestTemplate.getForEntity(urlServiceC, String.class).getBody());
		
		span.annotate(String.format("Llamada al servicio con url %s", urlServicePython));
		
		retornoServicios.append("\n").append(myRestTemplate.getForObject(urlServicePython, String.class));
		
		return retornoServicios.toString();
	}
    
}   
