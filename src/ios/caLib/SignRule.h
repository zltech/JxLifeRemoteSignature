//
//  SignRule.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/17.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KWRule.h"
#import "XYZRule.h"
@interface SignRule : NSObject
@property (nonatomic, strong) NSString * RuleType;//签名规则0关键字签名,1坐标,2规则，有服务器端配置
@property (nonatomic, strong) NSString * Tid;//规则号
@property (nonatomic, strong) KWRule * KWRule;//关键字签名
@property (nonatomic, strong) XYZRule * XYZRule;//坐标
@end
