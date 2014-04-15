var download = {

    downLoadFile: function(successCallback,errorCallback,fileUrl, dirName, fileName, overwrite, idSesion) {
	
		if (overwrite === false){
			overwrite = "false";
		}else{
			overwrite = "true";
		}
        cordova.exec(
            successCallback,
            errorCallback, 
            'Download', 
            'downLoadFile', 
            [fileUrl, dirName, fileName, overwrite, idSesion]
        ); 
    }
}
module.exports = download;
