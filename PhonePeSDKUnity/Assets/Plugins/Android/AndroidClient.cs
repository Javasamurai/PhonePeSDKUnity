using UnityEngine;

namespace PhonePaySDK
{
    public class AndroidClient : IClient
    {
        private AndroidJavaObject unityPlayer; 
        private AndroidJavaObject applicationContext;
        private AndroidJavaClass pluginClass;

        public override void Initialize(ENVIRONMENT environment, string merchantID, string appID)
        {
            unityPlayer = new AndroidJavaObject(Utils.UNITY_CLASS);
            pluginClass = new AndroidJavaClass(Utils.MAIN_CLASS);
            applicationContext = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            pluginClass.CallStatic("InitPhonePe", new object[] { applicationContext, (int) environment, merchantID, appID });
        }

        public override void StartTransaction(string merchantID, string salt, int saltIndex)
        {
            pluginClass.CallStatic("CreateTransaction", applicationContext, merchantID, salt, saltIndex);
        }

        public override void GetTransactionStatus()
        {
            
        }
    }
}