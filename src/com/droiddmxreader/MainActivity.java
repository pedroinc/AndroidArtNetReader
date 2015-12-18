package com.droiddmxreader;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.ArtNetNodeDiscovery;
import artnet4j.ArtNetServer;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;
import artnet4j.packets.ArtPollPacket;

import com.tvglobo.dmxreader.R;

public class MainActivity extends Activity{
		
	protected SurfaceView mSurface;
	
	protected int dmxR;
	protected int dmxG;
	protected int dmxB;
	
	protected final int delay = 400; //milliseconds
	protected ArtNetNode netLynx;
	protected int sequenceID;
	protected ArtNetDiscoveryListener mDiscoveryListener;
	protected static final Logger logger = Logger.getLogger(ArtNet.class.getClass().getName());
	protected static final long ARTPOLL_REPLY_TIMEOUT = 3000;
	protected ConcurrentHashMap<InetAddress, ArtNetNode> discoveredNodes;
	
	protected ArtNetServer server;
	protected ArtNet mArtNet;
	protected ArtDmxPacket dmxPacket;
	protected DatagramSocket socket;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);				    
               
        mArtNet = new ArtNet();               
        mArtNet.setBroadCastAddress("255.255.255.255");	             
        
        try {       		        	  
			mArtNet.start();				
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (ArtNetException e) {				
			e.printStackTrace();
		}      
        		
 
//apenas teste...        
//        Random rand = new Random();        
        //le o dmx e alimenta o vetor
//        dmxRGB = new int[3];
//        dmxRGB[0] = rand.nextInt(255) + 1;
//        dmxRGB[1] = rand.nextInt(255) + 1;
//        dmxRGB[2] = rand.nextInt(255) + 1;               		           

        
        
        //codigo para setar o background da SurfaceView com o RGB enviado via broadcast no pacote Art-Net.
        
//        mSurface = (SurfaceView) findViewById(R.id.surface_color);        
//        mSurface.setBackgroundColor(Color.argb(255, dmxRGB[0], dmxRGB[1], dmxRGB[2]));        
	}   
    		
	@Override
	protected void onStart() 
	{
		super.onStart();
		
		//new NodeDiscoveryAsyncTask().execute();		
		callAsynchronousTask();
	}
		
	public void callAsynchronousTask() 
	{
	    final Handler handler = new Handler();
	    Timer timer = new Timer();
	    TimerTask doAsynchronousTask = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() {       
	                    try {
	                    	
	                    	new NodeDiscoveryAsyncTask().execute();
	                    	
	                    } catch (Exception e) {
	                        // TODO Auto-generated catch block
	                    }
	                }
	            });
	        }
	    };
	    timer.schedule(doAsynchronousTask, 0, 3000); //executar a cada 3 segundos apenas para teste.
	}	
	
    class NodeDiscoveryAsyncTask extends AsyncTask<Void, Void, Void> {
    	        
        private final boolean isActive = true;
        protected byte[] dmxValues;        
        protected ArtDmxPacket artDmxPacket;
		protected ArtNetNodeDiscovery artNetNodeDiscovery;
        
		protected void onPreExecute() {
			//mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}
		@Override
		protected Void doInBackground(Void... params) 
		{   									
	        try {     
	        	
	        	mArtNet.startNodeDiscovery();
	        	
	        	while (isActive) 
	        	{		        		
	        		ArtPollPacket packet = new ArtPollPacket();	 
                    mArtNet.broadcastPacket(packet);
                    artNetNodeDiscovery = new ArtNetNodeDiscovery(mArtNet);
                    
                    //indices 0, 1 e 2 serão respectivamente RGB.
                    dmxValues = packet.getData();                       
	        	}
				
			}catch (Exception e) {
	        	e.printStackTrace();
			}
								
			return null;
		}
		
		protected void onPostExecute(Void result) 
		{		          		
			if(dmxValues != null)
			{
				Log.i("LOG - PEDRO", dmxValues[0] + "" + dmxValues[1] + "" + dmxValues[2] + "" + dmxValues[3]);
			}
		}      
    }		
//	private NetworkInfo getNetworkInfo(Context context) {
//		
//		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		return cm.getActiveNetworkInfo();
//	}	
//
//	private boolean isOnline(Context context) {
//		
//		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo netInfo = cm.getActiveNetworkInfo();
//
//		if (netInfo != null && netInfo.isConnected()) {
//			return true;
//		}
//		return false;
//	}	
}