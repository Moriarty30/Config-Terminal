package com.superpay.config.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ByIds {

    private List<String> ids;


    public List<String> getIds() {
        return ids;
    }


}

