package com.sinbad.demo.gateway.handler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;

/**
 * @author sinbad
 * Created on 2021-04-20
 */
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest)
            throws Exception {

        FullHttpResponse fullHttpResponse;
        Map<String, Object> paramsFromChannel = null;
        if (fullHttpRequest.method() == HttpMethod.GET) {
            paramsFromChannel = getGetParamsFromChannel(fullHttpRequest);
        } else if (fullHttpRequest.method() == HttpMethod.POST) {
            paramsFromChannel = getPostParamsFromChannel(fullHttpRequest);
        }

        System.out.println("Request param: " + new Gson().toJson(paramsFromChannel));


        String data = "GET method over";
        ByteBuf buf = Unpooled.copiedBuffer(data, CharsetUtil.UTF_8);
        fullHttpResponse = responseOK(HttpResponseStatus.OK, buf);
        channelHandlerContext.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
        System.out.println("Finish");
    }


    /*
     * 获取GET方式传递的参数
     */
    private Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {

        Map<String, Object> paramMap = new HashMap<>();

        if (fullHttpRequest.method() == HttpMethod.GET) {
            // 处理get请求
            QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> paramList = decoder.parameters();
            for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
                paramMap.put(entry.getKey(), entry.getValue().get(0));
            }
            return paramMap;
        } else {
            return paramMap;
        }

    }

    /*
     * 获取POST方式传递的参数
     */
    private Map<String, Object> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {

        Map<String, Object> paramMap = new HashMap<>();

        if (fullHttpRequest.method() == HttpMethod.POST) {
            // 处理POST请求
            String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
            if (strContentType.contains("x-www-form-urlencoded")) {
                paramMap = getFormParams(fullHttpRequest);
            } else if (strContentType.contains("application/json")) {
                paramMap = getJSONParams(fullHttpRequest);
            }
        }
        return paramMap;
    }

    /*
     * 解析from表单数据（Content-Type = x-www-form-urlencoded）
     */
    private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> paramMap = new HashMap<>();

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);
        List<InterfaceHttpData> postDataList = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : postDataList) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                paramMap.put(attribute.getName(), attribute.getValue());
            }
        }
        return paramMap;
    }

    /*
     * 解析json数据（Content-Type = application/json）
     */
    private Map<String, Object> getJSONParams(FullHttpRequest fullHttpRequest) {

        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = new String(reqContent, StandardCharsets.UTF_8);
        Map<String, Object> paramMap = new Gson().fromJson(strContent, new TypeToken<Map<String, Object>>() {
        }.getType());

        return paramMap;
    }

    private FullHttpResponse responseOK(HttpResponseStatus httpResponseStatus, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus, content);
        response.headers().set("Content-Type", "text/plain;charset=UTF-8");
        response.headers().set("Content_Length", response.content().readableBytes());
        return response;
    }
}
