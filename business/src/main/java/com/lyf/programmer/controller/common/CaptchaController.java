package com.lyf.programmer.controller.common;

import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.util.CaptchaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2020-09-20 9:44
 */

/**
 * 验证码Captcha控制类
 */
@RestController
public class CaptchaController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private Logger log = LoggerFactory.getLogger(CaptchaController.class);

    /**
     * 通用验证码生成器
     */
    @PostMapping("/captcha/generate_captcha")
    public ResponseDTO<String> generateCaptcha(){
        CaptchaUtil captchaUtil = new CaptchaUtil();
        String generatorVCode = captchaUtil.generatorVCode(); //验证码的值
        // 存入Redis 定时30s
        stringRedisTemplate.opsForValue().set(generatorVCode, generatorVCode, 30, TimeUnit.SECONDS);
        log.info("验证码成功生成：" + generatorVCode);
        return ResponseDTO.success(generatorVCode);
    }

}
