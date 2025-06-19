import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { DialogService } from '@app/core/public-api';
import { StaffService } from '@app/core/http/staff.service';

declare var Fp: any

@Component({
  selector: 'tb-add-finger',
  templateUrl: './add-finger.component.html',
  styleUrls: ['../../../common/scss/dialog.common.scss', './add-finger.component.scss']
})
export class AddFingerComponent implements OnInit {

  fingerGot = 0;
  addFingerParams = {
    devicesKey: "",
    devicesType: 1,
    personnelId: "",
  }

  constructor(
    public dialogRef: MatDialogRef<AddFingerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogService: DialogService,
    private StaffService: StaffService,
    public _dialog: MatDialog,
  ) { }



  ngOnInit(): void {
    console.log(this.data);
    this.addFingerParams.personnelId = this.data.personnelId;
    this.startGetFinger();
  }


  startGetFinger() {
    let _this = this;

    Fp.connect(function () {
      console.log("fingerprint service connect succ")
      Fp.setDeviceType(Fp.SCSI_DEVICE, function () {

        // 连接指纹仪
        Fp.deviceCount(function (returnObj) {
          console.log("device count:" + returnObj.count)
          if (returnObj.count > 0) {
          } else {
            console.log("Please insert fingerprint")
          }
        })

        console.log("start open device");
        Fp.openDevice(-1, function (returnObj) {
          let handle = returnObj.handle;
          if (handle > 0) {
            console.log("openDevice succ");
            _this.getFeature(handle);
            // test();
          } else {
            console.log("openDevice failed");
          }
        })
      })
    })
  }

  //test指纹获取
  getFeature(handle) {
    let _this = this;
    console.log("please press finger");

    Fp.getFeature(handle, function (returnObj) {

      var stateCode = returnObj.stateCode

      if (stateCode == Fp.RT_SUCCESS) {
        _this.fingerGot = 1;
        _this.addFingerParams.devicesKey = returnObj.feature;
        // console.log("feature size:" + returnObj.featureLen + ", feature:" + returnObj.feature)
        _this.StaffService.fetchGetFinger(_this.addFingerParams).subscribe(res => {
          Fp.closeDevice(handle, function (returnObj) {
            if (returnObj.retCode == Fp.RT_SUCCESS) {
              console.log("device closed")
            }
          })
        })
      } else if (stateCode == Fp.RT_FP_CANCEL) {
        _this.fingerGot = 2;
        console.log("getFeature task cancelled")
      } else {
        if (this.lastStateCode != stateCode) {
          this.lastStateCode = stateCode

          switch (stateCode) {
            case -1:
              break;
            case Fp.RT_BAD_QUALITY:
              
              console.log("##Bad fingerprint quality, please retry")
              break;
            default:
              console.log("stateCode:", stateCode)

          }
        }

        Fp.sleep(200)
        _this.getFeature(handle)
      }
    })
  }

  //关闭弹窗
  DialogClose(): void {
    Fp.openDevice(-1, function (returnObj) {
      let handle = returnObj.handle;
      Fp.closeDevice(handle, function (returnObj) {
        if (returnObj.retCode == Fp.RT_SUCCESS) {
          console.log("device closed")
        }
      })
    })
    this.dialogRef.close(this.fingerGot);
  }

}
