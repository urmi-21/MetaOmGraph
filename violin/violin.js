function unpack(rows, key) {
    return rows.map(function(row) { return row[key]; });
}

var data = [{
    type: 'violin',
    x: unpack(rows, 'rowName'),
    y: unpack(rows, 'output'),
    points: 'none',
    box: {
        visible: true
    },
    line: {
        color: 'green',
    },
    meanline: {
        visible: true
    },
    transforms: [{
        type: 'groupby',
        groups: unpack(rows, 'rowName'),
    }]
}]

var layout = {
    title: "Violin Plot",
    height: 700,
    yaxis: {
        zeroline: false,
        title: {
            text: "Value",
            font: {
                family: 'Courier New, monospace',
            }
        }
    },
    xaxis: {
        title: {
            text: "Sample",
            font: {
                family: 'Courier New, monospace',
            }
        }
    }
}
var config = {responsive: true}

Plotly.newPlot('myDiv', data, layout, config);
