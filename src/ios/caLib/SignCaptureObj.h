//
//  SignCaptureObj.h
//  SignAPI
//
//  Created by pingwanhui on 15/8/19.
//  Copyright (c) 2015年 pingwanhui. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "Signer.h"
#import "SignRule.h"
@interface SignCaptureObj : NSObject
typedef NS_ENUM(NSInteger, TimePos) {
    TIMRPOD_UP = 1,
    TIMRPOD_DOWN,
    TIMRPOD_RIGHT
};

@property (nonatomic, assign)int index;//"0-99"
@property (nonatomic, strong)Signer *signer;//签名人
@property (nonatomic, strong)SignRule *signRule;//签名规则
@property (nonatomic, strong)UIColor *strokeColor;//笔颜色
@property (nonatomic, assign)float strokeWidth;//笔粗细
@property (nonatomic, assign)CGSize SignImageSize;//获得图片大小
@property (nonatomic, assign)float scale;//照片清晰度
@property (nonatomic, assign)TimePos pos;//时间方向
@property (nonatomic, strong)NSString *timeFormat;//时间格式
@property (nonatomic, assign) BOOL isViewMyself;//是否自定义签名框
@property (nonatomic, copy) NSString *isTss;//是否加盖时间戳
@property (nonatomic, copy)  NSString *remindTitle;//单签名框中上部提醒文字
@property (nonatomic, assign)int titleSpanFromOffset;//单签名框中需要突出显示部分的起始位置
@property (nonatomic, assign)int titleSpanToOffset;//单签名框中需要突出显示部分的结束位置

@end
