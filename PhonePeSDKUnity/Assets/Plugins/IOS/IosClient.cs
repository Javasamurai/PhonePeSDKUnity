using System.Runtime.InteropServices;

namespace PhonePaySDK
{
    public class IosClient : IClient
    {
        #if UNITY_IOS
        [DllImport("__Internal")]
        private static extern void _initialize(int environment, string merchantID, string appID);

        [DllImport("__Internal")]
        private static extern void _startTransaction(string merchantID, string salt, int saltIndex);
        
        public override void Initialize(ENVIRONMENT environment, string merchantID, string appID)
        {
            _initialize((int) environment, merchantID, appID);
        }

        public override void StartTransaction(string merchantID, string salt, int saltIndex)
        {
            _startTransaction(merchantID, salt, saltIndex);
        }

        public override void GetTransactionStatus()
        {
            
        }
    }
}