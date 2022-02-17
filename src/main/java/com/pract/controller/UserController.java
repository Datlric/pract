package com.pract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.pract.domain.User;
import com.pract.service.UserService;
import com.pract.utils.JwtUtils;
import com.pract.utils.RedisUtils;
import com.pract.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;

    private ObjectMapper objectMapper = new ObjectMapper();

    private int keyExpireTime = 60 * 30;

    @PostMapping("/save")
    public Result save(@RequestBody User user) throws Exception {
        if (user != null) {
            if (user.getUsername() == null || user.getUsername().contains(" ")) {
                return Result.error("用户名不得为空并不得包含空格", null);
            }
            int saveFlag = userService.SaveOneUser(user);
            if (saveFlag <= 0) {
                return Result.error("保存用户信息失败,可能用户名已经被占用", null);
            }
        } else return Result.error("用户信息不得为空", null);
        return Result.success("保存用户信息成功", null);
    }

    @GetMapping("/getAll")
    public Result getAll() {
        return Result.success("所有信息", userService.getAll());
    }

    @GetMapping("/findPage")    //pageNum:当前页数；pageSize：每页多少条;search: 查询的关键字--根据用户名
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "5") Integer pageSize,
                           @RequestParam(defaultValue = "") String search) {
        PageInfo<?> userList = userService.selectByPage(pageNum, pageSize, search);
        return Result.success("查询成功", userList);
    }

    @PostMapping("/deleteUser")
    public Result deleteUser(@RequestBody() User user) {
        return Result.success("删除成功", userService.deleteUserInfo(user.getId()));
    }

    @PutMapping("/update")
    public Result update(@RequestBody() User user) {
        return Result.success("更新成功", userService.updateUserInfo(user));
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletRequest request) {
        String oldToken = request.getHeader("userToken");
        //登录输入判空检验，返回提示
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error("0", "用户名或密码不得为空", null);
        }

        //登录逻辑判断,当且仅当没登陆过或过期后的时候使用（userToken为空或过期,没过期进来的时候会新设置token过期时间
        User userLogin = userService.userLogin(user);
        if (userLogin != null) {
            //对于token还在有效期内，重复登录删除上一个对应浏览器内Token
            if (oldToken != null) {
                redisUtils.delKey(oldToken);
            }

            //Token为空为用户生成新Token并返回
            //根据username信息字符串生成token
            Map<String, String> map = new HashMap<>();
            map.put("user", userLogin.getUsername());
            String newToken = JwtUtils.getToken(map);

            //创建一个map放入新token
            HashMap<String, String> resultMap = new HashMap<>();
            resultMap.put("newToken", newToken);

            //将创建好的token放入redis中，以备自动登录使用，超时时长目前暂设半个小时，自动登录的处理逻辑可在拦截器中写
            //redis中，存储当前登录信息的key为token，value是username
            redisUtils.setExpire(newToken, keyExpireTime, userLogin.getUsername());

            return Result.success("登录成功:" + userLogin.getUsername(), resultMap);
        } else {
            return Result.error("用户名或密码不正确", null);
        }
    }


    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        //退出登录，先获取Token，然后在redis中删除即可
        String userToken = request.getHeader("userToken");
        if (userToken != null) {
            Long delKey = redisUtils.delKey(userToken);
            System.out.println("redis已删除:" + delKey);
            return Result.success("请重新登录", null);
        } else {
            return Result.error("498", "请重新登录", null);
        }
    }
}
