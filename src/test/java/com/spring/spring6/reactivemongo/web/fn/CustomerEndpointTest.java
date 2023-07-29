package com.spring.spring6.reactivemongo.web.fn;

import com.spring.spring6.reactivemongo.model.CustomerDTO;
import com.spring.spring6.reactivemongo.service.CustomerServiceImplTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndpointTest {
    @Autowired
    WebTestClient webTestClient;

    public CustomerDTO getSavedCustomer() {
        FluxExchangeResult<CustomerDTO> customerDTOFluxExchangeResult =
        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(CustomerServiceImplTest.getCustomer()), CustomerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .returnResult(CustomerDTO.class);

        List<String> location = customerDTOFluxExchangeResult.getResponseHeaders().get("Location");
        return webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange().returnResult(CustomerDTO.class).getResponseBody().blockFirst();
    }

    @Test
    @Order(1)
    void testListCustomers() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()", hasSize(greaterThan(1)));
    }

    @Test
    @Order(2)
    void testGetCustomerById() {
        CustomerDTO customerDTO = getSavedCustomer();
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    @Test
    @Order(3)
    void testGetCustomerByIdNotFound() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testGetCustomerByName() {
        final String name = "David Robinson";
        CustomerDTO customerDTO = getSavedCustomer();
        customerDTO.setName(name);

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange();

        webTestClient.get().uri(UriComponentsBuilder
                .fromPath(CustomerRouterConfig.CUSTOMER_PATH)
                .queryParam("name", name).build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    @Test
    @Order(5)
    void testCreateCustomer() {
        CustomerDTO customerDTO = getSavedCustomer();
        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated();

    }

    @Test
    @Order(6)
    void testCreateCustomerBadData() {
        CustomerDTO customerDTO = getSavedCustomer();
        customerDTO.setName("");

        webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(7)
    void tstUpdateCustomer() {
        CustomerDTO customerDTO = getSavedCustomer();
        customerDTO.setName("Gnarls Barkley");

        webTestClient.put().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(8)
    void testUpdateCustomerBadData() {
        CustomerDTO customerDTO = getSavedCustomer();
        customerDTO.setName("");

        webTestClient.put().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(9)
    void testUpdateCustomerNotFound() {
        CustomerDTO customerDTO = getSavedCustomer();

        webTestClient.put().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 9999)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(10)
    void testPatchCustomer() {
        CustomerDTO customerDTO = getSavedCustomer();
        customerDTO.setName("Brendan Urie");

        webTestClient.patch().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(11)
    void testPatchCustomerBadData() {
        CustomerDTO customerDTO = getSavedCustomer();
        customerDTO.setName("");

        webTestClient.patch().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(12)
    void testPatchCustomerNotFound() {
        CustomerDTO customerDTO = getSavedCustomer();
        webTestClient.patch().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 9999)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(13)
    void testDeleteCustomer() {
        CustomerDTO customerDTO = getSavedCustomer();
        webTestClient.delete().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(14)
    void testDeleteCustomerNotFound() {
        CustomerDTO customerDTO = getSavedCustomer();
        webTestClient.delete().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 899999)
                .exchange()
                .expectStatus().isNotFound();
    }
}
