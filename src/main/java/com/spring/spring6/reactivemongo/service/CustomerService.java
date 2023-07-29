package com.spring.spring6.reactivemongo.service;

import com.spring.spring6.reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDTO> listCustomers();
    Mono<CustomerDTO> getById(String id);

    Mono<CustomerDTO> saveCustomer(Mono<CustomerDTO> customerDTO);

    Mono<CustomerDTO> updateCustomer(String id, CustomerDTO customerDTO);

    Mono<CustomerDTO> patchCustomer(String id, CustomerDTO customerDTO);

    Mono<Void> deleteCustomer(String id);

    Flux<CustomerDTO> findByName(String name);
}
