import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddDictionaryFormComponent } from './add-dictionary-form.component';

describe('AddDictionaryFormComponent', () => {
  let component: AddDictionaryFormComponent;
  let fixture: ComponentFixture<AddDictionaryFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddDictionaryFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddDictionaryFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
