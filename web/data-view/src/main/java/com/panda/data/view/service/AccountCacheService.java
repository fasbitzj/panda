package com.panda.data.view.service;

import com.panda.data.view.entity.Account;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by jiang.zheng on 2018/2/8.
 */
@Service
public class AccountCacheService {

    // 使用了一个缓存名叫 accountCache
    @Cacheable(value="accountCache")
    public Account getAccountByName(String accountName) {

        // 方法内部实现不考虑缓存逻辑，直接实现业务
        System.out.println("real querying account... " + accountName);
        Optional<Account> accountOptional = getFromDB(accountName);
        if (!accountOptional.isPresent()) {
            throw new IllegalStateException(String.format("can not find account by account name : [%s]", accountName));
        }

        return accountOptional.get();
    }

    private Optional<Account> getFromDB(String accountName) {
        System.out.println("real querying db... " + accountName);
        //Todo query data from database
        return Optional.ofNullable(new Account(accountName));
    }

    @Cacheable(value="default")//使用一个缓存名叫accountCache
    public List getAccountList() {
        System.out.println("real query account");
        List list = new ArrayList();
        Account account1 = new Account("zyc");
        Account account2 = new Account("swm");
        list.add(account1);
        list.add(account2);
        return list;
    }

    @CacheEvict(value="accountCache",allEntries=true)
    public void reload() {
    }
}
