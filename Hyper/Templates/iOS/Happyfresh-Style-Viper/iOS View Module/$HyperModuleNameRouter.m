//
//  $HyperModuleNameRouter.m
//  $HyperAuthorOrganization
//
//  Created by $HyperAuthorName on $HyperCreatedDate.
//  Copyright © $HyperCreatedYear $HyperAuthorOrganization Inc. All rights reserved.
//

#import "$HyperModuleNameRouter.h"
#import "$HyperModuleNameView.h"
#import "$HyperModuleNameInteractor.h"
#import "$HyperModuleNamePresenter.h"

@implementation $HyperModuleNameRouter

#pragma mark - BaseRouter

- (id <ViewBehavior>)createView {
    return [$HyperModuleNameView createInstance];
}

- (BasePresenter *)createPresenter {
    return [$HyperModuleNamePresenter new];
}

- (BaseInteractor *)createInteractor {
    return [$HyperModuleNameInteractor new];
}

@end
