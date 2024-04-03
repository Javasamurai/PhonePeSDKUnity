using UnityEngine;

namespace PhonePaySDK
{
    public abstract class IClient
    {
        [SerializeField] PhonePeConfig phonePeConfig;

        public abstract void Initialize(ENVIRONMENT environment, string merchantID, string appID);
        public abstract void StartTransaction(string merchantID, string salt, int saltIndex);
        public abstract void GetTransactionStatus();
    }
}