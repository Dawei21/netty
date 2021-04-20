package com.sinbad.demo.gateway.pipline;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author sinbad
 * Created on 2021-04-20
 */
public abstract class BusinessHandler {


    protected abstract void execute(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest,
            Map<String, Object> paramMap)
            throws Exception;


}
