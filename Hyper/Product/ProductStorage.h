//
//  ProductStorage.h
//  HappyFresh Inc
//
//  Created by Teguh Hidayatullah on 10/5/17.
//  Copyright © 2017 HappyFresh Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

//@class Product, ProductDatabase, ProductProductDefaults;

@interface ProductStorage : NSObject

@property (nonatomic) ProductDatabase *database;
@property (nonatomic) ProductUserDefaults *userDefaults;

//@property (nonatomic) NSNumber *ProductID;

//- (Product *)findFirstByID:(NSNumber *)ProductID;

@end
