package com.hollykunge.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hollykunge.config.ItemStatusConfig;
import com.hollykunge.constants.VoteConstants;
import com.hollykunge.exception.BaseException;
import com.hollykunge.model.*;
import com.hollykunge.service.ItemService;
import com.hollykunge.service.UserVoteItemService;
import com.hollykunge.service.VoteItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author lark
 */
@Slf4j
@Controller
public class UserVoteController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private VoteItemService voteItemService;
    @Autowired
    private UserVoteItemService userVoteItemService;


    @RequestMapping(value = VoteConstants.INVITECODE_RPC+"{id}/{code}", method = RequestMethod.GET)
    public String inviteCodeView(@PathVariable Long id,
                                 @PathVariable String code,
                                 Model model,
                                 HttpServletRequest request) throws Exception {
        try{
            Optional<Item> itemTemp = itemService.findByIdAndCode(id,code);
            if(!itemTemp.isPresent()){
                throw new BaseException("无效地址...");
            }
            Optional<List<VoteItem>> optVoteItems = voteItemService.findByItem(itemTemp.get());
            model.addAttribute("item",itemTemp.get());
            model.addAttribute("itemStatus", ItemStatusConfig.getEnumByValue(itemTemp.get().getStatus()).getName());
            //用户列表页面显示的投票项
            model.addAttribute("voteItems", JSONObject.toJSONString(optVoteItems.get()));

            String clientIp = getClientIp(request);
            List<UserVoteItem> userVoteItems = userVoteItemService.findByItemAndIp(itemTemp.get(), clientIp);
            //用户投完票项目，前台展示可采用如果userVoteItems没有数据，则为第一次投票列表使用voteItems
            model.addAttribute("userVoteItems",JSONObject.toJSONString(userVoteItems));
            return "/userVote";
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 用户开始投票的保存
     * @param userVoteItems
     * @return
     * @throws Exception
     */
    @RequestMapping(value = VoteConstants.INVITECODE_RPC+"add/{id}/{code}", method = RequestMethod.POST)
    @ResponseBody
    public String add(@PathVariable Long id,
            @PathVariable String code,
            @RequestBody String userVoteItems,
                      Model model,
                      HttpServletRequest request) throws Exception {
        try{
            String clientIp = getClientIp(request);
            List<UserVoteItem> userVoteItemlist = JSONArray.parseArray(userVoteItems, UserVoteItem.class);
            for (UserVoteItem userVoteItem:
                    userVoteItemlist) {
                userVoteItem.setIp(clientIp);
                userVoteItemService.add(userVoteItem);
            }
            Item item = itemService.findById(id);
            Long memgerNum = userVoteItemService.countIpByItem(item);
            item.setMemberNum(Integer.parseInt(String.valueOf(memgerNum)));
            itemService.save(item);
            model.addAttribute("showMessage","操作成功！");
            return "success";
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 用户开始投票之后显示自己投票完的数据
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = VoteConstants.INVITECODE_RPC+"all", method = RequestMethod.GET)
    public String getAll(
            Model model,
            HttpServletRequest request) throws Exception {
        try{
            String clientIp = getClientIp(request);
            //为了不影响user中前台提示信息，给username，password设置系统默认值了
            List<UserVoteItem> voteItems = userVoteItemService.findByUserIp(clientIp);
            Item item = new Item();
            item.setId((long) 2);
            userVoteItemService.findByItemAndIp(item,clientIp);
            //用户列表页面显示的投票项
            model.addAttribute("voteItems",voteItems);
            return "/userVote";
        }catch (Exception e){
            throw e;
        }
    }
    
    public String getClientIp(HttpServletRequest request){
        String clientIp = request.getHeader("clientIp");
        //如果请求头中没有ip，则为本地测试，使用默认值了
        if(StringUtils.isEmpty(clientIp)){
            clientIp = VoteConstants.DEFUALT_CLIENTIP;
        }
        return clientIp;
    }
}
