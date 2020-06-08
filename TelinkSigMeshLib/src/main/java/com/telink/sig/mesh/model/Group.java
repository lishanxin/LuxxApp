/********************************************************************************************************
 * @file     Group.java 
 *
 * @brief    for TLSR chips
 *
 * @author	 telink
 * @date     Sep. 30, 2010
 *
 * @par      Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
 *           All rights reserved.
 *           
 *			 The information contained herein is confidential and proprietary property of Telink 
 * 		     Semiconductor (Shanghai) Co., Ltd. and is available under the terms 
 *			 of Commercial License Agreement between Telink Semiconductor (Shanghai) 
 *			 Co., Ltd. and the licensee in separate contract or the terms described here-in. 
 *           This heading MUST NOT be removed from this file.
 *
 * 			 Licensees are granted free, non-transferable use of the information in this 
 *			 file under Mutual Non-Disclosure Agreement. NO WARRENTY of ANY KIND is provided. 
 *           
 *******************************************************************************************************/
package com.telink.sig.mesh.model;

import java.io.Serializable;

/**
 * Created by kee on 2017/8/18.
 */

public class Group implements Serializable {
    public enum BOUND_TYPE implements Serializable{
        NONE, PIS_GROUP, TELINK_GROUP
    }
    public String name;
    public int address;
    public boolean selected = false;
    public String PISKeyString; // 同一时间创建的pis 灯组地址
    public BOUND_TYPE type = BOUND_TYPE.NONE; // 灯组绑定的灯类别
}
