package com.hsb.apollo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blog: https://www.heshengbang.tech
 * Twitter: https://twitter.com/heshengbang
 * Github: https://github.com/heshengbang
 * Time: 2019/1/10 17:29
 *
 * @author heshengbang
 */
@RestController
public class HelloApolloController {

    @Value("${timeout:0}")
    private String timeout;

    @GetMapping("/hello")
    public String hello() {
        return timeout;
    }
}
