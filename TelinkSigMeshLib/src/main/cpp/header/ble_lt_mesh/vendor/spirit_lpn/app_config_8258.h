/********************************************************************************************************
 * @file     app_config_8258.h 
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
#pragma once

/* Enable C linkage for C++ Compilers: */
#if defined(__cplusplus)
extern "C" {
#endif

#include "../../vendor/common/version.h"    // include mesh_config.h inside.
//////////////////board sel/////////////////////////////////////
#define PCBA_8258_DONGLE_48PIN          1
#define PCBA_8258_C1T139A30_V1_0        2
#define PCBA_8258_C1T139A30_V1_2        3
#define PCBA_8258_SEL			PCBA_8258_DONGLE_48PIN



#define _USER_CONFIG_DEFINED_	1	// must define this macro to make others known
#define	__LOG_RT_ENABLE__		0
//#define	__DEBUG_PRINT__			0
//////////// product  Information  //////////////////////////////
#define ID_VENDOR				0x248a			// for report
#define ID_PRODUCT_BASE			0x880C
#define STRING_VENDOR			L"Telink"
#define STRING_PRODUCT			L"BLE Mesh"
#define STRING_SERIAL			L"TLSR825X"

#define DEV_NAME                "SigMesh"

#define APPLICATION_DONGLE		0					// or else APPLICATION_DEVICE
#define	USB_PRINTER				1
#define	FLOW_NO_OS				1
#define XIAOMI_MODULE_ENABLE	0

/////////////////////HCI ACCESS OPTIONS///////////////////////////////////////
#define HCI_USE_NONE	0
#define HCI_USE_UART	1
#define HCI_USE_USB		2

#define HCI_ACCESS		HCI_USE_NONE

#if (HCI_ACCESS==HCI_USE_UART)
#define UART_TX_PIN		UART_TX_PB1
#define UART_RX_PIN		UART_RX_PB0
#endif

#define HCI_LOG_FW_EN   0
#if HCI_LOG_FW_EN
#define DEBUG_INFO_TX_PIN           		GPIO_PB2
#define PRINT_DEBUG_INFO                    1
#endif

#define ADC_ENABLE		0
#if ADC_ENABLE
#define ADC_CHNM_ANA_INPUT 		AVSS
#define ADC_CHNM_REF_SRC 		RV_1P428
#endif

/////////////////// mesh project config /////////////////////////////////
#if (MESH_RX_TEST || (!MD_DEF_TRANSIT_TIME_EN))
#define TRANSITION_TIME_DEFAULT_VAL (0)
#else
#define TRANSITION_TIME_DEFAULT_VAL (0)  // 0x41: 1 second // 0x00: means no default transition time
#endif

/////////////////// MODULE /////////////////////////////////
#define BLE_REMOTE_PM_ENABLE			0
#define PM_DEEPSLEEP_RETENTION_ENABLE   1
#define BLE_REMOTE_SECURITY_ENABLE      0
#define BLE_IR_ENABLE					0
#define BLE_SIG_MESH_CERTIFY_ENABLE 	0

#ifndef BLT_SOFTWARE_TIMER_ENABLE
#define BLT_SOFTWARE_TIMER_ENABLE		0
#endif

//////////////////////////// KEYSCAN/MIC  GPIO //////////////////////////////////
#define	MATRIX_ROW_PULL					PM_PIN_PULLDOWN_100K
#define	MATRIX_COL_PULL					PM_PIN_PULLUP_10K

#define	KB_LINE_HIGH_VALID				0   //dirve pin output 0 when keyscan, scanpin read 0 is valid
#define DEEPBACK_FAST_KEYSCAN_ENABLE	1   //proc fast scan when deepsleep back trigged by key press, in case key loss
#define KEYSCAN_IRQ_TRIGGER_MODE		0
#define LONG_PRESS_KEY_POWER_OPTIMIZE	1   //lower power when pressing key without release

//stuck key
#define STUCK_KEY_PROCESS_ENABLE		0
#define STUCK_KEY_ENTERDEEP_TIME		60  //in s

//repeat key
#define KB_REPEAT_KEY_ENABLE			0
#define	KB_REPEAT_KEY_INTERVAL_MS		200
#define KB_REPEAT_KEY_NUM				1
//

//----------------------- GPIO for UI --------------------------------
//---------------  Button 
#if (PCBA_8258_SEL == PCBA_8258_DONGLE_48PIN)
#define PULL_WAKEUP_SRC_PD6     PM_PIN_PULLUP_1M	//btn
#define PULL_WAKEUP_SRC_PD5     PM_PIN_PULLUP_1M	//btn
#define PD6_INPUT_ENABLE		1
#define PD5_INPUT_ENABLE		1
#define	SW1_GPIO				GPIO_PD6
#define	SW2_GPIO				GPIO_PD5
#else   // PCBA_8258_DEVELOPMENT_BOARD
#define PULL_WAKEUP_SRC_PD2     PM_PIN_PULLUP_1M	//btn
#define PULL_WAKEUP_SRC_PD1     PM_PIN_PULLUP_1M	//btn
#define PD2_INPUT_ENABLE		1
#define PD1_INPUT_ENABLE		1
#define	SW1_GPIO				GPIO_PD2
#define	SW2_GPIO				GPIO_PD1
#endif

//---------------  LED / PWM
#if(PCBA_8258_SEL == PCBA_8258_DONGLE_48PIN)
#define PWM_R       GPIO_PWM1A3		//red
#define PWM_G       GPIO_PWM0A2		//green
#define PWM_B       GPIO_PWM3B0		//blue
#define PWM_W       GPIO_PWM4B1		//white
#elif(PCBA_8258_SEL == PCBA_8258_C1T139A30_V1_0)   // PCBA_8258_DEVELOPMENT_BOARD
#define PWM_R       GPIO_PWM1ND3	//red
#define PWM_G       GPIO_PWM2ND4	//green
#define PWM_B       GPIO_PD5		//blue
#define PWM_W       GPIO_PWM3D2		//white
#elif(PCBA_8258_SEL == PCBA_8258_C1T139A30_V1_2)
#define PWM_R       GPIO_PD5	//red
#define PWM_G       GPIO_PWM1ND3	//green
#define PWM_B       GPIO_PWM3D2		//blue
#define PWM_W       GPIO_PWM2ND4		//white
#endif

#define PWM_FUNC_R  AS_PWM  // AS_PWM_SECOND
#define PWM_FUNC_G  AS_PWM  // AS_PWM_SECOND
#define PWM_FUNC_B  AS_PWM  // AS_PWM_SECOND
#define PWM_FUNC_W  AS_PWM  // AS_PWM_SECOND

#define PWMID_R     (GET_PWMID(PWM_R, PWM_FUNC_R))
#define PWMID_G     (GET_PWMID(PWM_G, PWM_FUNC_G))
#define PWMID_B     (GET_PWMID(PWM_B, PWM_FUNC_B))
#define PWMID_W     (GET_PWMID(PWM_W, PWM_FUNC_W))
                    
#define PWM_INV_R   (GET_PWM_INVERT_VAL(PWM_R, PWM_FUNC_R))
#define PWM_INV_G   (GET_PWM_INVERT_VAL(PWM_G, PWM_FUNC_G))
#define PWM_INV_B   (GET_PWM_INVERT_VAL(PWM_B, PWM_FUNC_B))
#define PWM_INV_W   (GET_PWM_INVERT_VAL(PWM_W, PWM_FUNC_W))

#define GPIO_LED	PWM_R


/////////////open SWS digital pullup to prevent MCU err, this is must ////////////
#define PA7_DATA_OUT			1


//save suspend current
#define PA5_FUNC 	AS_GPIO     // USB DM
#define PA6_FUNC 	AS_GPIO     // USB DP

/////////////////// Clock  /////////////////////////////////
#define	USE_SYS_TICK_PER_US
#define CLOCK_SYS_TYPE  		CLOCK_TYPE_PLL	//  one of the following:  CLOCK_TYPE_PLL, CLOCK_TYPE_OSC, CLOCK_TYPE_PAD, CLOCK_TYPE_ADC
#define CLOCK_SYS_CLOCK_HZ  	16000000

//////////////////Extern Crystal Type///////////////////////
#define CRYSTAL_TYPE			XTAL_12M		//  extern 12M crystal

/////////////////// watchdog  //////////////////////////////
#define MODULE_WATCHDOG_ENABLE		0
#define WATCHDOG_INIT_TIMEOUT		2000  //ms


/////////////////// set default   ////////////////

#include "../common/default_config.h"

/* Disable C linkage for C++ Compilers: */
#if defined(__cplusplus)
}
#endif
