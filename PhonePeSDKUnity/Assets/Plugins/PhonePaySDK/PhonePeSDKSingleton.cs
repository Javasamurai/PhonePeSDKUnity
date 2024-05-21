using System;
using UnityEngine;

namespace PhonePaySDK
{
    public class PhonePeSDKSingleton : MonoBehaviour
    {
        [SerializeField] private PhonePeConfig _config;

        private static PhonePeSDKSingleton _instance;
        public static PhonePeSDKSingleton Instance => _instance;
        private IClient _client;

        private void Awake()
        {
            if (_instance == null)
            {
                _instance = this;
            }
            else
            {
                Destroy(gameObject);
            }

            Init();
        }

        private void Init()
        {
#if UNITY_EDITOR
    _client = new EditorClient();
    _client.Initialize(_config.environment, _config.merchantId, string.IsNullOrEmpty(_config.appID) ? null : _config.appID);
#elif UNITY_ANDROID
            _client = new AndroidClient();
            _client.Initialize(_config.environment, _config.merchantId, string.IsNullOrEmpty(_config.appID) ? null : _config.appID);
#elif UNITY_IOS
            _client = new IosClient();
            _client.Initialize(_config.environment, _config.merchantId, string.IsNullOrEmpty(_config.appID) ? null : _config.appID);
            _client.onTransactionDone += (status) =>
            {
                Debug.Log("Transaction status: " + status);
            };
#endif
        }
        
        public void OnTransactionDone(string status)
        {
            if (status == "SUCCESS")
            {
                Debug.Log("Transaction successful");
            }
            else
            {
                Debug.Log("Transaction failed");
            }
        }
        
        public void StartTransaction(float amount)
        {
            if (!_client.IsPhonePeInstalled())
            {
                Debug.LogError("PhonePe app not installed");
                return;
            }
            _client.StartTransaction(_config.merchantId, _config.salt, _config.saltIndex, amount);
        }

        private void OnDestroy()
        {
            _client.onTransactionDone = null;
        }
    }

    internal class EditorClient : IClient
    {
        public override void Initialize(ENVIRONMENT environment, string merchantID, string appID)
        {
            Debug.Log("EditorClient initialized");
        }

        public override void StartTransaction(string merchantID, string salt, int saltIndex, float amount)
        {
            Debug.Log("Transaction started");
        }

        public override bool IsPhonePeInstalled()
        {
            return true;
        }
    }
}