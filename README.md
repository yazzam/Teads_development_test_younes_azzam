# Teads development test

## Use case
This java application calculate the winner of a second-price, sealed-bid auction.

lets assume

An object is for sale with a reserve price.

We have several potential buyers, each one being able to place one or more bids.

The buyer winning the auction is the one with the highest bid above or equal to the reserve price.

The winning price is the highest bid price from a non-winning buyer above the reserve price (or the reserve price if none applies)


### Example
Consider 5 potential buyers (A, B, C, D, E) who compete to acquire an object with a reserve price set at 100 euros, bidding as follows:

A: 2 bids of 110 and 130 euros

B: 0 bid

C: 1 bid of 125 euros

D: 3 bids of 105, 115 and 90 euros

E: 3 bids of 132, 135 and 140 euros

The buyer E wins the auction at the price of 130 euros.

If two buyers have the same bid, then the auction is cancelled

## Installation
Install git

intall jdk 11.0.10 or above

intsall maven 3.6.3 or above

clone the project

`git clone --branch master https://github.com/yazzam/teadsTest`

Navigate to root directory

`cd teadsTest`

build the project, this phase will also run the tests

`mvn clean install`

## Usage
Now you can run the application.

By default the input will come from a default file containing two auctions: `src/main/resources/data/auctionInputs.txt`

The file must be formatted like follow for the application to work

`aution_name: reserve_price
bidder_name: comma separated values of his bid
bidder_name: comma separated values of his bid
bidder_name: comma separated values of his bid
#_#
aution_name: reserve_price
bidder_name: comma separated values of his bid
bidder_name: comma separated values of his bid
bidder_name: comma separated values of his bid`

The separator between two auctions is `#_#`
The names are alpha numerical with _
here is a functionning example
`
auction0: 5
bidder0: 8, 10
bidder1: 10, 12
#_#
auction1: 10
bidder0: 8, 12
bidder1: 10, 14
bidder1: 12, 18
`

To run the application with the default file  `src/main/resources/data/auctionInputs.txt` use :
`mvn exec:java -Dexec.mainClass="com.teads.developmenttest.main.Application"`

To run the application with your own file run:
`mvn exec:java -Dexec.mainClass="com.teads.developmenttest.main.Application" -Dexec.args="path_to_your_file"`

