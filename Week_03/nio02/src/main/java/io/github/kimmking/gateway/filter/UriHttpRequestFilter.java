package io.github.kimmking.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class UriHttpRequestFilter implements HttpRequestFilter {
    @Override
    public void filter(final FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        String uri = fullRequest.uri();
        System.out.println("uri: " + uri);
        if (!uri.contains("api/hello")) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            ctx.write(response);
            ctx.flush();
            ctx.close();
        }
    }
}
