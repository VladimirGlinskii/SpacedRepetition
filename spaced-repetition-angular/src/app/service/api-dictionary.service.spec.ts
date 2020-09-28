import { TestBed } from '@angular/core/testing';

import { ApiDictionaryService } from './api-dictionary.service';

describe('ApiDictionaryService', () => {
  let service: ApiDictionaryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiDictionaryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
