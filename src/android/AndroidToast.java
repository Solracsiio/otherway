//THanks to this forums 
//https://stackoverflow.com/questions/54062673/cordova-plugin-use-aar
//

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

import android.util.Log;

import java.io.IOException;
import java.util.Set;

import honeywell.connection.*;
import honeywell.connection.ConnectionBase;
import honeywell.connection.Connection_Bluetooth;

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
        } else if ("Imprimir".equals(action)) {
            Imprimir(args.getString(0), args.getString(1), callbackContext);
            return true;
        } else if ("obtenInfoPrinter".equals(action)) {
            obtenInfoPrinter(args.getString(0),callbackContext);
            return true;
        } else if ("closeConnection".equals(action)) {
            closeConnection(callbackContext);
            return true;
        } else if ("configuraRP2B".equals(action)) {
            configuraRP2B(args.getString(0),callbackContext);
            return true;
        }else if("ImprimirImagen".equals(action)){
            JSONArray labels = args.getJSONArray(0);
            String MACAddress = args.getString(1);
            ImprimirImagen(labels, MACAddress, callbackContext);
            return true;
        } else {

        }

        return false;

    }

    private void configuraRP2B(String MACADDRESS,CallbackContext callbackContext) {
        if (coneccion(MACADDRESS, callbackContext)) {
            docDPL.clear();// limpiar el cache de la impresora
            docExPCL_PP.clear();
            try {// Imprimir el texto especificado

                // medLa_DPL.setLabelLengthLimit(false);
                // medLa_DPL.setLabelWidth(576);
                medLa_DPL.setStopLocation(MediaLabel_DPL.StopLocationValue.NEXTLABEL);
                // medLa_DPL.setContinuousLabelLength(100);
                closeConnection(callbackContext);
                callbackContext.success("Configuracion aplicada!");
            } catch (Exception e) {

                e.printStackTrace();
                callbackContext.error("Error al imprimir Error : " + e.getMessage());
            }
        } else {
            callbackContext.error("No se pudo conectar, sorry, bai  : ");
        }
        

    }

    private boolean coneccion(String MACAddress, CallbackContext callbackContext) {
        //MACAddress="86:6B:0F:BD:C1:8F";
        
        boolean res=false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()) {
            // Log.d(LOG_TAG, "Creating a bluetooth-connection for mac-address " +
            // MACAddress);

            //conn =(ConnectionBase) new BluetoothConnection(MACAddress);
            
            try {
                conn=Connection_Bluetooth.createClient(MACAddress);
                mensaje+="paso1 con";
                //closeConnection(callbackContext);
                //callbackContext.success("Conexion Establecida");
                conn.open();
                mensaje+="paso2 con";
                res = true;
                mensaje+="paso3 con";
                // callbackContext.success("Se conecto con éxito a "+MACAddress+mensaje+" ");
               
            } catch (Exception e) {

                callbackContext.error("Error al conectar a "+MACAddress+mensaje+",  : " + e.getMessage());
                res=false;
            }

            // Log.d(LOG_TAG, "connection successfully opened...");

            
        } else {
            // Log.d(LOG_TAG, "Bluetooth is disabled...");
            callbackContext.error("Bluetooth no está activado");
        }

        return res;
    }

    public void Imprimir(String text, String MACADDRESS, CallbackContext callbackContext) {

        if (coneccion(MACADDRESS, callbackContext)) {
           
            docDPL.clear();
            // docExPCL_PP.clear();
            try {// Imprimir el texto especificado
                 // docLP.clear();// limpiar el cache de la impresora, muy importante realizar antes de cada impresion
            // docEZ.clear();// limpiar el cache de la impresora
            // docEZ.writeText(text,0,1);
            // conn.write(docEZ.getDocumentData());

            // docExPCL_LP.clear();
            // docExPCL_LP.writeText(text);
            // conn.write(docExPCL_LP.getDocumentData());

            // docExPCL_PP.clear();
            // docExPCL_PP.writeText(text,0,0);
            // conn.write(docExPCL_PP.getDocumentData());   
                
            // docDPL.clear();
            // docDPL.writeText(text, 0, 0, "00",paramDPL);
            // conn.write(docDPL.getDocumentData());  
            
            docLP.clear();
            docLP.writeText(text);
            conn.write(docLP.getDocumentData());  
                // conn.write(docEZ.getDocumentData());
                //
                

                // Imprime  la informacoin en cache de la impresora
                
                callbackContext.success("Impresion exitosa");
                closeConnection(callbackContext);

            } catch (Exception e) {

                e.printStackTrace();
                callbackContext.error("Error al imprimir Error : " + e.getMessage());
            }
        } else {
            callbackContext.error("No se pudo conectar, sorry, bai");
        }

    }


    //For PR2/PR3 Intermec Printers works with docLP, for 
    public void ImprimirImagen(JSONArray imagen, String MACADDRESS, CallbackContext callbackContext) {//imagen viene como un dato tipo JSONArray
        if (coneccion("88:6B:0F:BD:C1:8F", callbackContext)) {

        String mensaje="";
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
        docLP.writeImage(decodedByte,576);
        mensaje += "paso 4 ";
        
        conn.write(docLP.getDocumentData());//se impriume la imagen almacenada
     

        // docLP.writeText("                                                                                                      \n\n");
        // conn.write(docLP.getDocumentData());//se impriume la imagen almacenada
        // conn.write(docDPL.getDocumentData());//se impriume la imagen almacenada
        mensaje += "paso 5 ";

        closeConnection(callbackContext);
         callbackContext.success("Impresion Exitosa de imagen");
    }

} catch (Exception e) {
    callbackContext.error("mensaje "+e.getMessage());
}

} else {
    callbackContext.error("No se pudo conectar, sorry, bai");
}

    }

    public String obtenInfoPrinter(String MACADDRESS,CallbackContext callbackContext) {// obtener informacion de la impresora
        // ====DPL Printers(eg. RL3, RL4, etc.)========//
        // Query Printer info
        if (coneccion(MACADDRESS, callbackContext)) {
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
        } else {
            callbackContext.error("No se pudo conectar, sorry, bai");
        }

        return mensaje;
    }

    public void closeConnection(CallbackContext callbackContext) {// cerrar conexion
        // ====Method 1========//
        try {
            conn.close();
            // callbackContext.success("Cerro conexion");
        } catch (Exception e) {
            callbackContext.error("Error al desconectar Error : " + e.getMessage());
           
        }
        // ====Method 2========//
        // conn.setIsClosing(true);

    }
}