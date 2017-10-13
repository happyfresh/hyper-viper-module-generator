//
//  ProductService.m
//  HappyFresh Inc
//
//  Created by Teguh Hidayatullah on 10/5/17.
//  Copyright © 2017 HappyFresh Inc. All rights reserved.
//

#import "ProductService.h"
#import "ProductStorage.h"
//#import "Product+CoreDataClass.h"
#import "ProductModel.h"
#import "ProductManagedTransformer.h"

@implementation ProductService

#pragma mark - Constructors

+ (instancetype)sharedInstance {
    static id sharedInstance = nil;
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [ProductService new];
    });
    
    return sharedInstance;
}

#pragma mark - Properties

- (ProductStorage *)storage {
    if (_storage == nil) {
        _storage = [ProductStorage new];
    }
    
    return _storage;
}

//- (ProductModel *)ProductModel {
//    Product *Product = self.Product;
//    if (Product == nil) {
//        return nil;
//    }
//    
//    return [ProductManagedTransformer modelFromManaged:Product];
//}

#pragma mark - ProductService

//- (Product *)findFirstByID:(NSNumber *)ProductID {
//    return [self.storage findFirstByID:ProductID];
//}

@end
