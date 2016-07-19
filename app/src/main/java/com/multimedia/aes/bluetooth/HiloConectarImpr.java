package com.multimedia.aes.bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.widget.Toast;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;

public class HiloConectarImpr extends AsyncTask<BluetoothDevice, Void, String> {

	private Impresora impresora;
	private Activity activity;
	private ProgressDialog dialog;

	public HiloConectarImpr(Impresora impresora, Activity activity) {
		super();
		this.impresora = impresora;
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(activity);
		dialog.setTitle(activity.getResources().getString(R.string.bluetooth));
		dialog.setMessage(activity.getResources().getString(R.string.conectando));
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);
		dialog.show();
	}

	@Override
	protected String doInBackground(BluetoothDevice... params) {
		try {
			impresora.bp.connect(params[0]);
			RequestHandler rh = new RequestHandler();
			impresora.hThread = new Thread(rh);
			impresora.hThread.start();			
			impresora.realizarImpresion();
			return Constantes.SUCCES;
		} catch (IOException e) {
			return Constantes.ERROR;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		dialog.dismiss();
		if (result.equals(Constantes.SUCCES)) {
			
		}			
		else 
			Toast.makeText(activity, R.string.err_impr, Toast.LENGTH_SHORT).show();
		
	}

}
