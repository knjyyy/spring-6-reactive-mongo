package com.spring.spring6.reactivemongo.mappers;

import org.mapstruct.Mapper;
import com.spring.spring6.reactivemongo.model.BeerDTO;
import com.spring.spring6.reactivemongo.domain.Beer;
@Mapper
public interface BeerMapper {

    BeerDTO beerToBeerDTO(Beer beer);
    Beer beerDTOToBeer(BeerDTO beerDTO);
}
