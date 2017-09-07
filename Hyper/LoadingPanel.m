//
//  LoadingPanel.m
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import "LoadingPanel.h"
#import "AppSettingsManager.h"

@implementation LoadingPanel

- (instancetype)init {
    if (self = [super init]) {
        [self commontInit];
    }
    return self;
}

- (void)commontInit {
    [[NSBundle mainBundle] loadNibNamed:NSStringFromClass([self class]) owner:self topLevelObjects:nil];
    [self setFrame:self.customContentView.frame display:YES];
    [self setContentView:self.customContentView];
    [self.progressBar startAnimation:nil];
    [self addObserver];
    
}

- (void)addObserver {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(processCompleted) name:GeneratingSuccessNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(processFailedWithErrorMessage:) name:GeneratingFailedNotification object:nil];
}

- (void)processCompleted {
    self.progressBar.hidden = YES;
    self.loadingLabel.stringValue = @"Success!";
}

- (void)processFailedWithErrorMessage:(NSString *)msg {
    self.progressBar.hidden = YES;
    self.loadingLabel.stringValue = [NSString stringWithFormat:@"Failed with message: %@", msg];
}

@end
