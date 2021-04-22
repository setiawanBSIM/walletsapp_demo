package org.bsim.intern.service.impl;

import org.bsim.intern.io.entity.TransactionsEntity;
import org.bsim.intern.io.entity.WalletsEntity;
import org.bsim.intern.io.irepository.TransactionsRepository;
import org.bsim.intern.io.irepository.WalletsRepository;
import org.bsim.intern.service.iservice.IServiceTransactions;
import org.bsim.intern.shared.dto.TransactionsDto;
import org.bsim.intern.shared.utils.GenerateRandomPublicId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionsImpl implements IServiceTransactions {

    private final TransactionsRepository transactionsRepository;
    private final WalletsRepository walletsRepository;
    private final GenerateRandomPublicId generateRandomPublicId;

    public TransactionsImpl(TransactionsRepository transactionsRepository, WalletsRepository walletsRepository, GenerateRandomPublicId generateRandomPublicId) {
        this.transactionsRepository = transactionsRepository;
        this.walletsRepository = walletsRepository;
        this.generateRandomPublicId = generateRandomPublicId;
    }

    @Override
    public List<TransactionsDto> getAllTransactions() {
        ModelMapper modelMapper = new ModelMapper();

        List<TransactionsDto> returnValue = new ArrayList<>();

        List<TransactionsEntity> transactionsEntities = transactionsRepository.findAll();

        for (TransactionsEntity entity : transactionsEntities) {
            returnValue.add(modelMapper.map(entity, TransactionsDto.class));
        }

        return returnValue;
    }

    @Override
    public TransactionsDto addNewTransactions(String walletId, TransactionsDto transactionsDto) {
        ModelMapper modelMapper = new ModelMapper();

        WalletsEntity walletsEntity = walletsRepository.findByWalletid(walletId);

        TransactionsEntity entity = modelMapper.map(transactionsDto, TransactionsEntity.class);
        entity.setWalletsEntity(walletsEntity);
        entity.setTransactionsId(generateRandomPublicId.generateUserId(35));

        TransactionsEntity storedValue = transactionsRepository.save(entity);

        TransactionsDto returnValue = modelMapper.map(storedValue, TransactionsDto.class);
        return returnValue;
    }

    @Override
    public List<TransactionsDto> getAllTransactionsBywalletId(String walletId) {
        List<TransactionsDto> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        WalletsEntity walletsEntity = walletsRepository.findByWalletid(walletId);

        List<TransactionsEntity> transactionsEntities = transactionsRepository.findAllByWalletsEntity(walletsEntity);

        for (TransactionsEntity entity : transactionsEntities) {
            returnValue.add(modelMapper.map(entity, TransactionsDto.class));
        }

        return returnValue;
    }

    @Override
    public TransactionsDto updateTransactionsByTransactionsId(String walletId, String transactionsId, TransactionsDto transactionsDto) {
        ModelMapper modelMapper = new ModelMapper();

        long balanceInit = 0;
        long amountInit=0; //100
        long amountUpdate= transactionsDto.getAmount(); //90


        WalletsEntity walletsEntity = walletsRepository.findByWalletid(walletId);
        balanceInit = walletsEntity.getBalance();

        TransactionsEntity transactionsEntity = transactionsRepository.findByTransactionsId(transactionsId);
        amountInit = transactionsEntity.getAmount();

        TransactionsEntity entity = modelMapper.map(transactionsDto, TransactionsEntity.class);
        entity.setWalletsEntity(walletsEntity);
        entity.setId(transactionsEntity.getId());
        entity.setTransactionsId(transactionsEntity.getTransactionsId());

        if (amountInit - amountUpdate > 0){
            walletsEntity.setBalance(balanceInit + (amountUpdate-amountInit) );
        }else{
            walletsEntity.setBalance(balanceInit - (amountInit-amountUpdate));
        }

        walletsRepository.save(walletsEntity);

        TransactionsEntity storedValue = transactionsRepository.save(entity);

        return modelMapper.map(storedValue, TransactionsDto.class);
    }

    @Override
    public TransactionsDto deleteTransactions(String walletId, String transactionsId) {
        WalletsEntity walletsEntity = walletsRepository.findByWalletid(walletId);
        TransactionsEntity transactionsEntity = transactionsRepository.findByTransactionsId(transactionsId);
        transactionsEntity.setWalletsEntity(walletsEntity);
        transactionsEntity.setDeleted(true);

        TransactionsEntity storedValue = transactionsRepository.save(transactionsEntity);

        ModelMapper modelMapper = new ModelMapper();

        return  modelMapper.map(storedValue, TransactionsDto.class);
    }
}
