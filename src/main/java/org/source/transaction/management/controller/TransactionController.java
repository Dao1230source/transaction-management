package org.source.transaction.management.controller;

import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import org.source.spring.io.Response;
import org.source.spring.valid.groups.Add;
import org.source.spring.valid.groups.Update;
import org.source.transaction.management.facade.TransactionFacade;
import org.source.transaction.management.facade.param.TransactionParam;
import org.source.transaction.management.facade.view.TransactionView;
import org.source.transaction.management.infrastructure.constant.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/transaction")
@AllArgsConstructor
public class TransactionController {

    private final TransactionFacade transactionFacade;

    @GetMapping("/{transactionId}")
    public Response<TransactionView> get(@PathVariable(value = "transactionId") String transactionId) {
        return Response.success(transactionFacade.get(transactionId));
    }

    @GetMapping("/page")
    public Response<Page<TransactionView>> page(@RequestParam(value = "transactionId", required = false) String transactionId,
                                                @RequestParam(value = "fromAccount", required = false) String fromAccount,
                                                @RequestParam(value = "toAccount", required = false) String toAccount,
                                                @RequestParam(value = "type", required = false) Integer type,
                                                @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        TransactionParam param = TransactionParam.builder().transactionId(transactionId).fromAccount(fromAccount)
                .toAccount(toAccount).type(type).build();
        pageNumber = Objects.requireNonNullElse(pageNumber, Constants.PAGE_NUMBER_DEFAULT);
        pageSize = Objects.requireNonNullElse(pageSize, Constants.PAGE_SIZE_DEFAULT);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "updateTime"));
        return Response.success(transactionFacade.page(param, pageRequest));
    }

    @PostMapping("/add")
    public Response<TransactionView> add(@RequestBody @Validated(value = {Add.class, Default.class}) TransactionParam param) {
        return Response.success(transactionFacade.add(param));
    }

    @PutMapping("/{transactionId}")
    public Response<TransactionView> update(@PathVariable(value = "transactionId") String transactionId,
                                            @RequestBody @Validated(value = {Update.class, Default.class}) TransactionParam param) {
        return Response.success(transactionFacade.update(transactionId, param));
    }

    @DeleteMapping("/{transactionId}")
    public Response<TransactionView> delete(@PathVariable(value = "transactionId") String transactionId) {
        return Response.success(transactionFacade.delete(transactionId));
    }

}
