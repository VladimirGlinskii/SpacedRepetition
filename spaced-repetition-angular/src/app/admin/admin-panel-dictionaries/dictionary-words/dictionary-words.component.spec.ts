import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DictionaryWordsComponent } from './dictionary-words.component';

describe('DictionaryWordsComponent', () => {
  let component: DictionaryWordsComponent;
  let fixture: ComponentFixture<DictionaryWordsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DictionaryWordsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DictionaryWordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
