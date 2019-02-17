package org.stool.finance.account.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.stool.finance.account.domain.Account;
import org.stool.finance.account.domain.Item;
import org.stool.finance.account.domain.Saving;

import java.util.List;

public interface AccountMapper {

    @Select({
            "select * from finance_account",
            "where name = #{accountName}"
    })
    Account findAccountByName(@Param("accountName") String accountName);

    @Select({
            "select * from finance_saving",
            "where accountId = #{accountId}"
    })
    Saving findSavingByAccountId(@Param("accountId") long accountId);

    @Select({
            "select * from finance_item",
            "where accountId = #{accountId} and type = #{type} and title = #{title}"
    })
    Item findItem(Item item);

    @Select({
            "select * from finance_item",
            "where accountId = #{accountId} and type = 0"
    })
    List<Item> findIncomesByAccountId(long accountId);

    @Select({
            "select * from finance_item",
            "where accountId = #{accountId} and type = 1"
    })
    List<Item> findExpensesByAccountId(long accountId);


    @Insert({
            "insert into finance_account(name, lastSeen, note)",
            "values(#{name}, #{lastSeen}, #{note})"
    })
    int createAccount(Account account);

    @Insert({
            "insert into finance_saving(amount, interest, deposit, capitalization, accountId)",
            "values(#{amount}, #{interest}, #{deposit}, #{capitalization}, #{accountId})"
    })
    int createSaving(Saving saving);

    @Insert({
            "insert into finance_item(title, icon, period, currency, type, accountId, amount)",
            "values(#{title}, #{icon}, #{period}, #{currency}, #{type}, #{accountId}, #{amount})"
    })
    int createItem(Item item);

    @Update({
            "update finance_account",
            "set",
            "name = #{name},",
            "lastSeen = #{lastSeen},",
            "note = #{note}",
            "where id = #{id}"

    })
    int updateAccount(Account account);

    @Update({
            "update finance_saving",
            "set",
            "amount = #{amount},",
            "interest = #{interest},",
            "deposit = #{deposit},",
            "capitalization = #{capitalization}",
            "where accountId = #{accountId}"

    })
    int updateSaving(Saving saving);

    @Update({
            "update finance_item",
            "set",
            "title = #{title},",
            "icon = #{icon},",
            "period = #{period},",
            "currency = #{currency},",
            "amount = #{amount}",
            "where accountId = #{accountId} and type = #{type} and title = #{title}"

    })
    int updateItem(Item item);
}
