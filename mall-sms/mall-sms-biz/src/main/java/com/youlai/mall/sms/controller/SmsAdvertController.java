package com.youlai.mall.sms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.common.core.result.PageResult;
import com.youlai.common.core.result.Result;
import com.youlai.mall.sms.api.SmsAdvert;
import com.youlai.mall.sms.service.ISmsAdvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api(tags = "广告接口")
@RestController
@RequestMapping("/adverts")
@Slf4j
@AllArgsConstructor
public class SmsAdvertController {

    private ISmsAdvertService iSmsAdvertService;

    @ApiOperation(value = "列表分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "每页数量", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "advert", value = "广告信息", paramType = "query", dataType = "SmsAdvert")
    })
    @GetMapping
    public Result list(Integer page, Integer limit, SmsAdvert advert) {
        LambdaQueryWrapper<SmsAdvert> queryWrapper = new LambdaQueryWrapper<SmsAdvert>()
                .like(StrUtil.isNotBlank(advert.getName()), SmsAdvert::getName, advert.getName())
                .orderByDesc(SmsAdvert::getUpdateTime)
                .orderByDesc(SmsAdvert::getCreateTime);

        if (page != null && limit != null) {
            Page<SmsAdvert> result = iSmsAdvertService.page(new Page<>(page, limit), queryWrapper);
            return PageResult.success(result.getRecords(), result.getTotal());
        } else if (limit != null) {
            queryWrapper.last("LIMIT " + limit);
        }
        List<SmsAdvert> list = iSmsAdvertService.list(queryWrapper);
        return Result.success(list);
    }

    @ApiOperation(value = "广告详情", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "广告id", required = true, paramType = "path", dataType = "Integer")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SmsAdvert advert = iSmsAdvertService.getById(id);
        return Result.success(advert);
    }

    @ApiOperation(value = "新增广告", httpMethod = "POST")
    @ApiImplicitParam(name = "advert", value = "实体JSON对象", required = true, paramType = "body", dataType = "SmsAdvert")
    @PostMapping
    public Result add(@RequestBody SmsAdvert advert) {
        boolean status = iSmsAdvertService.save(advert);
        return Result.status(status);
    }

    @ApiOperation(value = "修改广告", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "广告id", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "advert", value = "实体JSON对象", required = true, paramType = "body", dataType = "SmsAdvert")
    })
    @PutMapping(value = "/{id}")
    public Result update(
            @PathVariable Integer id,
            @RequestBody SmsAdvert advert) {
        advert.setUpdateTime(new Date());
        boolean status = iSmsAdvertService.updateById(advert);
        return Result.status(status);
    }

    @ApiOperation(value = "删除广告", httpMethod = "DELETE")
    @ApiImplicitParam(name = "ids[]", value = "id集合", required = true, paramType = "query", allowMultiple = true, dataType = "Integer")
    @DeleteMapping
    public Result delete(@RequestParam("ids") List<Long> ids) {
        boolean status = iSmsAdvertService.removeByIds(ids);
        return Result.status(status);
    }

    @ApiOperation(value = "修改广告(部分更新)", httpMethod = "PATCH")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "advert", value = "实体JSON对象", required = true, paramType = "body", dataType = "SmsAdvert")
    })
    @PatchMapping(value = "/{id}")
    public Result patch(@PathVariable Integer id, @RequestBody SmsAdvert advert) {
        LambdaUpdateWrapper<SmsAdvert> luw = new LambdaUpdateWrapper<SmsAdvert>().eq(SmsAdvert::getId, id);
        if (advert.getStatus() != null) { // 状态更新
            luw.set(SmsAdvert::getStatus, advert.getStatus());
        }
        boolean update = iSmsAdvertService.update(luw);
        return Result.success(update);
    }
}
