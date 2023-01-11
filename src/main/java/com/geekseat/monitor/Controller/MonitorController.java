package com.geekseat.monitor.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @GetMapping("/ping")
    public String ping() {
        return "Ok";
    }

    @GetMapping("/list")
    public ResponseEntity<String> list(@RequestParam(value = "token") String token) {
        if (!"epsilon".equals(token)) {
            return new ResponseEntity<>("Error: wrong token", HttpStatus.BAD_REQUEST);
        }
        StringBuilder sb = new StringBuilder();
        for (File file : File.listRoots()) {
            sb.append(file.toString() + " - drive: " + file.toString() + " total:" + file.getTotalSpace() + " usable:" + file.getUsableSpace() + " free:" + file.getFreeSpace() + "\n");
        }
        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @GetMapping("/storage")
    public ResponseEntity<String> check(@RequestParam(value = "token") String token,
                                        @RequestParam(value = "drive") String drive,
                                        @RequestParam(value = "thresholdSize", required = false) Long thresholdSize,
                                        @RequestParam(value = "thresholdPercentage", required = false) Integer thresholdPercentage) {
        if (!"epsilon".equals(token)) {
            return new ResponseEntity<>("Error: wrong token", HttpStatus.BAD_REQUEST);
        }
        if (thresholdPercentage == null || thresholdPercentage < 0) {
            thresholdPercentage = 20;
        }
        for (File file : File.listRoots()) {
            if (file.toString().equals(drive)) {
                if (thresholdSize != null) {
                    long value = file.getFreeSpace();
                    if (value < thresholdSize) {
                        return new ResponseEntity<>("Error: threshold reached - " + file.toString() + " - total:" + file.getTotalSpace() + " usable:" + file.getUsableSpace() + " free:" + file.getFreeSpace() + " threshold:" + thresholdSize + " value:" + value, HttpStatus.BAD_REQUEST);
                    } else {
                        return new ResponseEntity<>("OK - " + file.toString() + " - total:" + file.getTotalSpace() + " usable:" + file.getUsableSpace() + " free:" + file.getFreeSpace() + " threshold:" + thresholdSize + " value:" + value, HttpStatus.OK);
                    }
                }
                if (thresholdPercentage != null) {
                    int value = (int) Math.floor((double) file.getFreeSpace() * 100 / file.getTotalSpace());

                    if (value < thresholdPercentage) {
                        return new ResponseEntity<>("Error: threshold reached - " + file.toString() + " - total:" + file.getTotalSpace() + " usable:" + file.getUsableSpace() + " free:" + file.getFreeSpace() + " threshold:" + thresholdPercentage + " value:" + value, HttpStatus.BAD_REQUEST);
                    } else {
                        return new ResponseEntity<>("OK - " + file.toString() + " - total:" + file.getTotalSpace() + " usable:" + file.getUsableSpace() + " free:" + file.getFreeSpace() + " threshold:" + thresholdPercentage + " value:" + value, HttpStatus.OK);
                    }
                }
                return new ResponseEntity<>("Error: no threshold to check", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Error: could not find drive", HttpStatus.BAD_REQUEST);
    }
}


