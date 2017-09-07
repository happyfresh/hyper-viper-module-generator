//
//  $HyperModuleNameStorage.m
//  $HyperAuthorOrganization
//
//  Created by $HyperAuthorName on $HyperCreatedDate.
//  Copyright © $HyperCreatedYear $HyperAuthorOrganization. All rights reserved.
//

#import "$HyperModuleNameStorage.h"
//#import "$HyperModuleName+CoreDataClass.h"
#import "$HyperModuleNameDatabase.h"
#import "$HyperModuleNameUserDefaults.h"

@implementation $HyperModuleNameStorage

#pragma mark - Properties

- ($HyperModuleNameUserDefaults *)userDefaults {
    if (_userDefaults == nil) {
        _userDefaults = [$HyperModuleNameUserDefaults new];
    }
    
    return _userDefaults;
}

- ($HyperModuleNameDatabase *)database {
    if (_database == nil) {
        _database = [$HyperModuleNameDatabase new];
    }
    
    return _database;
}

#pragma mark - $HyperModuleNameStorage

//- (NSNumber *)$HyperModuleNameID {
//    return self.$HyperModuleNameDefaults.$HyperModuleNameID;
//}
//
//- (void)set$HyperModuleNameID:(NSNumber *)$HyperModuleNameID {
//    self.$HyperModuleNameDefaults.$HyperModuleNameID = $HyperModuleNameID;
//}
//
//- ($HyperModuleName *)findFirstByID:(NSNumber *)$HyperModuleNameID {
//    return [self.database findFirstByID:$HyperModuleNameID];
}

@end
