google.charts.load('current', { 'packages': ['treemap'], 'language': 'cn' });
google.charts.setOnLoadCallback(drawChart);
function drawChart() {
  data = google.visualization.arrayToDataTable([%s, %s]);

  for (var i = 0, length = data.getNumberOfRows(); i < length; i++) {
    var absPath = data.getValue(i, 0);
    var ndx = absPath.lastIndexOf("/");
    var fileName = absPath.substring(ndx + 1);
    data.setFormattedValue(i, 0, fileName);
  }

  tree = new google.visualization.TreeMap(document.getElementById('chart_div'));
  google.visualization.events.addListener(tree, 'rollup', onRollUp);
  google.visualization.events.addListener(tree, 'drilldown', onDrillDown);
  google.visualization.events.addListener(tree, 'highlight', onHighLight);

  tree.draw(data, {
    minColor: '#f6b26b',
    midColor: '#f6b26b',
    maxColor: '#f6b26b',
    headerHeight: 15,
    allowHtml: true,
    fontColor: 'black',
    generateTooltip: showFullTooltip,
    eventsConfig: {
      highlight: ['contextmenu'],
      unhighlight: ['mouseout'],
      rollup: [],
      drilldown: ['click'],
    }
  });

  function onRollUp(e) {
    var row = e['row'];
    console.log(row);
    console.log(data.getValue(row, 0));
    Android.showToast('123');
  }

  function showFullTooltip(row, size, value) {
    return '<div style="background:#fd9; padding:10px; border-style:solid; max-width=500px">' +
      'File Name: ' + data.getFormattedValue(row, 0) + '<br>' +
      data.getColumnLabel(0) + ': ' + data.getValue(row, 0) + '<br>' +
      data.getColumnLabel(2) + ': ' + humanFileSize(size) + ' </div>';
  }
}

function rollUp() {
  tree.goUpAndDraw();
  var item = tree.getSelection()[0];
  console.log("selection " + item)
  var parentId = data.getValue(item.row, 0);
  console.log("curr dir is " + parentId)
  console.log("rollup to dir: " + parentId)
  OnEnterDirCallBack.setCurrDir(parentId);
}

function onDrillDown() {
  var item = tree.getSelection()[0];
  var parentId = data.getValue(item.row, 0);
  console.log("drilldown to dir: " + parentId)
  OnEnterDirCallBack.setCurrDir(parentId);
}

function onHighLight(e) {
  var row = e['row'];
  var path = data.getValue(row, 0);
  console.log("click " + path)
  TreeMapIf.onClickFile(path);
}

function humanFileSize(bytes, si = false, dp = 1) {
    const thresh = si ? 1000 : 1024;

    if (Math.abs(bytes) < thresh) {
      return bytes + ' B';
    }

    const units = si
      ? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
      : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    let u = -1;
    const r = 10 ** dp;

    do {
      bytes /= thresh;
      ++u;
    } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);


    return bytes.toFixed(dp) + ' ' + units[u];
}