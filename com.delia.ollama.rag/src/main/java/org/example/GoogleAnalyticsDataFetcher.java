package org.example;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class GoogleAnalyticsDataFetcher {

    public String fetchData(String startDate, String endDate) throws IOException {
        String propertyId = System.getenv("GA_PROPERTY_ID");
        if (propertyId == null || propertyId.isEmpty()) {
            throw new IllegalStateException("GA_PROPERTY_ID environment variable not set.");
        }

        // Using a default constructor instructs the client to use the credentials
        // specified in GOOGLE_APPLICATION_CREDENTIALS environment variable.
        try (BetaAnalyticsDataClient analyticsData = BetaAnalyticsDataClient.create()) {
            RunReportRequest request =
                    RunReportRequest.newBuilder()
                            .setProperty("properties/" + propertyId)
                            .addDimensions(Dimension.newBuilder().setName("age"))
                            .addDimensions(Dimension.newBuilder().setName("gender"))
                            .addDimensions(Dimension.newBuilder().setName("country"))
                            .addDimensions(Dimension.newBuilder().setName("city"))
                            .addDimensions(Dimension.newBuilder().setName("sessionSource"))
                            .addDimensions(Dimension.newBuilder().setName("sessionMedium"))
                            .addDimensions(Dimension.newBuilder().setName("sessionCampaignName"))
                            .addDimensions(Dimension.newBuilder().setName("browser"))
                            .addDimensions(Dimension.newBuilder().setName("deviceCategory"))
                            .addDimensions(Dimension.newBuilder().setName("operatingSystem"))
                            .addMetrics(Metric.newBuilder().setName("activeUsers"))
                            .addMetrics(Metric.newBuilder().setName("newLeads"))
                            .addMetrics(Metric.newBuilder().setName("convertedLeads"))
                            .addDateRanges(DateRange.newBuilder().setStartDate(startDate).setEndDate(endDate))
                            .build();

            // Make the request.
            RunReportResponse response = analyticsData.runReport(request);

            // Process the response and return it as a string.
            StringBuilder result = new StringBuilder();
            result.append("Report result:\n");
            for (Row row : response.getRowsList()) {
                for (int i = 0; i < response.getDimensionHeadersCount(); i++) {
                    result.append(String.format("%s: %s, ", response.getDimensionHeaders(i).getName(), row.getDimensionValues(i).getValue()));
                }
                for (int i = 0; i < response.getMetricHeadersCount(); i++) {
                    result.append(String.format("%s: %s, ", response.getMetricHeaders(i).getName(), row.getMetricValues(i).getValue()));
                }
                result.append("\n");
            }
            return result.toString();
        }
    }

}
