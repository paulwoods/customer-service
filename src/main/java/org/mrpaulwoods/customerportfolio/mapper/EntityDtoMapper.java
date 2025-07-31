package org.mrpaulwoods.customerportfolio.mapper;

import org.mrpaulwoods.customerportfolio.dto.CustomerInformation;
import org.mrpaulwoods.customerportfolio.dto.Holding;
import org.mrpaulwoods.customerportfolio.entity.Customer;
import org.mrpaulwoods.customerportfolio.entity.PortfolioItem;

import java.util.List;

public class EntityDtoMapper {

    public static CustomerInformation toCustomerInformation(
            Customer customer,
            List<PortfolioItem> items
    ) {
        var holdings = items.stream()
                .map(item -> new Holding(item.getTicker(), item.getQuantity()))
                .toList();

        return new CustomerInformation(
                customer.getId(),
                customer.getName(),
                customer.getBalance(),
                holdings
        );
    }
}
