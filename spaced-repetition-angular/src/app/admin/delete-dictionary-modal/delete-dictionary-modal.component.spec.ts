import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteDictionaryModalComponent } from './delete-dictionary-modal.component';

describe('DeleteDictionaryModalComponent', () => {
  let component: DeleteDictionaryModalComponent;
  let fixture: ComponentFixture<DeleteDictionaryModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteDictionaryModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteDictionaryModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
