//
//  $HyperModuleNameService.h
//  $HyperAuthorOrganization
//
//  Created by $HyperAuthorName on $HyperCreatedDate.
//  Copyright © $HyperCreatedYear $HyperAuthorOrganization. All rights reserved.
//

#import <Foundation/Foundation.h>

//@class $HyperModuleNameStorage, $HyperModuleName, $HyperModuleNameModel;

@interface $HyperModuleNameService : NSObject

@property (nonatomic) $HyperModuleNameStorage *storage;
//@property (readonly, nonatomic) $HyperModuleNameModel *$HyperModuleNameModel;

+ (instancetype)sharedInstance;
//- ($HyperModuleName *)findFirstByID:(NSNumber *)$HyperModuleNameID;

@end
