using System;
using PhonePaySDK;
using UnityEngine;
using UnityEngine.UI;

namespace DefaultNamespace
{
    public class TestUI : MonoBehaviour
    {
        [SerializeField] Button startButton;

        private void Awake()
        {
            startButton.onClick.AddListener(startTransaction);
        }
        
        private void startTransaction()
        {
            Debug.Log("Transaction started!");
            PhonePeSDKSingleton.Instance.StartTransaction();
        }
    }
}