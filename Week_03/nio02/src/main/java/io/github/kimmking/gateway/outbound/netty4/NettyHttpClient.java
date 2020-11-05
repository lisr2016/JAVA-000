package io.github.kimmking.gateway.outbound.netty4;

import io.github.kimmking.gateway.router.MyFirstHttpEndpointRouter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NettyHttpClient {
    private List<String> backendUrlList;

    public NettyHttpClient(List<String> backendUrlList) {
        this.backendUrlList = backendUrlList;
    }

    public void connect(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) throws Exception {
        MyFirstHttpEndpointRouter myFirstHttpEndpointRouter = new MyFirstHttpEndpointRouter();
        String backendUrl = myFirstHttpEndpointRouter.route(backendUrlList);
        // System.out.println("Current backendUrl: " + backendUrl);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new NettyHttpOutboundHandler(ctx, fullHttpRequest, backendUrl));
                }
            });


            /*DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, new URI("/api/hello").toASCIIString());
            // 构建http请求
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderNames.CONNECTION);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                    request.content().readableBytes());*/
            // Start the client.
            URL url = new URL(backendUrl);
            int port = url.getPort() > 0 ? url.getPort() : 80;
            String host = url.getHost();
            ChannelFuture f = b.connect(host, port).sync();
            /*f.channel().write(request);
            f.channel().flush();*/
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}
