//
//  ProductStorage.m
//  HappyFresh Inc
//
//  Created by Teguh Hidayatullah on 10/5/17.
//  Copyright © 2017 HappyFresh Inc. All rights reserved.
//

#import "ProductStorage.h"
//#import "Product+CoreDataClass.h"
#import "ProductDatabase.h"
#import "ProductUserDefaults.h"

@implementation ProductStorage

#pragma mark - Properties

- (ProductUserDefaults *)userDefaults {
    if (_userDefaults == nil) {
        _userDefaults = [ProductUserDefaults new];
    }
    
    return _userDefaults;
}

- (ProductDatabase *)database {
    if (_database == nil) {
        _database = [ProductDatabase new];
    }
    
    return _database;
}

#pragma mark - ProductStorage

//- (NSNumber *)ProductID {
//    return self.ProductDefaults.ProductID;
//}
//
//- (void)setProductID:(NSNumber *)ProductID {
//    self.ProductDefaults.ProductID = ProductID;
//}
//
//- (Product *)findFirstByID:(NSNumber *)ProductID {
//    return [self.database findFirstByID:ProductID];
}

@end
