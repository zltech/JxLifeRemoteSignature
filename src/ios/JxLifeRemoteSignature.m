/********* JxLifeRemoteSignature.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "JXCAFuntion.h"

@interface JxLifeRemoteSignature : CDVPlugin {
  // Member variables go here.
}

-(void)fetchTemplate:(CDVInvokedUrlCommand*)command;

-(void)onlySign:(CDVInvokedUrlCommand*)command;

-(void)submitSig:(CDVInvokedUrlCommand*)command;

@end

@implementation JxLifeRemoteSignature

//获取文件流
-(void)fetchTemplate:(CDVInvokedUrlCommand*)command{
    NSDictionary *commandDic = [command.arguments objectAtIndex:0];
    //模板获取成功 配置模板
    __block CDVPluginResult* pluginResult = nil;
    [[JXCAFuntion sharedJXCAFuntion] configTempModel:commandDic successBlock:^(NSString *successStr) {
        //封装返回对象
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:successStr];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } faileBlock:^(NSString *failedStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"获取文件模板失败"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

-(void)onlySign:(CDVInvokedUrlCommand*)command{
    NSDictionary *commandDic = [command.arguments objectAtIndex:0];
    __block CDVPluginResult* pluginResult = nil;
    if([[commandDic objectForKey:@"signType"] intValue] == 1){
        //普通签名
        [[JXCAFuntion sharedJXCAFuntion] showSignView:commandDic backBlock:^(UIImage *image) {
            if(image!= nil){
                NSData *imageData = UIImageJPEGRepresentation(image, 0.5);
                NSString *encodedImageStr = [imageData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
                [[JXCAFuntion sharedJXCAFuntion] uploadFile:commandDic imageData:imageData successBlock:^(NSDictionary *dic) {
                    //获取加密包
                    [[JXCAFuntion sharedJXCAFuntion] signEncode:^(NSString *successStr) {
                        NSLog(@"获取加密包成功");
                        [[JXCAFuntion sharedJXCAFuntion] sendEncodeStr:commandDic codeStr:successStr serviceUrlDic:dic imageString:encodedImageStr successBlock:^(NSString *successStr) {
                            NSLog(@"调用签名接口成功");
                            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:successStr];
                            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                        } faileBlock:^(NSString *failedStr) {
                            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"调用保存签名接口失败"];
                            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                        }];
                    } faileBlock:^(NSString *failedStr) {
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"获取加密包失败"];
                        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                    }];
                } faileBlock:^(NSString *failedStr) {
                    NSLog(@"%@",failedStr);
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"上传文件失败"];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }];
            }else{
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR  messageAsString:@"签名失败"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        }];
    }else{
        //38个字签名
        [[JXCAFuntion sharedJXCAFuntion] showSignMutiView:commandDic backBlock:^(UIImage *image) {
            if(image != nil){
                NSData *imageData = UIImageJPEGRepresentation(image, 0.5);
                NSString *encodedImageStr = [imageData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
                [[JXCAFuntion sharedJXCAFuntion] uploadFile:commandDic imageData:imageData successBlock:^(NSDictionary *dic) {
                    //获取加密包
                    [[JXCAFuntion sharedJXCAFuntion] signEncode:^(NSString *successStr) {
                        [[JXCAFuntion sharedJXCAFuntion] sendEncodeStr:commandDic codeStr:successStr serviceUrlDic:dic imageString:encodedImageStr successBlock:^(NSString *successStr) {
                            NSLog(@"调用签名接口成功");
                            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:successStr];
                            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                        } faileBlock:^(NSString *failedStr) {
                            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"调用保存签名接口失败"];
                            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                        }];
                    } faileBlock:^(NSString *failedStr) {
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"获取加密包失败"];
                        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                    }];
                } faileBlock:^(NSString *failedStr) {
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"上传文件失败"];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }];
            }else{
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR  messageAsString:@"38个字签名失败"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        }];
    }
}

//submitSig 生成签名加密包 并提交
-(void)submitSig :(CDVInvokedUrlCommand*)command{
    NSDictionary *commandDic = [command.arguments objectAtIndex:0];
    __block CDVPluginResult* pluginResult = nil;
    [[JXCAFuntion sharedJXCAFuntion] sendFinalInfo:commandDic successBlock:^(NSString *successStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Success"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } faileBlock:^(NSString *failedStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}


//保存文件到沙盒
- (BOOL)saveImage:(UIImage *)image {
    NSArray *paths =NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask,YES);
    
    
    NSString *filePath = [[paths objectAtIndex:0]stringByAppendingPathComponent:
                          [NSString stringWithFormat:@"sign.png"]];  // 保存文件的名称
    
    BOOL result =[UIImagePNGRepresentation(image)writeToFile:filePath   atomically:YES]; // 保存成功会返回YES
    
    return result;
}

@end
