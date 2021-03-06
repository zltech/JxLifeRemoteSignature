//
//  AFNHttpHelp.m
//  IosProject
//
//  Created by nwk on 16/8/9.
//  Copyright © 2016年 ZZ. All rights reserved.
//

#import "MAFNetworkingTool.h"
#import "AFNetworking.h"

#define NetWorkNotReachableErrorInfo @"网络连线错误，请检查网络连线设定！"

@interface MAFNetworkingTool()

@property (strong, nonatomic) NSMutableArray *operations;
@end

@implementation MAFNetworkingTool

static MAFNetworkingTool *afnHttp;

+ (instancetype)sharedAFNHttp{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        afnHttp = [[MAFNetworkingTool alloc] init];
    });
    return afnHttp;
}
NSString * getContentTypeForPath(NSURL *url){
    NSString *extension = [url pathExtension];
#ifdef __UTTYPE__
    NSString *UTI = (__bridge_transfer NSString *)UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, (__bridge CFStringRef)extension, NULL);
    NSString *contentType = (__bridge_transfer NSString *)UTTypeCopyPreferredTagWithClass((__bridge CFStringRef)UTI, kUTTagClassMIMEType);
    if (!contentType) {
        return @"application/octet-stream";
    } else {
        return contentType;
    }
#else
#pragma unused (extension)
    return @"application/octet-stream";
#endif
}


+ (void)POST:(NSString *)name parameters:(id)parameters successBlock:(void (^)(id responesObj))successBlock failedBlock:(void (^)(NSError *error))failedBlock{
//    NSString *netStatus = [MASessionTimeOut getNetStatus];
//    if ([MASessionTimeOut getNetStatus] == NetworkOfflineStatus || netStatus == NetworkDefaultStatus) {
//        NSError *error = [NSError errorWithDomain:NetWorkNotReachableErrorInfo code:NSURLErrorNotConnectedToInternet userInfo:nil];
//        failedBlock(error);
//        return;
//    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    // 设置超时时间
    [manager.requestSerializer willChangeValueForKey:@"timeoutInterval"];
    manager.requestSerializer.timeoutInterval = 30;
    [manager.requestSerializer didChangeValueForKey:@"timeoutInterval"];
    
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];

    [manager setResponseSerializer:[AFHTTPResponseSerializer serializer]];
    
    AFSecurityPolicy *securityPolicy = [AFSecurityPolicy defaultPolicy];
    //设置是否信任服务端返回的证书
    securityPolicy.allowInvalidCertificates = YES;
    securityPolicy.validatesDomainName = NO;
    manager.securityPolicy = securityPolicy;
    
    //根据HttpTag 查找 请求url
    NSString *urlStr = name;
    
    NSLog(@"请求路径：%@",urlStr);
    
    NSURLSessionDataTask *dataTask = [manager POST:urlStr parameters:parameters success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSDictionary *resultsDictionary = [NSJSONSerialization JSONObjectWithData:responseObject options:kNilOptions error:nil];
        if (successBlock) {
            successBlock(resultsDictionary);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failedBlock) {
            failedBlock(error);
        }
    }];
    
//    AFHTTPRequestSerializer *operation = [manager POST:urlStr parameters:parameters success:^(AFHTTPRequestOperation *operation, id responseObject) {
//        
//        NSDictionary *resultsDictionary = [NSJSONSerialization JSONObjectWithData:responseObject options:kNilOptions error:nil];
//        if (successBlock) {
//            successBlock(resultsDictionary);
//        }
//    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
//        if (failedBlock) {
//            failedBlock(error);
//        }
//    }];
    if (dataTask) {
        [[MAFNetworkingTool sharedAFNHttp].operations addObject:@{name:dataTask}];
    }
    
}
+ (void)GET:(NSString *)name parameters:(NSDictionary *)parameters successBlock:(void (^)(id responesObj))successBlock failedBlock:(void (^)(NSError *error))failedBlock{
//    NSString *netStatus = [MASessionTimeOut getNetStatus];
//    if ([MASessionTimeOut getNetStatus] == NetworkOfflineStatus || netStatus == NetworkDefaultStatus) {
//        NSError *error = [NSError errorWithDomain:NetWorkNotReachableErrorInfo code:NSURLErrorNotConnectedToInternet userInfo:nil];
//        failedBlock(error);
//        return;
//    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    // 设置超时时间
    [manager.requestSerializer willChangeValueForKey:@"timeoutInterval"];
    manager.requestSerializer.timeoutInterval = 10;
    [manager.requestSerializer didChangeValueForKey:@"timeoutInterval"];
    
    [manager setResponseSerializer:[AFHTTPResponseSerializer serializer]];
    
    AFSecurityPolicy *securityPolicy = [AFSecurityPolicy defaultPolicy];
    //设置是否信任服务端返回的证书
    securityPolicy.allowInvalidCertificates = YES;
    manager.securityPolicy = securityPolicy;
    
    //根据HttpTag 查找 请求url
    NSString *urlStr = name;
    
    NSURLSessionDataTask *dataTask = [manager GET:urlStr parameters:parameters success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSDictionary *resultsDictionary = [NSJSONSerialization JSONObjectWithData:responseObject options:kNilOptions error:nil];
        if (successBlock) {
            successBlock(resultsDictionary);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failedBlock) {
            failedBlock(error);
        }
    }];
    
//    AFHTTPRequestOperation *operation = [manager GET:urlStr parameters:parameters success:^(AFHTTPRequestOperation *operation, id responseObject) {
//        
//        NSDictionary *resultsDictionary = [NSJSONSerialization JSONObjectWithData:responseObject options:kNilOptions error:nil];
//        if (successBlock) {
//            successBlock(resultsDictionary);
//        }
//    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
//        if (failedBlock) {
//            failedBlock(error);
//        }
//    }];
    
    if (dataTask) {
        [[MAFNetworkingTool sharedAFNHttp].operations addObject:@{name:dataTask}];
    }
}
/**
 *  上传文件
 *  @param postUrl  请求路径（.do）
 *  @param filePath 本地文件路径
 */
+ (void)uploadWithPOST:(NSString *)postUrl filePath:(NSString *)filePath configDic:(NSMutableDictionary *)configDic imageData:(NSData *)imageData successBlock:(void (^)(NSDictionary *successDic))successBlock faileBlock:(void (^)(NSError *error))faileBlock{
    
    AFHTTPSessionManager *mgr = [AFHTTPSessionManager manager];
    
    // 设置超时时间
    [mgr.requestSerializer willChangeValueForKey:@"timeoutInterval"];
    mgr.requestSerializer.timeoutInterval = 10;
    [mgr.requestSerializer didChangeValueForKey:@"timeoutInterval"];
    
    //[mgr.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];

    [mgr setResponseSerializer:[AFHTTPResponseSerializer serializer]];
    
    NSString *url = [@"" stringByAppendingPathComponent:postUrl];
    
    [mgr POST:url parameters:configDic constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        
        //[formData appendPartWithFileURL:[NSURL fileURLWithPath:filePath] name:@"fileToUpload" error:nil];
         [formData appendPartWithFileData:imageData name:@"sign" fileName:@"sign.png"mimeType:@"image/png"];
    } success:^(NSURLSessionDataTask *task, id responseObject) {
        
        NSString *jsonStr = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSDictionary *dic = [self dictionaryWithJsonString:jsonStr];
        if (dic[@"data"] && successBlock) {
            successBlock([self dictionaryWithJsonString:[dic objectForKey:@"data"]]);
        }else{
            NSError *error = [NSError errorWithDomain:@"服务器返回异常" code:NSURLErrorBadServerResponse userInfo:nil];
            faileBlock(error);
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        if (faileBlock) {
            faileBlock(error);
        }
        NSLog(@"%@",error);
    }];
}


//上传文件

/**
 *  判断是否有网络
 *  @return 网络状态
 */
BOOL whetherNetworkWithOnline(){
    AFNetworkReachabilityManager *manager = [AFNetworkReachabilityManager sharedManager];
    //网络只有在startMonitoring完成后才可以使用检查网络状态
    [manager startMonitoring];  //开启网络监视器；
    
    [manager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
        
        switch (status) {
            case AFNetworkReachabilityStatusNotReachable:{
                NSLog(@"网络不通");
                break;
            }
            case AFNetworkReachabilityStatusReachableViaWiFi:{
                NSLog(@"网络通过WIFI连接");
                break;
            }
            case AFNetworkReachabilityStatusReachableViaWWAN:{
                NSLog(@"网络通过无线连接");
                break;
            }
            default:
                break;
        }
        
        NSLog(@"网络状态数字返回：%zd", status);
        NSLog(@"网络状态返回: %@", AFStringFromNetworkReachabilityStatus(status));
        
    }];
    
    return manager.networkReachabilityStatus == AFNetworkReachabilityStatusUnknown;
}

+ (void)cancleOperationWithHttpMark:(NSString *)name{
    if (afnHttp) {
        for (NSDictionary *dic in afnHttp.operations) {
            NSURLSessionDataTask *task = dic[name];
            [task cancel];
            NSLog(@"%@",dic[name]);
        }
    }
}
+ (void)cancelAllHttp
{
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager.operationQueue cancelAllOperations];
}


-(NSMutableArray *)operations{
    if (!_operations) {
        _operations = [NSMutableArray array];
    }
    
    return _operations;
}


+ (NSDictionary *)dictionaryWithJsonString:(NSString *)jsonString {
    if (jsonString == nil) {
        return nil;
    }
    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&err];
    if(err) {
        NSLog(@"json解析失败：%@",err);
        return nil;
    }
    NSLog(@"%@",dic);
    return dic;
}
@end
