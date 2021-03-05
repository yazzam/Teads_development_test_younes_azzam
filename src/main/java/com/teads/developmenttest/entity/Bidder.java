package com.teads.developmenttest.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Bidder {
    private String name;
    private List<Integer> bids;
}
