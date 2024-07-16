package com.genesis.studio.sibs.ordermanager.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;
    private int quantity;
}
