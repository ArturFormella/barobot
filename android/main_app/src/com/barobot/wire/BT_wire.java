package com.barobot.wire;

import com.barobot.activity.BarobotMain;
import com.barobot.hardware.virtualComponents;
import com.barobot.utils.Arduino;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BT_wire implements Wire {
    private static final String TAG = BT_wire.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter = null;    // Local Bluetooth adapter
    private BluetoothChatService mChatService = null;    // Member object for the chat services
	private int baud = 115200;
	private InputListener listener;

	@Override
	public boolean init() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
    		if(mChatService != null ){
    			mChatService.destroy();
    		}
			mChatService = new BluetoothChatService( this.mHandler );
		} catch (Exception e) {
			Log.e(TAG, "BluetoothChatService fatal error", e);
			return false;
		}

		if( mBluetoothAdapter != null ){
			boolean res = false;
			if (mChatService == null){ 
				res = false;
	    	}
			res =  mChatService.initBt();
			BarobotMain barobotMain =  BarobotMain.getInstance();
	        if( res){		// jesli jest wlaczony
	        	Arduino.getInstance().setupBT( barobotMain );
	      //  	 setup();
	        	return true;
	        }else{	// jesli wymaga wlaczenia to wroci do onActivityResult
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            barobotMain.startActivityForResult(enableIntent, BluetoothChatService.REQUEST_ENABLE_BT);
	            return true;
	        }
        }
		return false;
	}

	@Override
	public void setSearching(boolean active) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
        Log.d(TAG, "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
         }
	}

	@Override
	public boolean isConnected() {
        if (mChatService != null){ 
        	return mChatService.is_connected;
        }
        return false;
	}

	@Override
	public void close() {
        if (mChatService != null){ 
        	mChatService.stop();
        }
	}

	@Override
	public boolean send(String command) {
		if(mChatService!=null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED ) {
			Log.d(TAG, "BT SEND:["+ command +"]");
			String command2 = command;
        	byte[] send = command2.getBytes();
        	mChatService.write(send);
		}
		return false;
	}
	public boolean implementAutoConnect() {
		return true;
	}

	@Override
	public boolean canConnect() {
        // Get local Bluetooth adapter
		if(mBluetoothAdapter == null){
	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        // If the adapter is null, then Bluetooth is not supported
	        if (mBluetoothAdapter == null) {
				mChatService.is_connected		= false;
				return false;
	        }  
		}
        return true;
	}
	public void stateHasChanged() {
		if(isMainConnection){
			Arduino ar =  Arduino.getInstance();
			ar.clear();
		}
	}
	
	// The Handler that gets information back from the BluetoothChatService
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothChatService.MESSAGE_READ:
               // byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
              //  String readMessage = new String(readBuf, 0, msg.arg1);
                //Log.i(TAG, "buffer read " + readMessage );
            	String input = (String) msg.obj;
		    	if(listener!=null){
		    		listener.onNewData( input.getBytes() );
		    	}
                break;
        	case BluetoothChatService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                	stateHasChanged();
                    break;
            //    case Constant.STATE_CONNECTING:
            //    case Constant.STATE_LISTEN:
            //    case Constant.STATE_NONE:
                }
                break;

            case BluetoothChatService.MESSAGE_DEVICE_NAME:
                // save the connected device's name
            	mChatService.bt_connected_device = msg.getData().getString(BluetoothChatService.DEVICE_NAME);
            	mChatService.is_connected		= true;
            	String address =  msg.getData().getString(BluetoothChatService.DEVICE_ADDRESS);
                virtualComponents.set( "LAST_BT_DEVICE", address );    	// remember device ID

           // 	Activity act1 = BarobotMain.getInstance();
           // 	Toast.makeText( act1.getApplicationContext(), "Connected to "
           //                    + mChatService.bt_connected_device, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

	@Override
	public void destroy() {
        if (mChatService != null){ 
        	mChatService.destroy();
        }	
	}

	@Override
	public boolean setAutoConnect(boolean active) {
		if(active){
	    	//String bt_id = virtualComponents.get( "LAST_BT_DEVICE", "");
	    	String bt_id	= "00:12:09:29:51:76";
	    	//String bt_id	= "00:12:09:29:52:22";
	    	//String bt_id	= "00:12:09:29:51:76";
		    if(bt_id!= null && !"".equals(bt_id) ){
		    	mChatService.connectBTDeviceId(bt_id);
		    	return true;
		    }
		}
		return false;
	}
	@Override
	public void connectToId(String address) {
		mChatService.connectBTDeviceId(address);
	}
	@Override
	public String getName() {
		return "Bluetooth";
	}
	@Override
	public void setOnReceive(InputListener inputListener) {
		this.listener = inputListener;
	}
	@Override
	public void setBaud( int baud ) {
		this.baud = baud;
		
	}
}
