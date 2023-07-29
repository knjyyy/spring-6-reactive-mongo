package com.spring.spring6.reactivemongo.bootstrap;

import com.spring.spring6.reactivemongo.domain.Beer;
import com.spring.spring6.reactivemongo.domain.Customer;
import com.spring.spring6.reactivemongo.repo.BeerRepository;
import com.spring.spring6.reactivemongo.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        beerRepository.deleteAll()
                .doOnSuccess(success -> loadBeerData())
                .subscribe();

        customerRepository.deleteAll()
                .doOnSuccess(success -> loadCustomerData())
                .subscribe();
    }

    private void loadBeerData() {
        beerRepository.count().subscribe(count -> {
            if (count == 0) {
                System.out.println("loadBeerData() start!");

                Beer beer1 = Beer.builder()
                        .name("Galaxy Cat")
                        .style("Pale Ale")
                        .upc("12356")
                        .price(new BigDecimal("12.99"))
                        .quantityOnHand(122)
                        .build();

                Beer beer2 = Beer.builder()
                        .name("Crank")
                        .style("Pale Ale")
                        .upc("12356222")
                        .price(new BigDecimal("11.99"))
                        .quantityOnHand(392)
                        .build();

                Beer beer3 = Beer.builder()
                        .name("Sunshine City")
                        .style("IPA")
                        .upc("12356")
                        .price(new BigDecimal("13.99"))
                        .quantityOnHand(144)
                        .build();

                beerRepository.save(beer1).subscribe();
                beerRepository.save(beer2).subscribe();
                beerRepository.save(beer3).subscribe();

                System.out.println("loadBeerData() end");
            }
        });
    }

    private void loadCustomerData() {
        customerRepository.count().subscribe(count -> {
            if(count == 0) {
                System.out.println("loadCustomerData() start!");

                Customer customer1 = Customer.builder()
                        .name("Tim Duncan")
                        .build();
                Customer customer2 = Customer.builder()
                        .name("Tony Parker")
                        .build();
                Customer customer3 = Customer.builder()
                        .name("Manu Ginobili")
                        .build();

                customerRepository.save(customer1).subscribe();
                customerRepository.save(customer2).subscribe();
                customerRepository.save(customer3).subscribe();
                System.out.println("loadCustomerData() end");
            }
        });
    }
}
