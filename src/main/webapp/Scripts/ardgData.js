/*
This script gets the GET request sent by the Servlet to the Web server.
The GET request contains the result of the Elasticsearch query coming from Ardgetti index
 */

/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

/***********************************************************************************************************************
 *Copyright (c) Damak Mahdi. github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin/damakmahdi
 **********************************************************************************************************************/

let statsArdg;
let getardgData=[];
ardgDataFunction = function () {
    let xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            statsArdg=JSON.parse(this.responseText.substring(this.responseText.indexOf(']')+1,this.responseText.length));
            getardgData = JSON.parse(this.responseText.substring(0,this.responseText.indexOf(']')+1));
            document.getElementById("statsArdg").innerHTML =
                " Min = "+ parseFloat(JSON.stringify(statsArdg['min'])).toFixed(2)+
                " Max = "+ parseFloat(JSON.stringify(statsArdg['max'])).toFixed(2)+
                " Average = "+ parseFloat(JSON.stringify(statsArdg['avg'])).toFixed(2)+
                " Variance = "+ parseFloat(JSON.stringify(statsArdg['variance'])).toFixed(2)+
                " Count = "+ JSON.stringify(statsArdg['count'])

            ;
        }
        };
    xmlhttp.open("GET", "/SimpleServlet_war/ardgData", true);
    xmlhttp.send();};

