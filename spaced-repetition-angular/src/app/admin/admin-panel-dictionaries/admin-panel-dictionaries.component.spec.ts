import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPanelDictionariesComponent } from './admin-panel-dictionaries.component';

describe('AdminPanelDictionariesComponent', () => {
  let component: AdminPanelDictionariesComponent;
  let fixture: ComponentFixture<AdminPanelDictionariesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminPanelDictionariesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminPanelDictionariesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
