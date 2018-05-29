//
//  SignAPI.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/17.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "OrigialContentObj.h"
#import "SignCaptureObj.h"
#import "CachetArray.h"
#import "CommentCaptureObj.h"
#import "OCRCaptureObj.h"

@protocol CASignAPIDelegate;
@interface SignAPI : NSObject

/**
 *  SignAPI代理;返回签名和批注图片时使用。
 */
@property (nonatomic, assign) id<CASignAPIDelegate>signAPIDelegate;
@property (nonatomic) BOOL screenLock;//全局设置是否锁定屏幕旋转，false不锁，true锁。
@property (nonatomic) BOOL signScreenOrientation;//screenLock==true时生效，强制签名页方向，false横屏，true竖屏
/**
 *  证据类型
 */
typedef NS_ENUM(NSInteger, BioTypeEnum) {
    /**
     *  签名人居民身份证正面
     */
    PHOTO_SIGNER_IDENTITY_CARD_FRONT,
    /**
     *  签名人居民身份证背面
     */
    PHOTO_SIGNER_IDENTITY_CARD_BACK,
    /**
     *  签名人复述录音
     */
    SOUND_SIGNER_RETELL,
    /**
     *  签名人自定义录音
     */
    SOUND_SIGNER_OTHER,
};

/**
 *  设置渠道号
 *
 *  @param channel 渠道号
 *
 *  @return 错误码
 */
- (int)setChannel:(NSString *)channel;

/**
 *  设置模版信息
 *
 *  @param template_type 模版属性
 *  @param data          原文数据
 *
 *  @return 错误码
 */
- (int)setOrigialContent:(OrigialContentObj *)template_type mydata :(NSData *)data;

/**
 *  添加单人签名信息
 *
 *  @param captureObj 配置单签名属性
 *
 *  @return 错误码
 */
- (int)addSignCaptureObj:(SignCaptureObj *)captureObj;

/**
 *  添加批注签字信息
 *
 *  @param captureObj 配置批注属性
 *
 *  @return 错误码
 */
- (int)addCommentCaptureObj:(CommentCaptureObj *)captureObj;

/**
 *  展示签名框
 *
 *  @param index 第几个签名(0-99)
 *
 *  @return 签名视图
 */
- (UIViewController *)showSignatureDialog:(int)index;

/**
 *  展示批注签字框
 *
 *  @param index 第几个批注(0-99)
 *
 *  @return 批注视图
 */
- (UIViewController *)showCommentDialog:(int)index;

/**
 *  清除签名画布
 */
- (void)clearScreen;

/**
 *  签名确认
 *
 *  @return 签名图片
 */
- (void)saveScreen;

/**
 *  取消签名
 */
- (void)cancleScreen;

/**
 *  添加证据
 *
 *  @param signIndex 属于第几个签名(0-99)
 *  @param data      证据数据
 *  @param type      证据类型
 *
 *  @return 错误码
 */
- (int)addEvidence:(int)signIndex contentData:(NSData *)data bioType:(BioTypeEnum)type;

/**
 *  添加证据hash
 *
 *  @param signIndex 属于第几个签名(0-99)
 *  @param data      证据数据
 *  @param type      证据类型
 *
 *  @return 错误码
 */
- (int)addEvidenceHash:(int)signIndex contentHash:(NSString *)hash bioType:(BioTypeEnum)type;

/**
 *  添加单位签章
 *
 *  @param cachet 单位签章属性
 *
 *  @return 错误码
 */
- (int)addChachetObj:(CachetArray *)cachet;

/**
 *  添加附件(生成pdf后面的图片)
 *
 *  @param index 第几张图片(0-99)
 *  @param data  图片数据
 *  @param type  证据类型
 *
 *  @return 错误码
 */
- (int)addPhotoFileWithIndex:(int)index contentUtf8:(NSData *)data bioType:(BioTypeEnum)type;

/**
 *  是否有打包数据
 *
 *  @return 错误码
 */
- (int)isReadyToUpload;

/**
 *  获取上传服务端的加密请求包
 *
 *  @return 加密数据
 */
- (NSString *)genSignRequest;

/**
 *  获取版本号信息
 *
 *  @return 版本号
 */
- (NSString *)getVersionNumber;

/**
 *  删除所有数据
 */
- (void)resetApi;

/**
 *  缓存数据
 *
 *  @param sessionId 缓存数据ID
 *  @param key       对称加密密钥
 *
 *  @return 错误码
 */
- (int)saveCacheDataWithSessionId:(NSString *)sessionId symmetryKey:(NSString *)key;

/**
 *  恢复缓存数据
 *
 *  @param sessionId 恢复缓存数据ID
 *  @param key       对称加密密钥
 *
 *  @return 错误码
 */
- (int)readCacheDataWithSessionId:(NSString *)sessionId symmetryKey:(NSString *)key;

/**
 *  删除缓存数据
 *
 *  @param sessionId 删除缓存数据ID
 *
 *  @return 错误码
 */
- (int)deleteCacheDataWithSessionId:(NSString *)sessionId;

/**
 *  模糊删除缓存数据
 *
 *  @param fuzzyId 模糊ID
 *
 *  @return 错误码
 */
- (int)fuzzyDeleteCacheDataWithFuzzyId:(NSString *)fuzzyId;

/**
 *  判断是否存在此缓存
 *
 *  @param sessionId 缓存ID
 *
 *  @return 错误码
 */
- (int)hasBufferedWithSessionId:(NSString *)sessionId;

/**
 *  识别模块（识别模块默认关闭,需要内部手动开启）
 *  @param ocr 识别配置对象
 *
 *  @return 0识别成功，1识别识别
 */
- (int)startOCR: (OCRCaptureObj *) ocr;


@end

@protocol CASignAPIDelegate <NSObject>

/**
 *  代理方法：返回签名图片
 *
 *  @param signImage 签名图片
 */
- (void)didFinishedSign:(UIImage *)signImage;

/**
 *  代理方法：返回批注图片
 *
 *  @param commentImage 批注图片
 */
- (void)didFinishedComment:(UIImage *)commentImage;

/**
 *  取消签名视图
 *
 *  @param signIndex 第几个签名
 */
- (void)cancelSign:(int)signIndex;

/**
 *  清除签名视图
 *
 *  @param signIndex 第几个签名
 */
- (void)clearSign:(int)signIndex;
@end
