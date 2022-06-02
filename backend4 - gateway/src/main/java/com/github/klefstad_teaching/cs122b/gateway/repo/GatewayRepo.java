package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.data.GatewayRequestObject;
import com.jayway.jsonpath.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Types;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GatewayRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public Mono<int[]> insertRequests(List<GatewayRequestObject> requests)
    {
        return Mono.fromCallable(() -> insert(requests));
    }

    public int[] insert(List<GatewayRequestObject> requests)
    {
        MapSqlParameterSource[] sourceArray = new MapSqlParameterSource[requests.size()];

        for (int i = 0; i < requests.size(); i++) {
            MapSqlParameterSource source = new MapSqlParameterSource();
            source.addValue("id", requests.get(i).getId(), Types.INTEGER);
            source.addValue("ipAddress", requests.get(i).getIpAddress(), Types.VARCHAR);
            source.addValue("path", requests.get(i).getPath(), Types.VARCHAR);
            source.addValue("callTime", requests.get(i).getCallTime(), Types.TIMESTAMP);
            sourceArray[i] = source;
        }

        return this.template.batchUpdate(
                "INSERT INTO gateway.request(id, ip_address, call_time, path) VALUES (:id, :ipAddress, :callTime, :path)",
                sourceArray
        );
    }
}
