package com.flowabletest.flow.controller;

import com.flowabletest.flow.config.OkPost;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  编写一个动态刷新配置文件的Controller层，需要与 HttpClient、Swagger 和 Cloud 的云配置 配合使用,
 *  yml文件需要配置暴露 env 和 refresh 访问接口
 *  @author: Lizq
 *  @Date: 2019/12/17 16:19
 */
@RestController
@RequestMapping("/refresh")
@Api("动态刷新配置的接口")
@RefreshScope
public class RefreshController {

    @Value("${server.port}")
    private String port;

    /**
     * 开始执行刷新配置文件的请求
     * @return
     */
    @GetMapping("/startrefresh")
    @Async
    @ApiOperation(value = "开始更新配置文件",notes = "start")
    public String startFresh(){
        String url = "http://localhost:"+port+"/actuator/refresh";
        OkPost.postUseOkhttp(url,"{}");
        return "看日志信息";
    }
}
