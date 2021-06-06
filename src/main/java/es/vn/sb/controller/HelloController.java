package es.vn.sb.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import brave.Span;
import brave.Tracer;
import es.vn.sb.service.HelloService;
import es.vn.sb.utils.Constants;
import es.vn.sb.utils.Utils;
import io.micrometer.core.annotation.Timed;

@RestController
@RequestMapping("/hello")
public class HelloController {

	private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

	@Autowired
	HelloService helloService;

	@Value("${spring.application.name}")
	private String appName;

	@Value("${spring.application.version}")
	private String appVersion;
	
	@Autowired
	Tracer tracer;
	
	@Autowired
    private HttpServletRequest request;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> hello(@RequestHeader Map<String, String> headers) {
		logger.info("START hello():");
		headers.forEach((key, value) -> {
			logger.info(String.format("Header '%s' = %s", key, value));
	    });
		return new ResponseEntity<String>(String.format("HELLO from '%s' with context path '%s'\n", appName,  
				request.getRequestURI()), HttpStatus.OK);
	}

	@RequestMapping(path = "/version", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> version(
			@RequestHeader(value = "sprint", required = false, defaultValue = "0") String sprint) {
		logger.info("START hello(): sprint: " + sprint);

		return new ResponseEntity<String>(
				String.format("HELLO from '%s' in sprint: '%s', version: '%s' with context path '%s'", appName, sprint,
						appVersion, request.getRequestURI()),
				HttpStatus.OK);
	}

	@RequestMapping(path = "/direct", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> helloDirect(@RequestHeader Map<String, String> headers) {
		logger.info("peticion_iniciada");
		Span span = tracer.currentSpan();
		span.tag("controller", "entrada al controller");

		try {
			if (Constants.ERROR == 0) {
				span.annotate("Petición normal hacia servicio-c");
				return new ResponseEntity<String>(String.format("OK from '%s', version '%s'\n%s", appName,
						appVersion, helloService.helloDirect()), HttpStatus.OK);
			}

			if (Utils.getRandomInt() == 1) {
				span.annotate("Generamos error en el servicio-b");
				return new ResponseEntity<String>(String.format("HELLO from '%s', version '%s'", appName,
						appVersion), HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				span.annotate("Petición sin error hacia servicio-c");
				return new ResponseEntity<String>(String.format("HELLO from '%s', version '%s'\n'%s'", appName,
						appVersion, helloService.helloDirect()), HttpStatus.OK);
			}
		} catch (HttpClientErrorException e) {
			span.annotate("Petición con error hacia servicio-c");
			logger.error(String.format("Exception: %s", e.getLocalizedMessage()));
			return new ResponseEntity<String>(String.format("KO from '%s', version '%s'\t%s", appName,
					appVersion, "ERROR en el flujo de peticiones llamando al service-c"), e.getStatusCode());
		} catch (Exception e) {
			span.annotate("Petición con error hacia servicio-c");
			logger.error(String.format("Exception: %s", e.getLocalizedMessage()));
			return new ResponseEntity<String>(String.format("KO from '%s', version '%s'\t'%s'", appName,
					appVersion, "ERROR en el flujo de peticiones llamando al service-c"), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@RequestMapping(path = "/error", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> error() {
		logger.info("START error():");
		
		if (Constants.ERROR == 0) {
			return new ResponseEntity<String>(String.format("ERROR value from '%s', version '%s' and error '%d'", appName,
					appVersion, Constants.ERROR), HttpStatus.OK);
		} 
		return new ResponseEntity<String>(String.format("ERROR value from '%s', version '%s' and error '%d'", appName,
				appVersion, Constants.ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(path = "/error/{error}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> helloError(@PathVariable int error) {
		logger.info("START helloError():");
		Constants.ERROR = error;
		
		return new ResponseEntity<String>(String.format("ERROR value from '%s', version '%s', error '%d'", appName,
				appVersion, error),	HttpStatus.OK);
	}

}
