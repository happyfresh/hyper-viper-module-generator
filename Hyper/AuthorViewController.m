//
//  AuthorViewController.m
//  Hyper
//
//  Created by Teguh Hidayatullah on 10/4/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import "AuthorViewController.h"
#import "AppSettingsManager.h"

@interface AuthorViewController ()

@property (weak) IBOutlet NSTextField *authorNameTF;
@property (weak) IBOutlet NSTextField *organizationNameTF;

@end

@implementation AuthorViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.authorNameTF.stringValue = [AppSettingsManager sharedInstance].authorName;
    self.organizationNameTF.stringValue = [AppSettingsManager sharedInstance].authorOrganization;
}

- (IBAction)cancelButtonTapped:(id)sender {
    [self dismiss];
}

- (IBAction)saveButtonTapped:(id)sender {
    [[AppSettingsManager sharedInstance] setAuthorName:self.authorNameTF.stringValue];
    [[AppSettingsManager sharedInstance] setAuthorOrganization:self.organizationNameTF.stringValue];
    [self dismiss];
}

- (void)dismiss {
    if (self.presentingViewController) {
        [self.presentingViewController dismissViewController:self];
    }
}

@end
