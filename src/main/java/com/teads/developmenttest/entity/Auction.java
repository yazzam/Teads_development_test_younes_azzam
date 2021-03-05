package com.teads.developmenttest.entity;

import com.teads.developmenttest.exception.functional.FunctionalException;
import com.teads.developmenttest.exception.technical.TechnicalException;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Auction {
    private String name;
    private int reservePrice;
    private List<Bidder> bidders = new ArrayList<>();

    public AuctionResult calculateAuctionResult() {

        if (CollectionUtils.isEmpty(bidders)) {
            throw new FunctionalException("No bidders, the auction is cancelled");
        }

        Map<Bidder, Integer> maxBidByBidder = bidders
                .stream()
                .filter(bidder -> CollectionUtils.isNotEmpty(bidder.getBids()))
                .collect(Collectors.toMap(bidder -> bidder, bidder -> Collections.max(bidder.getBids())));

        Optional<Map.Entry<Bidder, Integer>> winner = maxBidByBidder.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .filter(entry -> entry.getValue() >= reservePrice);

        if (winner.isEmpty()) {
            throw new FunctionalException("The auction '" + this.name +"' is cancelled because the reserve price '" + this.reservePrice +"' has not been reached");
        }

        // The case of multiple winners
        List<String> allWinners = maxBidByBidder.entrySet().stream()
                .filter(entry -> entry.getValue().equals(winner.get().getValue()))
                .map(entry -> entry.getKey().getName())
                .collect(Collectors.toList());

        if (allWinners.size() == 1) {
            int winningPrice = getWinningPrice(maxBidByBidder, winner.get());
            if (winningPrice <= 0) {
                throw new FunctionalException("The auction '" + this.name +"' is cancelled because the winning price must be > 0. Please check your data");
            }
            return new AuctionResult(this.name, winner.get().getKey(), winningPrice);
        }

        if (allWinners.size() > 1) {
            String winners = String.join(", ", allWinners);
            throw new FunctionalException("The auction '" + this.name +"' is cancelled because bidders '" + winners + "' had the same bid : '" + winner.get().getValue() + "'");
        }
        throw new TechnicalException("We weren't able to calculate the auction winner please report the bug");
    }

    private Integer getWinningPrice(Map<Bidder, Integer> maxBidByBidder, Map.Entry<Bidder, Integer> winner) {
        // exclude the winner to get the highest bid price from a non-winning bidder or the reservePrice
        return maxBidByBidder.entrySet().stream()
                .filter(entry -> entry.getKey() != winner.getKey())
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getValue)
                .filter(bid -> bid >= reservePrice)
                .orElse(reservePrice);
    }
}
