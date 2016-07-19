package com.multimedia.aes.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import com.sewoo.jpos.POSPrinterService;
import com.sewoo.port.android.BluetoothPort;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import jpos.JposException;
import jpos.POSPrinterConst;

public class Impresora {

	public Activity activity;
	private BluetoothAdapter bluetoothAdapter;
	public BluetoothPort bp;
	private String textoImpresion = "";
	private int ancho_sewoo_seleccionado = 32;
	private String ocupados;
	private Resources res;
	public Thread hThread;
	BluetoothDevice mmDevice;

	public Impresora(Activity activity,BluetoothDevice mmDevice) {
		super();
		this.activity = activity;
		this.mmDevice = mmDevice;
		res = activity.getResources();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bp = BluetoothPort.getInstance();
	}
	public void imprimir() {
		iniciarConexion();
		HiloConectarImpr hci = new HiloConectarImpr(this, activity);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			hci.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mmDevice);
		else
			hci.execute(mmDevice);
	}
	private void iniciarConexion() {
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			try {
				Thread.sleep(3600);
			} catch (Exception e) {
			}
		}
	}
	public void realizarImpresion() {
		POSPrinterService pps = new POSPrinterService();
		try {
			imprimirImagenEncabezado(pps);
			generarTexto(pps);
			bluetoothAdapter.disable();
		} catch (IOException | JposException | SQLException | InterruptedException e) {
			Toast.makeText(activity, R.string.err_durante_impr, Toast.LENGTH_SHORT).show();
		}
	}
	private void generarTexto(POSPrinterService pps) throws JposException, SQLException, IOException, InterruptedException {
		String fecha = "22/06/2016";
		String hora = "12:06";
		String fecha_hora = "FECHA Y HORA: "+fecha+"-"+hora + "\n\n";
		String datos_cliente = "---------DATOS CLIENTE---------" + "\n";
		String nombre_cliente = "Maria Garcia Hinojosa" + "\n";
		String num_contrato = "000111522";
		String numero_contrato = "N. Contrato: "+num_contrato + "\n";
		String serv = "Mantenimiento Gas";
		String servicio = "Servicio: "+serv+ "\n";
		String dir = "Calle Ribadavia 11,2-A,"+"\n"+"Madrid,Madrid,20156";
		String direccion = "Direccion"+"\n"+dir+"\n\n";
		String datos_tecnico = "---------DATOS TECNICO---------" + "\n";
		String emp = "IBERDROLA";
		String empresa = "Empresa: "+emp+"\n";
		String cif_emp = "02365474S";
		String cif = "CIF: "+cif_emp+"\n";
		String num_emp_mant = "44556678";
		String numero_empresa_mantenedora = "N. Empresa Mantenedora: "+"\n"+num_emp_mant+"\n";
		String tec = "Pedro Buenhombre Lopez";
		String tecnico = "Tecnico: "+tec+"\n";
		String num_insta = "659898741";
		String numero_instalador = "N. Instalador: "+num_insta+"\n\n";
		String datos_averia = "----------DATOS AVERIA----------" + "\n";
		String noti = "21/06/2016";
		String notificada = "Notificada: "+noti+"\n";
		String atend = "18/06/2016-14:00";
		String atendida = "Atendida: "+atend+"\n";
		String prev_repar = "26/06/2016-13:30";
		String prevista_reparacion = "Prevista reparacion: "+"\n"+prev_repar+"\n";
		String repa = "24/06/2016-12:48";
		String reparada = "Reparada: "+repa+"\n";
		String num_solic = "6547952";
		String numero_solicitud = "N. Solicitud: "+num_solic+"\n";
		String cod_ave = "3216565";
		String codigo_averia = "Codigo Averia: "+cod_ave+"\n";
		String desc = "Una averia sin importancia";
		String descripcion = "Descripcion: "+"\n"+desc+"\n";
		String textoImpresion =fecha_hora+datos_cliente+nombre_cliente+numero_contrato+servicio+direccion+
				datos_tecnico+empresa+cif+numero_empresa_mantenedora+tecnico+numero_instalador+datos_averia+
				notificada+atendida+prevista_reparacion+reparada+numero_solicitud+codigo_averia+descripcion;
		pps.printNormal(POSPrinterConst.PTR_S_RECEIPT, textoImpresion);
		Thread.sleep(2000);
	}
	private void imprimirImagenEncabezado(POSPrinterService pps) throws IOException, JposException, InterruptedException {
		InputStream bitmap = null;
		int img[][] = null;
		int ancho = 1;
		try {
			bitmap = activity.getAssets().open("logo.png");
			Bitmap bit = BitmapFactory.decodeStream(bitmap);
			img = new int[bit.getWidth()][bit.getHeight()];
			ancho = bit.getWidth();
			for (int i = 0; i < bit.getHeight(); i++) {
				for (int j = 0; j < bit.getWidth(); j++) {
					img[j][i] = bit.getPixel(j, i);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bitmap.close();
		}
		pps.printBitmap(POSPrinterConst.PTR_S_RECEIPT, img, ancho, POSPrinterConst.PTR_BM_LEFT);
		Thread.sleep(2000);
	}
	/*private void generarTextoFinal(POSPrinterService pps) throws JposException, InterruptedException {
		try {
			imprimirFirma(pps);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void imprimirFirma(POSPrinterService pps) throws IOException, JposException, InterruptedException {
			final int MAX_FIRMA_SIZE = 375;
			FileInputStream fis = new FileInputStream(ip.getRuta_local());
			Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
			int w = imageBitmap.getWidth();
			int h = imageBitmap.getHeight();
			float multiplicador = (float) w / MAX_FIRMA_SIZE;
			imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (w / multiplicador), (int) (h / multiplicador), false);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			OutputStream out = new FileOutputStream(ip.getRuta_local() + "_tmp");
			baos.writeTo(out);
			pps.printBitmap(POSPrinterConst.PTR_S_RECEIPT, ip.getRuta_local() + "_tmp", 1, POSPrinterConst.PTR_BM_LEFT);
			Thread.sleep(2000);
			File f = new File(ip.getRuta_local() + "_tmp");
			f.delete();
	}*/
	private String limpiarAcentos(String texto_entrada) {
		String original = "��������������u������������������Ǻ";
		String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcCo";
		String output = texto_entrada;
		for (int i = 0; i < original.length(); i++) {
			output = output.replace(original.charAt(i), ascii.charAt(i));
		}
		return output;
	}
	private String generarLineaSewoo(String texto_entrada) {

		texto_entrada = limpiarAcentos(texto_entrada);
		String texto_retorno = "";
		for (int i = 0; i < texto_entrada.length(); i = i + ancho_sewoo_seleccionado - 1) {
			texto_retorno += texto_entrada.substring(i, (i + ancho_sewoo_seleccionado - 1) <= texto_entrada.length() ? (i + ancho_sewoo_seleccionado - 1) : texto_entrada.length()) + "-\n";
			ocupados = texto_entrada.substring(i, (i + ancho_sewoo_seleccionado - 1) <= texto_entrada.length() ? (i + ancho_sewoo_seleccionado - 1) : texto_entrada.length()) + "-\n";
		}
		if (texto_retorno.length() > 0) {
			texto_retorno = texto_retorno.substring(0, texto_retorno.length() - 3);
		}
		return texto_retorno + "\n";
	}
}
