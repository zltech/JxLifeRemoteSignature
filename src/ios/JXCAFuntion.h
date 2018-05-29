//
//  AXACAFuntion.h
//  pluginApp
//
//  Created by 龚纯 on 2018/4/21.
//

#import <Foundation/Foundation.h>
#import "SignAPI.h"

@interface JXCAFuntion : NSObject

+ (instancetype)sharedJXCAFuntion;

//配置模板参数
- (void)configTempModel:(NSDictionary *)configTempInfo successBlock:(void (^)(NSString *successStr))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock;
//弹出签名框
- (void)showSignView:(NSDictionary *)configInfoDic backBlock:(void (^)(UIImage *image))resultBlock;
//弹出风险校验签字框
- (void)showSignMutiView:(NSDictionary *)configInfoDic backBlock:(void (^)(UIImage *image))resultBlock;

//添加图片证据
- (BOOL)imageCertification:(NSData *)imageData signCid:(int)s_id;
//获取ca密文
- (void)signEncode:(void (^)(NSString *successStr))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock;

//重置caApi
- (void)resetCA;

//调用吉祥接口传加密包
-(void)sendEncodeStr:(NSDictionary *)configDic codeStr:(NSString *)codeStr serviceUrlDic:(NSDictionary *)serviceUrlDic imageString:(NSString *)imageString successBlock:(void (^)(NSString *successStr))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock;

//调用吉祥签名完成接口
-(void)sendFinalInfo:(NSDictionary *)finalDic successBlock:(void (^)(NSString *successStr))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock;

//调用吉祥上传文件接口
-(void)uploadFile:(NSDictionary *)configDictionary imageData:(NSData *)imageData  successBlock:(void(^)(NSDictionary *))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock;
@end
