package org.mrpaulwoods.customerportfolio.service;

import org.mrpaulwoods.customerportfolio.dto.CustomerInformation;
import org.mrpaulwoods.customerportfolio.entity.Customer;
import org.mrpaulwoods.customerportfolio.exceptions.ApplicationExceptions;
import org.mrpaulwoods.customerportfolio.mapper.EntityDtoMapper;
import org.mrpaulwoods.customerportfolio.repository.CustomerRepository;
import org.mrpaulwoods.customerportfolio.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            PortfolioItemRepository portfolioItemRepository
    ) {
        this.customerRepository = customerRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .flatMap(this::buildCustomerInformation);
    }

    private Mono<CustomerInformation> buildCustomerInformation(Customer customer) {
        return portfolioItemRepository.findAllByCustomerId(customer.getId())
                .collectList()
                .map(items -> EntityDtoMapper.toCustomerInformation(customer, items));
    }

}
