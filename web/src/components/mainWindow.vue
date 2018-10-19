<template>
  <div style="background: linear-gradient(#003348,#001721)">
    <!--1. navigator-->
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo03"
              aria-controls="navbarTogglerDemo03" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <a class="navbar-brand" href="#">STEPCOIN</a>
      <div class="collapse navbar-collapse" id="navbarTogglerDemo03">
        <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
          <li class="nav-item active">
            <a class="nav-link" href="#">{{today}}</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Last Sync : {{lastSync}}</a>
          </li>
        </ul>
      </div>
      <form class="form-inline my-2 my-lg-0">
        <button class="btn btn-outline-primary my-2 my-sm-0">Sync</button> <!--type="submit"-->
      </form>
    </nav>
    <div class="card-group" style="padding-top:15px;">
      <div class="card position-relative overflow-hidden m-md-3 text-center bg-light">
        <div class="card-header">
          <b>Steps Graph</b>
        </div>
        <div class="card-body" style="padding:0; background-color: #ffffff;">
          <div class="row" style="height: 100%;">
            <div class="col-md-8">
              <div id="chart_div" style="width: 100%; height: 200px;"></div>
            </div>
            <div class="col-md-4" style="display: -ms-flexbox; display:flex; -ms-flex-align:center; align-items:center; -ms-flex-pack:center; justify-content:center; height: 200px;">
              <div class="card-title" style="font-size:30px; margin-top:1vw; display: block; width: 100%; text-align: center;"><p style="font-size:30px;margin-bottom: 0;">Today</p><i class="fas fa-walking" style="font-family: 'Font Awesome 5 Free'"></i><b style="font-size:60px;">{{' 11,657'}}</b>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="card-group">
      <div class="card position-relative overflow-hidden m-md-3 text-center bg-light">
        <div class="card-header">
          <b>Daily STEPCOIN Graph</b>
        </div>
        <div class="card-body" style="padding:0; background-color: #ffffff;">
          <div class="row">
            <div class="col-md-8"><div id="chart_div2" style="width: 100%; height: 200px;"></div></div>
            <div class="col-md-4"><div class="card-title" style="font-size:30px; color:#007bff; margin-top:1vw;">
              <p><b style="font-size:40px;">{{'+ 90'}}</b> STEPCOIN</p>
              <p>Total <b style="font-size:40px;">{{' 130'}}</b> STEPCOIN</p>
            </div></div>
          </div>
        </div>
      </div>
    </div>
    <div class="card-group">
      <div class="card position-relative overflow-hidden m-md-3 text-center bg-light">
        <div class="card-header">
          <b>Heart Rate</b>
        </div>
        <div class="card-body" style="padding:0; background-color: #ffffff;">
          <div id="chart_div3" style="width: 100%; height: 200px;"></div>
        </div>
      </div>
      <div class="card position-relative overflow-hidden m-md-3 text-center bg-light">
        <div class="card-header">
          <b>Resting Heart Rate</b>
        </div>
        <div class="card-body" style="padding:0; background-color: #ffffff;">
          <div class="row">
            <div class="col-md-8"><div id="chart_div4" style="width: 100%; height: 200px;"></div></div>
            <div class="col-md-4" style="display: -ms-flexbox; display:flex; -ms-flex-align:center; align-items:center; -ms-flex-pack:center; justify-content:center; height: 200px;">
              <div class="card-title"
                   style="margin-top:1vw; display: block; width: 100%; text-align: center;">
                <p style="font-size:25px; color:#e83e8c; margin-bottom:0;"><i class="fas fa-heartbeat" style="font-family: 'Font Awesome 5 Free'"></i> <b style="font-size:50px"> 70</b>
                  bpm</p>
                <p style="font-size:25px;">Resting HR</p></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import '../../node_modules/bootstrap/dist/css/bootstrap.min.css'
import moment from '../../node_modules/moment'

function chart1 () {
  let google = window.google
  google.charts.load('current', {packages: ['corechart']})
  google.charts.setOnLoadCallback(drawChart)
  function drawChart () {
    let data = google.visualization.arrayToDataTable([
      ['Element', 'Steps', {role: 'style'}],
      ['AM 0', 243, '#4ac0c0'],
      ['1', 0, '#4ac0c0'],
      ['2', 0, '#4ac0c0'],
      ['3', 0, '#4ac0c0'],
      ['4', 0, '#4ac0c0'],
      ['5', 10, '#4ac0c0'],
      ['6', 400, '#4ac0c0'],
      ['7', 343, '#4ac0c0'],
      ['8', 3302, '#4ac0c0'],
      ['9', 904, '#4ac0c0'],
      ['10', 1132, '#94c83d'],
      ['11', 4223, '#94c83d'],
      ['PM 12', 1623, '#94c83d'],
      ['13', 135, '#4ac0c0'],
      ['14', 0, '#4ac0c0'],
      ['15', 0, '#4ac0c0'],
      ['16', 0, '#4ac0c0'],
      ['17', 0, '#4ac0c0'],
      ['18', 0, '#4ac0c0'],
      ['19', 0, '#4ac0c0'],
      ['20', 0, '#4ac0c0'],
      ['21', 0, '#4ac0c0'],
      ['22', 0, '#4ac0c0'],
      ['23', 0, '#4ac0c0']
    ])

    let view = new google.visualization.DataView(data)
    view.setColumns([0, 1,
      { calc: 'stringify',
        sourceColumn: 1,
        type: 'string',
        role: 'annotation' },
      2])

    let options = {
      title: "Today's Steps",
      bar: {groupWidth: '50%'},
      legend: {position: 'none'},
      vAxis: {textStyle: {fontSize: 11}},
      hAxis: {
        textStyle: {
          fontSize: 9
        }
      }
    }
    let chart = new google.visualization.ColumnChart(document.getElementById('chart_div'))
    chart.draw(view, options)
  }
  window.testDrawChart1 = drawChart
}

function chart2 () {
  let google = window.google
  google.charts.load('current', {'packages': ['corechart']})
  google.charts.setOnLoadCallback(drawVisualization)

  function drawVisualization () {
    // Some raw data (not necessarily accurate)
    let data = google.visualization.arrayToDataTable([
      ['Month', 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'line'],
      ['1~7 Oct', 10050, 14041, 9832, 10100, 20412, 10000, 9504, 10000],
      ['8~14 Oct', 8150, 4033, 7132, 2134, 10200, 10000, 23991, 10000],
      ['15~21 Oct', 19310, 23130, 3092, 23304, 21123, 10000, 9919, 10000],
      ['22~28 Oct', 21150, 4131, 502, 7634, 22200, 10000, 19887, 10000]
    ])

    let options = {
      title: 'My STEPCOIN (Weekly)',
      vAxis: {title: 'Total Steps'},
      hAxis: {title: 'Date'},
      seriesType: 'bars',
      series: {7: {type: 'line'}},
      colors: ['#94c83d', '#4ac0c0', '#4ac0c0', '#4ac0c0', '#4ac0c0', '#4ac0c0', '#94c83d', 'red']
    }

    let chart = new google.visualization.ComboChart(document.getElementById('chart_div2'))
    chart.draw(data, options)
  }

  window.testDrawChart2 = drawVisualization
}

function chart3 () {
  let google = window.google
  google.charts.load('current', {packages: ['corechart', 'line']})
  google.charts.setOnLoadCallback(drawBasic)

  function drawBasic () {
    let data = new google.visualization.DataTable()
    data.addColumn('number', 'X')
    data.addColumn('number', 'HR')

    data.addRows([
      [0, 65], [1, 68], [2, 62], [3, 60], [4, 66], [5, 65],
      [6, 67], [7, 78], [8, 71], [9, 81], [10, 72], [11, 67],
      [12, 69], [13, 66], [14, 62], [15, 77], [16, 84], [17, 88],
      [18, 81], [19, 71], [20, 74], [21, 65], [22, 56], [23, 57],
      [24, 60], [24.5, 55]
    ])

    let options = {
      hAxis: {
        title: 'Time'
      },
      vAxis: {
        title: 'Heart Rate'
      }
    }

    let chart = new google.visualization.LineChart(document.getElementById('chart_div3'))

    chart.draw(data, options)
  }

  window.testDrawChart3 = drawBasic
}

function chart4 () {
  let google = window.google
  google.charts.load('current', {packages: ['corechart']})
  google.charts.setOnLoadCallback(drawChart)
  function drawChart () {
    let data = google.visualization.arrayToDataTable(
      [
        ['X', 'Y'],
        ['22 Oct', 75],
        ['23 Oct', 74],
        ['24 Oct', 71],
        ['25 Oct', 72],
        ['26 Oct', 73],
        ['27 Oct', 71],
        ['28 Oct', 70]
      ])

    let options = {
      legend: 'none',
      hAxis: { minValue: 0, maxValue: 9 },
      colors: ['rgb(232, 62, 140)'],
      pointSize: 20,
      pointShape: 'square'
    }

    let chart = new google.visualization.LineChart(document.getElementById('chart_div4'))
    chart.draw(data, options)
  }

  window.testDrawChart4 = drawChart
}

function resizeChart () {
  window.testDrawChart1()
  window.testDrawChart2()
  window.testDrawChart3()
  window.testDrawChart4()
}

if (document.addEventListener) {
  window.addEventListener('resize', resizeChart)
} else if (document.attachEvent) {
  window.attachEvent('onresize', resizeChart)
} else {
  window.resize = resizeChart
}

export default {
  name: 'HelloWorld',
  data () {
    return {
      today: moment().format('MMMM Do YYYY'),
      lastSync: moment().format('MMMM Do YYYY')
    }
  },
  mounted: function () {
    chart1()
    chart2()
    chart3()
    chart4()
  }
}
</script>
<style>
  * {
    font-family: 'K2D', sans-serif;
  }
</style>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  * {
    font-family: 'K2D', sans-serif;
  }

  h1, h2 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }

  .row {
    display: -ms-flexbox;
    display: flex;
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;
    margin-right: 0;
    margin-left: 0;
  }
</style>
