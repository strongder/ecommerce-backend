package com.example.shop.controller;

import com.example.shop.dtos.response.StatisticResponse;
import com.example.shop.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/category/category-sold")
    public List<StatisticResponse.SatisticCategory> getStatisticCategoyByProduct() {
        return statisticService.getStatisticCategoyByProduct();
    }

    @GetMapping("/category/category-total")
    public List<StatisticResponse.SatisticCategory>getStatisticProductByTotal() {
        return statisticService.getStatisticProductByTotal();
    }

    @GetMapping("/revenue/month/{year}")
    public List<StatisticResponse.StatisticRevenue> getStatisticRevenueByMonth(@PathVariable("year") int year) {
        return statisticService.getStatisticRevenueByMonth(year);
    }

    @GetMapping("/order/overview")
    public StatisticResponse.OrderOverview getStatisticOrderOverview(
            @RequestParam(value = "startDate", required = false)  String startDate,
            @RequestParam(value = "endDate", required = false) String  endDate) {

        return statisticService.getStatisticOrderOverview(startDate, endDate);
    }

    @GetMapping("/customer/overview")
    public StatisticResponse.CustomerOverview getStatisticCustomer() {
        return statisticService.getStatisticCustomer();
    }
}
