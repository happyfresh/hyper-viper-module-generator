//
//  ViewController.m
//  Hyper
//
//  Created by Teguh Hidayatullah on 8/18/17.
//  Copyright © 2017 HappyFresh. All rights reserved.
//

#import "MainViewController.h"
#import "FileManager.h"
#import "TemplateIO.h"
#import "AppSettingsManager.h"
#import "LoadingPanel.h"

@interface MainViewController ()

@property (nonatomic) NSURL *targetURL;

@property (weak) IBOutlet NSTextField *nameTextField;
@property (weak) IBOutlet NSPopUpButton *templateDropDown;
@property (weak) IBOutlet NSButton *createFolderCheckBox;
@property (weak) IBOutlet NSTextField *saveToTextField;
@property (weak) IBOutlet NSButton *browseButton;

@property (nonatomic) TemplateIO *templateIO;
@property (nonatomic) FileManager *fileManager;
@property (nonatomic) AppSettingsManager *settingsManager;
@property (nonatomic) NSPanel *loadingPanel;

@end

@implementation MainViewController

#pragma mark - Construct

- (TemplateIO *)templateIO {
    if (_templateIO == nil) {
        _templateIO = [TemplateIO new];
    }
    return _templateIO;
}

- (FileManager *)fileManager {
    if (_fileManager == nil) {
        _fileManager = [FileManager sharedInstance];
    }
    return _fileManager;
}

- (AppSettingsManager *)settingsManager {
    if (_settingsManager == nil) {
        _settingsManager = [AppSettingsManager new];
    }
    return _settingsManager;
}

- (NSPanel *)loadingPanel {
    if (_loadingPanel == nil) {
        _loadingPanel = [[LoadingPanel alloc] init];
    }
    return _loadingPanel;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.fileManager createTemplatesFolder];
    [self setupView];
    [self addObserver];
}

- (void)setupView {
    NSArray *titleArray = [self.fileManager templateNames];
    [self.templateDropDown addItemsWithTitles: titleArray];
}

- (void)addObserver {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hideLoadingPanel) name:GeneratingSuccessNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hideLoadingPanel) name:GeneratingFailedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setupView) name:CreateDocumentsFolderSuccess object:nil];
    
}

#pragma mark - IBAction

- (IBAction)generateButtonTapped:(id)sender {
    NSString *selectedTemplate = self.templateDropDown.titleOfSelectedItem;
    NSString *moduleName = self.nameTextField.stringValue;
    if (!moduleName || !self.targetURL || ![self.templateDropDown titleOfSelectedItem]) {
        return;
    }
    
    [self.settingsManager setModuleName:moduleName];
    [self showLoadingPanel];
    [self.templateIO readFileFromTemplate:selectedTemplate thenWriteItToDirectory:self.targetURL withModuleName:moduleName createPhysicalFolder:[self.createFolderCheckBox state]];
}

- (IBAction)resetButtonTapped:(id)sender {
    self.targetURL = nil;
    self.nameTextField.stringValue = @"";
    self.saveToTextField.stringValue = @"";
    [self.settingsManager setModuleName:nil];
}

- (IBAction)browseButtonTapped:(id)sender {
    NSOpenPanel *panel = [NSOpenPanel openPanel];
    [panel setCanChooseFiles:NO];
    [panel setCanChooseDirectories:YES];
    [panel setAllowsMultipleSelection:NO]; // yes if more than one dir is allowed
    
    NSInteger clicked = [panel runModal];
    
    if (clicked == NSFileHandlingPanelOKButton) {
        for (NSURL *url in [panel URLs]) {
            self.targetURL = url;
            self.saveToTextField.stringValue = url.relativeString;
            NSLog(@"url: %@", url.absoluteString);
        }
    }
}

- (void)showLoadingPanel {
    [[NSApplication sharedApplication].mainWindow beginSheet:self.loadingPanel completionHandler:^(NSModalResponse returnCode) {
        
    }];
}

- (void)hideLoadingPanel {
    [[NSApplication sharedApplication].mainWindow endSheet:self.loadingPanel];
}

@end
