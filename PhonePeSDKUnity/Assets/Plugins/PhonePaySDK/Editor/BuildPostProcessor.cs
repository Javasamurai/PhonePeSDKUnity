using System.IO;
using PhonePaySDK;
using UnityEditor;
using UnityEditor.Callbacks;
using UnityEditor.iOS.Xcode;

public class BuildPostProcessor
{
    public PhonePeConfig phonePeConfig;
    
    [PostProcessBuild]
    public static void OnPostProcessBuild(BuildTarget buildTarget, string path)
    {
        if (buildTarget == BuildTarget.iOS)
        {
            ModifyPlist(path, buildTarget);
        }
    }

    private static void ModifyPlist(string path, BuildTarget buildTarget)
    {
        // Get plist
        string plistPath = path + "/Info.plist";
        PlistDocument plist = new PlistDocument();
        plist.ReadFromFile(plistPath);
        PlistElementDict rootDict = plist.root;
        PlistElementArray plistElementArray = rootDict.CreateArray("LSApplicationQueriesSchemes");
        plistElementArray.AddString("ppemerchantsdkv1");
        plistElementArray.AddString("ppemerchantsdkv2");
        plistElementArray.AddString("ppemerchantsdkv3");
        plistElementArray.AddString("paytmmp");
        plistElementArray.AddString("gpay");
        rootDict.SetString("PhonePeAppId", "phonePeConfig.appID");
        
        File.WriteAllText(plistPath, plist.WriteToString());
    }
}
