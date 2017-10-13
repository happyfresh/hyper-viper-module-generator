//
//  TemplateIO.h
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TemplateIO : NSObject

//- (void)readFileFromTemplate:(NSString *)templateName thenWriteItToDirectory:(NSURL *)dir withModuleName:(NSString *)moduleName createPhysicalFolder:(BOOL)createFolder;

- (void)readFileFromTemplate:(NSString *)templateName selectedFiles:(NSArray *)selectedFiles thenWriteItToDirectory:(NSURL *)dir withModuleName:(NSString *)moduleName createPhysicalFolder:(BOOL)createFolder;

@end
