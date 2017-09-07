//
//  NSUserDefaults+Additions.m
//  HappyFresh
//
//  Created by Robby Cahyadi Hendro Saputro on 1/16/15.
//  Copyright (c) 2015 HappyFresh Inc. All rights reserved.
//

#import "NSUserDefaults+Additions.h"

@implementation NSUserDefaults (Additions)

- (void)setOrRemoveObject:(id)value forKey:(NSString *)defaultName synchronize:(BOOL)synchronize {
    if (value == nil) {
        [self removeObjectForKey:defaultName];
    } else {
        [self setObject:value forKey:defaultName];
    }
    
    if (synchronize) {
        [self synchronize];
    }
}

@end
