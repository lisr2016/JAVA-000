package io.github.kimmking.gateway.inbound;

import io.github.kimmking.gateway.filter.HttpMethodRequestFilter;
import io.github.kimmking.gateway.filter.UriHttpRequestFilter;
import io.github.kimmking.gateway.outbound.httpclient4.HttpOutboundHandler;
import io.github.kimmking.gateway.outbound.netty4.NettyHttpClient;
import io.github.kimmking.gateway.outbound.netty4.NettyHttpOutboundHandler;
import io.github.kimmking.gateway.util.ByteBufToBytes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private final List<String> proxyServerList;
    private NettyHttpClient handler;

    public HttpInboundHandler(List<String> proxyServerList) {
        this.proxyServerList = proxyServerList;
        handler = new NettyHttpClient(proxyServerList);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            String uri = fullRequest.uri();
            fullRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);
            //System.out.println("接收到的请求url为" + uri);
//            if (uri.contains("/test")) {
//                handlerTest(fullRequest, ctx);
//            }

            //handler.handle(fullRequest, ctx);
            HttpMethodRequestFilter methodHttpRequestFilter = new HttpMethodRequestFilter();
            methodHttpRequestFilter.filter(fullRequest, ctx);
            // UriHttpRequestFilter uriHttpRequestFilter = new UriHttpRequestFilter();
            // uriHttpRequestFilter.filter(fullRequest, ctx);
            handler.connect(fullRequest, ctx);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

//    private void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
//        FullHttpResponse response = null;
//        try {
//            String value = "hello,kimmking";
//            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
//            response.headers().set("Content-Type", "application/json");
//            response.headers().setInt("Content-Length", response.content().readableBytes());
//
//        } catch (Exception e) {
//            logger.error("处理测试接口出错", e);
//            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
//        } finally {
//            if (fullRequest != null) {
//                if (!HttpUtil.isKeepAlive(fullRequest)) {
//                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//                } else {
//                    response.headers().set(CONNECTION, KEEP_ALIVE);
//                    ctx.write(response);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        cause.printStackTrace();
//        ctx.close();
//    }

}
