package com.multimedia.aes.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Constantes {

	public static final String ERROR = "NOK";
	public static final String SUCCES = "OK";
	public static final int CAMARA = 1;
	public static final int IMAGE_MAX_SIZE = 800;
	public static final long UPDATE_INTERVAL = 30 * 1000;
	public static final long MINUTO = 60 * 1000;
	public static final long SERVICIOS_LENTOS = (MINUTO / UPDATE_INTERVAL) * 5;
	public static final long SERVICIOS_MUY_LENTOS = (MINUTO / UPDATE_INTERVAL) * 30;
	public static final int REQUEST_ENABLE_BT = 2;

	public static final int T_HUMOS_GRADOS = 1;
	public static final int PPM_CO_CORREG = 2;
	public static final int PORC_O2 = 3;
	public static final int PPM_CO = 4;
	public static final int PORC_EXC_AIRE = 5;
	public static final int PORC_CO2 = 6;
	public static final int PORC_QA = 7;
	public static final int MBAR_TIRO = 8;
	public static final int T_AMBIENTE = 9;
	public static final int T_TH = 10;
	public static final int PORC_RENDIMIENTO = 11;
	public static final int PPM_CO_AMBIENTE = 12;
	public static final int PPM_CO2_AMBIENTE = 13;

	// Menus
	public static final String DOCUMENTOS = "docs";
	public static final String CIERRE = "cierre";
	public static final String ALMACEN = "almacen";
	public static final String LOGOUT = "logout";
	public static final String RECARGAR = "recargar";
	public static final String FECHA = "fecha";
	public static final String ASIGNACION = "asignacion";

	// Informacion del parte
	public static final String tabUsuario = "USUARIO";
	public static final String tabAveria = "AVERIA";
	public static final String tabInstalacion = "INSTALACION";
	public static final String tabMateriales = "MATERIALES";
	public static final String tabFinalizacion = "FINALIZACION";
	public static final String tabGaleria = "GALERIA";

	public static String montarFecha(SharedPreferences sp) {
		String dia = "" + sp.getInt("day", Calendar.getInstance().get(Calendar.DATE));
		String mes = "" + sp.getInt("month", Calendar.getInstance().get(Calendar.MONTH) + 1);
		String anio = "" + sp.getInt("year", Calendar.getInstance().get(Calendar.YEAR));
		if (dia.length() == 1) {
			dia = "0" + dia;
		}
		if (mes.length() == 1) {
			mes = "0" + mes;
		}
		return dia + "/" + mes + "/" + anio;
	}

	public static String montarFechaInversa(SharedPreferences sp) {
		String dia = "" + sp.getInt("day", Calendar.getInstance().get(Calendar.DATE));
		String mes = "" + sp.getInt("month", Calendar.getInstance().get(Calendar.MONTH) + 1);
		String anio = "" + sp.getInt("year", Calendar.getInstance().get(Calendar.YEAR));
		if (dia.length() == 1) {
			dia = "0" + dia;
		}
		if (mes.length() == 1) {
			mes = "0" + mes;
		}
		return anio + "-" + mes + "-" + dia;
	}

	public static String transformarFecha(String fecha) {
		String[] trozo = fecha.split("/");
		String dia = trozo[0];
		String mes = trozo[1];
		String anio = trozo[2];
		return anio + "-" + mes + "-" + dia;
	}

	public static String transformarFechaInversa(String fecha) {
		String[] trozo = fecha.split("-");
		String dia = trozo[0];
		String mes = trozo[1];
		String anio = trozo[2];
		return dia + "/" + mes + "/" + anio;
	}

	public static String convertirHora(Date hInicio) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(hInicio);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		String hf;
		if (h < 10)
			hf = "0" + h + ":";
		else
			hf = h + ":";
		if (m < 10)
			hf = hf + "0" + m;
		else
			hf = hf + m;
		return hf;
	}
	public static String rutaRemotaImagen() {
		return new Date().getTime() + ".jpg";
	}

	public static void modoEstricto() {
		// Estas 2 lineas son necesarias para versiones Honeycomb y superior
		// Ya que traen el modo estricto activado por defecto
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public static InputStream descarga(String path) throws IOException {
		URL u = new URL(path);
		HttpURLConnection c = (HttpURLConnection) u.openConnection();
		c.setRequestMethod("GET");
		c.setDoOutput(true);
		c.connect();
		return c.getInputStream();

	}

	public static Drawable createDrawableFromURL(String urlString) {
		Drawable image = null;
		URL url;
		try {
			url = new URL(urlString);
			InputStream is = (InputStream) url.getContent();
			image = Drawable.createFromStream(is, "src");
		} catch (IOException e) {
			return null;
		}
		return image;
	}

	public static String comprobarVersion(String url) {
		try {
			URL link = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) link.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			InputStream in;
			String respuesta = null;
			in = new BufferedInputStream(conn.getInputStream());
			respuesta = convertStreamToString(in);
			respuesta = respuesta.replace("\n", "");
			in.close();
			return respuesta;
		} catch (IOException e1) {
			return null;
		}
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static Bitmap decodeFile(File f) throws IOException {
		Bitmap b = null;
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		FileInputStream fis = new FileInputStream(f);
		BitmapFactory.decodeStream(fis, null, o);
		fis.close();
		int scale = 1;
		if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
			scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
		}
		o = new BitmapFactory.Options();
		o.inSampleSize = scale;
		fis = new FileInputStream(f);
		b = BitmapFactory.decodeStream(fis, null, o);
		fis.close();
		return b;
	}

}
