package com.teads.developmenttest.entity;


import com.teads.developmenttest.exception.functional.FunctionalException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest
{
    @Test
    public void shouldThrowExceptionWhenNoOneReachesTheReservePrice() {
        final int reservePrice = 10;
        Bidder bidder0 = new Bidder("bidder0", List.of(1, 2, 4));
        Bidder bidder1 = new Bidder("bidder1", List.of(1, 2, 4));
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1));

        Exception exception = assertThrows(FunctionalException.class, auction0::calculateAuctionResult);
        String expectedMessage = "cancelled because the reserve price '" + reservePrice +"' has not been reached";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldThrowExceptionWhenThereIsNoBidders() {
        Auction auction0 = new Auction();
        auction0.setName("auction0");
        auction0.setReservePrice(1);
        Exception exception = assertThrows(FunctionalException.class, auction0::calculateAuctionResult);
        String expectedMessage = "No bidders";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldReturnTheWinningPrice() {
        final int reservePrice = 7;
        Bidder bidder0 = new Bidder("bidder0", List.of(1, 5));
        Bidder bidder1 = new Bidder("bidder1", List.of(1, 6, 9));
        Bidder bidder2 = new Bidder("bidder2", List.of(1, 2, 50));
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1, bidder2));
        AuctionResult expectedActionResult = new AuctionResult("auction0", bidder2, 9);
        AuctionResult auctionResult = auction0.calculateAuctionResult();
        assertEquals(expectedActionResult, auctionResult);
    }

    @Test
    public void shouldReturnTheWinningPriceWhenABidderHasNoBid() {
        final int reservePrice = 7;
        Bidder bidder0 = new Bidder("bidder0", Collections.emptyList());
        Bidder bidder1 = new Bidder("bidder1", List.of(1, 6, 9));
        Bidder bidder2 = new Bidder("bidder2", List.of(1, 2, 50));
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1, bidder2));
        AuctionResult expectedActionResult = new AuctionResult("auction0", bidder2, 9);
        AuctionResult auctionResult = auction0.calculateAuctionResult();
        assertEquals(expectedActionResult, auctionResult);
    }

    @Test
    public void shouldReturnExceptionWhenAllBidderHaveNoBid() {
        final int reservePrice = 7;
        Bidder bidder0 = new Bidder("bidder0", Collections.emptyList());
        Bidder bidder1 = new Bidder("bidder1", Collections.emptyList());
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1));

        Exception exception = assertThrows(FunctionalException.class, auction0::calculateAuctionResult);
        String expectedMessage = "cancelled because the reserve price '" + reservePrice +"' has not been reached";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldReturnTheWinningPriceWhenBidsAreNotOrdered() {
        final int reservePrice = 7;
        Bidder bidder0 = new Bidder("bidder0", List.of(1, 5, 2));
        Bidder bidder1 = new Bidder("bidder1", List.of(1, 6, 9, 7));
        Bidder bidder2 = new Bidder("bidder2", List.of(1, 2, 50, 4, 8, 6));
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1, bidder2));
        AuctionResult expectedActionResult = new AuctionResult("auction0", bidder2, 9);
        AuctionResult auctionResult = auction0.calculateAuctionResult();
        assertEquals(expectedActionResult, auctionResult);
    }

    @Test
    public void shouldReturnTheWinningPriceWhenSomeBidsAreRepeated() {
        final int reservePrice = 7;
        Bidder bidder0 = new Bidder("bidder0", List.of(1, 5, 2, 5, 1));
        Bidder bidder1 = new Bidder("bidder1", List.of(1, 6, 9, 7, 1, 6));
        Bidder bidder2 = new Bidder("bidder2", List.of(1, 2, 50, 4, 8, 6, 50, 1));
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1, bidder2));
        AuctionResult expectedActionResult = new AuctionResult("auction0", bidder2, 9);
        AuctionResult auctionResult = auction0.calculateAuctionResult();
        assertEquals(expectedActionResult, auctionResult);
    }

    @Test
    public void shouldReturnTheReservePriceWhenTheSecondHighestBidIsLessThanTheReservePrice() {
        Bidder bidder0 = new Bidder("bidder0", List.of(1, 5));
        Bidder bidder1 = new Bidder("bidder1", List.of(1, 6, 9));
        Bidder bidder2 = new Bidder("bidder2", List.of(1, 2, 50));
        final int reservePrice = 30;
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1, bidder2));
        AuctionResult expectedActionResult = new AuctionResult("auction0", bidder2, reservePrice);
        AuctionResult auctionResult = auction0.calculateAuctionResult();
        assertEquals(expectedActionResult, auctionResult);
    }

    @Test
    public void shouldReturnTheReservePriceWhenThereIsOnlyOneBidder() {
        Bidder bidder0 = new Bidder("bidder0", List.of(1, 5));
        final int reservePrice = 3;
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0));
        AuctionResult expectedActionResult = new AuctionResult("auction0", bidder0, reservePrice);
        AuctionResult auctionResult = auction0.calculateAuctionResult();
        assertEquals(expectedActionResult, auctionResult);
    }

    @Test
    public void shouldThrowExceptionWhenMultipleHighestBidsWithMaxBid8() {
        Bidder bidder0 = new Bidder("bidder0", List.of(1, 2));
        Bidder bidder1 = new Bidder("bidder1", List.of(2, 6, 8));
        Bidder bidder2 = new Bidder("bidder2", List.of(4, 7));
        Bidder bidder3 = new Bidder("bidder3", List.of(1, 5, 8));
        Auction auction0 = new Auction("auction0", 6, List.of(bidder0, bidder1, bidder2, bidder3));

        Exception exception = assertThrows(FunctionalException.class, auction0::calculateAuctionResult);
        assertTrue(exception.getMessage().contains("8"));
        assertTrue(exception.getMessage().contains("bidder1"));
        assertTrue(exception.getMessage().contains("bidder3"));
        assertFalse(exception.getMessage().contains("bidder0"));
        assertFalse(exception.getMessage().contains("bidder2"));
    }

    @Test
    public void shouldThrowExceptionWhenWinningPriceIs0OrLess() {
        int reservePrice = 0;
        Bidder bidder0 = new Bidder("bidder0", List.of(-1, -2));
        Bidder bidder1 = new Bidder("bidder1", List.of(-2, -6, 8));
        Auction auction0 = new Auction("auction0", reservePrice, List.of(bidder0, bidder1));

        Exception exception = assertThrows(FunctionalException.class, auction0::calculateAuctionResult);
        String expectedMessage = "winning price must be > 0";
        assertTrue(exception.getMessage().contains(expectedMessage));

        auction0.setReservePrice(-5);
        exception = assertThrows(FunctionalException.class, auction0::calculateAuctionResult);
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
