import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RenameDictionaryModalComponent } from './rename-dictionary-modal.component';

describe('RenameDictionaryModalComponent', () => {
  let component: RenameDictionaryModalComponent;
  let fixture: ComponentFixture<RenameDictionaryModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RenameDictionaryModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RenameDictionaryModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
