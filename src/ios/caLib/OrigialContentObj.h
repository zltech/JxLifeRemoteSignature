//
//  OrigialContentObj.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/19.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OrigialContentObj : NSObject
//原文数据类型0:PDF,1:HTML,2:XML.
typedef NS_ENUM(NSInteger, OrigialContentType) {
    CONTENT_TPYE_PDF = 0,
    CONTENT_TPYE_HTML,
    CONTENT_TPYE_XML
};
//加密算法
typedef NS_ENUM(NSInteger, EncType) {
    EncType_RSA = 0,
    EncType_SM2
};
@property (nonatomic, strong) NSString *businessId;//工单号
@property (nonatomic, strong) NSString *DocStyleTid;//对应服务端的xslt的id
@property (nonatomic, assign) OrigialContentType content_type;//原文模版格式
@property (nonatomic, assign) BOOL isCachet;//是否单位签章
@property (nonatomic, assign) EncType serverEncType;       //加密算法RSA,SM2
@end
