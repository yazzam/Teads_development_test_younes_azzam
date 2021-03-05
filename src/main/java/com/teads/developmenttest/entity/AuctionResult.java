package com.teads.developmenttest.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AuctionResult {
    private String auctionName;
    private Bidder winner;
    private int winningPrice;
}
