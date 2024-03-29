/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

var samples = 20;
var speed = 250;
let timeout = samples * speed;
var values = [];
var labels = [];
var charts = [];
var value = 0;
var scale = 1;

addEmptyValues(values, samples);



var originalCalculateXLabelRotation = Chart.Scale.prototype.calculateXLabelRotation

function initialize() {
    charts.push(new Chart(document.getElementById("chart0"), {
        type: 'line',
        data: {
            //labels: labels,
            datasets: [{
                data: values,
                backgroundColor: 'rgba(255, 99, 132, 0.1)',
                borderColor: 'rgb(255, 99, 132)',
                borderWidth: 2,
                lineTension: 0.25,
                pointRadius: 0
            }]
        },
        options: {
            responsive: true,
            animation: {
                duration: speed * 1.5,
                easing: 'linear'
            },
            legend: false,
            scales: {
                xAxes: [{
                    type: "time",
                    display: true
                }],
                yAxes: [{
                    ticks: {
                        max: 1,
                        min: -1
                    }
                }]
            }
        }
    }), new Chart(document.getElementById("chart1"), {
        type: 'bar',
        data: {
            //labels: labels,
            datasets: [{
                data: values,
                backgroundColor: 'rgba(255, 99, 132, 0.1)',
                borderColor: 'rgb(255, 99, 132)',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            animation: {
                duration: speed * 1.5,
                easing: 'linear'
            },
            legend: false,
            scales: {
                xAxes: [{
                    type: "time",
                    display: true
                }],
                yAxes: [{
                    ticks: {
                        max: 1,
                        min: -1
                    }
                }]
            }
        }
    }));
}

function addEmptyValues(arr, n) {
    for(var i = 0; i < n; i++) {
        arr.push({
            x: moment().subtract((n - i) * speed, 'milliseconds').toDate(),
            y: null
        });
    }
}

function rescale() {
    var padding = [];

    addEmptyValues(padding, 10);
    values.splice.apply(values, padding);

    scale++;
}

function updateCharts(){
    charts.forEach(function(chart) {
        chart.update();
    });
}

function progress() {
    value = Math.min(Math.max(value + (0.1 - Math.random() / 5), -1), 1);
    values.push({
        x: new Date(),
        y: value
    });
    values.shift();
}

function advance() {
    if (values[0] !== null && scale < 4) {
        //rescale();
        updateCharts();
    }

    progress();
    updateCharts();

    setTimeout(function() {
        requestAnimationFrame(advance);
    }, speed);
}

window.onload = function() {
    initialize();
    advance();
};
