package com.Shoots.controller;

import com.Shoots.domain.BusinessUser;
import com.Shoots.domain.PaginationResult;
import com.Shoots.redis.RedisService;
import com.Shoots.service.BusinessUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class LocationController {

    private final RedisService redisService;
    private final BusinessUserService businessUserService;


    @GetMapping("/location")
    public ModelAndView getLocationPage( @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int limit,
                                         @RequestParam(defaultValue = "") String search_word,
                                         ModelAndView mv) {

        int listcount = businessUserService.listCount(search_word);
        List<BusinessUser> list = businessUserService.getListForLocation(search_word, page, limit);

        PaginationResult result = new PaginationResult(page, limit, listcount);

        mv.setViewName("map/location");
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

    @GetMapping("/location/data")
    @ResponseBody
    public List<Map<String, String>> getAllLocations() {

        List<Integer> businessIdxList = businessUserService.getAllBusinessIndexes();

        Map<Integer, String> addressData = redisService.getAddressData(businessIdxList);
        Map<Integer, String> businessNames = businessUserService.getBusinessNames(businessIdxList);

        return addressData.entrySet().stream()
                .map(entry -> Map.of(
                        "businessIdx", String.valueOf(entry.getKey()),
                        "address", entry.getValue(),
                        "businessName", businessNames.getOrDefault(entry.getKey(), "Unknown")))
                .collect(Collectors.toList());
    }
}
