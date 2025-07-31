package org.mrpaulwoods.customerportfolio.exceptions;

public class InsufficientBalanceException extends RuntimeException {

    public static final String MESSAGE = "Customer [id=%d] does not have enough funds to complete the transaction";

    public InsufficientBalanceException(Integer customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
