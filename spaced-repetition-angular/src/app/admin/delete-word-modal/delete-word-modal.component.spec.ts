import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteWordModalComponent } from './delete-word-modal.component';

describe('DeleteWordModalComponent', () => {
  let component: DeleteWordModalComponent;
  let fixture: ComponentFixture<DeleteWordModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteWordModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteWordModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
