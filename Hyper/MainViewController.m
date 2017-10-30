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
#import "AuthorViewController.h"

@interface MainViewController () <NSTableViewDelegate, NSTableViewDataSource, NSTextFieldDelegate>

@property (nonatomic) NSURL *targetURL;

@property (weak) IBOutlet NSTextField *nameTextField;
@property (weak) IBOutlet NSPopUpButton *platformDropDown;
@property (weak) IBOutlet NSPopUpButton *styleDropDown;
@property (weak) IBOutlet NSPopUpButton *templateDropDown;
@property (weak) IBOutlet NSButton *createFolderCheckBox;
@property (weak) IBOutlet NSTextField *saveToTextField;
@property (weak) IBOutlet NSButton *browseButton;

@property (nonatomic) TemplateIO *templateIO;
@property (nonatomic) FileManager *fileManager;
@property (nonatomic) AppSettingsManager *settingsManager;
@property (nonatomic) NSPanel *loadingPanel;
@property (weak) IBOutlet NSTableView *tableView;
@property (nonatomic) NSMutableArray *templateFilesArray;
@property (nonatomic) NSMutableArray *selectedTemplateFilesArray;
@property (nonatomic) NSString *selectedPlatform;
@property (nonatomic) NSString *selectedStyle;

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

- (NSMutableArray *)templateFilesArray {
    if (_templateFilesArray == nil) {
        _templateFilesArray = [NSMutableArray new];
    }
    return _templateFilesArray;
}

- (NSMutableArray *)selectedTemplateFilesArray {
    if (_selectedTemplateFilesArray == nil) {
        _selectedTemplateFilesArray = [NSMutableArray new];
    }
    return _selectedTemplateFilesArray;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.fileManager createTemplatesFolder];
    [self setupView];
    [self addObserver];
}

- (void)setupView {
    NSArray *platformArray = [NSArray arrayWithObjects: @"Android", @"iOS", nil];
    [self.styleDropDown setEnabled:NO];
    [self.templateDropDown setEnabled:NO];
    [self.platformDropDown addItemsWithTitles:platformArray];
}

-(void)setupStyleDropdown {
    [self.fileManager setSelectedPlatform:_selectedPlatform];
    NSArray *titleArray = [self.fileManager styleNames];
    [self.styleDropDown addItemsWithTitles: titleArray];
}

- (void)setupModuleDropdown {
    [self.fileManager setSelectedStyle:_selectedStyle];
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
    if (!moduleName || !self.targetURL || ![self.templateDropDown titleOfSelectedItem] || self.selectedTemplateFilesArray.count == 0) {
        NSBeep();
        return;
    }
    
    [self.settingsManager setModuleName:moduleName];
    [self showLoadingPanel];
    [self.templateIO readFileFromTemplate:selectedTemplate selectedFiles:self.selectedTemplateFilesArray thenWriteItToDirectory:self.targetURL withModuleName:moduleName createPhysicalFolder:[self.createFolderCheckBox state]];
}

- (IBAction)resetButtonTapped:(id)sender {
    self.targetURL = nil;
    self.nameTextField.stringValue = @"";
    self.saveToTextField.stringValue = @"";
    [self.settingsManager setModuleName:nil];
    [self.templateFilesArray removeAllObjects];
    [self.selectedTemplateFilesArray removeAllObjects];
    [self getModuleFiles:[self.templateDropDown titleOfSelectedItem]];
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

- (IBAction)authorButtonTapped:(NSButton *)sender {
    NSViewController* vc = [[AuthorViewController alloc] initWithNibName:nil bundle:nil];
    [self presentViewControllerAsSheet:vc];
}

- (IBAction)platformDropdownDidChangeValue:(id)sender {
    _selectedPlatform = [(NSPopUpButton *) sender titleOfSelectedItem];
    [self.styleDropDown setEnabled:YES];
    [self setupStyleDropdown];
}

- (IBAction)styleDropdownDidChangeValue:(id)sender {
    _selectedStyle = [(NSPopUpButton *) sender titleOfSelectedItem];
    [self.templateDropDown setEnabled:YES];
    [self setupModuleDropdown];
}

- (IBAction)templateDropdownDidChangeValue:(id)sender {
    [self.templateFilesArray removeAllObjects];
    [self.selectedTemplateFilesArray removeAllObjects];
    NSString *selectedModuleName = [(NSPopUpButton *) sender titleOfSelectedItem];
    NSLog(@"My NSPopupButton selected value is: %@", [(NSPopUpButton *) sender titleOfSelectedItem]);
    [self getModuleFiles:selectedModuleName];
}

- (void)showLoadingPanel {
    [[NSApplication sharedApplication].mainWindow beginSheet:self.loadingPanel completionHandler:^(NSModalResponse returnCode) {
        
    }];
}

- (void)hideLoadingPanel {
    [[NSApplication sharedApplication].mainWindow endSheet:self.loadingPanel];
}

- (void)getModuleFiles:(NSString *)moduleName {
    NSURL *url = [self.fileManager.templateDir URLByAppendingPathComponent:moduleName];
    NSArray *templateFiles = [self.fileManager readContentOfDirectoryWithURL:url];
    for (NSURL *url in templateFiles) {
        NSDictionary *urlDictionary = [url resourceValuesForKeys:@[NSURLLocalizedNameKey] error:nil];
        NSString *fileName = urlDictionary[@"NSURLLocalizedNameKey"];
        
        fileName = [fileName stringByReplacingOccurrencesOfString:@"$HyperModuleName" withString:self.nameTextField.stringValue];
        fileName = [[fileName componentsSeparatedByString:@"."] firstObject];
        if (![self.templateFilesArray containsObject:fileName]) {
            [self.templateFilesArray addObject:fileName];
            [self.selectedTemplateFilesArray addObject:fileName];
        }
    }
    [self.tableView reloadData];
}

#pragma mark - Textfield

- (void)controlTextDidChange:(NSNotification *)obj {
    [self.templateFilesArray removeAllObjects];
    [self.selectedTemplateFilesArray removeAllObjects];
    [self getModuleFiles:[self.templateDropDown titleOfSelectedItem]];
}
#pragma mark - TableView

- (NSInteger)numberOfRowsInTableView:(NSTableView *)tableView {
    return self.templateFilesArray.count;
}

- (NSView *)tableView:(NSTableView *)tableView
   viewForTableColumn:(NSTableColumn *)tableColumn
                  row:(NSInteger)row {
    
    // Retrieve to get the @"MyView" from the pool or,
    // if no version is available in the pool, load the Interface Builder version
    NSTableCellView *result = [tableView makeViewWithIdentifier:@"MyView" owner:self];
    
    // Set the stringValue of the cell's text field to the nameArray value at row
    result.textField.stringValue = [self.templateFilesArray objectAtIndex:row];
    NSImageView *checkedImage = [result viewWithTag:10];
    checkedImage.hidden = ![self.selectedTemplateFilesArray containsObject:self.templateFilesArray[row]];
    
    // Return the result
    return result;
}

- (void)tableViewSelectionDidChange:(NSNotification *)notification {
    NSString *selectedFile = self.templateFilesArray[self.tableView.selectedRow];
    if ([self.selectedTemplateFilesArray containsObject:selectedFile]) {
        [self.selectedTemplateFilesArray removeObject:selectedFile];
    } else {
        [self.selectedTemplateFilesArray addObject:selectedFile];
    }
    [self.tableView reloadData];
}

@end
