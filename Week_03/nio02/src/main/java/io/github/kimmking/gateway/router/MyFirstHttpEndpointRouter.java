package io.github.kimmking.gateway.router;

import io.github.kimmking.gateway.router.HttpEndpointRouter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class MyFirstHttpEndpointRouter implements HttpEndpointRouter {

    @Override
    public String route(List<String> endpoints) {
        int size = endpoints.size();
        return endpoints.get(new Random().nextInt(size));
    }

    public static void main(String[] args) {
        URL url = null;
        try {
            url = new URL("http://localhost:8088/api/hello");
            URI uri = url.toURI();
            System.out.println(uri.toASCIIString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
