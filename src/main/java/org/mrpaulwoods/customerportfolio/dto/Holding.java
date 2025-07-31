package org.mrpaulwoods.customerportfolio.dto;

import org.mrpaulwoods.customerportfolio.domain.Ticker;

public record Holding(Ticker ticker, Integer quantity) {
}
