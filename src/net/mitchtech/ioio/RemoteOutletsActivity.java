package net.mitchtech.ioio;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;

import java.util.Timer;
import java.util.TimerTask;

import net.mitchtech.ioio.remoteoutlets.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RemoteOutletsActivity extends AbstractIOIOActivity {
	private static final int OUTLET1_ON_PIN = 34;
	private static final int OUTLET1_OFF_PIN = 35;
	private static final int OUTLET2_ON_PIN = 36;
	private static final int OUTLET2_OFF_PIN = 37;
	private static final int OUTLET3_ON_PIN = 38;
	private static final int OUTLET3_OFF_PIN = 39;
	
	private static final int PULSE_PERIOD = 200;
	
	private Button mOutlet1OnButton;
	private Button mOutlet1OffButton;
	private Button mOutlet2OnButton;
	private Button mOutlet2OffButton;
	private Button mOutlet3OnButton;
	private Button mOutlet3OffButton;
	
	Timer mTimer = null;
	
	private boolean mOutlet1OnState = true;
	private boolean mOutlet1OffState = true;
	private boolean mOutlet2OnState = true;
	private boolean mOutlet2OffState = true;
	private boolean mOutlet3OnState = true;
	private boolean mOutlet3OffState = true;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mOutlet1OnButton = (Button) findViewById(R.id.outlet1on);
		mOutlet1OnButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				pulsePin(OUTLET1_ON_PIN);
			}
		});
		
		mOutlet1OffButton = (Button) findViewById(R.id.outlet1off);
		mOutlet1OffButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				pulsePin(OUTLET1_OFF_PIN);
			}
		});
		
		mOutlet2OnButton = (Button) findViewById(R.id.outlet2on);
		mOutlet2OnButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				pulsePin(OUTLET2_ON_PIN);
			}
		});
		
		mOutlet2OffButton = (Button) findViewById(R.id.outlet2off);
		mOutlet2OffButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				pulsePin(OUTLET2_OFF_PIN);
			}
		});
		
		mOutlet3OnButton = (Button) findViewById(R.id.outlet3on);
		mOutlet3OnButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				pulsePin(OUTLET3_ON_PIN);
			}
		});
		
		mOutlet3OffButton = (Button) findViewById(R.id.outlet3off);
		mOutlet3OffButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				pulsePin(OUTLET3_OFF_PIN);
			}
		});
		
		enableUi(false);
	}

	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		
		private DigitalOutput mOutlet1On, mOutlet1Off, mOutlet2On, mOutlet2Off, mOutlet3On, mOutlet3Off;

		public void setup() throws ConnectionLostException {
			try {
				mOutlet1On = ioio_.openDigitalOutput(OUTLET1_ON_PIN, true);
				mOutlet1Off = ioio_.openDigitalOutput(OUTLET1_OFF_PIN, true);
				mOutlet2On = ioio_.openDigitalOutput(OUTLET2_ON_PIN, true);
				mOutlet2Off = ioio_.openDigitalOutput(OUTLET2_OFF_PIN, true);
				mOutlet3On = ioio_.openDigitalOutput(OUTLET3_ON_PIN, true);
				mOutlet3Off = ioio_.openDigitalOutput(OUTLET3_OFF_PIN, true);
				enableUi(true);
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}

		public void loop() throws ConnectionLostException {
			try {
				mOutlet1On.write(mOutlet1OnState);
				mOutlet1Off.write(mOutlet1OffState);
				mOutlet2On.write(mOutlet2OnState);
				mOutlet2Off.write(mOutlet2OffState);
				mOutlet3On.write(mOutlet3OnState);
				mOutlet3Off.write(mOutlet3OffState);
				sleep(10);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
	}
	
	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mOutlet1OnButton.setEnabled(enable);
				mOutlet1OffButton.setEnabled(enable);
				mOutlet2OnButton.setEnabled(enable);
				mOutlet2OffButton.setEnabled(enable);
				mOutlet3OnButton.setEnabled(enable);
				mOutlet3OffButton.setEnabled(enable);
			}
		});
	}
		
	private void turnPinOff (int pin) {
		switch (pin) {
		case OUTLET1_ON_PIN:
			mOutlet1OnState = true;
			break;
		case OUTLET1_OFF_PIN:
			mOutlet1OffState = true;
			break;
		case OUTLET2_ON_PIN:
			mOutlet2OnState = true;
			break;
		case OUTLET2_OFF_PIN:
			mOutlet2OffState = true;
			break;
		case OUTLET3_ON_PIN:
			mOutlet3OnState = true;
			break;
		case OUTLET3_OFF_PIN:
			mOutlet3OffState = true;
			break;
		default:
			break;
		}
	}
	
	private void turnPinOn (int pin) {
		switch (pin) {
		case OUTLET1_ON_PIN:
			mOutlet1OnState = false;
			break;
		case OUTLET1_OFF_PIN:
			mOutlet1OffState = false;
			break;
		case OUTLET2_ON_PIN:
			mOutlet2OnState = false;
			break;
		case OUTLET2_OFF_PIN:
			mOutlet2OffState = false;
			break;
		case OUTLET3_ON_PIN:
			mOutlet3OnState = false;
			break;
		case OUTLET3_OFF_PIN:
			mOutlet3OffState = false;
			break;
		default:
			break;
		}
	}
	
	private void pulsePin (int pin) {
		turnPinOn(pin);
		mTimer = new Timer();
		mTimer.schedule(new PinOffTask(pin), PULSE_PERIOD);
	}
    
    private class PinOffTask extends TimerTask
    { 
    	int pin;
        public PinOffTask(int pin) {
			super();
			this.pin = pin;
		}
        
		public void run() 
        {
			turnPinOff(pin);
        }
    }

}