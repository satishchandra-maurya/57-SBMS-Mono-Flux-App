package in.satish.rest;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import in.satish.binding.CustomerEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
public class CustomerEventController {

	@GetMapping(value= "/event", produces="application/json")
	public ResponseEntity<Mono<CustomerEvent>> getEvent(){
		
		CustomerEvent event = new CustomerEvent("Ashok", new Date());
		
		Mono<CustomerEvent> CustomerMono = Mono.just(event);
		
		return new ResponseEntity<>(CustomerMono, HttpStatus.OK);
	}
	
	@GetMapping(value="/events", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<CustomerEvent>> getEvents(){
		
		// creating Binding Object with data
		CustomerEvent event = new CustomerEvent("Satish", new Date());
		
		//create stream for binding object
		 Stream<CustomerEvent> customerStream = Stream.generate(() -> event);
		// creating flux object using stream 
		 Flux<CustomerEvent> cFlux = Flux.fromStream(customerStream);
		 
		 // setting response interval 
		 Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(5));
		 
		 //combine interval flux and customer event flux
		 Flux<Tuple2<Long, CustomerEvent>> zip = Flux.zip(intervalFlux, cFlux);
		 
		 //getting Tuple value as T2
		 Flux<CustomerEvent> fluxMap = zip.map(Tuple2::getT2);
		 
		 // sending the response
		 
		 return new ResponseEntity<> (fluxMap, HttpStatus.OK);
		 
	}
}
