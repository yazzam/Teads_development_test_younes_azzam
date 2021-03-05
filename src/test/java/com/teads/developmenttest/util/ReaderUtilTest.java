package com.teads.developmenttest.util;

import com.teads.developmenttest.entity.Auction;
import com.teads.developmenttest.entity.Bidder;
import com.teads.developmenttest.exception.functional.FunctionalException;
import com.teads.developmenttest.exception.technical.TechnicalException;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReaderUtilTest {

    @Test
    @SneakyThrows
    public void shouldTrimAllWhiteSpaces() {
        final String inputFileName = "data/inputWithEmptyLines.txt";
        String input = ReaderUtil.readInput(inputFileName);
        String expectedOutput = "1" + System.lineSeparator()
                + "2" + System.lineSeparator()
                + "3" + System.lineSeparator()
                + "4" + System.lineSeparator()
                + "5 7";
        assertEquals(expectedOutput, input);
    }

    @Test
    @SneakyThrows
    public void shouldThrowExceptionWhenAllLinesAreEmpty() {
        final String inputFileName = "data/inputWithOnlyEmptyLines.txt";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.readInput(inputFileName));
        String expectedMessage = "File '" + inputFileName + "' is empty";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldThrowExceptionWhenFileDoesNotExist() {
        final String inputFileName = "data/fileThatDoesNotExist.txt";
        Exception exception = assertThrows(TechnicalException.class, () -> ReaderUtil.readInput(inputFileName));
        String expectedMessage = "does not exist";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldReturnResultWhenTheInputIsWellFormattedWithSupernumeraryBlanksAndSeparators() {
        String input = "auction0: 10\n" +
                "bidder0:8,10\n" +
                "bidder1: 10  ,       12\n" +
                "#_#\n" +
                "auction1:     20     \n" +
                "bidder0:       8    ,              12\n" +
                "bidder1:        10,14       \n" +
                "bidder2: 12,18\n" +
                "#_#  #_#       #_#      #_#\n" +
                "#_#\n" +
                "#_#";
        List<String> auction0 = List.of("auction0:10", "bidder0:8,10", "bidder1:10,12");
        List<String> auction1 = List.of("auction1:20", "bidder0:8,12", "bidder1:10,14", "bidder2:12,18");
        List<List<String>> expectedOutput = List.of(auction0, auction1);
        List<List<String>> actualOutput = ReaderUtil.validateAndFormatInput(input);
        assertTrue(CollectionUtils.isEqualCollection(expectedOutput, actualOutput));
    }

    @Test
    public void shouldThrowExceptionWhenAuctionHasNegativeReservePrice() {
        String input = "auction0: 10\n" +
                "bidder0: 8, 10\n" +
                "bidder1: 10, 12\n" +
                "#_#\n" +
                "auction1: -1\n" +
                "bidder0: 8, 12\n" +
                "bidder1: 10, 14\n" +
                "bidder2: 12, 18";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.validateAndFormatInput(input));
        String expectedMessage = "The auction 'auction1:-1' didn't match the input format";
        assertTrue(exception.getMessage().contains(expectedMessage), exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenAuctionHasNoReservePrice() {
        String input = "auction0:\n" +
                "bidder0: 8, 10\n" +
                "bidder1: 10, 12";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.validateAndFormatInput(input));
        String expectedMessage = "The auction 'auction0:' didn't match the input format";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldThrowExceptionWhenReservePriceHasBadFormat() {
        String input = "auction0:1e\n" +
                "bidder0: 8, 10\n" +
                "bidder1: 10, 12";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.validateAndFormatInput(input));
        String expectedMessage = "The auction 'auction0:1e' didn't match the input format";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldThrowExceptionWhenAuctionNameHasBadFormat() {
        String input = "auct&ion0: 1\n" +
                "bidder0: 8, 10\n" +
                "bidder1: 10, 12";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.validateAndFormatInput(input));
        String expectedMessage = "The auction 'auct&ion0:1' didn't match the input format";
        assertTrue(exception.getMessage().contains(expectedMessage), exception.getMessage());
    }

    @Test
    public void shouldReturnResultWhenABidderHasNoBids() {
        String input = "auction0: 10\n" +
                "bidder0: 8, 10\n" +
                "bidder1: 10, 12\n" +
                "#_#\n" +
                "auction1: 20\n" +
                "bidder0: 8, 12\n" +
                "bidder1:\n" +
                "bidder2: 12, 18";
        List<String> auction0 = List.of("auction0:10", "bidder0:8,10", "bidder1:10,12");
        List<String> auction1 = List.of("auction1:20", "bidder0:8,12", "bidder1:", "bidder2:12,18");
        List<List<String>> expectedOutput = List.of(auction0, auction1);
        List<List<String>> actualOutput = ReaderUtil.validateAndFormatInput(input);
        assertTrue(CollectionUtils.isEqualCollection(expectedOutput, actualOutput));
    }


    @Test
    public void shouldThrowExceptionWhenBidderHasNegativeBid() {
        String input = "auction0: 10\n" +
                "bidder0: 8, 10\n" +
                "bidder1: 10, 12\n" +
                "#_#\n" +
                "auction1: 1\n" +
                "bidder0: 8, 12\n" +
                "bidder1: 10, -14\n" +
                "bidder1: 12, 18";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.validateAndFormatInput(input));
        String expectedMessage = "The bid 'bidder1:10,-14' didn't match the input format";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldThrowExceptionWhenBidderHasABadFormattedBid() {
        String input = "auction0: 10\n" +
                "bidder0: 56t\n" +
                "bidder1: 10, 5";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.validateAndFormatInput(input));
        String expectedMessage = "The bid 'bidder0:56t' didn't match the input format";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void shouldThrowExceptionWhenBidderHasABadFormattedName() {
        String input = "auction0: 10\n" +
                "bid$der0: 56\n" +
                "bidder1: 10, 5";
        Exception exception = assertThrows(FunctionalException.class, () -> ReaderUtil.validateAndFormatInput(input));
        String expectedMessage = "The bid 'bid$der0:56' didn't match the input format";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @SneakyThrows
    public void shouldConvertAuctionDefinitionsListToAuctionEntityList() {
        List<String> auction0 = List.of("auction0:10", "bidder0:8,10", "bidder1:10,12");
        List<String> auction1 = List.of("auction1:20", "bidder0:8,5");
        List<List<String>> auctionDefinitionsList = List.of(auction0, auction1);
        List<Auction> result = ReaderUtil.convertToEntityList(auctionDefinitionsList);
        Auction auctionEntity0 = new Auction("auction0", 10, List.of(new Bidder("bidder0", List.of(8, 10)), new Bidder("bidder1", List.of(10, 12))));
        Auction auctionEntity1 = new Auction("auction1", 20, List.of(new Bidder("bidder0", List.of(8, 5))));
        assertTrue(CollectionUtils.isEqualCollection(List.of(auctionEntity0, auctionEntity1), result));
    }

    @Test
    @SneakyThrows
    public void shouldConvertAuctionDefinitionsToAuctionEntityWhenABidderHasNoBids() {
        List<String> auction0 = List.of("auction0:10", "bidder0:", "bidder1:10,12");
        List<String> auction1 = List.of("auction1:20", "bidder0:8,5");
        List<List<String>> auctionDefinitionsList = List.of(auction0, auction1);
        List<Auction> result = ReaderUtil.convertToEntityList(auctionDefinitionsList);
        Auction auctionEntity0 = new Auction("auction0", 10, List.of(new Bidder("bidder0", Collections.emptyList()), new Bidder("bidder1", List.of(10, 12))));
        Auction auctionEntity1 = new Auction("auction1", 20, List.of(new Bidder("bidder0", List.of(8, 5))));
        assertTrue(CollectionUtils.isEqualCollection(List.of(auctionEntity0, auctionEntity1), result));
    }

}