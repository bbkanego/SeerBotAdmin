package com.seerlogics.botadmin.service;

import com.lingoace.common.exception.NotAuthorizedException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.botadmin.exception.ErrorCodes;
import com.seerlogics.chatbot.model.Transaction;
import com.seerlogics.chatbot.repository.TransactionRepository;
import com.seerlogics.commons.CommonConstants;
import com.seerlogics.commons.dto.BotDetail;
import com.seerlogics.commons.dto.SearchBots;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.Bot;
import com.seerlogics.commons.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE;
import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_ROLE;

@Service
public class TransactionService extends BaseServiceImpl<Transaction> {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final BotService botService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService, BotService botService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.botService = botService;
    }

    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public List<Transaction> findByTargetBotId(Long targetBotId) {
        return this.transactionRepository.findByTargetBotIdAndAccountId(targetBotId,
                this.accountService.getAuthenticatedUser().getId());
    }

    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public List<Transaction> findByTargetBotIdAndSuccess(Long targetBotId, Boolean isSuccess) {
        return this.transactionRepository.findByTargetBotIdAndAccountIdAndSuccessAndIntentNotContaining(targetBotId,
                this.accountService.getAuthenticatedUser().getId(), isSuccess, CommonConstants.MAY_BE);
    }

    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public List<Transaction> findByTargetBotIdAndIntentLike(Long targetBotId, String intentLike) {
        return this.transactionRepository.findByTargetBotIdAndAccountIdAndIntentLike(targetBotId,
                this.accountService.getAuthenticatedUser().getId(), intentLike);
    }

    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public List<BotDetail> findAllBotsAndTransaction(SearchBots searchBots) {
        List<BotDetail> botDetails = new ArrayList<>(0);
        Collection<Bot> allBots = this.botService.getAll();
        for (Bot bot : allBots) {
            BotDetail botDetail = new BotDetail();
            botDetail.setId(bot.getId());
            botDetail.setName(bot.getName());
            botDetail.setDescription(bot.getDescription());

            if (searchBots.getTransactionMaybe() != null) {
                botDetail.getMaybeTransactions().addAll(this.transactionRepository.findByTargetBotIdAndAccountIdAndIntentLike(bot.getId(),
                        this.accountService.getAuthenticatedUser().getId(), CommonConstants.MAY_BE + "%"));
            } else if (searchBots.getTransactionSuccess() == null) {
                List<Transaction> allTransactions = this.transactionRepository.findByTargetBotIdAndAccountId(bot.getId(),
                        this.accountService.getAuthenticatedUser().getId());
                for (Transaction transaction : allTransactions) {
                    if (transaction.getIntent().startsWith(CommonConstants.MAY_BE)) {
                        botDetail.getMaybeTransactions().add(transaction);
                    } else if (Boolean.TRUE.equals(transaction.getSuccess())) {
                        botDetail.getSuccessTransactions().add(transaction);
                    } else {
                        botDetail.getFailureTransactions().add(transaction);
                    }
                }

                this.calculatePercentages(botDetail);
            } else {
                List<Transaction> successOrFailureTrans =
                        this.transactionRepository.findByTargetBotIdAndAccountIdAndSuccessAndIntentNotContaining(bot.getId(),
                                this.accountService.getAuthenticatedUser().getId(), searchBots.getTransactionSuccess(),
                                CommonConstants.MAY_BE);
                if (Boolean.TRUE.equals(searchBots.getTransactionSuccess())) {
                    botDetail.getSuccessTransactions().addAll(successOrFailureTrans);
                } else {
                    botDetail.getFailureTransactions().addAll(successOrFailureTrans);
                }
            }

            botDetails.add(botDetail);
        }
        return botDetails;
    }

    private void calculatePercentages(BotDetail botDetail) {
        int failureTrans = botDetail.getFailureTransactions().size();
        int successTrans = botDetail.getSuccessTransactions().size();
        int maybeTrans = botDetail.getMaybeTransactions().size();
        int totalTrans = failureTrans + successTrans + maybeTrans;

        botDetail.setPercentageFailure(Math.round((float)failureTrans * 100 / totalTrans));
        botDetail.setPercentageSuccess(Math.round((float)successTrans * 100 / totalTrans));
        botDetail.setPercentageMaybe(Math.round((float)maybeTrans * 100 / totalTrans));
    }

    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public List<Transaction> findByTargetBotIdAndAccountUserName(Long targetBotId, String userName) {
        Account currentAccount = this.accountService.getAuthenticatedUser();
        Account targetAccount = this.accountService.getAccountByUsername(userName);
        if (isUberAdminOrOwnerAccount(userName, currentAccount)) {
            return this.transactionRepository.findByTargetBotIdAndAccountId(targetBotId, targetAccount.getId());
        } else {
            throw new NotAuthorizedException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }
    }

    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public List<Transaction> findByTargetBotIdAndAccountUserNameAndSuccess(Long targetBotId, String userName, Boolean isSuccess) {
        Account currentAccount = this.accountService.getAuthenticatedUser();
        if (isUberAdminOrOwnerAccount(userName, currentAccount)) {
            return this.transactionRepository.findByTargetBotIdAndAccountIdAndSuccessAndIntentNotContaining(targetBotId,
                    currentAccount.getId(), isSuccess, CommonConstants.MAY_BE);
        } else {
            throw new NotAuthorizedException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }
    }

    private boolean isUberAdminOrOwnerAccount(String userName, Account currentAccount) {
        return currentAccount.getRoles().stream().
                anyMatch(role -> role.getCode().equals(Role.ROLE_TYPE.UBER_ADMIN.name()))
                || currentAccount.getUserName().equals(userName);
    }

    @Override
    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public Transaction getSingle(Long id) {
        Transaction transaction = this.transactionRepository.getOne(id);
        Account currentAccount = this.accountService.getAuthenticatedUser();
        if (currentAccount.getRoles().stream().
                anyMatch(role -> role.getCode().equals(Role.ROLE_TYPE.UBER_ADMIN.name()))
                || currentAccount.getId().equals(transaction.getAccountId())) {
            return transaction;
        } else {
            throw new NotAuthorizedException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    @PreAuthorize(HAS_UBER_ADMIN_ROLE)
    public Collection<Transaction> getAll() {
        return this.transactionRepository.findAll();
    }
}
