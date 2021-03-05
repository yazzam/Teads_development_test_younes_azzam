package com.teads.developmenttest.util;

import com.teads.developmenttest.entity.Auction;
import com.teads.developmenttest.entity.Bidder;
import com.teads.developmenttest.exception.functional.FunctionalException;
import com.teads.developmenttest.exception.technical.TechnicalException;
import com.teads.developmenttest.main.Application;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReaderUtil {

    public static final String SEPARATOR = "#_#";
    public static final String AUCTION_FORMAT_REG_EX = "^(\\w+?):(\\d+)$";
    public static final String BID_FORMAT_REG_EX = "^(\\w+?):(\\d*(?:,\\d+)*)$";

    public static String readInput(String fileName) throws IOException {
        ClassLoader classLoader = Application.class.getClassLoader();
        File file;

        try {
            URL resource = classLoader.getResource(fileName);
            if (resource != null) {
                file = new File(resource.getFile());
            } else {
                throw new TechnicalException("File '" + fileName + "' does not exist");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Unable to open the file '" + fileName + "'");
            throw e;
        }

        String fileContent = new String(Files.readAllBytes(file.toPath()));
        fileContent = trimEmptyLines(fileContent);
        if (StringUtils.isBlank(fileContent)) {
            throw new FunctionalException("File '" + fileName + "' is empty");
        }
        return fileContent;
    }

    public static List<List<String>> validateAndFormatInput(String input) {
        String[] tokensArray = splitBy(input, SEPARATOR);

        //removes all empty lines
        List<String> tokensList = Arrays.stream(tokensArray)
                .filter(StringUtils::isNotBlank)
                .map(ReaderUtil::removeEmptyLines)
                .collect(Collectors.toList());

        List<List<String>> auctionDefinitionsList = separateAuctionDefinitions(tokensList);

        checkAuctionDefinitionsFormat(auctionDefinitionsList);

        return auctionDefinitionsList;
    }

    public static List<Auction> convertToEntityList(List<List<String>> auctionDefinitionsList) {
        return auctionDefinitionsList.stream()
                .map(ReaderUtil::convertToEntity)
                .collect(Collectors.toList());
    }

    private static Auction convertToEntity(List<String> auctionDefinition) {
        Auction auction = new Auction();
        for (int i = 0; i < auctionDefinition.size(); i++) {
            if (i == 0) {
                Pattern AuctionFormatRegEx = Pattern.compile(AUCTION_FORMAT_REG_EX);
                Matcher matcher = AuctionFormatRegEx.matcher(auctionDefinition.get(i));
                matcher.find();
                auction.setName(matcher.group(1));
                auction.setReservePrice(Integer.parseInt(matcher.group(2)));
            } else {
                Bidder bidder = new Bidder();
                Pattern BidFormatRegEx = Pattern.compile(BID_FORMAT_REG_EX);
                Matcher matcher = BidFormatRegEx.matcher(auctionDefinition.get(i));
                matcher.find();
                bidder.setName(matcher.group(1));
                List<Integer> bids = Arrays.stream(matcher.group(2).split(","))
                        .filter(StringUtils::isNotBlank)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                bidder.setBids(bids);
                auction.getBidders().add(bidder);
            }
        }
        return auction;
    }

    private static List<List<String>> separateAuctionDefinitions(List<String> tokensList) {
        return tokensList.stream()
                .map(token -> splitBy(token, "\\R"))
                .map(array -> Arrays.stream(array)
                        //removes white spaces for
                        .map(token -> token.replaceAll("\\s", ""))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
    }

    private static void checkAuctionDefinitionsFormat(List<List<String>> auctionsList) {
        for (List<String> auction : auctionsList) {
            for (int i = 0; i < auction.size(); i++) {
                if (i == 0) {
                    if (!auction.get(i).matches(AUCTION_FORMAT_REG_EX)) {
                        throw new FunctionalException("The auction '" + auction.get(i) + "' didn't match the input format: 'auction_name:reserve_price' ex: 'my_auction:10");
                    }
                } else {
                    if (!auction.get(i).matches(BID_FORMAT_REG_EX)) {
                        throw new FunctionalException("The bid '" + auction.get(i) + "' didn't match the input format: 'bidder_name:bid0,Bid1...' ex: 'myself:10,20,30");
                    }
                }
            }
        }
    }

    private static String[] splitBy(String input, String regex) {
        Pattern p = Pattern.compile(regex);
        return p.split(input);
    }

    private static String trimEmptyLines(String input) {
        String output = removeEmptyLines(input);
        // removes heading and tailing whitespaces
        output = output.replaceAll("(?m)^\\s*(\\S*)\\s*$", "$1");
        output = StringUtils.chomp(output.trim());
        return output;
    }

    private static String removeEmptyLines(String input) {
        return input.replaceAll("(?m)(^\\s*$\\R)", "");
    }
}
