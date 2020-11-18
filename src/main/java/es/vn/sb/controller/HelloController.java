package es.vn.sb.controller;

import java.util.Map;

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

import brave.Span;
import brave.Tracer;
import es.vn.sb.service.HelloService;
import es.vn.sb.utils.Constants;
import es.vn.sb.utils.Utils;

@RestController
@RequestMapping("/hello")
public class HelloController {

	private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

	@Autowired
	HelloService helloService;

	@Value("#{systemEnvironment['VERSION']}")
	String serviceVersion;

	@Value("${spring.application.name}")
	private String appName;

	@Value("${spring.application.version}")
	private String appVersion;
	
	@Autowired
	Tracer tracer;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> hello(@RequestHeader Map<String, String> headers) {
		logger.info("START hello():");
		headers.forEach((key, value) -> {
			logger.info(String.format("Header '%s' = %s", key, value));
	    });
		return new ResponseEntity<String>(String.format("HELLO from '%s' in pod '%s'\n", appName, Utils.getPodName()),
				HttpStatus.OK);
	}

	@RequestMapping(path = "/version", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> version(
			@RequestHeader(value = "sprint", required = false, defaultValue = "0") String sprint) {
		logger.info("START hello(): sprint: " + sprint);

		return new ResponseEntity<String>(
				String.format("HELLO from '%s' in sprint: '%s', version: '%s' and pod: '%s'", appName, sprint,
						appVersion, Utils.getPodName()),
				HttpStatus.OK);
	}

	@RequestMapping(path = "/direct", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> helloDirect(@RequestHeader Map<String, String> headers) {
		logger.info("START helloDirect():");
		Span span = tracer.currentSpan();
		span.tag("controller", "entrada al controller");
//		headers.forEach((key, value) -> {
//			logger.info(String.format("Header '%s' = %s", key, value));
//	    });
		
		if (Constants.ERROR == 0) {
			span.annotate("Petición normal hacia servicio-c");
			return new ResponseEntity<String>(String.format("HELLO from '%s', version '%s' in pod '%s', pomversion '%s' and serviceversion '%s'\n%s", appName,
					appVersion, Utils.getPodName(), appVersion, serviceVersion, helloService.helloDirect()),
					HttpStatus.OK);
		}

		if (Utils.getRandomInt() == 1) {
			span.annotate("Generamos error en el servicio-b");
			return new ResponseEntity<String>(String.format("HELLO from '%s', version '%s' in pod '%s', pomversion '%s' and serviceversion '%s'", appName,
					appVersion, Utils.getPodName(), appVersion, serviceVersion),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			span.annotate("Petición sin error hacia servicio-c");
			return new ResponseEntity<String>(String.format("HELLO from '%s', version '%s' in pod '%s', pomversion '%s' and serviceversion '%s'\n'%s'", appName,
					appVersion, Utils.getPodName(), appVersion, serviceVersion, helloService.helloDirect()), HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/error/{error}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public HttpEntity<String> helloError(@PathVariable int error) {
		logger.info("START helloError():");
		Constants.ERROR = error;
		
		return new ResponseEntity<String>(String.format("ERROR value from '%s', version: '%s', pod: '%s' and error: '%d'\n", appName,
				appVersion, Utils.getPodName(), error),
				HttpStatus.OK);
	}

}
