import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnselectDictionaryModalComponent } from './unselect-dictionary-modal.component';

describe('UnselectDictionaryModalComponent', () => {
  let component: UnselectDictionaryModalComponent;
  let fixture: ComponentFixture<UnselectDictionaryModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnselectDictionaryModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnselectDictionaryModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
