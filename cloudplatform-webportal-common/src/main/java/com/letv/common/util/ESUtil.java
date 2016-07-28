package com.letv.common.util;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by liuhao1 on 2015/12/31.
 */
public class ESUtil {
    private static TransportClient client;

    static {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name","es_matrix_monitor")//.put("cluster.name","elasticsearch")
                .put("client.transport.sniff",true) //自动探测其他节点，加入到机器列表
                .put("client", true)
                .put("data",false)
                .build();
        /*client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("10.58.185.86", 9300));*/
        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("10.140.65.12", 9300))
                .addTransportAddress(new InetSocketTransportAddress("10.140.65.13", 9300))
                .addTransportAddress(new InetSocketTransportAddress("10.140.65.14", 9300));

    }

    public static  synchronized TransportClient getClient() {
        return client;
    }

    public static String add(String index,String type,Object o) {
        String source;
        if (o instanceof String) {
            source = (String) o;
        } else {
            source = JsonUtil.transToString(o);
        }
        IndexResponse indexResponse = getClient().prepareIndex(index.toLowerCase(), type.toLowerCase())
                .setSource(source)
                .execute()
                .actionGet();
        return indexResponse.getId();
    }
  /*  public static void bulkAdd(String index,String type,Object o) {
        String source;
        if (o instanceof String) {
            source = (String) o;
        } else {
            source = JsonUtil.transToString(o);
        }
        BulkRequestBuilder bulkRequestBuilder = getClient().prepareBulk();
        bulkRequestBuilder.add(getClient().prepareIndex(index.toLowerCase(), type.toLowerCase())
                .setSource(source));
        bulkRequestBuilder.execute().actionGet();
    }*/

    public static SearchHits getFilterResult(String[] indexs, FilterBuilder filterBuilder) {
        SearchResponse response = getClient().prepareSearch(indexs)
                .setPostFilter(filterBuilder)
                .setSize(10000)
                .execute().actionGet();
        return response.getHits();
    }
    public static SearchHits getFilterSortResult(String[] indexs, FilterBuilder filterBuilder,String sortField) {
        SearchResponse response = getClient().prepareSearch(indexs)
                .setPostFilter(filterBuilder)
                .addSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC))
                .setSize(10000)
                .execute().actionGet();
        return response.getHits();
    }
    public static SearchHits getFilterResult(String[] indexs, FilterBuilder filterBuilder,SortBuilder sortBuilder,int size) {
        SearchResponse response = client.prepareSearch(indexs)
                .setPostFilter(filterBuilder)
                .addSort(sortBuilder)
                .setSize(size)
                .execute().actionGet();
        return response.getHits();
    }
    public static SearchResponse getFilterResultByAggAndSort(String[] indexs, FilterBuilder filterBuilder,TermsBuilder termsBuilder,FacetBuilder facetBuilder, SortBuilder sortBuilder,int size) {
        SearchResponse response = client.prepareSearch(indexs)
                .setPostFilter(filterBuilder)
                .addSort(sortBuilder)
                .setSize(size)
                .addAggregation(termsBuilder)
                .addFacet(facetBuilder)
                .execute().actionGet();
        return response;
    }

    public static String delete(String index,String type,String id) {
        DeleteResponse response = getClient().prepareDelete(index,type,id)
                .execute()
                .actionGet();
        return response.getId();
    }

}
