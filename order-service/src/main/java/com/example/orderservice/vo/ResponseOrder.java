package com.example.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ResponseOrder implements Serializable {
    private String productId;
    private Integer qty;

    private Integer unitPrice;
    private Integer totalPrice;
    private Date createdAt;

    private String orderId;
}
