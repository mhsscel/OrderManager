package com.genesis.studio.sibs.ordermanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class StockMovementDTO {
    private Long id;
    private Date creationDate;
    private Long itemId;
    private String itemName;
    private int quantity;
}
