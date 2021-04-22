package org.bsim.intern.service.iservice;

import org.bsim.intern.shared.dto.TransactionsDto;

import java.util.List;

public interface IServiceTransactions {
    List<TransactionsDto> getAllTransactions();

    TransactionsDto addNewTransactions(String walletId, TransactionsDto transactionsDto);

    List<TransactionsDto> getAllTransactionsBywalletId(String walletId);

    TransactionsDto updateTransactionsByTransactionsId(String walletId, String transactionsId, TransactionsDto transactionsDto);

    TransactionsDto deleteTransactions(String walletId, String transactionsId);

}
