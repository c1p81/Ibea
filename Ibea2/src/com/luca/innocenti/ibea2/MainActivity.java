package com.luca.innocenti.ibea2;

import java.io.IOException;
import java.util.Collection;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.ibeacon.RangeNotifier;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends ActionBarActivity implements IBeaconConsumer {
	protected static final String TAG = "RangingActivity";
	private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);
	public static WebView html5;
	public static TextView testo;
	public static int attivo;
	
    private int old_minore = 0;
	private static int lingua = 1; //lingua di default italiano
	private static int pagina = 0;
	
	private Menu menu;
	public static View rootView;
	static MediaPlayer mp;
	public static Button audio;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//full screen 
		// questa funzione e' stata disabilita per dare spazio alla action bar che altrimenti era invisibile
		 //requestWindowFeature(Window.FEATURE_NO_TITLE);
	     //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		verifyBluetooth();
		attivo = 0;
		
		iBeaconManager.bind(this);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	private void verifyBluetooth() {
		try {
			if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");			
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
			            System.exit(0);					
					}					
				});
				builder.show();
			}			
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");			
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
		            System.exit(0);					
				}

			});
			builder.show();

		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
	
		switch(id){
		case R.id.fr:
			lingua  = 3;
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_fr));
			Toast.makeText(getApplicationContext(), "Réglez maintenant en français", Toast.LENGTH_SHORT).show();
			break;
		case R.id.it:
			lingua = 1;			
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_it));
			Toast.makeText(getApplicationContext(), "Adesso impostato in italiano", Toast.LENGTH_SHORT).show();
			break;
		case R.id.uk:
			lingua = 2;
			menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_uk));
			Toast.makeText(getApplicationContext(), "Now Set in english", Toast.LENGTH_SHORT).show();
			break;
		case R.id.mappa:
			Intent intent = new Intent(this, Mappa.class);
			startActivity(intent);;
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_main, container,false);
			testo = (TextView) rootView.findViewById(R.id.textView1);
			testo.setText("");
			testo.setTextSize(12);
			html5 = (WebView) rootView.findViewById(R.id.webView1);
			html5.getSettings().setJavaScriptEnabled(true);
			html5.setWebChromeClient(new WebChromeClient());
			html5.loadUrl("file:///android_asset/www/1/index.html");
			audio = (Button) rootView.findViewById(R.id.button1);
			audio.setOnClickListener(audioplay);

	
			
			return rootView;
		}
	}
	
	static OnClickListener audioplay =  new OnClickListener(){

			    @Override
			    public void onClick(View v) {
					mp = new MediaPlayer();

			    	if(mp.isPlaying())
			        {  
			            mp.stop();
			            mp.reset();
			        }
			    	
			        try {

			            AssetFileDescriptor afd;
			            afd = rootView.getContext().getAssets().openFd("www/"+Integer.toString(lingua)+"/audio/"+Integer.toString(pagina)+".mp3");
			            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
			            

			            mp.prepare();
			            mp.start();
			        } catch (IllegalStateException e) {
			            e.printStackTrace();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }

			    }};	
	
@Override 
protected void onDestroy() {
	        super.onDestroy();
	        iBeaconManager.unBind(this);
	    }
	    @Override 
protected void onPause() {
	    	super.onPause();
	    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);    		
	    }
@Override 
protected void onResume() {
	    	super.onResume();
	    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, false);    		
	    }    
	


private void WebDisplay(final String urlo, final int test) {
	runOnUiThread(new Runnable() {
	    public void run() {
   	    			if (test != old_minore){
   	    				html5.loadUrl("file:///android_asset/www/"+Integer.toString(lingua)+"/" +urlo);
   	   	    			pagina = test;
   	    			}
   	    			old_minore = test;
   	    			}
	});
}

@Override

// gestisce la conferma di uscita
public void onBackPressed() {
    new AlertDialog.Builder(this)
           .setMessage("Are you sure you want to exit?")
           .setCancelable(false)
           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                    finish();
               }
           })
           .setNegativeButton("No", null)
           .show();
}





	@Override
	public void onIBeaconServiceConnect() {
		// modifica il tempo di scansione da 1 sec a 4 secondi 
		  long scantime = 4000l;
		  iBeaconManager.setForegroundScanPeriod(scantime); 
		  iBeaconManager.setBackgroundScanPeriod(scantime);
		  try {
			iBeaconManager.updateScanPeriods();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// TODO Auto-generated method stub
	       iBeaconManager.setRangeNotifier(new RangeNotifier() {
	       private String beaconid;
	       

			@Override 
	           public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
	               Log.d(TAG,"No of devices == "+iBeacons.size());
	               double vicino = 10.0;
	               beaconid = "";
	               int maggiore = 0;
	               int minore = 0;
	               double esterno = 100;
	            
	               
	               //cicla su tutti i beacon trovati
	               if (iBeacons.size() > 0) {
	            	   for (IBeacon iBeacon : iBeacons) {                              
	            	        //Log.d(TAG,String.format("( %.2f m ) %d", iBeacon.getAccuracy(), iBeacon.getProximity()));
	            		   Log.d("luca",iBeacon.getProximityUuid()+ "/"+ iBeacon.getMajor()+"/"+iBeacon.getMinor()+ "  -  "+iBeacon.getAccuracy()+ " " + iBeacon.getProximity()+ " Cl "+ iBeacon.getClass());

	            		   // cerca il piu' vicino che sia ad almeno la distanza preimpostata nella variabile vicino
	            		   if (iBeacon.getAccuracy() < vicino)
	            		   {
	            			   vicino =  iBeacon.getAccuracy();
	            			   beaconid = iBeacon.getProximityUuid();
	            			   maggiore = iBeacon.getMajor();
	            			   minore = iBeacon.getMinor();
	            		   }
	            	    }
	            	   
	            	   for (IBeacon iBeacon : iBeacons) {                              
	            		   if (iBeacon.getAccuracy() < esterno)
	            		   {
	            			   esterno =  iBeacon.getAccuracy();
	            		   }
	            	   }
	            	   
	            	  if (vicino <= 5)
	            	  {
			            	  //cerca solo nella solita classe 
			                  //if ((beaconid.equals("e2c56db5-dffb-48d2-b060-d0f5a71096e0")) && (maggiore == 0))
	            		     if ((beaconid.equals("4506f9c7-00f9-c206-c12c-c2f9c702d3c3")) && (maggiore == 0))
			                  {
	            		    	 //usa il minor per selezionare quale pagina mandare in esecuzione
			                	  WebDisplay(Integer.toString(minore)+".html",minore);
			                	  
			                  }
	            	  }
	                  //gestisce l'avvicinamento al Beacon
	                  if ((iBeacons.size() > 0) && (vicino >= 10) && (esterno <= 30))
	                  {
	                	  WebDisplay("vicino.html",101);
	                	  pagina = 0;
	                  }
	                  
	                  //se ci allontana rimanda alla pagina vuota
	                  if ((vicino >= 10) && (esterno  > 35)) 
	                  {
	                	  WebDisplay("index.html",100);
	                	  pagina = 0;
	                  }
	                
	               }
	           }
	           });

	           try {
	               iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
	           } catch (RemoteException e) {   }
	       }
	
	}


