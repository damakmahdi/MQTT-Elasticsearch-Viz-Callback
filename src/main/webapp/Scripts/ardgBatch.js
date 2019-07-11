/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/
let ardgBatch=[];
ardgBatchFunction = function () {
    let xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            ardgBatch = JSON.parse(this.responseText);
            //document.getElementById("test").innerHTML =JSON.stringify(ardgBatch);

        }
    };
    xmlhttp.open("GET", "/SimpleServlet_WAR/ardgBatch", true);
    xmlhttp.send();
};
