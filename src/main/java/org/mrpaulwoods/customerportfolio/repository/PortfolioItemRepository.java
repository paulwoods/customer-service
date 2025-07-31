package org.mrpaulwoods.customerportfolio.repository;

import org.mrpaulwoods.customerportfolio.domain.PortfolioItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioItemRepository extends ReactiveCrudRepository<PortfolioItem, Integer> {
}
