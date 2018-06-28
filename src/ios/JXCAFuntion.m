//
//  AXACAFuntion.m
//  pluginApp
//
//  Created by 龚纯 on 2018/4/21.
//

#import "JXCAFuntion.h"
#import "OrigialContentObj.h"
#import "SignCaptureObj.h"
#import "CommentCaptureObj.h"
#import "MAFNetworkingTool.h"

typedef void (^GetCaImageAndEncode)(UIImage *image);
typedef void (^successBlock)(NSString *SuceessStr);
typedef void (^faileBlock)(NSString *FailedStr);

@interface JXCAFuntion()<CASignAPIDelegate>
{
    SignAPI *signApi;
    
    GetCaImageAndEncode getCaImageAndEncode;
    
    UIViewController *signController;
    
    UIViewController *massSignViewController;
    
    NSDictionary *templetDic;
    
    NSString *uuidStr;
}

@end

@implementation JXCAFuntion

static JXCAFuntion *caFuntion = nil;

+ (instancetype)sharedJXCAFuntion{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        caFuntion = [[JXCAFuntion alloc] init];
        [caFuntion setupSignApi];
    });
    
    return caFuntion;
}


- (void)setupSignApi{
#if TARGET_IPHONE_SIMULATOR//模拟器
#elif TARGET_OS_IPHONE//真机
    signApi = [[SignAPI alloc] init];
    signApi.screenLock = true;
    signApi.signScreenOrientation = false;
    signApi.signAPIDelegate = self;
#endif
    
}

//配置模板信息
- (void)configTempModel:(NSDictionary *)configTempInfo successBlock:(successBlock)successBlock faileBlock:(faileBlock)faileBlock{
    //设置渠道号
    [signApi setChannel:@"88888888"];
    uuidStr = [self uuidString];
    __block OrigialContentObj *origialObj = nil;
    NSMutableDictionary *dic = [self joinRequestParam:@"EsignTemplateAction" withOperation:@"queryPadTemplate" withDictionary:configTempInfo];
    [MAFNetworkingTool POST:@"https://design.jxlife.com.cn/airsign/process.action" parameters:dic successBlock:^(id responesObj) {
        NSDictionary *dic = (NSDictionary *)responesObj;
        templetDic = [self dictionaryWithJsonString:[dic objectForKey:@"data"]];
        origialObj = [[OrigialContentObj alloc] init];
        //设置工单号
//        [origialObj setBusinessId:@"888888"];
        [origialObj setBusinessId:uuidStr];
        //设置对应服务端的xslt的id
        [origialObj setDocStyleTid:@"123123"];
        //设置上传原文的格式
        [origialObj setContent_type:CONTENT_TPYE_HTML];
        //设置加密方法（需项目经理指导）
        [origialObj setServerEncType:EncType_SM2];
        //从沙盒取出原文并转化为data数据
        NSString *htmlStr= [templetDic objectForKey:@"templateHtml"];
        NSData *orgdata= [htmlStr dataUsingEncoding:NSUTF8StringEncoding];
        //封装返回对象
        NSString *dataString = [self joinReturnStr:templetDic];
        //3.配置表单信息,orgdata为原文数据
        int CLS = [signApi setOrigialContent:origialObj mydata:orgdata];
        if (CLS != 1) {
            faileBlock(@"falied");
            NSLog(@"模版配置失败");
        }else {
            successBlock(dataString);
            NSLog(@"模版配置成功");
        }
    } failedBlock:^(NSError *error) {
        NSLog(@"error");
    }];
}


//弹出普通签名框
- (void)showSignView:(NSDictionary *)configInfoDic backBlock:(void (^)(UIImage *image))resultBlock{
    getCaImageAndEncode = resultBlock;
    
    int res = [self configSignInfo:configInfoDic];
    int index = [[configInfoDic objectForKey:@"sortNo"] intValue];
    if(res == 1){
        
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        
        signController = [signApi showSignatureDialog:index];
        
        //设置modal样式
        [signController setModalTransitionStyle:UIModalTransitionStyleFlipHorizontal];
        
        [window.rootViewController presentViewController:signController animated:YES completion:nil];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"签名参数配置错误" message:nil delegate:self cancelButtonTitle:@"确认" otherButtonTitles:nil, nil];
        [alert show];
    }
}


//弹出38个字签名框
- (void)showSignMutiView:(NSDictionary *)configInfoDic backBlock:(void (^)(UIImage *image))resultBlock{
    getCaImageAndEncode = resultBlock;
    int res = [self configSignMutiInfo:configInfoDic];
    
    if(res == 1){
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        massSignViewController = [signApi showCommentDialog:0];
        //设置modal样式
        [massSignViewController setModalTransitionStyle:UIModalTransitionStyleCrossDissolve];
        [window.rootViewController presentViewController:massSignViewController animated:YES completion:nil];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"38个字参数参数配置错误" message:nil delegate:self cancelButtonTitle:@"确认" otherButtonTitles:nil, nil];
        [alert show];
    }
    
}

//ca取证
- (BOOL)imageCertification:(NSData *)imageData signCid:(int)s_id{
    int isPass = [signApi addEvidence:s_id contentData:imageData bioType:PHOTO_SIGNER_IDENTITY_CARD_FRONT];
    if (isPass==1) {
        NSLog(@"添加图片证据成功");
        return true;
    }else {
        NSLog(@"错误码：%d",isPass);
        return false;
    }
}

//获取加密包
-(void)signEncode:(void (^)(NSString *))successBlock faileBlock:(void (^)(NSString *))faileBlock{
    int enable = [signApi isReadyToUpload];
    if (enable==1) {
        NSString *str=[signApi genSignRequest];
        if (!str) {
            NSLog(@"打包数据为空");
            faileBlock(@"failed");
        }else {
            //获取ca加密传成功 发送到服务端
            successBlock(str);
        }
    }else{
        faileBlock(@"failed");
    }
}

///重置CA数据
- (void)resetCA
{
#if TARGET_IPHONE_SIMULATOR//模拟器
#elif TARGET_OS_IPHONE//真机
    if (signApi) {
        [signApi resetApi];
    }
#endif
}


/**
 *  代理方法：返回签名图片
 *
 *  @param signImage 签名图片
 */
- (void)didFinishedSign:(UIImage *)signImage{
    getCaImageAndEncode(signImage);
    [signController dismissViewControllerAnimated:YES completion:nil];
}

/**
 *  代理方法：返回批注图片
 *
 *  @param commentImage 批注图片
 */
- (void)didFinishedComment:(UIImage *)commentImage{
    getCaImageAndEncode(commentImage);
    [massSignViewController dismissViewControllerAnimated:YES completion:nil];
}

/**
 *  取消签名视图
 *
 *  @param signIndex 第几个签名
 */
- (void)cancelSign:(int)signIndex{
    [signController dismissViewControllerAnimated:YES completion:nil];
}

/**
 *  清除签名视图
 *
 *  @param signIndex 第几个签名
 */
- (void)clearSign:(int)signIndex{
    
}


//配置签名参数
-(int)configSignInfo:(NSDictionary *)dic{
    
    SignCaptureObj *signCaptureObj = [[SignCaptureObj alloc] init];
    //此index对应签名接口的showSignatureDialog:(int)index
    signCaptureObj.index = [[dic objectForKey:@"sortNo"] intValue];
    //签名人信息：1.姓名 2.证件类型 3.证件号码
    NSString *userNameKey = [dic objectForKey:@"divId"];
    NSDictionary *strdic1 = [templetDic objectForKey:@"esignObjectInfoMap"];
    signCaptureObj.signer.UName = [[strdic1 objectForKey:userNameKey] objectForKey:@"objectLabel"];
    
    signCaptureObj.signer.IDType = @"0";
    signCaptureObj.signer.IDNumber = @"320682199409060100";
    
    //签名规则：0关键字签名,1坐标,2Tid
    signCaptureObj.signRule.RuleType = @"0";
    
    NSString *strName = [[strdic1 objectForKey:userNameKey] objectForKey:@"objectLabel"];
    
    signCaptureObj.signRule.KWRule.KW = strName;
    //决定在第几页搜寻关键字
    signCaptureObj.signRule.KWRule.Pageno = @"1";
    //搜寻当页的第几个关键字
    signCaptureObj.signRule.KWRule.KWIndex = @"1";
    
    //以下这2个参数控制签名相对关键字的位置，pos决定偏移方向，kwoffset控制偏移量
    signCaptureObj.signRule.KWRule.KWOffset = @"10";
    signCaptureObj.signRule.KWRule.KWPos = @"3";
    
    //设置笔迹颜色
    [signCaptureObj setStrokeColor:[UIColor blackColor]];
    //设置笔迹粗细
    [signCaptureObj setStrokeWidth:8.0];
    //设置图片大小（按照实际签名图片计算）
    [signCaptureObj setSignImageSize:CGSizeMake(50, 500)];
    //设置图片的压缩比
    [signCaptureObj setScale:3.0];
    
    //设置时间标签的方向，枚举：上、下、右下
    [signCaptureObj setPos:TIMRPOD_DOWN];
    
    //设置时间标签格式，如果不想要时间标签，此处不给值
    [signCaptureObj setTimeFormat:@"yyyy-MM-dd HH:mm:ss"];
    //设置是否自定义签名框
    [signCaptureObj setIsViewMyself:false];
    //设置是否开启时间戳，对应服务器端配置
    [signCaptureObj setIsTss:@"false"];
    
    //调用配置签名信息接口
    int CLS=[signApi addSignCaptureObj:signCaptureObj];
    if (CLS != 1) {
        NSLog(@"error %d",CLS);
    }else {
        NSLog(@"签名配置成功");
    }
    return CLS;
}


//配置38个字签名参数
-(int)configSignMutiInfo:(NSDictionary *)dic{
    CommentCaptureObj* massSignCapObj = [[CommentCaptureObj alloc]init];
    //此index对应批注接口的showCommentDialog:(int)index
    massSignCapObj.index = [[dic objectForKey:@"sortNo"] intValue];
    //签名人信息：1.姓名 2.证件类型 3.证件号码
    massSignCapObj.signer.UName = @"风险提示语抄录";
    massSignCapObj.signer.IDNumber = @"342921199308301111";
    massSignCapObj.signer.IDType = @"0";
    //签名规则：0关键字签名,1坐标,2Tid
    massSignCapObj.signRule.RuleType = @"0";
    massSignCapObj.signRule.KWRule.KW = @"风险提示语抄录";
    //决定在第几页搜寻关键字
    massSignCapObj.signRule.KWRule.Pageno = @"0";
    //搜寻当页的第几个关键字
    massSignCapObj.signRule.KWRule.KWIndex = @"1";
    //以下这2个参数控制签名相对关键字的位置，pos决定偏移方向，kwoffset控制偏移量
    massSignCapObj.signRule.KWRule.KWOffset = @"100";
    massSignCapObj.signRule.KWRule.KWPos = @"3";
    
    //设置笔迹颜色
    [massSignCapObj setStrokeColor:[UIColor blackColor]];
    //设置笔迹粗细
    [massSignCapObj setStrokeWidth:6.0];
    //设置图片大小（按照实际签名图片计算）
    massSignCapObj.SignImageSize = CGSizeMake(150, 150);
    //设置图片的压缩比
    massSignCapObj.scale = 5.0;
    
    //设置时间标签的方向，枚举：上、下、右下
    massSignCapObj.timePos = 1;
    //设置时间标签格式，如果不想要时间标签，此处不给值
    massSignCapObj.timeFormat = @"yyyy-MM-dd HH:mm:ss";
    //设置批注文字
    //massSignCapObj.commitment = @"本人已经阅读保险条款，产品说明书和投保提示书，了解本产品的特点和保单利益的不确定性。";
    massSignCapObj.commitment = @"测试";
    //本人已经阅读保险条款，产品说明书和投保提示书，了解本产品的特点和保单利益的不确定性。
    
    //@"这样就没，问题咯这样，没有问题咯这样，没有问题。这样有问题咯这样，问题咯这样。";
    //设置生成图片每行最多多少个字
    massSignCapObj.lineMax = 12;
    //设置是否回显上一次签名文字
    massSignCapObj.isEcho = false;
    //设置是否开启时间戳，对应服务器端配置
    massSignCapObj.isTss = @"false";
    massSignCapObj.isWriteRight = true;
    //调用配置批注信息接口
    int CLS = [signApi addCommentCaptureObj:massSignCapObj];
    if (CLS != 1) {
        NSLog(@"error %d",CLS);
    }else {
        NSLog(@"签名配置成功");
    }
    return CLS;
}

//上传文件接口
-(void)uploadFile:(NSDictionary *)configDictionary imageData:(NSData *)imageData  successBlock:(void(^)(NSDictionary *))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock{
    //NSString *paths = [self imageURL];
    NSMutableDictionary *paramDic = [self joinUploadRequestParam:configDictionary];
    //拼接参数
    [MAFNetworkingTool uploadWithPOST:@"https://design.jxlife.com.cn/airsign/fileupdown/upload.action" filePath:@"" configDic:paramDic imageData:imageData successBlock:^(NSDictionary *successDic) {
        successBlock(successDic);
    } faileBlock:^(NSError *error) {
        faileBlock(@"上传失败");
    }];
}


//拼接签名完成的参数
-(void)sendEncodeStr:(NSDictionary *)configDic codeStr:(NSString *)codeStr serviceUrlDic:(NSDictionary *)serviceUrlDic imageString:(NSString *)imageString successBlock:(void (^)(NSString *successStr))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock{
    NSDictionary *allConfigDic = templetDic;
    //拼接请求参数 公共参数
    NSMutableDictionary *dicParam = [NSMutableDictionary dictionary];
    NSMutableArray *signImageArray = [NSMutableArray array];
    [dicParam setObject:[configDic objectForKey:@"businessType"] forKey:@"businessType"];
    [dicParam setObject:codeStr forKey:@"encryptData"];
    [dicParam setObject:uuidStr forKey:@"woNo"];//加密包流水号
    [dicParam setObject:[configDic objectForKey:@"signType"] forKey:@"signKind"];//签名类别
    [dicParam setObject:@"1" forKey:@"resource"];//签名来源
    NSMutableDictionary *testDic = [self jionSignImageDic:serviceUrlDic configDic:configDic];
    [signImageArray addObject:testDic];
    [dicParam setObject:signImageArray forKey:@"signImages"];//
    [dicParam setObject:[configDic objectForKey:@"relaId"] forKey:@"relaId"];//交易id
    [dicParam setObject:[configDic objectForKey:@"divId"] forKey:@"divId"];//外围系统中生成的用来唯一标示的签名对象
    
    NSMutableDictionary *finalDic = [self joinRequestParam:@"EsignImageAction" withOperation:@"saveImageInfoByPlug" withDictionary:dicParam];
    NSString *str = [self dictionaryToJson:finalDic];
    NSLog(@"%@",str);
    //拼接参数
    [MAFNetworkingTool POST:@"https://design.jxlife.com.cn/airsign/process.action" parameters:[self joinRequestParam:@"EsignImageAction" withOperation:@"saveImageInfoByPlug" withDictionary:dicParam] successBlock:^(id responesObj) {
        //分装返回参数
        NSMutableDictionary *finalDic = [NSMutableDictionary dictionary];
        NSMutableDictionary *paramDic = [NSMutableDictionary dictionary];
        NSMutableDictionary *childDic = [NSMutableDictionary dictionary];
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        
        NSString *divID = [configDic objectForKey:@"divId"];
        
        [dic setObject:@"1" forKey:@"imageKind"];
        [dic setObject:imageString forKey:@"imgBase64"];
        [dic setObject:@"" forKey:@"localPath"];
        [dic setObject:divID forKey:@"subdivid"];
        [childDic setObject:dic forKey:[NSString stringWithFormat:@"%@_1",divID]];
        [paramDic setObject:@"" forKey:@"reminder"];
        [paramDic setObject:@"" forKey:@"handleDate"];
        [paramDic setObject:childDic forKey:@"cordovaImgMap"];
        [finalDic setObject:@"true" forKey:@"isSuccess"];
        [finalDic setObject:[self dictionaryToJson:paramDic] forKey:@"data"];
        
        NSString *str = [self dictionaryToJson:finalDic];
        successBlock(str);
    } failedBlock:^(NSError *error) {
        faileBlock(@"failed");
    }];
    
}

//拼接所有签名完成的参数
-(void)sendFinalInfo:(NSDictionary *)finalDic successBlock:(void (^)(NSString *successStr))successBlock faileBlock:(void (^)(NSString *failedStr))faileBlock{
    NSMutableDictionary *paramDic = [NSMutableDictionary dictionary];
    [paramDic setObject:[finalDic objectForKey:@"relaId"] forKey:@"relaId"];
    [paramDic setObject:[finalDic objectForKey:@"businessType"] forKey:@"businessType"];
    [paramDic setObject:[finalDic objectForKey:@"smsCode"] forKey:@"smsCode"];
    [paramDic setObject:[finalDic objectForKey:@"agentMobile"] forKey:@"agentMobile"];
    [paramDic setObject:[finalDic objectForKey:@"smstype"] forKey:@"smstype"];
    [paramDic setObject:[finalDic objectForKey:@"ruleFun"] forKey:@"ruleFun"];
    [paramDic setObject:@"2" forKey:@"smsBusinessType"];
    
    NSMutableDictionary *final = [self joinRequestParam:@"EsigntodoAction" withOperation:@"completeSign" withDictionary:paramDic];
    NSString *str = [self dictionaryToJson:final];
    NSLog(@"%@",str);
    //拼接请求参数
    [MAFNetworkingTool POST:@"https://design.jxlife.com.cn/airsign/process.action" parameters:final successBlock:^(id responesObj) {
        if([[responesObj objectForKey:@"msgCode"] isEqualToString:@"000000"]){
            
            NSMutableDictionary *dic = [NSMutableDictionary dictionary];
            [dic setObject:@"提交成功" forKey:@"data"];
            [dic setObject:@"true" forKey:@"isSuccess"];
            NSString *str = [self dictionaryToJson:dic];
            successBlock(str);
        }else{
            successBlock(@"failed");
        }
    } failedBlock:^(NSError *error) {
        faileBlock(@"failed");
    }];
}

//拼接获取模板接口参数
-(NSMutableDictionary *)joinRequestParam:(NSString *)beanId withOperation:(NSString *)operation withDictionary:(id)paramDic{
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    [dic setValue:beanId forKey:@"beanId"];
    [dic setValue:operation forKey:@"operation"];
    [dic setValue:@"" forKey:@"source"];
    [dic setValue:@"" forKey:@"usercode"];
    [dic setValue:@"" forKey:@"password"];
    [dic setValue:paramDic forKey:@"data"];
    return dic;
}

//拼接上传文件的接口参数
-(NSMutableDictionary *)joinUploadRequestParam:(NSDictionary *)dic{
    NSDictionary *newDic = templetDic;
    NSMutableDictionary *paramDic = [NSMutableDictionary dictionary];
    //dic[@"category"] = @"syncUp";
    paramDic[@"initType"] = @"commonFile";
    paramDic[@"filetype"] = @"image/png";
    paramDic[@"businesstype"] = @"WPE_LEFORM";
    paramDic[@"esignformid"] = @"abbc582c-a5a1-49f2-b38b-0a454c1b5164";
    return paramDic;
}

//拼接SignImage参数
-(NSMutableDictionary *)jionSignImageDic:(NSDictionary *)urlDic  configDic:(NSDictionary *)configDic{
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    
    
    NSString *userNameKey = [configDic objectForKey:@"divId"];
    NSDictionary *strdic1 = [templetDic objectForKey:@"esignObjectInfoMap"];
    NSDictionary *newDic = [[[strdic1 objectForKey:userNameKey] objectForKey:@"deesignImageList"] lastObject];
    dic[@"id"] = @"";
    dic[@"sortNo"] = @"";
    dic[@"imagetype"] = [newDic objectForKey:@"imageType"];
    dic[@"imagekind"] = [newDic objectForKey:@"imageKind"];
    dic[@"subdivid"] = @"";
    dic[@"status"] = @"";
    dic[@"remotePath"] = [urlDic objectForKey:@"sign.png"];
    dic[@"localPath"] = @"";
    return dic;
}

-(NSDictionary *)dictionaryWithJsonString:(NSString *)jsonString
{
    if (jsonString == nil) {
        return nil;
    }
    
    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                                                        options:NSJSONReadingMutableContainers
                                                          error:&err];
    if(err)
    {
        NSLog(@"json解析失败：%@",err);
        return nil;
    }
    return dic;
}

-(NSString*)dictionaryToJson:(NSDictionary *)dic

{
    
    NSError *parseError = nil;
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:&parseError];
    
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
}

//获取设备UUID
-(NSString *)getUUID{
    NSString *uuidString = [UIDevice currentDevice].identifierForVendor.UUIDString;
    return uuidString;
}

//读取图片路径
-(NSString *)imageURL{
    NSString *url=[NSString stringWithFormat:@"%@/Documents/%@.png",NSHomeDirectory(),@"sign"];
    return url;
}


//封装返回对象
-(NSString *)joinReturnStr:(NSDictionary *)dic{
    NSMutableDictionary *returnDic = [NSMutableDictionary dictionary];
    NSMutableDictionary *childDic = [NSMutableDictionary dictionary];
    [childDic setObject:[dic objectForKey:@"templateHtml"] forKey:@"templateHtml"];
    [childDic setObject:[dic objectForKey:@"agentMobile"] forKey:@"agentMobile"];
    [childDic setObject:[dic objectForKey:@"handleDate"] forKey:@"handleDate"];
    [childDic setObject:[dic objectForKey:@"signStatus"] forKey:@"signStatus"];
    [childDic setObject:[dic objectForKey:@"reminder"] forKey:@"reminder"];
    [childDic setObject:[dic objectForKey:@"esignObjectInfoMap"] forKey:@"signatureSateAndImgsMap"];
    NSString *str = [self dictionaryToJson:childDic];
    [returnDic setObject:str forKey:@"data"];
    [returnDic setObject:@"true" forKey:@"isSuccess"];
    
    NSString *finalStr = [self dictionaryToJson:returnDic];
    return finalStr;
}


- (NSString *)uuidString
{
    CFUUIDRef uuid_ref = CFUUIDCreate(NULL);
    CFStringRef uuid_string_ref= CFUUIDCreateString(NULL, uuid_ref);
    NSString *uuid = [NSString stringWithString:(__bridge NSString *)uuid_string_ref];
    CFRelease(uuid_ref);
    CFRelease(uuid_string_ref);
    return [uuid lowercaseString];
}
@end

