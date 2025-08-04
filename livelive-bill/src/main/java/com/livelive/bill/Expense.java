package com.livelive.bill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    private String user;
    private String date; // yyyy-MM-dd
    private int amount;

}
