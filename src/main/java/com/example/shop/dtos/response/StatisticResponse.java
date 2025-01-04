package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StatisticResponse {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SatisticCategory{
        private String name;
        private int quantity;
        private Double total;

        public SatisticCategory(String name, int quantity)
        {
            this.name = name;
            this.quantity = quantity;
        }
        public SatisticCategory(String name, Double total)
        {
            this.name = name;
            this.total = total;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public  static class StatisticRevenue{
        private int month;
        private Double total;
        private int quantitySold;
        private int totalOrder;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderOverview{
        private int totalOrder;
        private int totalCompleted;
        private int totalProcessing;
        private int totalCanceled;
        private int totalPending;
        private int totalShipping;
        private int totalVnpayPayment;
        private int totalCodPayment;
        private Double totalRevenue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerOverview{
        private int totalCustomer;
        private int totalNewCustomer;
        private int totalReturningCustomer;
    }
}
