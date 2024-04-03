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
#if UNITY_ANDROID
            _client = new AndroidClient();
            _client.Initialize(_config.environment, _config.merchantId, string.IsNullOrEmpty(_config.appID) ? null : _config.appID);
#elif  UNITY_IOS
            _client = new IosClient();
            _client.Initialize(_config.environment, _config.merchantId, string.IsNullOrEmpty(_config.appID) ? null : _config.appID);
#endif
        }
        
        public void StartTransaction()
        {
            _client.StartTransaction(_config.merchantId, _config.salt, _config.saltIndex);
        }
    }
    
}