


const querystring = require('querystring');
const AWS = require("aws-sdk");
const ddb = new AWS.DynamoDB.DocumentClient();


let awsConfig={
    region: "ap-northeast-2",
    endpoint:"arn:aws:dynamodb:ap-northeast-2:755264852886:table/kchDhtTest",
    "accessKeyId" : "AKIAIQO6IAOIYS5DO4BQ",
    "secretAccessKey" : "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"
};

//온습도 데이터를 DB에 저장해주는 함수
function DBPut(event, count, roomnumber){ // count는 현재 DHT 테이블에 존재하는 데이터의 수(가로 줄)

     if(roomnumber==0) {
         return ddb.put({
        TableName: 'iot_project_body',
        Item:{
            "idx":count+1,
            "heart":event["heart"]
        }
    }).promise();
         
     }
     else if(roomnumber==1) {
         return ddb.put({
        TableName: 'iot_project_livingroom',
        Item:{
            "idx":count+1,
            "humid":event["humid"],
            "temperature":event["temperature"],
            "motion":event["door"],
            "emergency":event["emergency"]
        }
        
    }).promise();
     }
     else if(roomnumber==2) {
         return ddb.put({
        TableName: 'iot_project_kitchen',
        Item:{
            "idx":count+1,
            "humid":event["humid"],
            "temperature":event["temperature"],
            "gas":event["gas"],
        }
    }).promise();
     }
     else if(roomnumber==3) {
         return ddb.put({
        TableName: 'iot_project_toilet',
        Item:{
            "idx":count+1,
            "humid":event["humid"],
            "temperature":event["temperature"],
            "led":event["led"],
            "emergency":event["emergency"]
        }
    }).promise();
     }
}


function errorResponse(errorMessage, awsRequestId, callback) {
  callback(null, {
    statusCode: 500,
    body: JSON.stringify({
      Error: errorMessage,
      Reference: awsRequestId,
    }),
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
  });
}



exports.handler = (event, context ,callback) => {
     var params0 = {
         TableName : "iot_project_body"
     };
    var params1 = {
         TableName : "iot_project_livingroom"
     };
    var params2 = {
         TableName : "iot_project_kitchen"
     };
    var params3 = {
         TableName : "iot_project_toilet"
     };
     
     var roomnumber = event["roomnumber"];
     
     ddb.scan(params1, onScan);
     
     if(roomnumber==0) ddb.scan(params0, onScan);
     else if(roomnumber==1) ddb.scan(params1, onScan);
     else if(roomnumber==2) ddb.scan(params2, onScan);
     else if(roomnumber==3) ddb.scan(params3, onScan);
     
     if(event["emergency"] == 1){
             var sns = new AWS.SNS();
    var params = {
        Message: "Emergency!", 
        Subject: "Emergency button press!",
        TopicArn: "arn:aws:sns:ap-northeast-2:755264852886:mytopic"
        };
    sns.publish(params, context.done);
    }

     function onScan(err,data){
         if(err){
         }else{
             DBPut(event, data["Count"], roomnumber);
         }
     }
};