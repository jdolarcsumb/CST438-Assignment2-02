
package com.cst438.controller;


import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthCheckController {
    /*
     * health check
     */
    @GetMapping("/check")
    public String healthCheck() {
        try {
            String ip = InetAddress.getLocalHost().toString();
            long pid = ProcessHandle.current().pid();
            return ip+" pid="+pid;
        } catch (UnknownHostException e) {
            return "unknown ip";
        }
    }
    /*
     * terminate the server
     */
    @GetMapping("/fail")
    public void fail() {
        System.out.println("fail");
        System.exit(1);
    }
}
