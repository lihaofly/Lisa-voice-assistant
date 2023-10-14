package com.lihao.lisa.model.core.baidu.wakeup;

import java.io.Serializable;

import lombok.Data;

@Data
public class WakeupBean implements Serializable {

    /**
     * errorDesc : wakup success
     * errorCode : 0
     * word : 小度你好
     */

    private String errorDesc;
    private int errorCode;
    private String word;
}
