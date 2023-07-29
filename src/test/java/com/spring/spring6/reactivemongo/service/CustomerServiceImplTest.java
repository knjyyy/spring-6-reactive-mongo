package com.spring.spring6.reactivemongo.service;

import com.spring.spring6.reactivemongo.domain.Customer;
import com.spring.spring6.reactivemongo.mappers.CustomerMapper;
import com.spring.spring6.reactivemongo.model.CustomerDTO;
import com.spring.spring6.reactivemongo.repo.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class CustomerServiceImplTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CustomerRepository customerRepository;

    CustomerDTO customerDTO;

    @BeforeEach
    void setUp() { customerDTO = customerMapper.customerToCustomerDTO(getCustomer()); }

    public static Customer getCustomer() {
        return Customer.builder()
                .name("Greg Popovich")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    public CustomerDTO getSavedCustomerDTO() {
        return customerService.saveCustomer(
               Mono.just(customerMapper.customerToCustomerDTO(getCustomer()))).block();
    }

    @Test
    @DisplayName("Test Get All Customer")
    void listCustomers() {
        customerService.saveCustomer(Mono.just(customerDTO))
                .subscribe();
        CustomerDTO newCustomerDTO = CustomerDTO.builder().name("RC Buford").build();
        customerService.saveCustomer(Mono.just(newCustomerDTO)).subscribe();

        AtomicReference<List<CustomerDTO>> atomicReference = new AtomicReference<>();
        customerService.listCustomers()
                .collectList()
                .flatMapMany(Flux::just)
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);
        List<CustomerDTO> customerList = atomicReference.get();
        assertThat(customerList.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Test Get Customer by ID")
    void getById() {
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();
        customerService.saveCustomer(Mono.just(customerDTO))
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);
        CustomerDTO persistedDTO = atomicReference.get();
        assertThat(persistedDTO.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Customer")
    void saveCustomer() {
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Mono<CustomerDTO> customerDTOMono = customerService.saveCustomer(Mono.just(customerDTO));
        customerDTOMono.subscribe(savedDTO -> {
            System.out.println(savedDTO.getId());
            atomicBoolean.set(true);
            atomicReference.set(savedDTO);
        });

        await().untilTrue(atomicBoolean);
        CustomerDTO persistedDTO = atomicReference.get();
        assertThat(persistedDTO).isNotNull();
        assertThat(persistedDTO.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Customer")
    void updateCustomer() {
        final String updatedName = "Clairo";
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();

        customerService.saveCustomer(Mono.just(customerMapper.customerToCustomerDTO(getCustomer())))
                .map(savedCustomerDTO -> {
                    savedCustomerDTO.setName(updatedName);
                    return savedCustomerDTO;
                })
                .flatMap(customerDTO -> customerService.saveCustomer(Mono.just(customerDTO)))
                .flatMap(updatedCustomerDTO -> customerService.getById(updatedCustomerDTO.getId()))
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);
        assertThat(atomicReference.get().getName()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("Test Patch Customer")
    void patchCustomer() {
        final String patchedName = "Julian Casablancas";
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();
        customerService.saveCustomer(Mono.just(customerMapper.customerToCustomerDTO(getCustomer())))
                .map(savedCustomerDTO -> {
                    savedCustomerDTO.setName(patchedName);
                    return savedCustomerDTO;
                })
                .flatMap(customerToPatch -> customerService.patchCustomer(customerToPatch.getId(), customerToPatch))
                .flatMap(patchedCustomer -> customerService.getById(patchedCustomer.getId()))
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);
        assertThat(atomicReference.get().getName()).isEqualTo(patchedName);
    }

    @Test
    @DisplayName("Test Delete Customer")
    void deleteCustomer() {
        CustomerDTO customerDTOToDelete = getSavedCustomerDTO();
        customerService.deleteCustomer(customerDTOToDelete.getId()).block();
        Mono<CustomerDTO> customerDTOMono = customerService.getById(customerDTOToDelete.getId());
        CustomerDTO deletedCustomerDTO = customerDTOMono.block();
        assertThat(deletedCustomerDTO).isNull();
    }

    @Test
    @DisplayName("Test Get Customer by Name")
    void findByName() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        CustomerDTO customerDTO = getSavedCustomerDTO();
        customerService.findByName(customerDTO.getName())
                .subscribe(dto -> {
                    System.out.println(dto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }
}