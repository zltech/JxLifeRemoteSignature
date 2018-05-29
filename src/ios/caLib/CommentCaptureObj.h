//
//  CommentCaptureObj.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/26.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "Signer.h"
#import "SignRule.h"
@interface CommentCaptureObj : NSObject
@property (nonatomic, assign) int index;//第几个批注；0-99
@property (nonatomic, strong) Signer *signer;//签名人
@property (nonatomic, strong) SignRule *signRule;//签名规则
@property (nonatomic, strong) UIColor *strokeColor;//笔颜色
@property (nonatomic, assign) float strokeWidth;//笔粗细
@property (nonatomic, assign) CGSize SignImageSize;//获得图片大小
@property (nonatomic, assign) float scale;//照片清晰度
@property (nonatomic, assign) int timePos;//时间方向
@property (nonatomic, strong) NSString *timeFormat;//时间位置
@property (nonatomic, strong) NSString *commitment;//设置批注签字底纹内容。
@property (nonatomic, assign) int lineMax;//生成签名图片一行多少个字
@property (nonatomic, assign) BOOL isEcho;//确认签名后是否回显签名内容
@property (nonatomic, assign) BOOL isWriteRight;//是否开启文字识别
@property (nonatomic, copy) NSString *isTss;//是否加盖时间戳"true"or"false"


@end
