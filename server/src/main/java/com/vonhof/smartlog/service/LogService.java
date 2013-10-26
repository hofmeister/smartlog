package com.vonhof.smartlog.service;


import com.vonhof.smartlog.dto.LogEntryDTO;
import com.vonhof.smartlog.dto.LogQueryDTO;
import com.vonhof.smartlog.dto.PagedList;
import com.vonhof.webi.bean.AfterInject;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.FilterBuilders.andFilter;
import static org.elasticsearch.index.query.FilterBuilders.inFilter;
import static org.elasticsearch.index.query.FilterBuilders.limitFilter;
import static org.elasticsearch.index.query.FilterBuilders.rangeFilter;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

public class LogService implements AfterInject {
    private static final Logger log = Logger.getLogger(LogService.class.getName());

    private ObjectMapper om = new ObjectMapper();
    
    @Inject
    private Client es;


    public void save(LogEntryDTO entry) throws IOException, ExecutionException, InterruptedException {
        es.prepareIndex("logs","log")
                .setSource(om.writeValueAsString(entry))
                .execute().actionGet();
    }

    public PagedList<LogEntryDTO> search(LogQueryDTO query) {
        PagedList<LogEntryDTO> out = new PagedList<LogEntryDTO>();

        BoolQueryBuilder queries = boolQuery();

        AndFilterBuilder filters = andFilter(limitFilter(query.getLimit()));

        if (query.getMessage() != null) {
            queries.must(prefixQuery("log.message", query.getMessage()));
        } else {
            //Needs at least one query param to be able to filter
            queries.must(prefixQuery("log.message", ""));
        }

        if (query.getNamespace() != null) {
            queries.must(prefixQuery("log.clazz", query.getNamespace()));
        }

        if (query.getTags() != null && query.getTags().length > 0) {
            filters.add(inFilter("log.tags", query.getTags()));
        }

        if (query.getSince() != null) {
            filters.add(rangeFilter("log.created").gte(query.getSince()));
        }

        SearchRequestBuilder qPrep = es.prepareSearch("logs")
                .setQuery(queries)
                .setFilter(filters)
                .setFrom(query.getOffset())
                .setSize(query.getLimit());

        SearchResponse result = qPrep.execute().actionGet();

        out.setTotal(result.getHits().getTotalHits());
        out.setOffset(query.getOffset());

        for(SearchHit hit : result.getHits().hits()) {
            try {
                LogEntryDTO entry = om.readValue(hit.getSourceAsString(), LogEntryDTO.class);
                out.getRows().add(entry);
            } catch (IOException e) {
                log.log(Level.WARNING,"Failed to deserialize log entry", e);
            }
        }

        return out;
    }

    @Override
    public void afterInject() {
        IndicesExistsResponse exists = es.admin().indices().exists(new IndicesExistsRequest("logs")).actionGet();
        if (!exists.isExists()) {
            es.admin().indices()
                    .create(new CreateIndexRequest("logs")).actionGet();
        }

        es.admin().indices()
                .preparePutMapping("logs")
                .setType("log")
                .setSource(mapping())
                .execute().actionGet();
    }

    private XContentBuilder mapping() {
        try {
            return jsonBuilder()
                    .startObject()
                        .startObject("log")
                            .startObject("properties")
                                .startObject("message")
                                    .field("type", "string")
                                    .field("analyzer", "english")
                                .endObject()
                                .startObject("tags")
                                    .field("type", "string")
                                    .field("index_name", "tag")
                                    .field("analyzer", "keyword")
                                .endObject()
                                .startObject("args")
                                    .field("type", "string")
                                    .field("index_name", "arg")
                                    .field("analyzer", "keyword")
                                .endObject()
                                .startObject("clazz")
                                    .field("type", "string")
                                    .field("analyzer", "english")
                                .endObject()
                                .startObject("created")
                                    .field("type", "date")
                                .endObject()
                        .endObject()
                    .endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
