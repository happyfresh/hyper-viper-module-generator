//
//  $HyperModuleNameService.m
//  $HyperAuthorOrganization
//
//  Created by $HyperAuthorName on $HyperCreatedDate.
//  Copyright © $HyperCreatedYear $HyperAuthorOrganization. All rights reserved.
//

#import "$HyperModuleNameService.h"
#import "$HyperModuleNameStorage.h"
//#import "$HyperModuleName+CoreDataClass.h"
#import "$HyperModuleNameModel.h"
#import "$HyperModuleNameManagedTransformer.h"

@implementation $HyperModuleNameService

#pragma mark - Constructors

+ (instancetype)sharedInstance {
    static id sharedInstance = nil;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [$HyperModuleNameService new];
    });
    
    return sharedInstance;
}

#pragma mark - Properties

- ($HyperModuleNameStorage *)storage {
    if (_storage == nil) {
        _storage = [$HyperModuleNameStorage new];
    }
    
    return _storage;
}

//- ($HyperModuleNameModel *)$HyperModuleNameModel {
//    $HyperModuleName *$HyperModuleName = self.$HyperModuleName;
//    if ($HyperModuleName == nil) {
//        return nil;
//    }
//    
//    return [$HyperModuleNameManagedTransformer modelFromManaged:$HyperModuleName];
//}

#pragma mark - $HyperModuleNameService

//- ($HyperModuleName *)findFirstByID:(NSNumber *)$HyperModuleNameID {
//    return [self.storage findFirstByID:$HyperModuleNameID];
//}

@end
