package com.example.utils;

import com.example.entity.PriceHistory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ChartGenerator {

    public byte[] generatePriceHistoryChart(List<PriceHistory> priceHistory) throws IOException {
        TimeSeries series = new TimeSeries("Price");
        for (PriceHistory price : priceHistory) {
            series.add(new Second(price.getRecordedAt().getSecond(),
                    price.getRecordedAt().getMinute(),
                    price.getRecordedAt().getHour(),
                    price.getRecordedAt().getDayOfMonth(),
                    price.getRecordedAt().getMonthValue(),
                    price.getRecordedAt().getYear()), price.getPrice());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Price History",
                "Time",
                "Price",
                dataset,
                false,
                true,
                false
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 800, 600);
        return baos.toByteArray();
    }
}