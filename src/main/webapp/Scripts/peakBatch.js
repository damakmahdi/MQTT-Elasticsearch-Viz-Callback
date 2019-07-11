/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

let peakBatch=[];
peakBatchFunction = function () {
    let xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            peakBatch = JSON.parse(this.responseText);
            //document.getElementById("test").innerHTML =JSON.stringify(peakBatch);
        }
    };
    xmlhttp.open("GET", "/SimpleServlet_WAR/peakBatch", true);
    xmlhttp.send();};

