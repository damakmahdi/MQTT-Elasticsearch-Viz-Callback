/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.ElasticIndexing;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;



/*
Implementation of the Bulk Index API in order to index Data collected
 from Ardgetti and Peaktech power meters into Elasticsearch
 */
public class BulkIndexing {
    final Map<String, Object> map = new HashMap<>();

    @Test
    public void bulkApiTest(String index,Float value,long timestamp) {
        try (final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        )) {
            this.map.put("value", value);
            this.map.put("timestamp", timestamp);
            final BulkRequest request = new BulkRequest()
                    .timeout(TimeValue.timeValueMinutes(2))
                    ;
            request.add(new IndexRequest()
                    .index(index)
                    .source(map)
            );
                final BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            bulkResponse.forEach(bulkItemResponse -> {
                final DocWriteResponse response = bulkItemResponse.getResponse();
                assertThat(response.getIndex(), is(index));
                    final IndexResponse indexResponse = (IndexResponse) response;
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


}