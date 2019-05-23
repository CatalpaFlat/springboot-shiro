package cn.catalpaflat.idal;

import cn.catalpaflat.config.http.HttpResponseSupport;
import cn.catalpaflat.service.IShiroService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.catalpaflat.constant.SystemConstant.SYSTEM_API;

@RestController
@RequestMapping( SYSTEM_API +"shiro")
public class ShiroIdal {

    @Resource
    private IShiroService iShiroService;


    @GetMapping
    public HttpEntity obtain(@RequestParam String name){
        return iShiroService.obtainUserByName(name);
    }

    @GetMapping("protect")
    public HttpEntity protect(){
        JSONObject json = new JSONObject();
        json.put("success","NICE");
        return HttpResponseSupport.success(json);
    }
}
