package com.guide.xiaoguo.weilee.mode;

import android.app.Application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2017/11/10.
 */

public class UserInfo extends Application {
    private String company_ID;//公司ID
    private String companyName;//公司名称
    private String account_ID;//账户ID
    private String account;//账户
    private String passWord;//密码
    private String userName;//用户名
    private String tel;//电话
    private String mail;//邮箱
    private String department;//部门
    private String position;//职务
    private List<Group_data_mode> group;
    private List<GrouporDevice_data_mode> groupordevice;
    public static  ArrayList<RTorHis_data_mode> his_printer_list = new ArrayList<>();
    private String url = "http://118.178.16.10:8080/appData/get.jsp";

    @Override
    public void onCreate() {
        super.onCreate();
        company_ID = " ";
        companyName = " ";
        account_ID = " ";
        account = " ";
        passWord = " ";
        userName = " ";
        tel = " ";
        mail = " ";
        department = " ";
        position = " ";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Group_data_mode> getGroup() {
        if (group == null) {
            group = new ArrayList<>();
        }
        return group;
    }

    public void setGroup(List<Group_data_mode> group) {
        this.group = group;
    }

    public List<GrouporDevice_data_mode> getGroupordevice() {
        if (groupordevice == null) {
            groupordevice = new ArrayList<>();
        }
        return groupordevice;
    }

    public void setGroupordevice(List<GrouporDevice_data_mode> groupordevice) {
        this.groupordevice = groupordevice;
    }

    public String getAccount_ID() {
        return account_ID;
    }

    public void setAccount_ID(String account_ID) {
        this.account_ID = account_ID;
    }

    public String getCompany_ID() {
        return company_ID;
    }

    public void setCompany_ID(String company_ID) {
        this.company_ID = company_ID;
    }

    public String getCompanyName() {

        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
