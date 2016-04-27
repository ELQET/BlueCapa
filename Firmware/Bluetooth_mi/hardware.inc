;Created by Matej Baran (c) Copyright
LIST        P=16F1823         ; list directive to define processor
#INCLUDE    <P16F1823.INC>    ; processor specific variable definitions
 
 
   
;#define LED_RED				RA0 
#define LED_BLUE				RA1
#define UART_CTS_IN				RA2
#define MCLR_RA3_IN				RA3
#define UART_RTS_OUT			RA4 
#define WAKE_HW_OUT				RA5

;#define BTN2_CAP4_AN4_IN		RC0 ;an4
;#define BTN3_CAP5_AN5_IN 		RC1 ;an5
#define AREA_CAP6_AN6_IN 		RC2 ;an6

#define WAKE_SW_OUT				RC3
#define UART_TX_OUT				RC4
#define UART_RX_IN				RC5


