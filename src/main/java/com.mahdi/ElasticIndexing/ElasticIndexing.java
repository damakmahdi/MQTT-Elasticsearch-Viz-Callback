/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.ElasticIndexing;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/*
Implementation of the Index API in order to index Data collected
 from Ardgetti and Peaktech power meters into Elasticsearch
 */
public class ElasticIndexing {
    public void indexApiSyncTest(String index,Float value,long timestamp) {
        try (final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")        ))) {
            // Map
            final Map<String, Object> map = new HashMap<>();
            map.put("value", value);
            map.put("timestamp", timestamp);
            final IndexRequest request = new IndexRequest()
                    .index(index)
                    .timeout(TimeValue.timeValueMinutes(2))
                    .source(map);
            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (final IOException e) {
            throw new RuntimeException(e); }
    }



}