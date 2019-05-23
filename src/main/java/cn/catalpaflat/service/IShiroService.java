package cn.catalpaflat.service;

import org.springframework.http.HttpEntity;

public interface IShiroService {
    HttpEntity obtainUserByName(String name);
}
