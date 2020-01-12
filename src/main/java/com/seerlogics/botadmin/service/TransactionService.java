package com.seerlogics.botadmin.service;

import com.lingoace.common.exception.NotAuthorizedException;
import com.lingoace.spring.service.BaseServiceImpl;
import com.lingoace.util.CommonUtil;
import com.seerlogics.botadmin.exception.ErrorCodes;
import com.seerlogics.chatbot.model.Transaction;
import com.seerlogics.chatbot.repository.TransactionRepository;
import com.seerlogics.commons.CommonConstants;
import com.seerlogics.commons.dto.BotDetail;
import com.seerlogics.commons.dto.SearchIntents;
import com.seerlogics.commons.dto.SearchInterval;
import com.seerlogics.commons.dto.SearchTransaction;
import com.seerlogics.commons.model.Account;
import com.seerlogics.commons.model.Bot;
import com.seerlogics.commons.model.IntentUtterance;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE;
import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_ROLE;

@Service
public class TransactionService extends BaseServiceImpl<Transaction> {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final BotService botService;
    private final HelperService helperService;
    private final IntentService intentService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService,
                              BotService botService, HelperService helperService, IntentService intentService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.botService = botService;
        this.helperService = helperService;
        this.intentService = intentService;
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
    public List<BotDetail> findAllBotsAndTransaction(SearchTransaction searchTransaction) {
        /**
         * If the current account is NOT uber Admin, then they can search only for their bots.
         * UberAdmins can search ALL bots as needed.
         */
        Account currentAccount = this.accountService.getAuthenticatedUser();
        boolean isUberAdmin = this.helperService.isAllowedFullAccess(currentAccount);
        if (!isUberAdmin) {
            searchTransaction.setOwnerAccount(currentAccount);
        }

        Date currentDate = new Date();
        if (SearchInterval.DAILY.equals(searchTransaction.getSearchInterval())) {
            searchTransaction.setStartDate(CommonUtil.getCurrentDayStartOfDay());
            searchTransaction.setEndDate(CommonUtil.getCurrentDayEndOfDay());
        } else if (SearchInterval.LAST_30_DAYS.equals(searchTransaction.getSearchInterval())) {
            searchTransaction.setStartDate(CommonUtil.getDateMinusDays(currentDate, 30));
            searchTransaction.setEndDate(CommonUtil.getCurrentDayEndOfDay());
        } else if (SearchInterval.LAST_180_DAYS.equals(searchTransaction.getSearchInterval())) {
            searchTransaction.setStartDate(CommonUtil.getDateMinusDays(currentDate, 180));
            searchTransaction.setEndDate(CommonUtil.getCurrentDayEndOfDay());
        } else if (SearchInterval.LAST_7_DAYS.equals(searchTransaction.getSearchInterval())) {
            searchTransaction.setStartDate(CommonUtil.getDateMinusDays(currentDate, 7));
            searchTransaction.setEndDate(CommonUtil.getCurrentDayEndOfDay());
        } else if (SearchInterval.LAST_YEAR.equals(searchTransaction.getSearchInterval())) {
            searchTransaction.setStartDate(CommonUtil.getDateMinusDays(currentDate, 365));
            searchTransaction.setEndDate(CommonUtil.getCurrentDayEndOfDay());
        }

        List<BotDetail> botDetails = new ArrayList<>(0);

        // if the search is for a specific bot then only search for that, otherwise search ALL bots.
        if (searchTransaction.getBotId() != null) {
            Bot desiredBot = this.botService.getSingle(searchTransaction.getBotId());

            BotDetail botDetail = new BotDetail();
            searchTransactionsWithCriteria(searchTransaction, desiredBot, botDetail);
            botDetails.add(botDetail);
        } else {
            Collection<Bot> allBots = this.botService.getAll();

            for (Bot desiredBot : allBots) {
                BotDetail botDetail = new BotDetail();
                searchTransactionsWithCriteria(searchTransaction, desiredBot, botDetail);
                botDetails.add(botDetail);
            }
        }
        return botDetails;
    }

    private void searchTransactionsWithCriteria(SearchTransaction searchTransaction, Bot desiredBot, BotDetail botDetail) {
        botDetail.setId(desiredBot.getId());
        botDetail.setName(desiredBot.getName());
        botDetail.setDescription(desiredBot.getDescription());
        searchTransaction.setBotId(desiredBot.getId());
        botDetail.setCategoryCode(desiredBot.getCategory().getCode());

        SearchIntents searchIntents = new SearchIntents();
        searchIntents.setCategory(desiredBot.getCategory());
        List<IntentUtterance> utterancesForCategory = this.intentService.findUtterance(searchIntents);

        if (searchTransaction.getTransactionMaybe() != null) { // search for Maybe ONLY
            botDetail.getMaybeTransactions().addAll(this.transactionRepository.findTransactions(searchTransaction));
        } else if (searchTransaction.getTransactionSuccess() == null) { // search ALL
            List<Transaction> allTransactions = this.transactionRepository.findTransactions(searchTransaction);
            for (Transaction transaction : allTransactions) {
                if (transaction.getIntent().startsWith(CommonConstants.MAY_BE)) {
                    botDetail.getMaybeTransactions().add(transaction);
                    findIntentsForTransaction(transaction, utterancesForCategory);
                } else if (Boolean.TRUE.equals(transaction.getSuccess())) {
                    botDetail.getSuccessTransactions().add(transaction);
                } else {
                    botDetail.getFailureTransactions().add(transaction);
                    findIntentsForTransaction(transaction, utterancesForCategory);
                }
            }
            this.calculatePercentages(botDetail);

        } else { // search only success or failure
            List<Transaction> successOrFailureTrans = this.transactionRepository.findTransactions(searchTransaction);
            if (Boolean.TRUE.equals(searchTransaction.getTransactionSuccess())) {
                botDetail.getSuccessTransactions().addAll(successOrFailureTrans);
            } else {
                botDetail.getFailureTransactions().addAll(successOrFailureTrans);
            }
        }
    }

    private void findIntentsForTransaction(Transaction transaction, List<IntentUtterance> utterancesForCategory) {
        // check to see if the utterance is already part of the category
        int numOfUtteranceFound = 0;
        for (IntentUtterance intentUtterance : utterancesForCategory) {
            if (intentUtterance.getUtterance().equals(transaction.getUtterance())) {
                if (numOfUtteranceFound == 0) {
                    numOfUtteranceFound++;
                    transaction.setIntentId(intentUtterance.getOwner().getId());
                } else {
                    throw new IllegalStateException("More than one intent found for the Utterance");
                }
            }
        }
    }

    private void calculatePercentages(BotDetail botDetail) {
        int failureTrans = botDetail.getFailureTransactions().size();
        int successTrans = botDetail.getSuccessTransactions().size();
        int maybeTrans = botDetail.getMaybeTransactions().size();
        int totalTrans = failureTrans + successTrans + maybeTrans;

        botDetail.setPercentageFailure(Math.round((float) failureTrans * 100 / totalTrans));
        botDetail.setPercentageSuccess(Math.round((float) successTrans * 100 / totalTrans));
        botDetail.setPercentageMaybe(Math.round((float) maybeTrans * 100 / totalTrans));
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
        return this.helperService.isAllowedFullAccess(currentAccount)
                || currentAccount.getUserName().equals(userName);
    }

    @Override
    @PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
    public Transaction getSingle(Long id) {
        Transaction transaction = this.transactionRepository.getOne(id);
        Account currentAccount = this.accountService.getAuthenticatedUser();
        if (this.helperService.isAllowedFullAccess(currentAccount)
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
