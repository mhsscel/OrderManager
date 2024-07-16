package com.genesis.studio.sibs.ordermanager.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    private int quantity;
    private int fulfilledQuantity;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
}