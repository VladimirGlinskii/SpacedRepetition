import { TestBed } from '@angular/core/testing';

import { ApiWordService } from './api-word.service';

describe('ApiWordService', () => {
  let service: ApiWordService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiWordService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
