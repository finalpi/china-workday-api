package com.github.chinaworkdayapi.controller;

import com.github.chinaworkdayapi.service.HolidayService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {
    private final HolidayService holidayService;

    @GetMapping("/isWorkday")
    public ResponseEntity isWorkday(){
        Map<String,Integer> result = new HashMap<>();
        result.put("isWorkDay",holidayService.isWorkday()?1:0);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/getInfo")
    public ResponseEntity getInfo(){
        Map<String,Integer> result = new HashMap<>();
        result.put("BigSmallCount",holidayService.getBigSmallCount());
        result.put("BigSmallSize",holidayService.getBigSmallSize());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/isWorkday/{workDay}")
    public ResponseEntity isWorkday(@PathVariable String workDay){
        Map<String,Integer> result = new HashMap<>();
        result.put("isWorkDay",holidayService.isWorkday(workDay)?1:0);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("setBigSmallCount/{count}")
    public ResponseEntity setBigSmallCount(@PathVariable Integer count){
        holidayService.setBigSmallCount(count);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("setBigSmallSize/{count}")
    public ResponseEntity setBigSmallSize(@PathVariable Integer count){
        holidayService.setBigSmallSize(count);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
