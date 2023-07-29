package com.spring.spring6.reactivemongo.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.spring.spring6.reactivemongo.model.BeerDTO;

public interface BeerService {
    Flux<BeerDTO> listBeers();
    Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDto);

    Mono<BeerDTO> saveBeer(BeerDTO beerDTO);
    Mono<BeerDTO> getById(String beerId);

    Mono<BeerDTO> updateBeer(String beerId, BeerDTO beerDTO);

    Mono<BeerDTO> patchBeer(String beerId, BeerDTO beerDTO);

    Mono<Void> deleteBeerById(String beerId);

    Mono<BeerDTO> findFirstByName(String name);

    Flux<BeerDTO> findByStyle(String style);
}
