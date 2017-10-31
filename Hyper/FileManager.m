//
//  FileManager.m
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import "FileManager.h"

NSString * const CreateDocumentsFolderSuccess = @"CreateDocumentsFolderSuccess";
NSString * const CreateDocumentsFolderFailed = @"CreateDocumentsFolderFailed";

@interface FileManager()
@property (nonatomic) NSString *selectedPlatform;
@property (nonatomic) NSString *selectedStyle;
@property (nonatomic) NSString *dir;
@end

@implementation FileManager

+ (instancetype)sharedInstance {
    static id sharedInstance = nil;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[FileManager alloc] init];
    });
    
    return sharedInstance;
}

- (instancetype)init {
    NSArray* theDirs = [[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory
                                                              inDomains:NSUserDomainMask];
    if ([theDirs count] > 0)
    {
        NSURL* documentDir = (NSURL*)[theDirs objectAtIndex:0];
        NSDictionary *info = [[NSBundle mainBundle] infoDictionary];
        NSString *bundleName = [NSString stringWithFormat:@"%@", [info objectForKey:@"CFBundleName"]];
        self.templateDir = [[documentDir URLByAppendingPathComponent:bundleName]
                            URLByAppendingPathComponent:@"Templates"];
    }
    
    return self;
}

- (void)setSelectedPlatform:(NSString *)selectedPlatform {
    _dir = @"Templates/";
    _selectedPlatform = selectedPlatform;
    _dir = [_dir stringByAppendingString:_selectedPlatform];
    
    NSArray* theDirs = [[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory
                                                              inDomains:NSUserDomainMask];
    if ([theDirs count] > 0)
    {
        NSURL* documentDir = (NSURL*)[theDirs objectAtIndex:0];
        NSDictionary *info = [[NSBundle mainBundle] infoDictionary];
        NSString *bundleName = [NSString stringWithFormat:@"%@", [info objectForKey:@"CFBundleName"]];
        self.templateDir = [[documentDir URLByAppendingPathComponent:bundleName]
                            URLByAppendingPathComponent:_dir];
    }
}

-(void)setSelectedStyle:(NSString *)selectedStyle {
    _selectedStyle = selectedStyle;
    _dir = [[_dir stringByAppendingString:@"/" ] stringByAppendingString:_selectedStyle];
    
    NSArray* theDirs = [[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory
                                                              inDomains:NSUserDomainMask];
    if ([theDirs count] > 0)
    {
        NSURL* documentDir = (NSURL*)[theDirs objectAtIndex:0];
        NSDictionary *info = [[NSBundle mainBundle] infoDictionary];
        NSString *bundleName = [NSString stringWithFormat:@"%@", [info objectForKey:@"CFBundleName"]];
        self.templateDir = [[documentDir URLByAppendingPathComponent:bundleName]
                            URLByAppendingPathComponent:_dir];
    }
}


- (BOOL)createFolderAtPath:(NSString *)path overWrite:(BOOL)overwrite {
    BOOL isDir;
    NSFileManager *fileManager= [NSFileManager defaultManager];
    NSError *error = nil;
    if (overwrite) {
        if([fileManager fileExistsAtPath:path isDirectory:&isDir]) {
            if ([fileManager removeItemAtPath:path error:NULL]) {
                if(![fileManager createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:NULL]) {
                    return NO;
                }
            }
        } else {
            if(![fileManager createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error]) {
                NSLog(@"Error: Create directoy at path failed %@", error);
                return NO;
            }
        }
    } else {
        if(![fileManager fileExistsAtPath:path isDirectory:&isDir]) {
            if(![fileManager createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error]) {
                NSLog(@"Error: Create directoy at path failed %@", error);
                return NO;
            }
        }
    }
    return YES;
}

- (BOOL)createFolderAtPath:(NSString *)path {
    return [self createFolderAtPath:path overWrite:NO];
}

- (void)createTemplatesFolder {

    NSURL *localTemplateDir = [[NSBundle mainBundle] URLForResource:@"Templates" withExtension:@""];

    if([self createFolderAtPath:self.templateDir.path]) {
        // Perform the copy asynchronously.
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            // It's good habit to alloc/init the file manager for move/copy operations,
            // just in case you decide to add a delegate later.
            NSFileManager* theFM = [[NSFileManager alloc] init];
            NSError* anError;
            
            // Just try to copy the directory.
            if (![theFM copyItemAtURL:localTemplateDir toURL:self.templateDir error:&anError]) {
                // If an error occurs, it's probably because a previous backup directory
                // already exists.  Delete the old directory and try again.
                if ([theFM removeItemAtURL:self.templateDir error:&anError]) {
                    // If the operation failed again, abort for real.
                    if (![theFM copyItemAtURL:localTemplateDir toURL:self.templateDir error:&anError]) {
                        // Report the error....
                        NSLog(@"Error : %@", anError.description);
                        [[NSNotificationCenter defaultCenter] postNotificationName:CreateDocumentsFolderFailed object:[anError localizedDescription]];
                    }
                }
            } else {
                [[NSNotificationCenter defaultCenter] postNotificationName:CreateDocumentsFolderSuccess object:nil];
            }
        });
    } else {
        [[NSNotificationCenter defaultCenter] postNotificationName:CreateDocumentsFolderFailed object:nil];
        NSLog(@"Error: Create folder failed %@", self.templateDir.path);
    }
    
}

- (NSArray *)readContentOfDirectoryWithURL:(NSURL *)url {
    NSError *error = nil;
    NSArray *properties = [NSArray arrayWithObjects: NSURLLocalizedNameKey,
                           NSURLCreationDateKey, NSURLLocalizedTypeDescriptionKey, NSURLNameKey, nil];
    
    NSArray *array = [[NSFileManager defaultManager]
                      contentsOfDirectoryAtURL:url
                      includingPropertiesForKeys:properties
                      options:(NSDirectoryEnumerationSkipsHiddenFiles)
                      error:&error];
    return array;
}

- (NSArray *)readTemplatesDir {
    return [self readContentOfDirectoryWithURL:self.templateDir];
}

- (NSArray *)styleNames {
    NSArray *urlArray = [self readTemplatesDir];
    NSMutableArray *nameArray = [NSMutableArray new];
    for (NSURL *url in urlArray) {
        NSDictionary *dict = [url resourceValuesForKeys:@[NSURLLocalizedNameKey] error:nil];
        [nameArray addObject:dict[@"NSURLLocalizedNameKey"]];
    }
    return nameArray;
}

- (NSArray *)templateNames {
    NSArray *urlArray = [self readTemplatesDir];
    NSMutableArray *nameArray = [NSMutableArray new];
    for (NSURL *url in urlArray) {
        NSDictionary *dict = [url resourceValuesForKeys:@[NSURLLocalizedNameKey] error:nil];
        [nameArray addObject:dict[@"NSURLLocalizedNameKey"]];
    }
    return nameArray;
}

@end
