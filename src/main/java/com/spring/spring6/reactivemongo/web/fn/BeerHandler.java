package com.spring.spring6.reactivemongo.web.fn;

import com.mongodb.internal.connection.Server;
import com.spring.spring6.reactivemongo.model.BeerDTO;
import com.spring.spring6.reactivemongo.service.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;
    private final Validator validator;

    private void validate(BeerDTO beerDTO) {
        Errors errors = new BeanPropertyBindingResult(beerDTO,  "beerDTO");
        validator.validate(beerDTO, errors);

        if(errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> createNewBeer(ServerRequest request) {
        return beerService.saveBeer(request.bodyToMono(BeerDTO.class)
                        .doOnNext(this::validate))
                .flatMap(beerDTO -> ServerResponse.created(UriComponentsBuilder
                        .fromPath(BeerRouterConfig.BEER_PATH_ID)
                        .build(beerDTO.getId())).build());
    }

    public Mono<ServerResponse> getBeerById(ServerRequest request) {
        return ServerResponse.ok()
                .body(beerService.getById(request.pathVariable("id"))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        BeerDTO.class);
    }

    public Mono<ServerResponse> listBeers(ServerRequest request) {
        Flux<BeerDTO> flux;
        if(request.queryParam("style").isPresent()) {
            flux = beerService.findByStyle(request.queryParam("style").get());
        } else {
            flux = beerService.listBeers();
        }

        return ServerResponse.ok()
                .body(flux, BeerDTO.class);
    }

    public Mono<ServerResponse> updateBeer(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validate)
                .flatMap(beerDTO -> beerService.updateBeer(request.pathVariable("id"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(updateBeer -> ServerResponse.noContent().build());
    }

        public Mono<ServerResponse> patchBeer(ServerRequest request) {
            return request.bodyToMono(BeerDTO.class)
                    .doOnNext(this::validate)
                    .flatMap(beerDTO -> beerService.patchBeer(request.pathVariable("id"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(patchedBeer -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteBeer(ServerRequest request) {
        return beerService
                .getById(request.pathVariable("id"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beer -> beerService.deleteBeerById(beer.getId())
                .then(ServerResponse.noContent().build()));
    }
}
