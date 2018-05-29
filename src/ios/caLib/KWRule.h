//
//  KWRule.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/17.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface KWRule : NSObject
@property (nonatomic, strong) NSString * KW;//关键字
@property (nonatomic, strong) NSString * KWPos;//关键字方向 1:Center，2:lower，3:right，4:lower-right
@property (nonatomic, strong) NSString * KWOffset;//对应关键字方向偏移
@property (nonatomic, strong) NSString * Pageno;//第几页查找关键字
@property (nonatomic, strong) NSString * XOffset;//X轴偏移量
@property (nonatomic, strong) NSString * YOffset;//Y轴偏移量
@property (nonatomic, strong) NSString * KWIndex;//第几个关键字
@end
