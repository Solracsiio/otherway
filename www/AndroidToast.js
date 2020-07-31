var exec = cordova.require('cordova/exec');

// var AndroidToast = function() {
//     console.log('AndroidToast instanced');
// };  
module.exports = {
    Imprimir: function(texto,MACAddress,onSuccess,onError){//Metodos llamados, (texto,Macaddress,onsucess,onerror) son los valores recibidos
        exec(onSuccess,onError,'AndroidToast','Imprimir',[texto,MACAddress])// los valres recibidos se asignan un en JSONArray para su uso a en el codigo nativo java
    },                                                                      // se tiene que especificar el nombre de la clase "AndroidToast" y el nombre del metodo "Imprimir". y los parametros que espera en formato de Arreglo []
    coneccion: function(MACAddress,onSuccess,onError){
        exec(onSuccess,onError,'AndroidToast','coneccion',[MACAddress])
    },
    analizador: function(texto,MACAddress,onSuccess,onError){
        exec(onSuccess,onError,'AndroidToast','analizador',[texto,MACAddress])
    },
    ImprimirEspacios: function(MACAddress,nSaltos,onSuccess,onError){
        exec(onSuccess,onError,'AndroidToast','ImprimirEspacios',[MACAddress,nSaltos])
    },
    closeConnection: function(onSuccess,onError){
        exec(onSuccess,onError,'AndroidToast','closeConnection',[])
    },
    activo: function(onSuccess,onError){
        exec(onSuccess,onError,'AndroidToast','activo',[])
    },
    obtenInfoPrinter: function(MACAddress,onSuccess,onError){
        exec(onSuccess,onError,'AndroidToast','obtenInfoPrinter',[MACAddress])
    },
    configuraRP2B: function(MACAddress,onSuccess,onError){
        exec(onSuccess,onError,'AndroidToast','configuraRP2B',[MACAddress])
    },
    ImprimirImagen: function(base64,MACAddress,onSuccess,onError){//base 64 es un Arreglo de codigo base 64 de una imagen SIN este texto  "data:image/png;base64," y debe estar entre corchetes asi: var base64 = [base64Code];
        exec(onSuccess,onError,'AndroidToast','ImprimirImagen',[base64,MACAddress])
    }
};