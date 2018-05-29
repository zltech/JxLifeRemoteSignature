//
//  OCRCaptureObj.h
//  bjcaHandwriteLib
//
//  Created by BJCA on 16/6/20.
//
//

#import <Foundation/Foundation.h>

@interface OCRCaptureObj : NSObject

typedef NS_ENUM(NSInteger, languageType) {
    CHS = 1,
    CHT,
    ENG
};

@property (nonatomic, copy)  NSString *text;//待校验内容（姓名）

@property (nonatomic, copy)  NSString *IPAddress;//服务器地址

@property (nonatomic, copy)  NSString *appID;//应用ID

@property (nonatomic, copy)  NSString *serviceID;//渠道号

@property (nonatomic, assign) int resolution;//相似度，默认为0

@property (nonatomic, assign) languageType language;//语言，默认为中文简体1，中文繁体2，英文3

@property (nonatomic, assign) int count;//返回候选的数目,默认为5

@end
