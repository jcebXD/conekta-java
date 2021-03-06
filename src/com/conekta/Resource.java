package com.conekta;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.json.JSONArray;
import org.json.JSONObject;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mauricio
 */
public class Resource extends ConektaObject {

    public Resource(String id) {
        super(id);
    }

    public Resource() {
        super();
    }

    public static String classUrl(String className) {
        String base = "/" + className.toLowerCase().replace("com.conekta.", "") + "s";
        return base;
    }

    public String instanceUrl() throws Error {
        if (id == null || id.length() == 0) {
            throw new Error("Could not get the id of Resource instance.", null, null, null, null);
        }
        String className = this.getClass().getSimpleName();
        String base = Resource.classUrl(className);
        return base + "/" + id;
    }

    protected static ConektaObject scpFind(String className, String id) throws Error {
        Constructor c;
        ConektaObject resource;
        try {
            c = Class.forName(className).getConstructor(String.class);
            resource = (ConektaObject) c.newInstance(id);
        } catch (Exception e) {
            throw new Error(e.toString(), null, null, null, null);
        }
        Requestor requestor = new Requestor();
        String url = ((Resource) resource).instanceUrl();
        JSONObject jsonObject = (JSONObject) requestor.request("GET", url, null);
        try {
            resource.loadFromObject(jsonObject);
        } catch (Exception e) {
            throw new Error(e.toString(), null, null, null, null);
        }
        return resource;
    }

    protected static ConektaObject scpCreate(String className, JSONObject params) throws Error {
        Requestor requestor = new Requestor();
        String url = Resource.classUrl(className);
        JSONObject jsonObject = (JSONObject) requestor.request("POST", url, params);
        ConektaObject resource;
        try {
            resource = (ConektaObject) Class.forName(className).newInstance();
            resource.loadFromObject(jsonObject);
        } catch (Exception e) {
            throw new Error(e.toString(), null, null, null, null);
        }
        return resource;
    }

    protected static ConektaObject scpWhere(String className, JSONObject params) throws Error {
        Requestor requestor = new Requestor();
        String url = Resource.classUrl(className);
        JSONArray jsonArray = (JSONArray) requestor.request("GET", url, params);
        ConektaObject resource = new ConektaObject();
        resource.loadFromArray(jsonArray);
        return resource;
    }

    protected ConektaObject delete(String parent, String member) throws Error {
        this.customAction("DELETE", null, null);
        return this;
    }

    protected void update(JSONObject params) throws Error {
        Requestor requestor = new Requestor();
        String url = this.instanceUrl();
        JSONObject jsonObject = (JSONObject) requestor.request("PUT", url, params);
        try {
            this.loadFromObject(jsonObject);
        } catch (Exception e) {
            throw new Error(e.toString(), null, null, null, null);
        }

    }

    protected ConektaObject customAction(String method, String action, JSONObject params) throws Error {
        if (method == null) {
            method = "POST";
        }
        Requestor requestor = new Requestor();
        String url = this.instanceUrl();
        if (action != null) {
            url = url + "/" + action;
        }
        JSONObject jsonObject = (JSONObject) requestor.request(method, url, params);
        try {
            this.loadFromObject(jsonObject);
        } catch (Exception e) {
            throw new Error(e.toString(), null, null, null, null);
        }
        return this;
    }

    protected ConektaObject createMember(String member, JSONObject params) throws Error {
        Requestor requestor = new Requestor();
        String url = this.instanceUrl() + "/" + member;
        JSONObject jsonObject = (JSONObject) requestor.request("POST", url, params);
        Field field;
        ConektaObject conektaObject = null;
        try {
            field = this.getClass().getField(member);
            field.setAccessible(true);
            String className;
            String parentClassName = this.getClass().getSimpleName().substring(0, 1).toLowerCase() + this.getClass().getSimpleName().substring(1);
            if (field.get(this).getClass().getSimpleName().equals("ConektaObject")) {
                className = "com.conekta." + member.substring(0, 1).toUpperCase() + member.substring(1, member.length() - 1);
                conektaObject = (ConektaObject) Class.forName(className).newInstance();
                conektaObject.loadFromObject(jsonObject);

                conektaObject.getClass().getField(parentClassName).set(conektaObject, this);

                ConektaObject objects = ((ConektaObject) field.get(this));
                objects.add(conektaObject);
                field.set(this, objects);
            } else {
                className = "com.conekta." + member.substring(0, 1).toUpperCase() + member.substring(1);
                conektaObject = (ConektaObject) Class.forName(className).newInstance();
                conektaObject.loadFromObject(jsonObject);
                conektaObject.getClass().getField(parentClassName).set(conektaObject, this);

                this.setVal(member, conektaObject);
                field.set(this, conektaObject);
                this.loadFromObject(null);
            }
        } catch (Exception e) {
            throw new Error(e.toString(), null, null, null, null);
        }
        return conektaObject;
    }
}
