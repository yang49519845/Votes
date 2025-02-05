package com.hollykunge.config;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StatisticsDownloadData {
    private Long voteItemId;
    /**
     * 被投票项目名称
     */
    private String name;
    /**
     * 扩展字段1
     */
    private String attr1;
    /**
     * 扩展字段2
     */
    private String attr2;
    /**
     * 扩展字段3
     */
    private String attr3;
    /**
     * 扩展字段4
     */
    private String attr4;

    private String attr5;

    private String attr6;

    private String statisticsNum;

    private String statisticsToalScore;

    private Integer statisticsOrderScore;
}