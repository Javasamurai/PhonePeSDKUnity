
import PhonePePayment
import Foundation
import UIKit

let apiEndPoint = "/pg/v1/pay";
let saltIndex: Int = 1
let salt: String = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399"
let merchantId: String = "PGTESTPAYUAT"
let appID = ""

let headers: [String: String] = [:]

class PhonePeSDKPlugin : UIViewController
{
    func Init(environment: Int, merchantID: String, appID: String)
    {
    //    PPPayment.init(environment: Environment.sandbox)
        PPPayment.enableDebugLogs = true
    }

    func StartTransaction()
    {
        let request : DPSTransactionRequest = DPSTransactionRequest (
            body : "base64EncodedString",
            apiEndPoint : apiEndPoint,
            checksum : "payloadChecksum",
            headers : headers,
            appSchema : "iOSIntentIntegration"
        );
        PPPayment(environment: .sandbox, enableLogging: true, appId: appID)
                    .startPG(transactionRequest: request, on: self, animated: true)
        { _, result in
            let text = "\(result)"
            print(text)
          print("Completion:---------------------")
            // Indicates the UI Callback is given back to the Merchant app.
            //Now, check the status with your server and update it to the user accordingly
        }
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        let handled = PPPayment.checkDeeplink(url)
        if handled {
           // Phonepe is handling this, no need for any processing
           return true
       }
       //Process your own deeplinks here
       return true
    }

}
