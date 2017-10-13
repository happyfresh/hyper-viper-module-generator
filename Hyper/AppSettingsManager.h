//
//  AppSettingsManager.h
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AppSettingsManager : NSObject

extern NSString * const GeneratingSuccessNotification;
extern NSString * const GeneratingFailedNotification;

@property (nonatomic, copy) NSString *moduleName;
@property (nonatomic, copy) NSString *authorName;
@property (nonatomic, copy) NSString *authorOrganization;

+ (instancetype)sharedInstance;

@end
