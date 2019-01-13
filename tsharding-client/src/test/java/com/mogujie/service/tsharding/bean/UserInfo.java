package com.mogujie.service.tsharding.bean;

/**
 * 单表
 * 
 * @CreateTime 2016年8月3日 下午2:42:15
 * @author SHOUSHEN LUAN
 */
public class UserInfo {
    private int id;
    private String name;
    private int age;
    private int sex;
    private String nickName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "{" + id + "," + name + "," + age + "," + sex + ","+nickName+"}";
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
