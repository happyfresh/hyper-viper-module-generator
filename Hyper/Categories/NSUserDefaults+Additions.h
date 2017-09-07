//
//  NSUserDefaults+Additions.h
//  HappyFresh
//
//  Created by Robby Cahyadi Hendro Saputro on 1/16/15.
//  Copyright (c) 2015 HappyFresh Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSUserDefaults (Additions)

- (void)setOrRemoveObject:(id)value forKey:(NSString *)defaultName synchronize:(BOOL)synchronize;

@end
