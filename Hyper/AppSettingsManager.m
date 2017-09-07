//
//  AppSettingsManager.m
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import "AppSettingsManager.h"
#import "NSUserDefaults+Additions.h"

NSString * const GeneratingSuccessNotification = @"GeneratingSuccessNotification";
NSString * const GeneratingFailedNotification = @"GeneratingFailedNotification";

@implementation AppSettingsManager

@synthesize moduleName = _moduleName;
@synthesize authorName = _authorName;
@synthesize authorOrganization = _authorOrganization;

- (void)setModuleName:(NSString *)moduleName {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setOrRemoveObject:moduleName forKey:@"moduleName" synchronize:YES];
}

- (NSString *)moduleName {
    NSString *savedModuleName = [[NSUserDefaults standardUserDefaults] objectForKey:@"moduleName"];
    return savedModuleName.length > 0 ? savedModuleName : @"Hyper Module Generator";
}

- (void)setAuthorName:(NSString *)authorName {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setOrRemoveObject:authorName forKey:@"authorName" synchronize:YES];
}

- (NSString *)authorName {
    NSString *savedAuthorName = [[NSUserDefaults standardUserDefaults] objectForKey:@"authorName"];
    return savedAuthorName.length > 0 ? savedAuthorName : @"Hyper Module Generator";
}

- (void)setAuthorOrganization:(NSString *)authorOrganization {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setOrRemoveObject:authorOrganization forKey:@"authorOrganization" synchronize:YES];
}

- (NSString *)authorOrganization {
    NSString *savedAuthorOrganization = [[NSUserDefaults standardUserDefaults] objectForKey:@"authorOrganization"];
    return savedAuthorOrganization.length > 0 ? savedAuthorOrganization : @"HappyFresh Inc";
}

@end
