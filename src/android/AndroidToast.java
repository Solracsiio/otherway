//THanks to this forums 
//https://stackoverflow.com/questions/54062673/cordova-plugin-use-aar
//
package com.nikolabreznjak;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.io.IOException;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.io.IOUtils;
// import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
// import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import android.util.Log;

import java.io.IOException;
import java.util.Set;

import honeywell.connection.*;
import honeywell.connection.ConnectionBase;
import honeywell.connection.Connection_Bluetooth;

// import com.zebra.sdk.comm.BluetoothConnection;
// import com.zebra.sdk.comm.Connection;
// import com.zebra.sdk.comm.ConnectionException;
import honeywell.printer.DocumentDPL;
import honeywell.printer.DocumentDPL.*;
import honeywell.printer.DocumentEZ;
import honeywell.printer.DocumentLP;
import honeywell.printer.DocumentExPCL_LP;
import honeywell.printer.DocumentExPCL_PP;
import honeywell.printer.DocumentExPCL_PP.*;
import honeywell.printer.ParametersDPL;
import honeywell.printer.ParametersDPL.*;
import honeywell.printer.ParametersEZ;
import honeywell.printer.ParametersExPCL_LP;
import honeywell.printer.ParametersExPCL_LP.*;
import honeywell.printer.ParametersExPCL_PP;
import honeywell.printer.ParametersExPCL_PP.*;
import honeywell.printer.UPSMessage;
import honeywell.printer.configuration.dpl.*;
import honeywell.printer.configuration.ez.*;
import java.io.File;
import honeywell.printer.configuration.expcl.*;
import honeywell.printer.ParametersLP;

public class AndroidToast extends CordovaPlugin {

    private CallbackContext callbackContext;

    ConnectionBase conn;
    //ConnectionBase conn2;
    // ====DPL Printers(eg. RL3, RL4, etc.)========//
    DocumentDPL docDPL = new DocumentDPL();
    MediaLabel_DPL medLa_DPL = new MediaLabel_DPL(conn);// para cambiar la configuracion de la etiqueta
    // como por ejemplo el tamaño, el grosor
    ParametersDPL paramDPL = new ParametersDPL();
    //====Apex Printers(Apex 2, Apex 3, etc..)========//
    DocumentExPCL_LP docExPCL_LP = new DocumentExPCL_LP(3); //Line Print mode. “3” is the font index.
    ParametersExPCL_LP paramExPCL_LP = new ParametersExPCL_LP();

    DocumentExPCL_PP docExPCL_PP = new DocumentExPCL_PP(PaperWidth.PaperWidth_576); //Page print mode
    ParametersExPCL_PP paramExPCL_PP = new ParametersExPCL_PP();

    DocumentEZ docEZ = new DocumentEZ("MF204"); //EZ mode. MF204 is the font name
    ParametersEZ paramEZ = new ParametersEZ();

    DocumentLP docLP = new DocumentLP("!"); //LinePrint mode.“!” is the font name
    ParametersLP paramLP = new ParametersLP();

    PrinterInformation_DPL printerInfo;
    String mensaje = "";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // this.callbackContext = callbackContext;
        if ("coneccion".equals(action)) {
            coneccion(args.getString(0), callbackContext);
            return true;
        } else if ("ImprimirEspacios".equals(action)) {
            ImprimirEspacios(args.getString(0), callbackContext);
            return true;
        } else if ("Imprimir".equals(action)) {
            analizador(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if ("analizador".equals(action)) {
            analizador(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if ("obtenInfoPrinter".equals(action)) {
            obtenInfoPrinter(args.getString(0), callbackContext);
            return true;
        } else if ("closeConnection".equals(action)) {
            closeConnection(callbackContext);
            return true;
        } else if ("activo".equals(action)) {
            activo(callbackContext);
            return true;
        } else if ("configuraRP2B".equals(action)) {
            configuraRP2B(args.getString(0), callbackContext);
            return true;
        } else if ("ImprimirImagen".equals(action)) {
            JSONArray labels = args.getJSONArray(0);
            String MACAddress = args.getString(1);
            ImprimirImagen(labels, MACAddress, callbackContext);
            return true;
        } else {

        }

        return false;

    }

    private void configuraRP2B(String MACADDRESS, CallbackContext callbackContext) {
        // if (coneccion(MACADDRESS, callbackContext)) {
        docDPL.clear();// limpiar el cache de la impresora
        docExPCL_PP.clear();
        try {// Imprimir el texto especificado

            medLa_DPL.setLabelLengthLimit(false);
            medLa_DPL.setLabelWidth(576);
            // medLa_DPL.setContinuousLabelLength(100);
            closeConnection(callbackContext);
            callbackContext.success("Configuracion aplicada!");
        } catch (Exception e) {

            e.printStackTrace();
            callbackContext.error("Error al imprimir Error : " + e.getMessage());
        }
        // } else {
        //     callbackContext.error("No se pudo conectar, sorry, bai  : ");
        // }

    }

    private boolean coneccion(String MACAddress, CallbackContext callbackContext) {
        boolean res = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()) {
            try {
                conn = Connection_Bluetooth.createClient(MACAddress);
                // mensaje += "paso1 con";
                conn.open();
                // mensaje += "paso2 con";
                res = true;
                // mensaje += "paso3 con";
                callbackContext.success("Se conecto con éxito a " + MACAddress + mensaje + " ");

            } catch (Exception e) {
                if (conn.getIsActive()) {
                    callbackContext.error("Impresora ya conectada");
                }
                callbackContext.error("Impresora fuera de rango o mac erronea");
                res = false;
            }
        } else {
            callbackContext.error("Bluetooth no está activado");
            res = false;
        }

        return res;
    }

    public void ImprimirEspacios(String MACADDRESS, CallbackContext callbackContext) {

        docEZ.clear();
        try {
            paramEZ.setIsBold(true);
            docEZ.writeText("    ", 0, 1);
            // docEZ.writeText("    ", 0, 1, paramEZ); // For Intermec PR3/PR2 DOcumentEZ is the best way to print text without garbage

            conn.write(docEZ.getDocumentData());//Imprime  la informacoin en cache de la impresora

        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("Error al imprimir Error : " + e.getMessage());
        }

    }

    public void analizador(String text, String MACADDRESS, CallbackContext callbackContext) {
        String t = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (i % 27 == 0 && i != 0) {
                t += c + "    ";
                ImprimirLinea(t, MACADDRESS, callbackContext);
                t = "";
            } else {
                t += c;
            }

        }
        Imprimir(t, MACADDRESS, callbackContext);
        callbackContext.success("Se imprimio");
    }

   
    public void activo(CallbackContext callbackContext) {
        if (conn.getIsActive()) {
            callbackContext.success("Impresora Conectada");
        } else {
            callbackContext.error("Impresora desconectada");
        }

    }

    public void ImprimirLinea(String text, String MACADDRESS, CallbackContext callbackContext) {
        docEZ.clear();// limpiar el cache de la impresora, muy importante realizar antes de cada impresion
        try {// Imprimir el texto especificado
            //paramEZ.setIsBold(true);
            docEZ.writeText("<--EZ\n" +
            "{PRINT:\n" +
            "@0,30:PE203,HMULT2,VMULT2|"+text+"|\n" + // Y, X , Font size, ? , ?, Text, JMP
            // "@60,30:PE203,HMULT2,VMULT2|01-01-05|\n" +
            "}", 0, 1, paramEZ); // For Intermec PR3/PR2 DOcumentEZ is the best way to print text without garbage
            conn.write(docEZ.getDocumentData());//Imprime  la informacoin en cache de la impresora
            // closeConnection(callbackContext);
            // ImprimirEspacios(MACADDRESS, callbackContext);
            callbackContext.success("Texto Imprimido " + text.toString());
        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("Impresora no conectada");
        }
    }

    public void Imprimir(String text, String MACADDRESS, CallbackContext callbackContext) {

        // if (coneccion(MACADDRESS, callbackContext)) {
        docEZ.clear();// limpiar el cache de la impresora, muy importante realizar antes de cada impresion

        try {// Imprimir el texto especificado
            // paramEZ.setHorizontalMultiplier(3);
            // paramEZ.setVerticalMultiplier(3);
            //paramEZ.setFont("HoneywellSans-Medium");
            docEZ.writeText("<--EZ\n" +
            "{PRINT:\n" +
            "@0,30:PE203,HMULT2,VMULT2|"+text+"|\n" + // Y, X , Font size, ? , ?, Text, JMP
            // "@60,30:PE203,HMULT2,VMULT2|01-01-05|\n" +
            "}", 0, 1, paramEZ); // For Intermec PR3/PR2 DOcumentEZ is the best way to print text without garbage

            conn.write(docEZ.getDocumentData());//Imprime  la informacoin en cache de la impresora
            // closeConnection(callbackContext);
            ImprimirEspacios(MACADDRESS, callbackContext);
            ImprimirEspacios(MACADDRESS, callbackContext);
            ImprimirEspacios(MACADDRESS, callbackContext);
            callbackContext.success("Texto Imprimido " + text.toString());

        } catch (Exception e) {

            e.printStackTrace();
            callbackContext.error("Impresora no conectada");
        }

    }

    public void ImprimirImagen(JSONArray imagen, String MACADDRESS, CallbackContext callbackContext) {//imagen viene como un dato tipo JSONArray
        // if (coneccion("88:6B:0F:BD:C1:8F", callbackContext)) {

        String mensaje = "";
        try {
            for (int i = imagen.length() - 1; i >= 0; i--) {//imprime en secuencia si envias varias imagenes
                docLP.clear();// limpiar el cache de la impresora
                //  docEZ.clear();
                String base64Image = imagen.get(i).toString();

                byte[] data = base64Image.getBytes("UTF-8");
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);//Decodificacion de base64 con utf--8

                mensaje = "paso1 ";
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                mensaje += "paso 2 ";
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mensaje += "paso 3 ";

                // medLa_DPL.setStopLocation(MediaLabel_DPL.StopLocationValue.CUT);
                docLP.writeImage(decodedByte, 576);
                mensaje += "paso 4 ";

                conn.write(docLP.getDocumentData());//se impriume la imagen almacenada
                ImprimirEspacios(MACADDRESS, callbackContext);
                ImprimirEspacios(MACADDRESS, callbackContext);
                ImprimirEspacios(MACADDRESS, callbackContext);

                mensaje += "paso 5 ";
                // closeConnection(callbackContext);
                callbackContext.success("Impresion Exitosa de imagen");
            }

        } catch (Exception e) {
            callbackContext.error("Impresora no conectada");
        }

    }

    public String obtenInfoPrinter(String MACADDRESS, CallbackContext callbackContext) {// obtener informacion de la impresora
        // ====DPL Printers(eg. RL3, RL4, etc.)========//
        // Query Printer info
        // if (coneccion(MACADDRESS, callbackContext)) {
        docDPL.clear();// limpiar el cache de la impresora
        try {
            printerInfo.queryPrinter(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String message = "";
        if (printerInfo.getValid() == false) {
            message = "No response from printer\r\n";
            mensaje = message;
        } else {
            message = String.format("Firmware Version: %s\n", printerInfo.getVersionInformation());
            mensaje = message;
        }
        return mensaje;
    }

    public void closeConnection(CallbackContext callbackContext) {// cerrar conexion
        // ====Method 1========//
        try {
            conn.close();
            callbackContext.success("Cerro conexion");
        } catch (Exception e) {
            callbackContext.error("Error al desconectar Error : " + e.getMessage());

        }
    }
}
