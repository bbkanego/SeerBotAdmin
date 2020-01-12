package com.seerlogics.botadmin.controller;

import com.lingoace.spring.controller.BaseController;
import com.seerlogics.botadmin.service.TransactionService;
import com.seerlogics.chatbot.model.Transaction;
import com.seerlogics.commons.dto.BotDetail;
import com.seerlogics.commons.dto.SearchBots;
import com.seerlogics.commons.dto.SearchTransaction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static com.seerlogics.commons.CommonConstants.HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE;

@RestController
@RequestMapping(value = "/api/v1/transactions")
@PreAuthorize(HAS_UBER_ADMIN_OR_ACCT_ADMIN_ROLE)
public class TransactionController extends BaseController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(value = "/{transactionId}")
    public Transaction getById(@PathVariable("transactionId") Long id) {
        return this.transactionService.getSingle(id);
    }

    @GetMapping(value = "/get-all")
    public Collection<Transaction> getAll() {
        return this.transactionService.getAll();
    }

    @PostMapping(value = "/get-bot-trans")
    public Collection<Transaction> getBotTransactions(@RequestBody SearchBots searchBots) {
        if (searchBots.getTransactionMaybe() != null) {
            return this.transactionService.findByTargetBotIdAndIntentLike(searchBots.getBotId(), "Maybe%");
        } else if (searchBots.getTransactionSuccess() == null) {
            return this.transactionService.findByTargetBotId(searchBots.getBotId());
        } else {
            return this.transactionService.findByTargetBotIdAndSuccess(searchBots.getBotId(), searchBots.getTransactionSuccess());
        }
    }

    @PostMapping(value = "/all-bots")
    public List<BotDetail> getAllTransactionsForBots(@RequestBody SearchTransaction searchTransaction) {
        return this.transactionService.findAllBotsAndTransaction(searchTransaction);
    }
}
