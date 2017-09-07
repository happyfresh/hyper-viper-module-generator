//
//  LoadingPanel.h
//  Hyper
//
//  Created by Teguh Hidayatullah on 9/6/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@interface LoadingPanel : NSPanel

@property (weak) IBOutlet NSProgressIndicator *progressBar;
@property (weak) IBOutlet NSTextField *loadingLabel;
@property (strong) IBOutlet NSView *customContentView;

@end
