//
//  XYZRule.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/17.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface XYZRule : NSObject
@property (nonatomic, strong) NSString * Left;//左偏移量
@property (nonatomic, strong) NSString * Top;//上偏移量
@property (nonatomic, strong) NSString * Right;//右偏移量
@property (nonatomic, strong) NSString * Bottom;//下偏移量
@property (nonatomic, strong) NSString * Pageno;//在第几页签名
@property (nonatomic, strong) NSString * Unit;//单位，默认"pt"

@end
