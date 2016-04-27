;Created by Matej Baran 2016 (c) Copyright

#INCLUDE    <hardware.INC>
    

__CONFIG _CONFIG1, _FCMEN_OFF & _IESO_OFF & _CP_ON & _CPD_ON & _BOREN_OFF & _MCLRE_ON & _PWRTE_ON & _FOSC_INTOSC & _CLKOUTEN_OFF & _WDTE_SWDTEN
__CONFIG _CONFIG2, _WRT_ALL & _PLLEN_OFF & _STVREN_ON & _BORV_19 & _LVP_OFF

;extern functions
EXTERN		Convert_num_to_char
EXTERN		MULV16_24
EXTERN		DIVV24
EXTERN		Measure_supply_voltage
EXTERN		Wait_512ms
EXTERN		Wait_256ms
EXTERN		Wait_128ms
EXTERN		Wait_64ms
EXTERN		Wait_32ms
EXTERN		Wait_16ms

;extern variables
EXTERN		TEMPX24
EXTERN		TEMPY16
EXTERN		RESULT24
EXTERN		TEMPY24
EXTERN		ad_value

;stav
STAV_TOUCH_MEASURE_DONE		EQU			0x00
STAV_ZNAMIENKO				EQU			0x01
STAV_FIRST_TIME				EQU			0x02
STAV_AVERAGE_ZNAMIENKO		EQU			0x03
STAV_KEY_PUSHED				EQU			0x04



;buttons
BUTTON_RF_POWER				EQU			0x00
BUTTON_BEACON				EQU			0x01
BUTTON_KEY					EQU			0x02
BUTTON_SENSOR_AREA			EQU			0x03
BUTTONS_LOCKED				EQU			0x04
BEACON_DATA					EQU			0x06
BEACON_ON					EQU			0x07

;------------------------------------------------------------------------------
; VARIABLE DEFINITIONS
;------------------------------------------------------------------------------

; Example of using Shared Uninitialized Data Section
INT_VAR        UDATA_SHR    	0x71 ; (ACCESS)
stav	        RES        	1    
raw				RES			2
current_average RES			2
temp			RES			2
average_index	RES			1
start_counter	RES			1
percent			RES			2
rx_index		RES			1
last_char_rx	RES			1

TEMP_VAR		UDATA			0x30
average			RES			2 
xwait			RES			3
tx_index		RES			1
buttons			RES			1
sensor			RES			1
log				RES			1


UART_VAR		UDATA			0x50
uart_rx_buff	RES			0x10
uart_tx_buff	RES			0x10

;------------------------------------------------------------------------------
; RESET VECTOR
;------------------------------------------------------------------------------

BEGIN     CODE    0x0000            ; processor reset vector
    PAGESEL START
    GOTO    START             

;------------------------------------------------------------------------------
; INTERRUPT SERVICE ROUTINE
;------------------------------------------------------------------------------

ISR       CODE    0x0004            ; interrupt vector location
	
	BANKSEL PIE1
	BTFSS	PIE1, RCIE
	GOTO	Jump_over_UART_Rx_interrupt
	BANKSEL PIR1
	BTFSC	PIR1, RCIF
	CALL	UART_Rx_interrupt
Jump_over_UART_Rx_interrupt	
	
	
RETFIE                    		; return from interrupt

    
   
;------------------------------------------------------------------------------
; MAIN PROGRAM
;------------------------------------------------------------------------------

START
;switch oscilator to 8MHZ
	BANKSEL OSCCON
	MOVLW	0x72 ;8MHz
	MOVWF	OSCCON
	
	CALL	Wait_64ms

	BANKSEL RCSTA 
	CLRF	RCSTA 
	
	BANKSEL TXSTA
	CLRF	TXSTA
	
	BANKSEL SSP1CON1 
	CLRF	SSP1CON1 
	
	BANKSEL MDCON
	CLRF	MDCON
	
	BANKSEL CCP1CON
	CLRF	CCP1CON
	
	BANKSEL CM1CON0 
	CLRF	CM1CON0 
	
	BANKSEL CM2CON0
	CLRF	CM2CON0  
	
	BANKSEL T2CON
	CLRF	T2CON
	
	BANKSEL ADCON0
	CLRF	ADCON0

	BANKSEL ANSELA
	CLRF	ANSELA
	CLRF	ANSELC
	BSF		ANSELC, AREA_CAP6_AN6_IN
	
	CLRF	stav
	
	BANKSEL LATC
	CLRF	LATC
	BSF		LATC, UART_TX_OUT
	CLRF	LATA
	BSF		LATA, UART_RTS_OUT
	BSF		LATA, LED_BLUE

	BANKSEL TRISC
	CLRF	TRISC
	BSF		TRISC, AREA_CAP6_AN6_IN
	BSF		TRISC, UART_RX_IN

	MOVLW	0x00 ;0x03
	MOVWF	TRISA
	BSF		TRISA, UART_CTS_IN
	BSF		TRISA, MCLR_RA3_IN
	
	BANKSEL WPUC
	CLRF	WPUC
	;BSF		WPUC, UART_RX_IN

	BANKSEL WPUA
	CLRF	WPUA
	;BSF		WPUA, UART_CTS_IN
	
	;deactivate interrupts
	CLRF	INTCON
	
;init capacitive touch
	CALL	Init_Cap_Touch
	CALL	Init_and_start_timers
	
	CALL	Wait_256ms	
	
	BANKSEL buttons
	CLRF	buttons
	
Nekonecna_slucka
	CLRWDT
	
;dotykovy snimac
	BTFSC	stav, STAV_TOUCH_MEASURE_DONE
	CALL	Snimac_dotyku

GOTO Nekonecna_slucka


Snimac_dotyku
	CLRWDT

	BCF		stav, STAV_TOUCH_MEASURE_DONE
	
	; average[index] = average[index] + (raw – average[index])/16;
	
	BCF		stav, STAV_AVERAGE_ZNAMIENKO
	
	MOVF	average_index, W
	ADDLW	average
	MOVWF	FSR0L
	;L
	MOVIW	FSR0++
	MOVWF	temp
	;H
	MOVIW	FSR0++
	MOVWF	temp+1
	
	MOVF	temp, W
	SUBWF	raw, F
	MOVF	temp+1, W
	SUBWFB 	raw+1, F
	;znamienko
	BTFSC	STATUS, C
	GOTO	Jump_over_sign
	COMF	raw, F
	COMF	raw+1, F
	BSF		stav, STAV_AVERAGE_ZNAMIENKO
Jump_over_sign

	;/16
	LSRF	raw+1, F
	RRF		raw, F
	LSRF	raw+1, F
	RRF		raw, F
	LSRF	raw+1, F
	RRF		raw, F
	LSRF	raw+1, F
	RRF		raw, F
	
	BTFSC	stav, STAV_AVERAGE_ZNAMIENKO
	GOTO	Subtract_because_of_sign
	
	;average[index] + 
	MOVF	raw, W
	ADDWF	temp, F
	MOVF	raw+1, W
	ADDWFC	temp+1, F
	
	GOTO 	Jump_over_sign_subtract
Subtract_because_of_sign

	;average[index] - 
	MOVF	raw, W
	SUBWF	temp, F
	MOVF	raw+1, W
	SUBWFB 	temp+1, F

Jump_over_sign_subtract
	
	;save to average[index]
	MOVF	average_index, W
	ADDLW	average
	MOVWF	FSR0L
	;L
	MOVF	temp, W
	MOVWI	FSR0++
	MOVWF	current_average
	;H
	MOVF	temp+1, W
	MOVWI	FSR0++
	MOVWF	current_average+1
	
	;reload raw
	BANKSEL	TMR1L
	MOVF	TMR1L, W
	MOVWF	raw
	BANKSEL TMR1H
	MOVF	TMR1H, W
	MOVWF	raw+1
	
	BTFSC	stav, STAV_FIRST_TIME
	GOTO	Do_normal
	CLRWDT
	CALL	Reset_and_start_timers
	GOTO	Increase_index

Do_normal

	;percent = ((long)average[index] - (long)raw[index]);
	BCF		stav, STAV_ZNAMIENKO
	MOVF	raw, W
	SUBWF	current_average, W
	MOVWF	percent
	MOVF	raw+1, W
	SUBWFB 	current_average+1, W
	MOVWF	percent+1
	BTFSC	STATUS, C
	GOTO	Positive_result
Negative_result
	COMF	percent, F
	COMF	percent+1, F
	MOVLW	0x01
	ADDWF	percent, F
	MOVLW	0x00
	ADDWFC	percent+1, F
	BSF		stav, STAV_ZNAMIENKO
Positive_result
	;percent = percent * 1000; rozlisenie 0.1%
	;percent = percent / average[index];
	BANKSEL TEMPX24
	MOVF	percent, W
	MOVWF	TEMPX24
	MOVF	percent+1, W
	MOVWF	TEMPX24+1
	CLRF	TEMPX24+2
	MOVLW	0xE8 ; 1000  0x64 ;100
	MOVWF	TEMPY16
	MOVLW	0x03
	MOVWF	TEMPY16+1
	CALL	MULV16_24
	MOVF	RESULT24, W
	MOVWF	TEMPX24
	MOVF	RESULT24+1, W
	MOVWF	TEMPX24+1
	MOVF	RESULT24+2, W
	MOVWF	TEMPX24+2
	MOVF	current_average, W
	MOVWF	TEMPY24
	MOVF	current_average+1, W
	MOVWF	TEMPY24+1
	CLRF	TEMPY24+2
	CALL	DIVV24
	MOVF	RESULT24, W
	MOVWF	percent
	MOVF	RESULT24+1, W
	MOVWF	percent+1
Jump_over_percent

	CLRWDT

	CALL	Difference

Increase_index	
	INCF	average_index, F
	INCF	average_index, F
	MOVF	average_index, W
	SUBLW	0x01 ; > 1
	BTFSC	STATUS, C
RETURN	 
;>13	
	CLRF	average_index
	MOVF	start_counter, F
	BTFSC	STATUS, Z
RETURN
	DECFSZ	start_counter, F
RETURN
	BSF		stav, STAV_FIRST_TIME
	BANKSEL LATA
	BCF		LATA, LED_BLUE
RETURN


Difference
;test ci percento je ok
	MOVF	percent+1, F
	BTFSS	STATUS, Z
	GOTO	Reset_timers
;normal
;urob rozdiel
;nastavenie citlivosti
	MOVLW	0x03 ;0.3% rozdiel vo frekvencii oscilatora spusti akciu
	SUBWF	percent, W
;vyhodnot
	BTFSS	STATUS, C
	GOTO	Reset_timers
;uloz hodnotu
	BANKSEL sensor
	MOVF	percent, W
	MOVWF	sensor
;tlacidlo -> prepni stav
	CALL	Send_change_command
Save_current_raw_value
;save to average[index]
	MOVF	average_index, W
	ADDLW	average
	MOVWF	FSR0L
	;L
	MOVF	raw, W
	MOVWI	FSR0++
	;H
	MOVF	raw+1, W
	MOVWI	FSR0++
Reset_timers
	CALL	Reset_and_start_timers
RETURN


Send_change_command
	BTFSC	stav, STAV_ZNAMIENKO
	GOTO	Released
	CALL	Sensor_area_action_touch
RETURN
Released
	CALL	Sensor_area_action_untouch
RETURN


Sensor_area_action_touch
	BANKSEL buttons
	BCF		buttons, BEACON_DATA
	BSF		buttons, BUTTON_SENSOR_AREA
	CALL	Broadcast_packet
RETURN


Sensor_area_action_untouch
	BANKSEL buttons
	BCF		buttons, BEACON_DATA
	BCF		buttons, BUTTON_SENSOR_AREA
	CALL	Broadcast_packet
RETURN


Broadcast_packet
;indikacia LEDkou 
	BANKSEL LATA
	BSF		LATA, LED_BLUE

;zmeraj napajacie napatie
	CALL	Measure_supply_voltage

;otvor UART
	CALL	Open_UART

;odosli prikaz N + data pre paket
	CALL	Send_N_cmd	
	
;cakaj odozvu
	MOVLW	0xFB
	CALL	Wait_for_rx_data

;skontroluj AOK
	BANKSEL log
	MOVLW	0x04
	MOVWF	log
	CALL	Check_if_AOK
	CALL	Log_and_RESET

;odosli prikaz A + casovanie => odosle data do eteru	
	CALL	Send_A_cmd
	
;cakaj odozvu
	MOVLW	0xFA	
	CALL	Wait_for_rx_data

;skontroluj AOK
	BANKSEL log
	MOVLW	0x05
	MOVWF	log
	CALL	Check_if_AOK
	CALL	Log_and_RESET
	
	BANKSEL TXSTA
	BCF		TXSTA, TXEN
	
	BANKSEL LATA
	BCF		LATA, LED_BLUE
	
;pockaj 384ms kym sa dovysiela zatial sa uspi
	;CALL	Wait_512ms
	CALL	Wait_256ms
	CALL	Wait_128ms
	
	CALL	Close_UART
RETURN


Send_R_cmd
;vytvor buffer na odoslanie
	BANKSEL tx_index
	CLRF	tx_index
	MOVLW	'R'
	CALL	Insert_byte_to_tx_buff
	MOVLW	','
	CALL	Insert_byte_to_tx_buff
	MOVLW	'1'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'\r'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'\n'
	CALL	Insert_byte_to_tx_buff
;odosli vygenerovany buffer
	MOVLW	uart_tx_buff
	MOVWF	FSR0
Send_next_char_R		
	MOVIW	FSR0++
	CALL	Send_char
	XORLW	'\n'
	BTFSS	STATUS, Z
	GOTO	Send_next_char_R
RETURN


Send_A_cmd
;vytvor buffer na odoslanie
	BANKSEL tx_index
	CLRF	tx_index
	MOVLW	'A'
	CALL	Insert_byte_to_tx_buff
	MOVLW	','
	CALL	Insert_byte_to_tx_buff
	MOVLW	'0'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'0'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'3'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'2'
	CALL	Insert_byte_to_tx_buff
	MOVLW	','
	CALL	Insert_byte_to_tx_buff
	MOVLW	'0'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'1'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'3'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'6'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'\r'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'\n'
	CALL	Insert_byte_to_tx_buff
;odosli vygenerovany buffer
	MOVLW	uart_tx_buff
	MOVWF	FSR0
Send_next_char_A		
	MOVIW	FSR0++
	CALL	Send_char
	XORLW	'\n'
	BTFSS	STATUS, Z
	GOTO	Send_next_char_A
RETURN


Send_N_cmd
	CLRWDT
;vytvor buffer na odoslanie
	BANKSEL tx_index
	CLRF	tx_index
	MOVLW	'N'
	CALL	Insert_byte_to_tx_buff
	MOVLW	','
	CALL	Insert_byte_to_tx_buff
;zaciatok paketu oznac znakom S => 0x53
	MOVLW	'5'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'3'
	CALL	Insert_byte_to_tx_buff
;zapis obsah registrov
	BANKSEL buttons
	MOVF	buttons, W
	CALL	Insert_register_to_tx_buff
	BANKSEL sensor
	MOVF	sensor, W
	CALL	Insert_register_to_tx_buff
	BANKSEL ad_value
	MOVF	ad_value+1, W
	CALL	Insert_register_to_tx_buff
	BANKSEL ad_value
	MOVF	ad_value, W
	CALL	Insert_register_to_tx_buff
;koniec paketu oznac znakom E => 0x45
	MOVLW	'4'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'5'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'\r'
	CALL	Insert_byte_to_tx_buff
	MOVLW	'\n'
	CALL	Insert_byte_to_tx_buff
;odosli vygenerovany buffer
	MOVLW	uart_tx_buff
	MOVWF	FSR0
Send_next_char_N		
	MOVIW	FSR0++
	CALL	Send_char
	XORLW	'\n'
	BTFSS	STATUS, Z
	GOTO	Send_next_char_N
RETURN


Insert_register_to_tx_buff
	MOVWF	temp+1
	SWAPF	temp+1, W
	ANDLW	0x0F
	CALL	Convert_num_to_char
	CALL	Insert_byte_to_tx_buff
	MOVF	temp+1, W
	ANDLW	0x0F
	CALL	Convert_num_to_char
	CALL	Insert_byte_to_tx_buff
RETURN


Insert_byte_to_tx_buff
	MOVWF	temp
	BANKSEL tx_index
	MOVF	tx_index, W
	ADDLW	uart_tx_buff
	MOVWF	FSR0
	MOVF	temp, W
	MOVWF	INDF0
	INCF	tx_index, F
	MOVF	tx_index, W
	SUBLW	0x0F
	BTFSC	STATUS, C
RETURN
	CLRF	tx_index
RETURN


Send_char
	MOVWF	temp
	BANKSEL TXSTA
	BSF		TXSTA, TXEN
	CLRWDT
;cakaj na handshake od modulu CTS = 0 
Wait_for_CTS
	BANKSEL PORTA
	BTFSC	PORTA, UART_CTS_IN
	GOTO	Wait_for_CTS
	CLRWDT
;mozes odoslat 
Wait_for_free_buffer
	BANKSEL TXSTA
	BTFSS	TXSTA, TRMT
	GOTO	Wait_for_free_buffer
	BANKSEL TXREG
	MOVF	temp, W
	MOVWF	TXREG
RETURN


Check_if_END
	MOVLW	'E'
	MOVWF	temp
	MOVLW	0x00
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	'N'
	MOVWF	temp
	MOVLW	0x01
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	'D'
	MOVWF	temp
	MOVLW	0x02
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	0x0D
	MOVWF	temp
	MOVLW	0x03
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
RETLW	0x01


Check_if_AOK
	MOVLW	'A'
	MOVWF	temp
	MOVLW	0x00
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	'O'
	MOVWF	temp
	MOVLW	0x01
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	'K'
	MOVWF	temp
	MOVLW	0x02
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	0x0D
	MOVWF	temp
	MOVLW	0x03
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
RETLW	0x01


Check_if_CMD
	MOVLW	'C'
	MOVWF	temp
	MOVLW	0x00
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	'M'
	MOVWF	temp
	MOVLW	0x01
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	'D'
	MOVWF	temp
	MOVLW	0x02
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
	MOVLW	0x0D
	MOVWF	temp
	MOVLW	0x03
	CALL	Check_char
	BTFSS	STATUS, Z
RETLW	0x00
RETLW	0x01


Check_char
	ADDLW	uart_rx_buff	
	MOVWF	FSR0	
	MOVF	INDF0, W
	XORWF	temp, W
RETURN


Wait_for_rx_data
	BANKSEL log
	MOVWF	log
	CLRWDT
;povol prijem dat RTS = 0
	BANKSEL LATA
	BCF		LATA, UART_RTS_OUT
Waiting_for_rx_data
	MOVF	last_char_rx, W
	XORLW	'\n'
	BTFSS	STATUS, Z
	GOTO	Waiting_for_rx_data
;stopni prijem dalsich dat RTS = 1
	BANKSEL LATA
	BSF		LATA, UART_RTS_OUT
	CLRF	last_char_rx
	CLRF	rx_index	
RETURN


Open_UART
;nastav seriovy port
	BANKSEL SPBRGL
	MOVLW	0x40
	MOVWF	SPBRGL
	
	BANKSEL SPBRGH
	MOVLW	0x03
	MOVWF	SPBRGH
	
	BANKSEL TXSTA
	MOVLW	0x06
	MOVWF	TXSTA
	
	BANKSEL BAUDCON
	MOVLW	0x48
	MOVWF	BAUDCON
	
	BANKSEL RCSTA
	MOVLW	0x00
	MOVWF	RCSTA
	
	CLRF	rx_index
	CLRF	last_char_rx
	
;nastav prerusenia
	BANKSEL PIR1
	BCF		PIR1, RCIF
	
	BANKSEL PIE1
	BSF		PIE1, RCIE
	
	MOVLW	0xC0
	MOVWF	INTCON

;aktivuj seriovy port - prijem dat	
	BANKSEL RCSTA
	BSF		RCSTA, SPEN
	BSF		RCSTA, CREN
	
;zobud bluetooth modul a ocakavaj CMD 	
	BANKSEL LATA
	BSF		LATA, WAKE_HW_OUT
	CALL	Wait_16ms
	BANKSEL LATC
	BSF		LATC, WAKE_SW_OUT
	
	;CALL	Wait_128ms
	;CALL	Wait_512ms

;cakaj odozvu
	MOVLW	0xF9	
	CALL	Wait_for_rx_data

;spracuj data
	BANKSEL log
	MOVLW	0x06
	MOVWF	log
	CALL	Check_if_CMD
	CALL	Log_and_RESET
RETURN


Close_UART
;uspi bluetooth modul a ocakavaj END	
	BANKSEL LATC
	BCF		LATC, WAKE_SW_OUT
;cakaj odozvu
	MOVLW	0xFF	
	CALL	Wait_for_rx_data
;skontroluj END
	BANKSEL log
	MOVLW	0x07
	MOVWF	log
	CALL	Check_if_END
	CALL	Log_and_RESET
;deep sleep	
	BANKSEL LATA
	BCF		LATA, WAKE_HW_OUT

	CLRF	last_char_rx
	CLRF	rx_index
;vypni UART
	BANKSEL TXSTA
	BCF		TXSTA, TXEN	
	BANKSEL RCSTA
	BCF		RCSTA, SPEN
RETURN


Reset_and_start_timers
	;CALL	Wait_128ms
	MOVLW	0x06 ;0x03
 	BANKSEL CPSCON1
	MOVWF	CPSCON1
	BANKSEL CPSCON0
	MOVLW	0xCC ;high range
	MOVWF	CPSCON0
Init_and_start_timers	
	BANKSEL TMR1L
	CLRF	TMR1L
	BANKSEL TMR1H
	CLRF	TMR1H
	BANKSEL T1CON
	BSF		T1CON, TMR1ON
Go_sleep_CPU
	;CALL	Wait_64ms

	CALL	Wait_16ms
	BANKSEL CPSCON0
	BCF		CPSCON0, CPSON
	CALL	Wait_32ms
	BANKSEL CPSCON0
	BSF		CPSCON0, CPSON

	CALL	Wait_16ms
	BANKSEL CPSCON0
	BCF		CPSCON0, CPSON
	CALL	Wait_32ms
	BANKSEL CPSCON0
	BSF		CPSCON0, CPSON

	CALL	Wait_16ms
	BANKSEL CPSCON0
	BCF		CPSCON0, CPSON
	CALL	Wait_32ms
	BANKSEL CPSCON0
	BSF		CPSCON0, CPSON
	CALL	Wait_16ms

	BANKSEL T1CON
	BCF		T1CON, TMR1ON
	BANKSEL	TMR1L
	MOVF	TMR1L, W
	MOVWF	raw
	BANKSEL TMR1H
	MOVF	TMR1H, W
	MOVWF	raw+1
	BANKSEL CPSCON0
	BCF		CPSCON0, CPSON
	BSF		stav, STAV_TOUCH_MEASURE_DONE

	CALL	Wait_32ms
RETURN


Init_Cap_Touch
	MOVLW	0x01
	MOVWF	average_index
	MOVLW	average
	MOVWF	FSR0L
Clear_average
	MOVLW	0x00
	MOVWI	FSR0++
	MOVWI	FSR0++
	DECFSZ	average_index, F
	GOTO	Clear_average
	
	CLRF	current_average
	CLRF	current_average+1
	CLRF	raw
	CLRF	raw+1
	MOVLW	0x68 ;0xF0
	MOVWF	start_counter

	BANKSEL OPTION_REG
	MOVLW	0x20 ;TMR0 off
	MOVWF	OPTION_REG
	BANKSEL T1CON
	MOVLW	0xC5
	MOVWF	T1CON
	BANKSEL T1GCON
	CLRF	T1GCON
	BANKSEL TMR1L
	CLRF	TMR1L
	BANKSEL TMR1H
	CLRF	TMR1H
	;FVRCON1
	BANKSEL FVRCON
	MOVLW	0xC9 ;Uref(comp) = 2.048V, Uref(ADC) = 1.024V
	MOVWF	FVRCON
	;DACCON0
	BANKSEL	DACCON0
	MOVLW	0x88
	MOVWF	DACCON0
	;DACCON1
	BANKSEL	DACCON1
	MOVLW	0x01
	MOVWF	DACCON1
	;CPSCON0
	BANKSEL CPSCON0
	MOVLW	0xCC ;high range
	MOVWF	CPSCON0
	;CPSCON1
	BANKSEL CPSCON1
	MOVLW	0x06
	MOVWF	CPSCON1
RETURN


Log_and_RESET
	XORLW	0x00
	BTFSS	STATUS, Z
	GOTO	Jump_over_RESET
	RESET
Jump_over_RESET
	BANKSEL log
	CLRF	log
RETURN



;#######################################################
;############# PRERUSENIA ##############################
;#######################################################

UART_Rx_interrupt
	BCF		PIR1, RCIF
	MOVF	rx_index, W
	ADDLW	uart_rx_buff	
	MOVWF	FSR0
	BANKSEL RCREG
	MOVF	RCREG, W
	MOVWF	INDF0
	MOVWF	last_char_rx
	INCF	rx_index, F
	MOVF	rx_index, W
	SUBLW	0x0F
	BTFSC	STATUS, C
RETURN
	CLRF	rx_index	
RETURN



EEPROM   CODE  0xF000
	DE	0x00, 0x00
END
