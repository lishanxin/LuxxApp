/********************************************************************************************************
 * @file Group.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2010
 *
 * @par Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
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
package com.telink.sig.mesh.model.message;

/**
 * Created by kee on 2019/2/25.
 */

public class SceneRecallMessage implements ModelMessage {

    // scene id
    public int sceneId;

    /**
     * Transaction Identifier
     */
    public byte tid;

    /**
     * @see TransitionTime#getValue()
     */
    public byte transTime;

    /**
     * unit 5ms
     */
    public byte delay;

    public static SceneRecallMessage createInstance(int sceneId) {
        SceneRecallMessage instance = new SceneRecallMessage();
        instance.sceneId = sceneId;
        instance.tid = 0;
        instance.transTime = 0;
        instance.delay = 0;
        return instance;
    }

    @Override
    public byte[] toBytes() {
        return new byte[]{(byte) (this.sceneId), (byte) ((this.sceneId >> 8) & 0xFF)
                , this.tid, this.transTime, this.delay};
    }
}
