using System;
using UnityEngine;

namespace PhonePaySDK
{
    public abstract class IClient
    {
        [SerializeField] PhonePeConfig phonePeConfig;
        public Action<bool> onTransactionDone;

        public abstract void Initialize(ENVIRONMENT environment, string merchantID, string appID);
        public abstract void StartTransaction(string merchantID, string salt, int saltIndex, float amount);
        public bool GetTransactionStatus(string status)
        {
            if (status == "SUCCESS")
            {
                onTransactionDone?.Invoke(true);
                return true;
            }
            onTransactionDone?.Invoke(false);
            return false;
        }

        public abstract bool IsPhonePeInstalled();
    }
}