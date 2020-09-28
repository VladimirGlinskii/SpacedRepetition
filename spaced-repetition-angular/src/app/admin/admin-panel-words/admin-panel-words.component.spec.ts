import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPanelWordsComponent } from './admin-panel-words.component';

describe('AdminPanelWordsComponent', () => {
  let component: AdminPanelWordsComponent;
  let fixture: ComponentFixture<AdminPanelWordsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminPanelWordsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminPanelWordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
