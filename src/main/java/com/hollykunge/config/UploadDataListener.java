package com.hollykunge.config;

import com.alibaba.fastjson.JSONObject;
import com.hollykunge.model.Item;
import com.hollykunge.model.Vote;
import com.hollykunge.model.VoteItem;
import com.hollykunge.service.VoteItemService;
import com.hollykunge.util.ExceptionCommonUtil;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhhongyu
 * @deprecation 读取excel文件
 */
@Slf4j
public class UploadDataListener extends AnalysisEventListener<ItemUploadData> {


    private final VoteItemService voteItemService;

    private final Item item;

    public UploadDataListener(Item item, VoteItemService voteItemService) {
        this.voteItemService = voteItemService;
        this.item = item;
    }


    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    List<ItemUploadData> list = new ArrayList<ItemUploadData>();

    @Override
    public void invoke(ItemUploadData data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);
        if (list.size() >= BATCH_COUNT) {
            saveData(list);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData(list);
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData(List<ItemUploadData> data) {
        log.info("{}条数据，开始存储数据库！", list.size());
        data.stream().forEach(item -> {
            VoteItem voteItem = JSONObject.parseObject(JSONObject.toJSONString(item), VoteItem.class);
            //所在轮数为第一轮
            voteItem.setTurnNum("1");
            voteItem.setItem(this.item);
            setAttr6(voteItem,item);
            try {
                voteItemService.add(voteItem);
                log.info("存储数据库成功！");
            } catch (Exception e) {
                log.error("存储数据库失败！");
                log.error(ExceptionCommonUtil.getExceptionMessage(e));
            }
        });
    }

    private void setAttr6(VoteItem voteItem, ItemUploadData itemUploadData) {
        String attr6 = "";
        if (!StringUtils.isEmpty(itemUploadData.getAttr6())) {
            attr6 += itemUploadData.getAttr6();
        }
        if (!StringUtils.isEmpty(itemUploadData.getAttr7())) {
            attr6 += itemUploadData.getAttr7();
        }
        if (!StringUtils.isEmpty(itemUploadData.getAttr8())) {
            attr6 += itemUploadData.getAttr8();
        }
        if (!StringUtils.isEmpty(itemUploadData.getAttr9())) {
            attr6 += itemUploadData.getAttr9();
        }
        if (!StringUtils.isEmpty(itemUploadData.getAttr10())) {
            attr6 += itemUploadData.getAttr10();
        }
        if (!StringUtils.isEmpty(itemUploadData.getAttr11())) {
            attr6 += itemUploadData.getAttr11();
        }
        if (!StringUtils.isEmpty(itemUploadData.getAttr12())) {
            attr6 += itemUploadData.getAttr12();
        }
        voteItem.setAttr6(attr6);
        if("".equals(attr6)){
            voteItem.setAttr6(null);
            return;
        }
    }
}