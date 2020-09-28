import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserWordsComponent } from './user-words.component';

describe('UserWordsComponent', () => {
  let component: UserWordsComponent;
  let fixture: ComponentFixture<UserWordsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserWordsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserWordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
