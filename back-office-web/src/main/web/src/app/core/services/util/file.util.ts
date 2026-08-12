import {saveAs} from 'file-saver';

class FileUtil {
  downloadTextAsFile(content: string, fileName: string) {
    const blob = new Blob([content], {
      type: 'text/plain;charset=utf-8'
    });
    saveAs(blob, fileName);
  }

  fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(`${reader.result}`);
      reader.onerror = () => reject('Error al leer el archivo');
    });
  }

  async fileToByteArray(file: File): Promise<number[]> {
    return Array.from(new Uint8Array(await file.arrayBuffer()));
  }

  dataURItoBlob(dataURI: any): Blob {
    const base64String = dataURI.replace("data:image/png;base64," , "");
    const byteString = window.atob(base64String);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([int8Array], { type: 'image/png' });
    return blob;
  }

  blobToFile(theBlob: Blob, fileName:string): File {
    const b: any = theBlob;
    //A Blob() is almost a File() - it's just missing the two properties below which we will add
    b.lastModifiedDate = new Date();
    b.name = fileName;
    //Cast to a File() type
    return theBlob as File;
  }
  codificarArrayBytes(objeto: any): Uint8Array {
    if (objeto.archivoBase64) {
      const archivoReporte = objeto;
      const binaryString: string = atob(archivoReporte.archivoBase64);
      const longitud: number = binaryString.length;
      const bytes: Uint8Array = new Uint8Array(longitud);
      for (let i = 0; i < longitud; i++) {
        bytes[i] = binaryString.charCodeAt(i);
      }
      return bytes;
    } else {
      const binaryString = 'No se pudo generar el reporte, contacte al proveedor';
      const longitud: number = binaryString.length;
      const bytes: Uint8Array = new Uint8Array(longitud);
      for (let i = 0; i < longitud; i++) {
        bytes[i] = binaryString.charCodeAt(i);
      }
      return bytes;
    }
  }
}

export const fileUtil = new FileUtil();
