package com.ls.s21;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/5/5 16:35
 */
@BankAPI(url = "/bank/pay", desc = "支付接口")
@Data
public class PayAPI extends AbstractAPI {
    @BankAPIField(order = 1, type = "N", length = 20)
    private long userId;
    @BankAPIField(order = 2, type = "M", length = 10)
    private BigDecimal amount;
}
