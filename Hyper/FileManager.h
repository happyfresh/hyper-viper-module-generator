//
//  FileManager.h
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString * const CreateDocumentsFolderSuccess;
extern NSString * const CreateDocumentsFolderFailed;

@interface FileManager : NSObject

@property (nonatomic, retain) NSURL *templateDir;

+ (instancetype)sharedInstance;
- (BOOL)createFolderAtPath:(NSString *)path;
- (BOOL)createFolderAtPath:(NSString *)path overWrite:(BOOL)overwrite;
- (void)createTemplatesFolder;
- (NSArray *)styleNames;
- (NSArray *)templateNames;
- (NSArray *)readContentOfDirectoryWithURL:(NSURL *)url ;
- (void)setSelectedPlatform:(NSString *)selectedPlatform;
- (void)setSelectedStyle:(NSString *)selectedStyle;

@end
