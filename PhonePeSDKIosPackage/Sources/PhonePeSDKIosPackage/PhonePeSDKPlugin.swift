
import PhonePePayment
import Foundation
import UIKit

let apiEndPoint = "/pg/v1/pay";
let saltIndex: Int = 1
let salt: String = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399"
let merchantId: String = "PGTESTPAYUAT"
var appID = ""
var currentView:UIViewController = UIViewController.init()

let headers: [String: String] = [:]

var newTxnId: String {
    "\(UUID().uuidString.suffix(35))" // MAX 35 characters allowed
}

public class PhonePeSDKPlugin : UIViewController
{
    public override func viewDidLoad() {
        super.viewDidLoad()
        currentView = self
        PPPayment.enableDebugLogs = true // Set to print debug logs
    }
}

@_cdecl("Init")
public func Init(environment: Int, _merchantID: String, _appID: String)
{
    PPPayment.enableDebugLogs = true
    appID = _appID
}

@_cdecl("StartTransaction")
func StartTransaction(_merchantID: String , _salt: String, _saltIndex: Int)
{
    let saltValue = _salt
    let saltIndexValue = _saltIndex
    let merchantID = merchantId
    let server = Environment.sandbox

    let service = "/pg/v1/pay"
    let txnId = newTxnId // This id must be unquie for each transaction
    let amount = 200 // Amount should be in paisa
    let userId = "U100121333" // Logged in user id
    let message = "Payment towards order No. OD139924923" // Message that will be displayed to user
    let iOSAppCallbackSchema = ""
    let callBackURL = "https://www.phonepe.com"
    let redirectURL = "https://www.phonepe.com"

    var paymentInstrument: [String: Any] = [:]
    paymentInstrument["vpa"] = nil

    var data: [String: Any] = [:]
    data["merchantId"] = merchantID
    data["merchantTransactionId"] = txnId
    data["amount"] = amount
    data["message"] = message
    data["merchantUserId"] = userId
    data["redirectUrl"] = redirectURL
    data["redirectMode"] = "GET"
    data["callbackUrl"] = callBackURL
    data["paymentInstrument"] = paymentInstrument

    guard let jsonData = try? JSONSerialization.data(withJSONObject: data, options: .prettyPrinted) else {
        print("Invalid Data to convert")
        return
    }

    let base64EncodedString = jsonData.base64EncodedString()
    let payloadChecksum = ChecksumHelper.calculateChecksum(of: base64EncodedString, api: service, salt: saltValue, saltIndex: saltIndexValue)

    let headers: [String: String] = [:]

    print("Initiating PG Request with data \(data)")
    print("Initiating PG Request with payloadChecksum \(payloadChecksum)")

    let request: DPSTransactionRequest = DPSTransactionRequest(body: base64EncodedString,
                                                              apiEndPoint: service,
                                                              checksum: payloadChecksum,
                                                              headers: headers,
                                                              appSchema: iOSAppCallbackSchema)

    // Set enableLogging = true for debug logs
    PPPayment(environment: server, enableLogging: true, appId: appID)
        .startPG(transactionRequest: request,
                 on: currentView,
                 animated: true) { _, result in
            let text = "\(result)"
            print(text)
            print("Completion:---------------------")
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
