/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.ElasticQueries;
import com.mahdi.Entities.Measure;
import com.mahdi.Entities.Stats;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/*
Implementation of the Elasticsearch queries in order to retrieve Data collected
 from Ardgetti and Peaktech power meters
 */

public class RetrieveMeasures {
    public List<Measure> peakList = new ArrayList<>();
    public List<Measure> ardgList = new ArrayList<>();
    public int n=0;
    public Long x;
    public Measure m;

    public List<Measure> getPeakList() {
        return peakList;
    }
    public Stats stats= new Stats();
    public void setPeakList(List<Measure> peakList) {
        this.peakList = peakList;
    }
    public List<Measure> getArdgList() {
        return ardgList;
    }
    public void setArdgList(List<Measure> ardgList) {
        this.ardgList = ardgList;
    }

    public void retrieveAll(String index, List<Double> list){
        try {
            try (final RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")))) {
                final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
                ExtendedStatsAggregationBuilder aggregation =
                        AggregationBuilders
                                .extendedStats("agg")
                                .field("value")
                        ;
                final SearchSourceBuilder builder = new SearchSourceBuilder()
                        .sort("timestamp")
                        .aggregation(aggregation)

                        .size(10000);

                final SearchRequest request = new SearchRequest(index).scroll(scroll)
                        .source(builder);
            /*
            Decimal format 2 places #,##
             */
                DecimalFormat df2 = new DecimalFormat("#.##");
                df2.setRoundingMode(RoundingMode.UP);

                SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                ExtendedStats agg = response.getAggregations().get("agg");
                stats.setMin( (agg.getMin()));
                stats.setMax(agg.getMax());
                stats.setAvg(agg.getAvg());
                stats.setSum(agg.getSum()) ;
                stats.setCount(agg.getCount());
                stats.setStdDeviation(agg.getStdDeviation());
                stats.setVariance(agg.getVariance());
                //System.out.println(stats);

                String scrollId = response.getScrollId();
                SearchHit[] hits = response.getHits().getHits();
                list.clear();
                while (hits != null && hits.length > 0) {
                    final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                            .scroll(scroll);
                    response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                    scrollId = response.getScrollId();
                    hits = response.getHits().getHits();
                    for (final SearchHit hit : hits) {
                        list.add( (Double)hit.getSourceAsMap().get("value"));
                    }
                }
                final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

public void perfectRetrieve(String index, List<Measure> list,Long x,Long y){
    try {
        try (final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")))) {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
            ExtendedStatsAggregationBuilder aggregation =
                    AggregationBuilders
                            .extendedStats("agg")
                            .field("value")
                    ;
            final SearchSourceBuilder builder = new SearchSourceBuilder()
                    .query(QueryBuilders
                            .rangeQuery("timestamp")
                            .gt(x)
                            .lt(y)
                    ).sort("timestamp")
                    .aggregation(aggregation)

                    .size(1);

            final SearchRequest request = new SearchRequest(index).scroll(scroll)
                    .source(builder);
            /*
            Decimal format 2 places #,##
             */
            DecimalFormat df2 = new DecimalFormat("#.##");
            df2.setRoundingMode(RoundingMode.UP);

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            ExtendedStats agg = response.getAggregations().get("agg");
            stats.setMin( (agg.getMin()));
            stats.setMax(agg.getMax());
            stats.setAvg(agg.getAvg());
            stats.setSum(agg.getSum()) ;
            stats.setCount(agg.getCount());
            stats.setStdDeviation(agg.getStdDeviation());
            stats.setVariance(agg.getVariance());
            //System.out.println(stats);

            String scrollId = response.getScrollId();
            SearchHit[] hits = response.getHits().getHits();
            list.clear();
            while (hits != null && hits.length > 0) {
                final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                        .scroll(scroll);
                response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = response.getScrollId();
                hits = response.getHits().getHits();
                for (final SearchHit hit : hits) {
                    m=new Measure();
                    m.setTIMESTAMP((Long) hit.getSourceAsMap().get("timestamp"));
                    m.setVALUE((Double) hit.getSourceAsMap().get("value"));
                    list.add(m);
                }
            }
            final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    public void retrieveData(String index, List<Measure> list) {
        try {
            try (final RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")))) {
                final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
                final SearchSourceBuilder builder = new SearchSourceBuilder()
                        .query(QueryBuilders
                                .rangeQuery("timestamp")

                                .gte(System.currentTimeMillis() - 3000)

                        )
                        .size(1);
                final SearchRequest request = new SearchRequest(index).scroll(scroll)
                        .source(builder);
                    SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                String scrollId = response.getScrollId();
                SearchHit[] hits = response.getHits().getHits();
                while (hits != null && hits.length > 0) {
                    final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                            .scroll(scroll);
                    response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                    scrollId = response.getScrollId();
                    hits = response.getHits().getHits();
                    for (final SearchHit hit : hits) {
                        Measure m = new Measure();
                        m.setTIMESTAMP((Long) hit.getSourceAsMap().get("timestamp"));
                        m.setVALUE((Double) hit.getSourceAsMap().get("value"));
                        list.add(m);
                    }
                }
                final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void retrieveLastMinute(String index, List<Measure> list) {
        try {
            try (final RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")))) {
                final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
                final SearchSourceBuilder builder = new SearchSourceBuilder()
                        .query(QueryBuilders
                                .rangeQuery("timestamp")
                                .gte(System.currentTimeMillis() - 60000)
                        )
                        .size(1);
                final SearchRequest request = new SearchRequest(index).scroll(scroll)
                        .source(builder);
                SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                String scrollId = response.getScrollId();
                SearchHit[] hits = response.getHits().getHits();
                while (hits != null && hits.length > 0) {
                    final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                            .scroll(scroll);
                    response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                    scrollId = response.getScrollId();
                    hits = response.getHits().getHits();
                    for (final SearchHit hit : hits) {
                        Measure m = new Measure();
                        m.setTIMESTAMP((Long) hit.getSourceAsMap().get("timestamp"));
                        m.setVALUE((Double) hit.getSourceAsMap().get("value"));
                        list.add(m);
                    }
                }
                final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void retrieveLastHour(String index, List<Measure> list) {
        try {
            try (final RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")))) {
                final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
                final SearchSourceBuilder builder = new SearchSourceBuilder()
                        .query(QueryBuilders
                                .rangeQuery("timestamp")
                                .gte(System.currentTimeMillis() - 3600000)
                        )
                        .size(1);
                final SearchRequest request = new SearchRequest(index).scroll(scroll)
                        .source(builder);
                SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                String scrollId = response.getScrollId();
                SearchHit[] hits = response.getHits().getHits();
                while (hits != null && hits.length > 0) {
                    final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                            .scroll(scroll);
                    response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                    scrollId = response.getScrollId();
                    hits = response.getHits().getHits();
                    for (final SearchHit hit : hits) {
                        Measure m = new Measure();

                        m.setTIMESTAMP((Long) hit.getSourceAsMap().get("timestamp"));
                        m.setVALUE((Double) hit.getSourceAsMap().get("value"));
                        list.add(m);
                    }
                }
                final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void retrieveLastDay(String index, List<Measure> list) {

        try {
            try (final RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")))) {
                final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
                final SearchSourceBuilder builder = new SearchSourceBuilder()
                        .query(QueryBuilders
                                .rangeQuery("timestamp")
                                .gte(System.currentTimeMillis() - 86400000)
                        )
                        .size(1);
                final SearchRequest request = new SearchRequest(index).scroll(scroll)
                        .source(builder);
                SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                String scrollId = response.getScrollId();
                SearchHit[] hits = response.getHits().getHits();
                while (hits != null && hits.length > 0) {
                    final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                            .scroll(scroll);
                    response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                    scrollId = response.getScrollId();
                    hits = response.getHits().getHits();
                    for (final SearchHit hit : hits) {
                        Measure m = new Measure();

                        m.setTIMESTAMP((Long) hit.getSourceAsMap().get("timestamp"));
                        m.setVALUE((Double) hit.getSourceAsMap().get("value"));
                        list.add(m);
                    }
                }
                final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void retrieveLastWeek(String index, List<Measure> list) {

        try {
            try (final RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")))) {
                final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
                final SearchSourceBuilder builder = new SearchSourceBuilder()
                        .query(QueryBuilders
                                .rangeQuery("timestamp")
                                .gte(System.currentTimeMillis() - 604800000
                                )
                        )
                        .size(500);
                final SearchRequest request = new SearchRequest(index).scroll(scroll)
                        .source(builder);
                SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                String scrollId = response.getScrollId();
                SearchHit[] hits = response.getHits().getHits();
                while (hits != null && hits.length > 0) {
                    final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                            .scroll(scroll);
                    response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                    scrollId = response.getScrollId();
                    hits = response.getHits().getHits();
                    for (final SearchHit hit : hits) {
                        Measure m = new Measure();

                        m.setTIMESTAMP((Long) hit.getSourceAsMap().get("timestamp"));
                        m.setVALUE((Double) hit.getSourceAsMap().get("value"));
                        list.add(m);
                    }
                }
                final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void retrievePreviousData(String index, List<Measure> list,int n,Long ts) {

        try {
            try (final RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")))) {
                final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(2));
                    final SearchSourceBuilder builder = new SearchSourceBuilder()
                            .query(QueryBuilders
                                    .rangeQuery("timestamp")
                                    .gte(ts- 3600000 * n)
                            )
                            .size(1);
                    final SearchRequest request = new SearchRequest(index).scroll(scroll)
                            .source(builder);
                    SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                    String scrollId = response.getScrollId();
                    SearchHit[] hits = response.getHits().getHits();
                    while (hits != null && hits.length > 0) {
                        final SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                                .scroll(scroll);
                        response = client.searchScroll(scrollRequest, RequestOptions.DEFAULT);
                        scrollId = response.getScrollId();
                        hits = response.getHits().getHits();
                        for (final SearchHit hit : hits) {
                            Measure m = new Measure();

                            m.setTIMESTAMP((Long) hit.getSourceAsMap().get("timestamp"));
                            m.setVALUE((Double) hit.getSourceAsMap().get("value"));
                            list.add(m);
                        }
                    }
                    final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                    clearScrollRequest.addScrollId(scrollId);
                    final ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
                }
            }catch (IOException e) {
            e.printStackTrace();
        }

        }


    public static void main(String[] args) {
        List<Double> l=new ArrayList<>();
        RetrieveMeasures r = new RetrieveMeasures();
        r.retrieveAll("peaktechpower",l);
        System.out.println(l.size());

    }
    }

