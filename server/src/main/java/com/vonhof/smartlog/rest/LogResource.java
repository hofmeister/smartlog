package com.vonhof.smartlog.rest;


import com.vonhof.babelshark.annotation.Name;
import com.vonhof.smartlog.dto.LogEntryDTO;
import com.vonhof.smartlog.dto.LogQueryDTO;
import com.vonhof.smartlog.dto.PagedList;
import com.vonhof.smartlog.service.LogService;
import com.vonhof.webi.HttpMethod;
import com.vonhof.webi.annotation.Body;
import com.vonhof.webi.annotation.Path;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Path("log")
@Name("log")
public class LogResource {

    @Inject
    private LogService logService;

    @Path(method = HttpMethod.PUT, value = "save")
    public void save(@Body LogEntryDTO entry) throws IOException, ExecutionException, InterruptedException {
        logService.save(entry);
    }

    @Path(method = HttpMethod.POST, value = "search")
    public PagedList<LogEntryDTO> search(@Body LogQueryDTO query) throws IOException {
        return logService.search(query);
    }
}
