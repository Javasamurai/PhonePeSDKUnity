using System.Runtime.InteropServices;

namespace PhonePaySDK
{
    #if UNITY_IOS
    public class IosClient : IClient
    {
        [DllImport("__Internal")]
        private static extern void initialize(int environment, string merchantID, string appID);
        
        public override void Initialize(ENVIRONMENT environment, string merchantID, string appID)
        {
            initialize((int) environment, merchantID, appID);
        }
        
        [DllImport("__Internal")]
        private static extern void startTransaction(string merchantID, string salt, int saltIndex, float amount);

        public override void StartTransaction(string merchantID, string salt, int saltIndex, float amount)
        {
             startTransaction(merchantID, salt, saltIndex, amount);
        }
        [DllImport("__Internal")]
        private static extern bool isPhonePeInstalled();

        public override bool IsPhonePeInstalled()
        {
            return isPhonePeInstalled();
        }
    }
    #endif
}