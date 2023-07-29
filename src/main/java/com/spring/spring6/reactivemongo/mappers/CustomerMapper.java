package com.spring.spring6.reactivemongo.mappers;

import org.mapstruct.Mapper;
import com.spring.spring6.reactivemongo.domain.Customer;
import com.spring.spring6.reactivemongo.model.CustomerDTO;

@Mapper
public interface CustomerMapper {
    CustomerDTO customerToCustomerDTO(Customer customer);
    Customer customerDTOtoCustomer(CustomerDTO customerDTO);
}
