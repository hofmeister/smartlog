package com.vonhof.smartlog;

import com.vonhof.babelshark.BabelShark;
import com.vonhof.babelshark.language.JsonLanguage;
import com.vonhof.smartlog.rest.LogResource;
import com.vonhof.smartlog.service.LogService;
import com.vonhof.webi.Webi;
import com.vonhof.webi.Webi.ShutdownHandler;
import com.vonhof.webi.rest.RESTServiceHandler;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class Server {

    public static void main(String[] args) throws Exception {
        BabelShark.register(new JsonLanguage());

        Webi webi = new Webi(8844);

        webi.addBean(BabelShark.getDefaultInstance());

        final Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

        webi.addBean(Client.class, client);

        webi.addBean(new LogService());

        RESTServiceHandler rest = webi.add("/rest/", new RESTServiceHandler());

        rest.expose(new LogResource());


        webi.add(new ShutdownHandler() {
            @Override
            public void onShutdown(boolean b) throws Exception {
                client.close();
            }
        });

        webi.start();
    }
}
