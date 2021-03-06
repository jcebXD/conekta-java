package com.conekta;


import org.json.JSONObject;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mauricio
 */
public class Subscription extends Resource {
    public Customer customer;
    public String status;
    public Integer created_at;
    public Integer billing_cycle_start;
    public Integer billing_cycle_end;
    public String plan_id;
    public String card_id;

    @Override
    public String instanceUrl() throws Error {
        if (id.length() == 0) {
            throw new Error("Could not get the id of Resource instance.", null, null, null, null);
        }
        String base = this.customer.instanceUrl();
        return base + "/subscription";
    }

    @Override
    public void update(JSONObject params) throws Error {
        super.update(params);
    }

    public void pause() throws Error {
        this.customAction("POST", "pause", null);
    }

    public void cancel() throws Error {
        this.customAction("POST", "cancel", null);
    }

    public void resume() throws Error {
        this.customAction("POST", "resume", null);
    }
}
