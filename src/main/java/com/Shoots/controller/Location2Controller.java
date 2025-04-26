package com.Shoots.controller;

import com.Shoots.domain.BusinessUser;
import com.Shoots.domain.PaginationResult;
import com.Shoots.redis.RedisService;
import com.Shoots.service.BusinessUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class Location2Controller {

    private final RedisService redisService;
    private final BusinessUserService businessUserService;


    @GetMapping("/location2")
    public ModelAndView getLocationPage( @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int limit,
                                         @RequestParam(defaultValue = "") String search_word,
                                         ModelAndView mv) {

        int listcount = businessUserService.listCount(search_word);
        List<BusinessUser> list = businessUserService.getListForLocation(search_word, page, limit);

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("map/location2");
        mv.addObject("page", page);
        mv.addObject("maxpage", result.getMaxpage());
        mv.addObject("startpage", result.getStartpage());
        mv.addObject("endpage", result.getEndpage());
        mv.addObject("listcount", listcount);
        mv.addObject("list", list);
        mv.addObject("limit", limit);
        mv.addObject("search_word", search_word);

        return mv;
    }

    @GetMapping("/location2/data")
    @ResponseBody
    public List<Map<String, String>> getAllLocations() {

        List<Integer> businessIdxList = businessUserService.getAllBusinessIndexes();

        Map<Integer, String> addressData = businessUserService.getAddressData(businessIdxList);
        Map<Integer, String> businessNames = businessUserService.getBusinessNames(businessIdxList);

        List<Map<String, String>> locationList = businessIdxList.stream()
                .map(businessIdx -> {
                    Map<String, String> locationData = new HashMap<>();
                    locationData.put("businessIdx", String.valueOf(businessIdx));
                    locationData.put("address", addressData.get(businessIdx));
                    locationData.put("businessName", businessNames.get(businessIdx));
                    return locationData;
                })
                .collect(Collectors.toList());

        // 디버깅용 로그
        System.out.println("locationList: " + locationList);

        return locationList;
    }
}
