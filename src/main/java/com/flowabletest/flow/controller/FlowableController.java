package com.flowabletest.flow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@Api("flowable的基本操作接口")
@RequestMapping("/flowable")
@RefreshScope
public class FlowableController {

    private Logger logger = LoggerFactory.getLogger(FlowableController.class);

    @Value("${testData}")
    private String testData;

    @PostMapping("/testrefresh")
    public String testRefresh(){
        return testData;
    }

    /********************               分割线             *******************************/

    @Autowired
    private RuntimeService runtimeService;              //执行管理，包括启动、推进、删除流程实例等操作
    @Autowired
    private TaskService taskService;                    //任务管理
    @Autowired
    private RepositoryService repositoryService;        //管理流程定义、所谓的仓库流程定义文档的两个文件：bpmn文件和流程图片

    @Resource
    private ProcessEngine processEngine;                //核心引擎，其他Service都由他而来

    //1、发布流程,部署好一个流程资源 , 设置好自定义的 key
    @GetMapping("/initflowable/{key}")
    @ApiOperation(value = "发布流程，部署好一个流程资源",notes = "请输入一个 key 值在 url 上")
    public String initFlowable(@PathVariable("key") String key){
        Deployment deployment = repositoryService.createDeployment()
                                                .addClasspathResource("process/demo.bpmn20.xml")
                                                .addClasspathResource("demo.bpmn20.png")
                                                .name("myflow")
                                                .key(key)
                                                .deploy();
        /** addClasspathResource 表示从类路径下加载资源文件，一次只加载一个文件 **/
        logger.error("deploymentID:"+deployment.getId() + ",deploymentName:"+deployment.getName());
        return "deploymentID:"+deployment.getId() + ",deploymentName:"+deployment.getName();
    }

    //2、启动流程实例，使用 RuntimeService 启动一个流程实例（一个流程实例是用来走流程图的）
    @GetMapping("/startprocess")
    @ApiOperation(value = "启动流程实例",notes = "输入流程定义的 key 来初始化一个流程实例，为xml文件的名称")
    public String startProcess(@RequestParam("key") String key){
        //通过runtimeService 启动流程，使用流程定义的 Key 启动流程实例，默认会按照最新的版本启动本流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
        logger.error("pid:"+processInstance.getId()+",activitId:"+processInstance.getActivityId());
        return "pid:"+processInstance.getId()+",activitId:"+processInstance.getActivityId();

    }

    //3、查看个人任务，TaskService 完成任务的查询
    @GetMapping("/findpersontask")
    @ApiOperation(value = "查看个人任务",notes = "请输入一个办理人Id assignee")
    public String findPersonTask(@RequestParam("assignee") String assignee){
        //指定任务办理人

        //查询任务的列表
        List<Task> tasks = taskService.createTaskQuery()        //创建任务查询对象
                                    .taskAssignee(assignee)     //指定个人任务办理人
                                    .list();
        //遍历结果查看内容
        for(Task task:tasks){
            logger.error("taskId:"+task.getId()+",taskName:"+task.getName());
            logger.error("******************************************************************");
        }

        return "返回第一个："+tasks.get(0).toString()+"具体结果查看日志";
    }

    //4、办理任务 TaskService api 来完成任务
    @GetMapping("/completetask")
    @ApiOperation(value = "完成当前办理的任务",notes = "请输入一个任务ID taskId")
    public String completeTask(@RequestParam("taskId") String taskId){
        //通过 taskService 完成任务
        taskService.complete(taskId);
        logger.error("完成了任务");
        return "完成了任务，请继续查询任务流程";
    }
}
