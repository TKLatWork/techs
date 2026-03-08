package me.ddd.shop.domain;

import lombok.Data;

@Data
public class Trade {
    String id;
    String importId;
    String type;
    Status status = Status.NEW;

    public enum Status {
        NEW, PENDING, SUCCESS, FAILED
    }

    public void onNewTrade(){
        status = Status.PENDING;
    }

    public void onImport(ImportResult importResult){
        if (importResult.isIfSuccess()) {
            status = Status.SUCCESS;
        } else {
            status = Status.FAILED;
        }
    }




}
