//
//  ProductService.h
//  HappyFresh Inc
//
//  Created by Teguh Hidayatullah on 10/5/17.
//  Copyright © 2017 HappyFresh Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

//@class ProductStorage, Product, ProductModel;

@interface ProductService : NSObject

@property (nonatomic) ProductStorage *storage;
//@property (readonly, nonatomic) ProductModel *ProductModel;

+ (instancetype)sharedInstance;
//- (Product *)findFirstByID:(NSNumber *)ProductID;

@end
