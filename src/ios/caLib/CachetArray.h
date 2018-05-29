//
//  CachetArray.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/18.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Signer.h"
#import "SignRule.h"
@interface CachetArray : NSObject
@property (nonatomic, strong) Signer *Signer;//签名人信息
@property (nonatomic, strong) SignRule *SignRule;//签名规则
@property (nonatomic, strong) NSString *AppName;//app名字
@property (nonatomic, strong) NSString *Image;//图片名字
@property (nonatomic, strong) NSString *IsTss;//是否开启时间戳"true"or"false"
@end
