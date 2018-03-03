package com.panda.data.view.controller.api;

import com.panda.data.view.entity.Account;
import com.panda.data.view.service.AccountCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiang.zheng on 2018/2/8.
 */
@RestController
@RequestMapping(value = "api")
public class AccountController {

    @Autowired
    private AccountCacheService accountCacheService;

    @RequestMapping(value = "account")
    public Object getAccount(String name) {
        Account account = accountCacheService.getAccountByName(name);
        System.out.println(account);
        return accountCacheService.getAccountList().toString();
    }
}
