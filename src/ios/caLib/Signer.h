//
//  Signer.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/17.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Signer : NSObject
@property (nonatomic, strong) NSString *UName;//姓名
@property (nonatomic, strong) NSString *IDNumber;//证据号
@property (nonatomic, strong) NSString *IDType;//证件类型，仅支持身份证类型0
@end
