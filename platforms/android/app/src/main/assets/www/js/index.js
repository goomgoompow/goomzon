/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        console.log("initialize");
        document.addEventListener('deviceready', this.onDeviceReady.bind(this),  false);
        document.addEventListener('resume', app.onResume, false);
        document.addEventListener('pause', app.onPause, false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
//        location.replace("https://m.naver.com");
        //===================================================================
        // 특정 서버로 바로 이동시킬 때 아래 주석 해제
        //===================================================================
        this.onClickGoToReal();
//        this.onClickGoToTest();
//        this.onClickGoToDev();
//        this.onClickOpenCameraForContractBtn();
        //-----------------------------------------------------------------------
        // vzon team test
        //-----------------------------------------------------------------------
        /*
        document.getElementById('btn_test').addEventListener('click',this.onClickGoToTest.bind(this));
        document.getElementById('btn_dev').addEventListener('click',this.onClickGoToDev.bind(this));
        document.getElementById('btn_53').addEventListener('click',this.onClickGoTo53.bind(this));
        document.getElementById('btn_92').addEventListener('click',this.onClickGoTo92.bind(this));
        document.getElementById('btn_94').addEventListener('click',this.onClickGoTo94.bind(this));
        document.getElementById('btn_95').addEventListener('click',this.onClickGoTo95.bind(this));
        document.getElementById('btn_96').addEventListener('click',this.onClickGoTo96.bind(this));*/

        //-----------------------------------------------------------------------
        // vzon native 기능 테스트
        //-----------------------------------------------------------------------

        /*
        document.getElementById('btn_go_to_fcm').addEventListener('click',this.onClickGoToFCM.bind(this));
        document.getElementById('btn_native_back').addEventListener('click',this.onClickNativeBack.bind(this));
        document.getElementById('btn_camera').addEventListener('click',this.onClickOpenCameraForContractBtn.bind(this));
        document.getElementById('btn_scan').addEventListener('click',this.onClickScanBtn.bind(this));
        document.getElementById('btn_upload').addEventListener('click',this.onClickCameraBtn.bind(this));
        document.getElementById('btn_toast').addEventListener('click',this.onClickToastBtn.bind(this));
        document.getElementById('btn_exit').addEventListener('click',this.onClickExitBtn.bind(this));*/
//        document.getElementById('btn_update_login').addEventListener('click',this.onClickUpdateLogin.bind(this));
        //-----------------------------------------------------------------------
        // vzon 증빙 문서 기능 테스트
        //-----------------------------------------------------------------------

        /*
        document.getElementById('btn_type_a').addEventListener('click',this.onClickTypeA.bind(this));
        document.getElementById('btn_type_b').addEventListener('click',this.onClickTypeB.bind(this));
        document.getElementById('btn_type_h').addEventListener('click',this.onClickTypeH.bind(this));
        document.getElementById('btn_type_e').addEventListener('click',this.onClickTypeE.bind(this));
        document.getElementById('btn_type_c').addEventListener('click',this.onClickTypeC.bind(this));
        document.getElementById('btn_type_d').addEventListener('click',this.onClickTypeD.bind(this));
        */

        /**
        * Notification이 발생하면 foreground 상태 일때 바로 호출되며, background 상태일 때는 사용자가 notification을 터치하여 앱에 진입시
        * 아래 함수가 호출됩니다.
        */
        /*
        function callbackNotification(payload)
        {
            console.log(payload);
            console.log("[[callbackNotification]] wasTapped = "+payload.wasTapped);
            //data.wasTapped == true means in Background :  Notification was received on device tray and tapped by the user.
            //data.wasTapped == false means in foreground :  Notification was received in foreground. Maybe the user needs to be notified.
            alert('Notification');
             if (payload.wasTapped) {
                 //Notification was received on device tray and tapped by the user.
                 console.log("### data.wasTapped = "+payload.wasTapped);
                 alert("From Background: \n"+JSON.stringify(payload));
             } else {
                 //Notification was received in foreground. Maybe the user needs to be notified.
                 console.log("$$$ data.wasTapped = "+payload.wasTapped);
                 alert("From Foreground: \n"+JSON.stringify(payload));
             }
        };

        function onSuccessNotification(message)
        {
            console.log('onNotification callback successfully registered: '+message);
        };

        function onFailNotification(error)
        {
            console.log('Error registering onNotification callback: '+ error);
        };
        FCMPlugin.onNotification(callbackNotification, onSuccessNotification, onFailNotification);
        */

       /* function success()
        {
           console.log("success to connect: dev");
        }
        function error()
        {
           console.log("fail");
        }
        Cordova.exec(success,error,"VzonPlugin", "selectServer",["dev"]);*/

    }
    ,onResume:function()
    {
        console.log("]]]]resume[[[[");
    }
    ,onPause:function()
    {
        console.log("[[[[pause]]]]");
    }
    // Update DOM on a Received Event
    ,receivedEvent: function(id) {
        console.log('Received Event: ' + id);
//        location.replace('http://192.168.0.96:18080/mobile/app/index.html');
//        location.replace('http://tapp.vzon.co.kr/mobile/app/index.html');

/*        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');*/

    }

    //-----------------------------------------------------------------------
    // vzon native 기능 테스트
    //-----------------------------------------------------------------------
    ,onClickOpenCameraForContractBtn: function(id){
             function success(data)
             {
                 console.log("SUCCESS_onClickOpenCameraForContractBtn");
                 console.log(data);
             }
             function error(data)
             {
                 console.log(data);
             }
              console.log("GOOM_ onClickOpenCameraForContractBtn");
              Cordova.exec(success,error,"VzonPlugin","openCameraForContract",["CONTRACT_PROOF_H"]);
              //Cordova.exec(success,error,"VzonPlugin","openCamera",["A", "Doc:A,Col,Mas:C", "0","C","C"]);
    }
    ,onClickCameraBtn: function(id){
        function success(data)
        {
            console.log("SUCCESS_onClickCameraBtn");
            console.log(data);
            var image = document.getElementById('image');
        }
        function error(data)
        {
            console.log(data);
        }
         console.log("GOOM_ onClickCameraBtn");
         Cordova.exec(success,error,"VzonPlugin","takePicture","");

    },onClickScanBtn: function(id){
       function success(data)
       {
           console.log("&&&&&&&&&&&& SCAN_BARCODE &&&&&&&&&&&&&&&");
           console.log(data);
       }
       function error(data)
       {
           console.log("&&&&&&&&&&&& onClickScanBtn_Fail &&&&&&&&&&&&&&&");
           console.log(data);
       }

         console.log("GOOM_onClickScanBtn");

         var barcodeParam =
         {"paramId":10314
         ,"paramItemId":10627
         ,"prodId":7
         ,"prodName":"POS본체-NICP 1901"
         ,"holdPartyId":""
         ,"type":"BARCODE_VERIFY_MERCHANT_INSTALL"
         ,"serialNr":[""]
         ,"totalNum":1};
         /*{
         	"holdPartyId": 0,
         	"paramId":1,
         	"paramItemId":1 ,
         	"prodId":0,
         	"serialNr": "0000000000000",
         	"type":"BARCODE_VERIFY_SHIPMENT_ISSUE"
         };*/

         Cordova.exec(success, error, "VzonPlugin", "scanBarcode",[barcodeParam]);
    }
    ,onClickToastBtn: function(id){
            function success(data)
            {
                console.log(data);
            }
            function error(data)
            {
                console.log(data);
            }

             console.log("GOOM_ onClickToastBtn");

             Cordova.exec(success, error, "VzonPlugin", "showToast",["Hello Goom"]);
     }
     ,resultScanBarcode: function(data){
        console.log("&&&&&&&&&&&& resultScanBarcode &&&&&&&&&&&&&");
        console.log(data);
     }
     ,uploadPhoto:function(imageURI)
     {
        function success(data)
        {
            console.log("uploadPhoto====ssssssssssssssssssssssssssss");
            console.log("Code = " + data.responseCode);
            console.log("Response = " + data.response);
            console.log("Sent = " + data.bytesSent);
        }
        function fail(error)
        {
            console.log("uploadPhoto====eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            alert("An error has occurred: Code = " + error.code);
            console.log("upload error source " + error.source);
            console.log("upload error target " + error.target);
        }
        console.log("uploadPhoto===============");
        var options = new FileUploadOptions();
        options.fileKey="file";
        options.fileName=imageURI.substr(imageURI.lastIndexOf('/')+1);
        console.log("fileName = "+options.fileName);
        options.mimeType="image/jpeg";

        var header = {'vzon_access_token':'eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ2enUiOiJJQDFAN0BIQDMiLCJpc3MiOiJodHRwczpcL1wvd3d3LnZ6b24uY29tIiwiZXhwIjoxNTI4MjYyMjQxLCJpYXQiOjE1MjgxNzU4NDF9.yppAh4loM-j7IP1XU0mNCVJtMID03usNQjM4j9fdF-0_Dw-WgPTeTxFiVLc18YQqR5d_Faxl3cwGr3ljVL_YJv-CTYf_-gCCagklz9OWigoYV6EkYVQ1utm6Fnbty4SpSCCnPhNek6HelQRFv58PIvorV8QZiH99DsQSdclSez8'};
        options.headers = header;
        var ft = new FileTransfer();
        ft.upload(imageURI, encodeURI("http://192.168.0.92:18080/api/file/picture/install/142"), success, fail, options);
     }
     ,onClickGoToFCM:function()
     {
        console.log("GOOM_ onClickGoToFCM");
        function success(token)
        {
            console.log(" - ");
            console.log("######################## [index.js] onClickGoToFCM_ function success(token), token : "+token);
            console.log(" - ");

            //this is the fcm token which can be used
            //to send notification to specific device
            console.log("[[[[ FCMPlugin.getToken ]]]] token: "+token);
            //FCMPlugin.onNotification( onNotificationCallback(data), successCallback(msg), errorCallback(err) )
            //Here you define your application behaviour based on the notification data.
            FCMPlugin.onNotification(function(data) {
                console.log(data);
                //data.wasTapped == true means in Background :  Notification was received on device tray and tapped by the user.
                //data.wasTapped == false means in foreground :  Notification was received in foreground. Maybe the user needs to be notified.
                 if (data.wasTapped) {
                     //Notification was received on device tray and tapped by the user.
                     alert('from Background / wasTapped: true'+JSON.stringify(data));
                 } else {
                     //Notification was received in foreground. Maybe the user needs to be notified.
                     alert('on Foreground / wasTapped: false'+JSON.stringify(data));
                 }
            });

        }
        function error(error)
        {
            console.log("onClickGoToFCM_error : "+error);
        }
        FCMPlugin.getToken(success, error);
     }
     ,onClickExitBtn: function()
     {
        navigator.app.exitApp();
        Cordova.exec(function success(){}, function error(){console.log("error")},"VzonPlugin","showToast",["Exit"]);
     }
     ,onClickUpdateLogin: function()
     {
        function success()
        {
          console.log("[updateLogin] success");
        }

        function error(message)
        {
          console.log("[updateLogin] message: "+message);
        }
        Cordova.exec(success,error,"VzonPlugin","updateLogin");
     }

     //-----------------------------------------------------------------------
     // vzon team test
     //-----------------------------------------------------------------------
     ,onClickGoToReal: function()
     {
             function success()
             {
                location.replace('https://www.vzon.co.kr/mobile/app/index.html');
             }
             function error()
             {
                console.log("fail");
             }
             Cordova.exec(success,error,"VzonPlugin", "selectServer",["real"]);
     }
     ,onClickGoToDev: function()
     {
        function success()
        {
           console.log("success to connect: dev");
           location.replace('https://tapp.vzon.co.kr/mobile/app/index.html');
        }
        function error()
        {
           console.log("fail");
        }
        Cordova.exec(success,error,"VzonPlugin", "selectServer",["tapp"]);
     }
     ,onClickGoToTest:function()
     {
         console.log("Click: test");
         function success()
         {
            console.log("success to connect: test");
            location.replace('https://demoweb.vzon.co.kr/mobile/app/index.html');
         }
         function error()
         {
            console.log("fail");
         }
         Cordova.exec(success,error,"VzonPlugin", "selectServer",["demoWeb"]);
     }
     ,onClickGoTo53:function()
      {
          location.replace('http://192.168.0.53:18080/mobile/app/index.html');
          function success()
          {
             console.log("success to connect: 53");
          }
          function error()
          {
             console.log("fail");
          }
          Cordova.exec(success,error,"VzonPlugin", "selectServer",["53"]);
      }
     ,onClickGoTo95:function()
     {
         location.replace('http://192.168.0.95:18080/mobile/app/index.html');
         function success()
         {
            console.log("success to connect: 95");
         }
         function error()
         {
            console.log("fail");
         }
         Cordova.exec(success,error,"VzonPlugin", "selectServer",["95"]);
     }
    ,onClickGoTo92:function()
    {
        location.replace('http://192.168.0.92:18080/mobile/app/index.html');
        function success()
        {
           console.log("success to connect: 92");
        }
        function error()
        {
           console.log("fail");
        }
        Cordova.exec(success,error,"VzonPlugin", "selectServer",["92"]);
    },onClickGoTo92:function()
         {
             location.replace('http://192.168.0.92:18080/mobile/app/index.html');
             function success()
             {
                console.log("success to connect: 94");
             }
             function error()
             {
                console.log("fail");
             }
             Cordova.exec(success,error,"VzonPlugin", "selectServer",["92"]);
         }
    ,onClickGoTo94:function()
    {
        location.replace('http://192.168.0.94:18080/mobile/app/index.html');
        function success()
        {
           console.log("success to connect: 94");
        }
        function error()
        {
           console.log("fail");
        }
        Cordova.exec(success,error,"VzonPlugin", "selectServer",["94"]);
    }
    ,onClickGoTo96:function()
    {
        location.replace('http://192.168.0.96:18080/mobile/app/index.html');
        function success()
        {
           console.log("success to connect: 96");
        }
        function error()
        {
           console.log("fail");
        }
        Cordova.exec(success,error,"VzonPlugin", "selectServer",["96"]);
    }


    //-----------------------------------------------------------------------
    // vzon 증빙 문서 테스트
    //-----------------------------------------------------------------------

    ,onClickTypeA: function(id){
                 function success(data)
                 {
                     console.log("SUCCESS_onClickOpenCameraForContractBtn");
                     console.log(data);
                 }
                 function error(data)
                 {
                     console.log(data);
                 }
                  console.log("GOOM_ onClickOpenCameraForContractBtn");


                  Cordova.exec(success,error,"VzonPlugin","openCameraForContract",["CONTRACT_PROOF_A"]);
                  //Cordova.exec(success,error,"VzonPlugin","openCamera",["A", "Doc:A,Col,Mas:C", "0","C","C"]);

    }
    ,onClickTypeB: function(id){
                 function success(data)
                 {
                     console.log("SUCCESS_onClickOpenCameraForContractBtn");
                     console.log(data);
                 }
                 function error(data)
                 {
                     console.log(data);
                 }
                 console.log("GOOM_ onClickTypeB == BB");

                 Cordova.exec(success,error,"VzonPlugin","openCameraForContract",["CONTRACT_PROOF_B"]);
                 //Cordova.exec(success,error,"VzonPlugin","openCamera",["A", "Doc:A,Col,Mas:C", "0","C","C"]);

    }
    ,onClickTypeC: function(id){
                function success(data)
                {
                   console.log("SUCCESS_onClickOpenCameraForContractBtn");
                   console.log(data);
                }
                function error(data)
                {
                   console.log(data);
                }
                console.log("GOOM_ onClickOpenCameraForContractBtn");


               Cordova.exec(success,error,"VzonPlugin","openCameraForContract",["CONTRACT_PROOF_C"]);
               //Cordova.exec(success,error,"VzonPlugin","openCamera",["A", "Doc:A,Col,Mas:C", "0","C","C"]);

    }
    ,onClickTypeD: function(id){
                function success(data)
                {
                   console.log("SUCCESS_onClickOpenCameraForContractBtn");
                   console.log(data);
                }
                function error(data)
                {
                   console.log(data);
                }
                console.log("GOOM_ onClickOpenCameraForContractBtn");


               Cordova.exec(success,error,"VzonPlugin","openCameraForContract",["CONTRACT_PROOF_D"]);
               //Cordova.exec(success,error,"VzonPlugin","openCamera",["A", "Doc:A,Col,Mas:C", "0","C","C"]);

    }
    ,onClickTypeE: function(id){
                    function success(data)
                    {
                       console.log("SUCCESS_onClickOpenCameraForContractBtn");
                       console.log(data);
                    }
                    function error(data)
                    {
                       console.log(data);
                    }
                    console.log("GOOM_ onClickTypeE == EEE");


                   Cordova.exec(success,error,"VzonPlugin","openCameraForContract",["CONTRACT_PROOF_E"]);
                   //Cordova.exec(success,error,"VzonPlugin","openCamera",["A", "Doc:A,Col,Mas:C", "0","C","C"]);

    }


    ,onClickTypeH: function(id){
              function success(data)
              {
                  console.log("SUCCESS_onClickOpenCameraForContractBtn");
                  console.log(data);
              }
              function error(data)
              {
                  console.log(data);
              }
               console.log("GOOM_ onClickOpenCameraForContractBtn");

               Cordova.exec(success,error,"VzonPlugin","openCameraForContract",["CONTRACT_PROOF_H",true]);
               //Cordova.exec(success,error,"VzonPlugin","openCamera",["A", "Doc:A,Col,Mas:C", "0","C","C"]);
    }
    ,onClickNativeBack: function(){
       function success(){
             console.log("SUCCESS_onClickNativeBack");
       }
       function error(){
             console.log("ERROR_onClickNativeBack");
       }
       console.log("GOOM_onClickNativeBack");
       Cordova.exec(success,error,"NativeBackEventPlugin","nativeBack");
    }
};

app.initialize();