package cn.catalpaflat.service.impl;

import cn.catalpaflat.config.http.HttpResponseSupport;
import cn.catalpaflat.model.po.UserPO;
import cn.catalpaflat.repository.ShiroPermissionRepository;
import cn.catalpaflat.service.IShiroService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ShiroServiceImpl implements IShiroService {

    @Resource
    private ShiroPermissionRepository shiroPermissionRepository;

    public HttpEntity obtainUserByName(String name) {
        JSONObject json = new JSONObject();
        json.put("name","CatalpaFlat");
        UserPO userPO = shiroPermissionRepository.findAllByName(name);
        return HttpResponseSupport.success(json);
    }
}
