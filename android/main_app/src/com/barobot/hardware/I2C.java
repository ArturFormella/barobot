package com.barobot.hardware;

import java.util.ArrayList;
import java.util.List;

import com.barobot.constant.LowHardware;
import com.barobot.constant.Methods;
import com.barobot.utils.Arduino;
import com.barobot.utils.ArduinoQueue;

public class I2C {
	public static String createCommand( int address, int why_code, byte[] params ){
		String res = ""+Methods.METHOD_SEND2SLAVE + (char)address + (char)why_code;
		if(params!=null){
			for(byte i=0;i<params.length;i++){
				res += (char)params[i];
			}
		}
		return res;
	}
	public static rpc_message send( int address, int why_code, byte[] params ){	
		String res = createCommand( address, why_code, params);
		rpc_message m =  new rpc_message( true, res, true );
		return m;
	}
	public static rpc_message send( int address, int why_code ){
		byte[] a = null;
		return send( address, why_code, a );
	}
	public static ArduinoQueue findNodes(){
		ArduinoQueue q = new ArduinoQueue();
		q.add("I2C", true );
		// skanuj magistralę. Gdy znaleziono wyslij informację o sprzęcie
		rpc_message m =  new rpc_message( true ){
			public boolean handle( String command ){
				if (command == "EI2C"){	// jesli nie znaleziono zadnego
					// clear active devices
					return true;
				}
				return false;
			}
			public ArduinoQueue run(){
				return null;
			}
		};
		q.add( m );
		return q;
	}

	public static void findNodesOrder(){

		List<I2C_device> lista = new ArrayList<I2C_device>();

		I2C_device MainBoard= new I2C_device( LowHardware.MAINBOARD_DEVICE_TYPE, "Mainboard ");
		I2C_device Carret	= new I2C_device( LowHardware.CARRET_DEVICE_TYPE, "Carret");
		I2C_device UpanelF0 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 1");
		I2C_device UpanelF1 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 3");
		I2C_device UpanelF2 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 5");
		I2C_device UpanelF3 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 7");
		I2C_device UpanelF4 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 9");
		I2C_device UpanelF5 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 11");
		I2C_device UpanelB0 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 0");
		I2C_device UpanelB1 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 2");
		I2C_device UpanelB2 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 4");
		I2C_device UpanelB3 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 6");
		I2C_device UpanelB4 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 8");
		I2C_device UpanelB5 = new I2C_device( LowHardware.UPANEL_DEVICE_TYPE, "Upanel 10");

		MainBoard.hasResetTo( Carret );
		MainBoard.hasResetTo( UpanelF0 );
		MainBoard.hasResetTo( UpanelB0 );
		MainBoard.hasResetTo( MainBoard );

		UpanelF0.hasResetTo( UpanelF1 );
		UpanelF1.hasResetTo( UpanelF2 );
		UpanelF2.hasResetTo( UpanelF3 );
		UpanelF3.hasResetTo( UpanelF4 );
		UpanelF4.hasResetTo( UpanelF5 );

		UpanelB0.hasResetTo( UpanelB1 );
		UpanelB1.hasResetTo( UpanelB2 );
		UpanelB2.hasResetTo( UpanelB3 );
		UpanelB3.hasResetTo( UpanelB4 );
		UpanelB4.hasResetTo( UpanelB5 );

		lista.add( MainBoard );
		lista.add( Carret );
		lista.add( UpanelF0 );
		lista.add( UpanelF1 );
		lista.add( UpanelF2 );
		lista.add( UpanelF3 );
		lista.add( UpanelF4 );
		lista.add( UpanelF5 );
		lista.add( UpanelB0 );
		lista.add( UpanelB1 );
		lista.add( UpanelB2 );
		lista.add( UpanelB3 );	
		lista.add( UpanelB4 );
		lista.add( UpanelB5 );	

		// resetuj wózek
		Arduino ar		= Arduino.getInstance();
		ArduinoQueue q = new ArduinoQueue();
		q.add( I2C.findNodes() );
		q.add( I2C.send( 0x12, 0x11, new byte[]{1,2,3} ));
		ar.send( q );
	}	
	public static void temp(){
		// resetuj wózek
		Arduino ar		= Arduino.getInstance();
		ArduinoQueue q = new ArduinoQueue();
		q.add( I2C.findNodes() );
		q.add( I2C.send( 0x12, 0x11, new byte[]{1,2,3} ));
		ar.send( q );
	}
}