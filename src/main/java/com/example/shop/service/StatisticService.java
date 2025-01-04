package com.example.shop.service;

import com.example.shop.dtos.response.StatisticResponse;
import com.example.shop.repository.StatisticRepository;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticService {

    @Autowired
    StatisticRepository statisticRepository;

    public List<StatisticResponse.SatisticCategory> getStatisticCategoyByProduct() {

        List<Tuple> categoryTuples = statisticRepository.getStatisticCategoyByProduct();
        List<StatisticResponse.SatisticCategory> categories = new ArrayList<>();

        categoryTuples.forEach(
                (tuple) -> {
                    // Tạo đối tượng StatisticCategory
                    StatisticResponse.SatisticCategory category = new StatisticResponse.SatisticCategory();
                    // Lấy totalSold và chuyển đổi từ BigDecimal sang Integer
                    BigDecimal totalSoldBD = tuple.get("quantity", BigDecimal.class);

                    category.setName(tuple.get(0, String.class));
                    category.setQuantity(totalSoldBD.intValue());
                    categories.add(category);
                }
        );
        return categories;
    }

    public List<StatisticResponse.SatisticCategory>getStatisticProductByTotal() {
        List<Tuple> categoryTuples = statisticRepository.getStatisticProductByTotal();
        List<StatisticResponse.SatisticCategory> categories = new ArrayList<>();

        categoryTuples.forEach(
                (tuple) -> {
                    // Tạo đối tượng StatisticCategory
                    StatisticResponse.SatisticCategory category = new StatisticResponse.SatisticCategory();
                    category.setName(tuple.get(0, String.class));
                    category.setTotal(tuple.get(1, Double.class));
                    categories.add(category);
                }
        );
        return categories;
    }

    public List<StatisticResponse.StatisticRevenue> getStatisticRevenueByMonth(int year) {
        List<Tuple> revenueTuples = statisticRepository.getStatisticRevenueByMonth(year);
        List<StatisticResponse.StatisticRevenue> revenues = new ArrayList<>();

        revenueTuples.forEach(
                (tuple) -> {
                    // Tạo đối tượng StatisticRevenue
                    StatisticResponse.StatisticRevenue revenue = new StatisticResponse.StatisticRevenue();
                    revenue.setMonth(tuple.get("month", Integer.class));
                    revenue.setTotal(tuple.get("total", Double.class));
                    revenue.setQuantitySold(tuple.get("quantitySold", BigDecimal.class).intValue());
                    revenue.setTotalOrder(tuple.get("totalOrder", Long.class).intValue());
                    revenues.add(revenue);
                }
        );
        return revenues;
    }

    public StatisticResponse.OrderOverview getStatisticOrderOverview(String startDate, String endDate)
    {
        if(startDate == null && endDate == null)
        {
            startDate = "1970-01-01";
            endDate = String.valueOf(LocalDate.now().plusDays(1));
        }
        LocalDate startDateFormatted = LocalDate.parse(startDate);
        LocalDate endDateFormatted = LocalDate.parse(endDate);
        Tuple orderTuple = statisticRepository.getStatisticOrderOverview(startDateFormatted, endDateFormatted);
        StatisticResponse.OrderOverview orderOverview = new StatisticResponse.OrderOverview();
        orderOverview.setTotalOrder(orderTuple.get("totalOrder", Long.class).intValue());
        orderOverview.setTotalCompleted(orderTuple.get("totalCompletedOrder", Long.class).intValue());
        orderOverview.setTotalCanceled(orderTuple.get("totalFailedOrder", Long.class).intValue());
        orderOverview.setTotalPending(orderTuple.get("totalPendingOrder", Long.class).intValue());
        orderOverview.setTotalProcessing(orderTuple.get("totalProcessingOrder", Long.class).intValue());
        orderOverview.setTotalShipping(orderTuple.get("totalShippingOrder", Long.class).intValue());
        orderOverview.setTotalRevenue(orderTuple.get("totalRevenue", Double.class));
        orderOverview.setTotalCodPayment(orderTuple.get("totalCodPayment", Long.class).intValue());
        orderOverview.setTotalVnpayPayment(orderTuple.get("totalVnpayPayment", Long.class).intValue());
        return orderOverview;
    }

    public StatisticResponse.CustomerOverview getStatisticCustomer()
    {
        Tuple customerTuple = statisticRepository.getStatisticCustomer();
        StatisticResponse.CustomerOverview customerOverview = new StatisticResponse.CustomerOverview();
        customerOverview.setTotalCustomer(customerTuple.get("totalCustomers", Long.class).intValue());
        customerOverview.setTotalNewCustomer(customerTuple.get("newCustomers", Long.class).intValue());
        customerOverview.setTotalReturningCustomer(customerTuple.get("returnCustomers", Long.class).intValue());
        return customerOverview;
    }
}
