import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { DeviceIotService } from '@app/core/http/device-iot.service';
import { Utils } from '../order-management/w-utils';
import { DictionaryService } from '@app/core/http/dictionary.service';

declare var echarts: any;

@Component({
  selector: 'tb-device-iot',
  templateUrl: './device-iot.html',
  styleUrls: ['./device-iot.scss']
})
export class DeviceIotComponent implements OnInit {
  ovenChartData: any = []
  tanSensorChartData: any = []
  isChart = false;
  // 默认前一天到当天
  pdRange = new FormGroup({
    start: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
    end: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
    date: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
  });
  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    deviceCode: "",
  });
  deviceList = []
  deviceOvenList = []
  deviceTempList = []
  curTable = 'Oven';
  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;

  dataSource = [];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);
  chartInstances: any = []

  constructor(protected store: Store<AppState>,
    public fb: FormBuilder,
    private deviceIotService: DeviceIotService,
    private utils: Utils,
    private dictionaryService: DictionaryService,
  ) { }

  ngOnInit(): void {
    this.getChartData();
    this.getTableData();
    this.deviceIotService.fetchGetDeviceList('Oven').subscribe(res => {
      this.deviceOvenList = [
        {
          deviceCode: '',
          deviceName: '全部',
        }
      ].concat(res.data);
      this.deviceList = this.deviceOvenList;
    })
    this.deviceIotService.fetchGetDeviceList('TANSensor').subscribe(res => {
      this.deviceTempList = [
        {
          deviceCode: '',
          deviceName: '全部',
        }
      ].concat(res.data);
    })
    // let par = {
    //   current: 0,
    //   size: 999,
    //   codeClId: 'iot_device_type',
    //   enabledSt: 1
    // }
    // this.dictionaryService.fetchGetTypeTableList(par).subscribe(res => {
    //   this.deviceTypeList = res.data.list;
    // })
  }

  initChart(params) {
    const opts = {
      grid: {
        // top: '150px',
        left: '4%',
        right: '8%',
        // bottom: '10%',
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross'
        }
      },
      legend: {
        data: [params.legend],
        top: '10px',
        // padding: [25, 0, 0, 0],
        // icon: "rect",
        textStyle: {
          // color: "#FFFFFF",
          // fontSize: 24,
        },
        itemWidth: 20, // 设置图例标记的宽度
        itemHeight: 4, // 设置图例标记的高度
        itemGap: 30,
      },
      xAxis: {
        type: 'category',
        data: params.xAxisData,
        boundaryGap: false,
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          // color: '#C8DAFF',
          // fontSize: 24,
          // margin: 10,
          // interval: 0
        },
      },
      yAxis: {
        type: 'value',
        name: params.yAxisName,
        min: params.yAxisMin,
        max: params.yAxisMax,
        nameTextStyle: {
          // fontSize: 24,
          // color: '#C8DAFF',
          // padding: [0, 70, 15, 0],
          // verticalAlign: "bottom"
        },
        axisLabel: {
          // color: '#C8DAFF',
          // fontSize: 24,
          // margin: 12
        },
        splitLine: {
          show: true,
          lineStyle: {
            // color: 'rgba(200, 218, 255, 0.5)'
          }
        }
      },
      dataZoom: [
        {
          type: 'inside',
          start: 0,
          end: 100
        },
        {
          start: 0,
          end: 10
        }
      ],
      series: [
        {
          name: params.legend,
          data: params.data,
          type: 'line',
          symbol: 'none',
          smooth: true,
          itemStyle: {
            // color: '#43BD46'
          },
          markLine: {
            symbol: ['none', 'none'],
            lineStyle: {
              color: 'red',
              type: 'dashed',
            },
            label: {
              position: 'end',
              formatter: function (params) {
                return params.name + ':' + params.value;
              }
            },
            data: [
              { yAxis: params.max, name: '标准最大值' },
              { yAxis: params.min, name: '标准最小值' },
            ]
          }
        }
      ]
    }
    const chart = echarts.init(document.getElementById(params.id));
    chart.setOption(opts);
    return chart;
  }

  // 获取图表数据
  getChartData(): void {
    let par = {
      deviceCode: this.searchFormGroup.value.deviceCode,
      deviceName: this.deviceList.find(item => item.deviceCode == this.searchFormGroup.value.deviceCode)?.deviceName,
      byDate: this.pdRange.value.date ? this.utils.dateFormat(new Date(this.pdRange.value.date), 'yyyy-MM-dd') : null,
    }
    if (this.curTable == 'Oven') {
      this.deviceIotService.fetchGetOvenChartData(par).subscribe(res => {
        if (res.data?.length) {
          res.data.forEach(item => {
            item.area1 = 'one'
            item.areaList1 = [
              {
                key: 'one',
                value: '一区',
              },
              {
                key: 'two',
                value: '二区',
              },
              {
                key: 'three',
                value: '三区',
              },
              {
                key: 'four',
                value: '四区',
              },
            ]
            item.area2 = 'Up'
            item.areaList2 = [
              {
                key: 'Up',
                value: '上半区',
              },
              {
                key: 'Down',
                value: '下半区',
              },
            ]
          })
        }
        this.ovenChartData = res.data || [];
        if (this.isChart) {
          this.initCharts();
        }
      })
    } else {
      this.deviceIotService.fetchGetTANSensorChartData(par).subscribe(res => {
        if (res.data?.length) {
          res.data.forEach(item => {
            item.area = 'temp'
            item.areaList = [
              {
                key: 'temp',
                value: '温度',
              },
              {
                key: 'hemp',
                value: '湿度',
              },
            ]
          })
        }
        this.tanSensorChartData = res.data || [];
        if (this.isChart) {
          this.initCharts();
        }
      })

    }
  }

  changeTable(table: string) {
    this.curTable = table;
    this.deviceList = this.curTable == 'Oven' ? this.deviceOvenList : this.deviceTempList;
    this.searchFormGroup.get('deviceCode').setValue(this.deviceList[0].deviceCode);
    this.getTableData();
  }
  changeChart(isChart: boolean) {
    this.isChart = isChart
    if (this.isChart) {
      this.initCharts();
    }
  }
  areaChange(item) {
    if (this.curTable === 'Oven') {
      const params = {
        id: item.deviceCode,
        legend: item.areaList2.find(areaItem => areaItem.key === item.area2).value,
        xAxisData: item[item.area1 + item.area2 + 'TempBoard'].map(item => item.byDate.split(' ')[1]),
        yAxisName: '(℃)',
        data: item[item.area1 + item.area2 + 'TempBoard'].map(item => item.byQty),
        max: item['over' + this.capitalizeFirstLetter(item.area1) + (item.area2) + 'MaxTemp'],
        min: item['over' + this.capitalizeFirstLetter(item.area1) + (item.area2) + 'MinTemp'],
        yAxisMin: 0,
        yAxisMax: 0
      }
      params.yAxisMin = Math.min(Math.min(...params.data.map(item => Number(item))), params.min)
      params.yAxisMax = Math.max(Math.max(...params.data.map(item => Number(item))), params.max)
      this.chartInstances.push(this.initChart(params));
      return;
    }

    const params1 = {
      id: item.deviceCode,
      legend: item.areaList.find(areaItem => areaItem.key === item.area).value,
      xAxisData: item[item.area + 'Board'].map(item => item.byDate.split(' ')[1]),
      yAxisName: item.area == 'temp' ? '(℃)' : '(%)',
      data: item[item.area + 'Board'].map(item => item.byQty),
      max: item['inMax' + this.capitalizeFirstLetter(item.area)],
      min: item['inMin' + this.capitalizeFirstLetter(item.area)],
      yAxisMin: 0,
      yAxisMax: 0
    }
    params1.yAxisMin = Math.min(Math.min(...params1.data.map(item => Number(item))), params1.min)
    params1.yAxisMax = Math.max(Math.max(...params1.data.map(item => Number(item))), params1.max)
    this.chartInstances.push(this.initChart(params1));
  }
  initCharts() {
    setTimeout(() => {
      this.chartInstances.forEach(chart => {
        chart.dispose();
      })
      this.chartInstances = [];
      if (this.curTable === 'Oven') {
        this.ovenChartData.forEach(item => {
          const params = {
            id: item.deviceCode,
            legend: item.areaList2.find(areaItem => areaItem.key === item.area2).value,
            xAxisData: item[item.area1 + item.area2 + 'TempBoard'].map(item => item.byDate.split(' ')[1]),
            yAxisName: '(℃)',
            data: item[item.area1 + item.area2 + 'TempBoard'].map(item => item.byQty),
            max: item['over' + this.capitalizeFirstLetter(item.area1) + (item.area2) + 'MaxTemp'],
            min: item['over' + this.capitalizeFirstLetter(item.area1) + (item.area2) + 'MinTemp'],
            yAxisMin: 0,
            yAxisMax: 0
          }
          params.yAxisMin = Math.min(Math.min(...params.data.map(item => Number(item))), params.min)
          params.yAxisMax = Math.max(Math.max(...params.data.map(item => Number(item))), params.max)
          this.chartInstances.push(this.initChart(params));
        })
        return;
      }
      this.tanSensorChartData.forEach(item => {
        const params = {
          id: item.deviceCode,
          legend: item.areaList.find(areaItem => areaItem.key === item.area).value,
          xAxisData: item[item.area + 'Board'].map(item => item.byDate.split(' ')[1]),
          yAxisName: item.area == 'temp' ? '(℃)' : '(%)',
          data: item[item.area + 'Board'].map(item => item.byQty),
          max: item['inMax' + this.capitalizeFirstLetter(item.area)],
          min: item['inMin' + this.capitalizeFirstLetter(item.area)],
          yAxisMin: 0,
          yAxisMax: 0
        }
        params.yAxisMin = Math.min(Math.min(...params.data.map(item => Number(item))), params.min)
        params.yAxisMax = Math.max(Math.max(...params.data.map(item => Number(item))), params.max)
        this.chartInstances.push(this.initChart(params));
      })
    }, 100);
  }
  capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }
  //获取表格数据
  getTableData(): void {
    this.getChartData();
    this.dataSource = [];
    this.total = 0;
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        deviceCode: this.searchFormGroup.value.deviceCode,
        startDate: this.pdRange.value.start ? this.utils.dateFormat(new Date(this.pdRange.value.start), 'yyyy-MM-dd') : null,
        endDate: this.pdRange.value.end ? this.utils.dateFormat(new Date(this.pdRange.value.end), 'yyyy-MM-dd') : null,
      }
    }
    if (this.curTable === 'Oven') {
      this.deviceIotService.fetchGetOvenDeviceList(par).subscribe(res => {
        this.dataSource = res.data.list || [];
        this.total = res.data.total || 0;
      })
    } else {
      this.deviceIotService.fetchGetTANSensorDeviceList(par).subscribe(res => {
        this.dataSource = res.data.list || [];
        this.total = res.data.total || 0;
      })
    }

  }

  export() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        deviceCode: this.searchFormGroup.value.deviceCode,
        startDate: this.pdRange.value.start ? this.utils.dateFormat(new Date(this.pdRange.value.start), 'yyyy-MM-dd') : null,
        endDate: this.pdRange.value.end ? this.utils.dateFormat(new Date(this.pdRange.value.end), 'yyyy-MM-dd') : null,
      }
    }
    if (this.curTable === 'Oven') {
      this.deviceIotService.exportOvenDeviceRunTableList(par).subscribe(res => {
        var name = res.headers.get('content-disposition')//获取文件名，（后台返回的文件名在响应头当中）
        name = decodeURIComponent(name)//由于中文通常都是乱码，所以需要解码
        name = name.substring(name.indexOf("=") + 1)//数据处理获得名字
        this.downloadFile(res.body, name)//数据流都存在body中
      })
    } else {
      this.deviceIotService.exportTANSensorDeviceRunTableList(par).subscribe(res => {
        var name = res.headers.get('content-disposition')//获取文件名，（后台返回的文件名在响应头当中）
        name = decodeURIComponent(name)//由于中文通常都是乱码，所以需要解码
        name = name.substring(name.indexOf("=") + 1)//数据处理获得名字
        this.downloadFile(res.body, name)//数据流都存在body中
      })
    }
  }
  downloadFile(data, name) {
    const contentType = "application/x-zip-compressed";
    const blob = new Blob([data], { type: contentType });
    const url = window.URL.createObjectURL(blob);
    // 打开新窗口方式进行下载
    // window.open(url);
    // 以动态创建a标签进行下载
    const a = document.createElement("a");
    a.href = url;
    a.download = name;
    a.click();
    window.URL.revokeObjectURL(url);
  }
  //翻页事件
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
  reset() {
    this.searchFormGroup.value.deviceCode = '';
    this.pdRange = new FormGroup({
      start: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
      end: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
      date: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
    });

    this.getTableData();
  }
  format(second) {
    // 转换成小时，保留一位小数
    return (second / 60 / 60).toFixed(1);
  }
}
