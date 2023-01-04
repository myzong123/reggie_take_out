package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author myz
 * @since 2022-12-30
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate  redisTemplate;

    /**
     * 用户登录验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String email = user.getEmail();
        String code = ValidateCodeUtils.generateValidateCode4String(4);
        // log.info(code);
        // session.setAttribute("code",code);
        // 将验证码存入redis中
        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
        try {
            SMSUtils.contextLoads(mailSender,email,code);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return R.success("验证码发送成功");
    }

    /**
     * 用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session){
        String avatar = map.get("code");
        String email = map.get("email");
        // String code =(String) session.getAttribute("code");
        // 从redis中取出验证码
        String code = (String) redisTemplate.opsForValue().get(email);
        if(!avatar.equals(code)){
            return R.error("验证码不正确");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        User user = userService.getOne(queryWrapper);
        if(user == null){
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setStatus(1);
            userService.save(newUser);
            session.setAttribute("user",newUser);
            return R.success(newUser);
        }
        session.setAttribute("user",user);
        // 登录成功删除验证码
        redisTemplate.delete(email);
        return R.success(user);
    }

    @PostMapping("loginout")
    public R<String> loginout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}

