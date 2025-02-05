package com.hollykunge.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hollykunge.model.Item;
import com.hollykunge.model.Vote;
import com.hollykunge.model.VoteItem;
import com.hollykunge.service.ItemService;
import com.hollykunge.service.VoteItemService;
import com.hollykunge.service.VoteService;
import com.hollykunge.util.ExceptionCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhhongyu
 * @deprecation 读取excel头文件
 */
@Slf4j
public class UploadHeaderDataListener extends AnalysisEventListener<ItemUploadData> {
    private final ItemService itemService;

    private final VoteService voteService;

    private final Item item;

    public UploadHeaderDataListener(Item item, VoteService voteService,ItemService itemService) {
        this.item = item;
        this.voteService = voteService;
        this.itemService = itemService;
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        Vote vote = null;
        try {
            Item itemtemp = itemService.findById(item.getId());
            vote = itemtemp.getVote();
            vote.setExcelHeader(JSON.toJSONString(headMap));
            voteService.updateById(vote);
            log.info("更新vote中的excel头成功！");
        } catch (Exception e) {
            log.error("更新vote数据库失败！");
            log.error(ExceptionCommonUtil.getExceptionMessage(e));
        }
    }

    @Override
    public void invoke(ItemUploadData itemUploadData, AnalysisContext analysisContext) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}