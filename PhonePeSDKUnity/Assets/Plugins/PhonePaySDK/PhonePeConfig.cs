using UnityEngine;

namespace PhonePaySDK
{
    [CreateAssetMenu(fileName = "PhonePeConfig", menuName = "PhonePaySDK/PhonePeConfig")]
    public class PhonePeConfig : ScriptableObject
    {
        public string merchantId;
        public ENVIRONMENT environment;
        public string appID;
        public string salt;
        public int saltIndex = 1;
    }
    public enum ENVIRONMENT
    {
        RELEASE,

        SANDBOX,

        STAGE,

        STAGE_SIMULATOR
    }
}