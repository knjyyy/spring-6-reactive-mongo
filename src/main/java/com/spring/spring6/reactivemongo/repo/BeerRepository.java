package com.spring.spring6.reactivemongo.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.spring.spring6.reactivemongo.domain.Beer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface  BeerRepository extends ReactiveMongoRepository<Beer, String> {
    Mono<Beer> findFirstByName(String name);
    Flux<Beer> findByStyle(String style);
}
