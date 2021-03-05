package com.teads.developmenttest.main;

import com.teads.developmenttest.entity.Auction;
import com.teads.developmenttest.entity.AuctionResult;
import com.teads.developmenttest.exception.functional.FunctionalException;
import com.teads.developmenttest.exception.technical.TechnicalException;
import com.teads.developmenttest.util.ReaderUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class Application {
    public static void main(String[] args) throws IOException {
        final String defaultPath = "data/auctionInputs.txt";
        String filePath;
        if (args.length != 0 && StringUtils.isNotBlank(args[0])) {
            filePath = args[0];
        } else {
            filePath = defaultPath;
        }
        System.out.println();
        System.out.println("Reading file '" + filePath + "'");
        String input = ReaderUtil.readInput(filePath);
        List<List<String>> auctionDefinitions = ReaderUtil.validateAndFormatInput(input);
        List<Auction> auctionsList = ReaderUtil.convertToEntityList(auctionDefinitions);

        for (Auction auction : auctionsList) {
            try {
                System.out.println(formatResult(auction.calculateAuctionResult()));
            } catch (TechnicalException | FunctionalException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static String formatResult(AuctionResult result) {
        return "The auction '" + result.getAuctionName() + "' were won by '" + result.getWinner().getName() + "' at the price of '" + result.getWinningPrice() + "'";
    }

}
