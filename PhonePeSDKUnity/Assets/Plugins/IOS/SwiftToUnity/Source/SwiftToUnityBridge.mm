#import <UnityFramework/UnityFramework-Swift.h>
#import <UnityFramework/UnityFramework-Swift.h>
#import "UnityInterface.h"
#import "Foundation/Foundation.h"

extern "C"
{
char* cStringCopy(const char* string) {
    if (string == NULL) {
        return NULL;
    }
    
    size_t length = strlen(string) + 1;
    char* res = (char*) malloc(length);
    
    if (res != NULL) {
        memcpy(res, string, length);
    }
    
    return res;
}


void initialize(int environment, const char* merchantID, const char* _appID)
{
    NSString* appID = [NSString stringWithUTF8String: _appID];
    [[PhonePeSDKPlugin shared] Init :environment :appID];
}

bool isPhonePeInstalled()
{
    return [[PhonePeSDKPlugin shared] IsPhonePeInstalled];
}

void startTransaction(const char* _merchantID , const char* _salt, int _saltIndex,float amount)
{
    NSString* merchantID = [NSString stringWithUTF8String:_merchantID];
    NSString* salt = [NSString stringWithUTF8String:_salt];
    
    [[PhonePeSDKPlugin shared] StartTransaction :merchantID :salt :_saltIndex :amount];
}

int cAdd(int x, int y)
{
    return (int) [[PhonePeSDKPlugin shared] swiftAdd :x :y];
}
}
