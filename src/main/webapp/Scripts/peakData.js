/*
This script gets the GET request sent by the Servlet to the Web server.
The GET request contains the result of the Elasticsearch query coming from Peaktech index
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

let getpeakData=[];
let statsPeak;
peakDataFunction = function () {
    let xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            statsPeak = JSON.parse(this.responseText.substring(this.responseText.indexOf(']') + 1, this.responseText.length));
            getpeakData = JSON.parse(this.responseText.substring(0, this.responseText.indexOf(']') + 1));
            document.getElementById("statsPeak").innerHTML =
                " Min = "+ parseFloat(JSON.stringify(statsPeak['min'])).toFixed(2)+
                " Max = "+ parseFloat(JSON.stringify(statsPeak['max'])).toFixed(2)+
                " Average = "+ parseFloat(JSON.stringify(statsPeak['avg'])).toFixed(2)+
                " Variance = "+ parseFloat(JSON.stringify(statsPeak['variance'])).toFixed(2)+
                " Count = "+ JSON.stringify(statsPeak['count'])
            ;
        }
    };
    xmlhttp.open("GET", "/SimpleServlet_war/peakData", true);
    xmlhttp.send();};

