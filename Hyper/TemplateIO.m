//
//  TemplateIO.m
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import "TemplateIO.h"
#import "FileManager.h"
#import "AppSettingsManager.h"

NSString *const ModuleNameConstant = @"$HyperModuleName";
NSString *const AuthorNameConstant = @"$HyperAuthorName";
NSString *const AuthorOrganizationonstant = @"$HyperAuthorOrganization";
NSString *const CreatedDateConstant = @"$HyperCreatedDate";
NSString *const CreatedYearConstant = @"$HyperCreatedYear";

@interface TemplateIO ()

@property (nonatomic) FileManager *fileManager;
@property (nonatomic) AppSettingsManager *settingsManager;

@end

@implementation TemplateIO

#pragma mark - Construct

- (FileManager *)fileManager {
    if (_fileManager == nil) {
        _fileManager = [FileManager sharedInstance];
    }
    return _fileManager;
}

- (AppSettingsManager *)settingsManager {
    if (_settingsManager == nil) {
        _settingsManager = [AppSettingsManager new];
    }
    return _settingsManager;
}

- (void)readFileFromTemplate:(NSString *)templateName thenWriteItToDirectory:(NSURL *)dir withModuleName:(NSString *)moduleName createPhysicalFolder:(BOOL)createFolder {
    
    if (createFolder) {
        dir = [dir URLByAppendingPathComponent:moduleName];
        [self.fileManager createFolderAtPath:dir.path overWrite:YES];
    }
    
    
    NSURL *url = [self.fileManager.templateDir URLByAppendingPathComponent:templateName];
    NSArray *templateFiles = [self.fileManager readContentOfDirectoryWithURL:url];

    for (NSURL *url in templateFiles) {
        NSError *error;
        NSString *templateContent = [[NSString alloc]
                                         initWithContentsOfURL:url
                                         encoding:NSUTF8StringEncoding
                                         error:&error];
        
        if (templateContent == nil) {
            // an error occurred
            NSLog(@"Error reading file at %@\n%@",
                  url, [error localizedFailureReason]);
            // implementation continues ...
            [[NSNotificationCenter defaultCenter] postNotificationName:GeneratingFailedNotification object:[error localizedDescription]];
        } else {
            templateContent = [self parseTemplate:templateContent];
            NSDictionary *urlDictionary = [url resourceValuesForKeys:@[NSURLLocalizedNameKey] error:nil];
            NSString *fileName = urlDictionary[@"NSURLLocalizedNameKey"];
            
            fileName = [fileName stringByReplacingOccurrencesOfString:@"$HyperModuleName" withString:moduleName];
            [self write:templateContent toFileWithURL:[dir URLByAppendingPathComponent:fileName]];
        }
    }
}

- (void)write:(NSString *)content toFileWithURL:(NSURL *)saveTarget {
    NSError *error;
    BOOL ok = [content writeToURL:saveTarget atomically:YES
                        encoding:NSUTF8StringEncoding error:&error];
    if (!ok) {
        // an error occurred
        NSLog(@"Error writing file at %@\n%@",
              saveTarget, [error localizedFailureReason]);
        // implementation continues ...
        [[NSNotificationCenter defaultCenter] postNotificationName:GeneratingFailedNotification object:[error localizedDescription]];
    } else {
        [[NSNotificationCenter defaultCenter] postNotificationName:GeneratingSuccessNotification object:nil];
    }
}

- (NSString *)parseTemplate:(NSString *)template {
    NSString *parsedTemplate = template;
    
    parsedTemplate = [parsedTemplate stringByReplacingOccurrencesOfString:ModuleNameConstant withString:[self.settingsManager moduleName]];
    
    parsedTemplate = [parsedTemplate stringByReplacingOccurrencesOfString:AuthorNameConstant withString:[self.settingsManager authorName]];
    
    parsedTemplate = [parsedTemplate stringByReplacingOccurrencesOfString:AuthorOrganizationonstant withString:[self.settingsManager authorOrganization]];
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    df.dateFormat = @"M/d/YY";
    NSString *todayDate = [df stringFromDate:[NSDate date]];
    
    parsedTemplate = [parsedTemplate stringByReplacingOccurrencesOfString:CreatedDateConstant withString:todayDate];
    
    df.dateFormat = @"YYYY";
    NSString *todayYear = [df stringFromDate:[NSDate date]];
    
    parsedTemplate = [parsedTemplate stringByReplacingOccurrencesOfString:CreatedYearConstant withString:todayYear];
    
    return parsedTemplate;
}

@end
